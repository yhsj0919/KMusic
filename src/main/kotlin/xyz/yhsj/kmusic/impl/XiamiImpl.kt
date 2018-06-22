package xyz.yhsj.kmusic.impl

import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.DecodeKaiserMatrix

/**
 * 虾米解析
 */
object XiamiImpl : Impl {
    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "http://api.xiami.com/web?key=$key&v=2.0&app_key=1&r=search/songs&page=$page&limit=$num"
                    , headers = mapOf("Referer" to "http://m.xiami.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONObject("data")
                        .getJSONArray("songs")
                val songs = songList.map {
                    val song = it as JSONObject
                    val songId = song.getLong("song_id").toString()
                    Song(
                            type = "xiami",
                            link = "http://www.xiami.com/song/$songId",
                            songid = songId,
                            title = song.getString("song_name"),
                            author = song.getString("artist_name"),
                            url = song.getString("listen_file"),
                            lrc = getLrcById(song.getString("lyric")),
                            pic = song.getString("album_logo"),
                            albumName = song.getString("album_name")
                    )
                }
                MusicResp.success(data = songs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    /**
     * @param songId 支持多个id，用","隔开
     */
    override fun getSongById(songIds: List<String>): MusicResp<List<Song>> {
        val songId = songIds.joinToString(",")
        return try {
            val songResp = get(url = "http://www.xiami.com/song/playlist/id/$songId/type/0/cat/json",
                    headers = mapOf("Referer" to "http://www.xiami.com",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
            if (songResp.statusCode != 200) {
                MusicResp.failure(code = songResp.statusCode, msg = "请求失败")
            } else {
                val songInfo = songResp.jsonObject

                val songList = songInfo
                        .getJSONObject("data")
                        .getJSONArray("trackList")
                val songs = songList.map {
                    val song = it as JSONObject
                    val radioSongId = song.getString("song_id")
                    Song(
                            type = "xiami",
                            link = "http://www.xiami.com/song/$radioSongId",
                            songid = radioSongId,
                            title = song.getString("songName"),
                            author = song.getString("singers"),
                            url = DecodeKaiserMatrix.decode(song.getString("location")).http(),
                            lrc = getLrcById(song.getString("lyric").http()),
                            pic = song.getString("album_pic").http(),
                            albumName = song.getString("album_name")
                    )
                }
                MusicResp.success(data = songs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    /**
     * @param songId 虾米需要完整的url
     */
    override fun getLrcById(songId: String): String {
        return try {
            val songResp = get(url = songId,
                    timeout = 5.0,
                    headers = mapOf("Referer" to "http://www.xiami.com",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
            if (songResp.statusCode == 200) {
                val songInfo = songResp.text
                songInfo
            } else {
                "[00:00:00]此歌曲可能没有歌词"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("获取歌词出现异常")
            "[00:00:00]此歌曲可能没有歌词"
        }
    }

    //组装http连接
    private fun String?.http() = when {
        this == null -> ""
        this.startsWith("http://") -> this
        this.startsWith("//") -> "http:$this"
        else -> "http://$this"
    }
}