package xyz.yhsj.kmusic.impl


import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song


/**
 * 百度解析
 */

object BaiduImpl : Impl {
    /**
     * 根据类型,获取歌曲排行榜详情
     * http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.billboard.billList&format=json&type=1&size=10&offset=0
     */
    override fun getSongTopDetail(topType: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.billboard.billList&format=json&type=$topType&size=$num&offset=${num * (page - 1)}"
                    , headers = mapOf("Referer" to "http://music.baidu.com/",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONArray("song_list")

                val songIds = songList.map {
                    (it as JSONObject).getString("song_id")
                }
                val musicData = getSongById(songIds)
                musicData
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    /**
     * 获取歌曲排行榜
     * http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.billboard.billCategory&format=json
     */
    override fun getSongTop(): MusicResp<List<MusicTop>> {
        return try {
            val resp = get(url = "http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.billboard.billCategory&format=json"
                    , headers = mapOf("Referer" to "http://music.baidu.com/",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONArray("content")

                val tops = songList.map {
                    val topObj = it as JSONObject
                    val topReap = MusicTop()
                    topReap.site = "baidu"
                    topReap.name = topObj.getString("name")
                    topReap.type = topObj.getInt("type").toString()
                    topReap.comment = topObj.getString("comment")
                    topReap.pic = topObj.getString("pic_s192")
                    topReap
                }
                MusicResp.success(data = tops)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    /**
     * 搜索
     */
    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.search.common&format=json&query=$key&page_no=$page&page_size=$num"
                    , headers = mapOf("Referer" to "http://music.baidu.com/",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONArray("song_list")

                val songIds = songList.map {
                    (it as JSONObject).getString("song_id")
                }
                val musicData = getSongById(songIds)
                musicData
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun getSongById(songIds: List<String>): MusicResp<List<Song>> {
        val songId = songIds.joinToString(",")

        return try {
            val songResp = get(url = "http://music.baidu.com/data/music/links?songIds=$songId",
                    headers = mapOf("Referer" to "music.baidu.com/song/$songId",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
            if (songResp.statusCode != 200) {
                MusicResp.failure(code = songResp.statusCode, msg = "请求失败")
            } else {
                val songInfo = songResp.jsonObject

                val songList = songInfo
                        .getJSONObject("data")
                        .getJSONArray("songList")
                val songs = songList.map {
                    val song = it as JSONObject
                    val radioSongId = song.getLong("songId").toString()
                    Song(
                            site = "baidu",
                            link = "http://music.baidu.com/song/$radioSongId",
                            songid = radioSongId,
                            title = song.getString("songName"),
                            author = song.getString("artistName"),
                            url = song.getString("songLink").replace("(yinyueshiting.baidu.com|zhangmenshiting.baidu.com|zhangmenshiting.qianqian.com)".toRegex(), "gss0.bdstatic.com/y0s1hSulBw92lNKgpU_Z2jR7b2w6buu"),
                            lrc = getLrcById(song.getString("lrcLink")),
                            pic = song.getString("songPicRadio"),
                            albumName = song.getString("albumName")
                    )
                }
                MusicResp.success(data = songs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun getLrcById(songId: String): String {
        return try {
            val songResp = get(url = songId,
                    timeout = 5.0,
                    headers = mapOf("Referer" to "http://music.baidu.com/",
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
}