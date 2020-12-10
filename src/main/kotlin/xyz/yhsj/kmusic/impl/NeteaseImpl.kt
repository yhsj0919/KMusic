package xyz.yhsj.kmusic.impl


import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.khttp.post
import xyz.yhsj.kmusic.entity.Album
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.MusicUtil
import xyz.yhsj.kmusic.utils.future

/**
 * 网易云音乐解析
 */
object NeteaseImpl : Impl {
    /**
     *根据ID获取专辑详情
     * @param albumId 专辑ID
     */
    override fun getAlbumById(albumId: String): MusicResp<Album> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * @param key 关键字
     * @param page 页数
     * 搜索专辑
     */
    override fun searchAlbum(key: String, page: Int, num: Int): MusicResp<List<Album>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 根据类型,获取歌曲排行榜详情
     * http://music.163.com/weapi/v3/playlist/detail
     * http://music.163.com/weapi/v3/playlist/detail
     */
    override fun getSongTopDetail(topId: String, topType: String, topKey: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val params = MusicUtil.arithmetic(mapOf("id" to topId, "n" to "200"))
            val resp = post(url = "http://music.163.com/weapi/v3/playlist/detail",
                    params = params,
                    headers = mapOf(
                            "Accept" to "*/*",
                            "Host" to "music.163.com",
                            "Referer" to "http://music.163.com",
                            "Cookie" to "appver=2.0.2",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                    ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONObject("playlist")
                        .getJSONArray("tracks")

                val songs = songList.future {
                    val song = it as JSONObject
                    val songId = song.getLong("id").toString()
                    val albumName = song.getJSONObject("al").getString("name")

                    val pic = song.getJSONObject("al").get("picUrl")

                    val author = song.getJSONArray("ar").joinToString(",") {
                        (it as JSONObject).getString("name")
                    }
                    Song(
                            site = "netease",
                            link = "http://music.163.com/#/song?id=$songId",
                            songid = songId,
                            title = song.getString("name"),
                            author = author,
                            url = "http://music.163.com/song/media/outer/url?id=$songId.mp3",
                            lrc = getLrcById(songId),
                            pic = "$pic?param=300x300",
                            albumName = albumName
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
     * 获取歌曲排行榜
     * http://music.163.com/eapi/toplist/detail
     */
    override fun getSongTop(): MusicResp<List<MusicTop>> {
        return try {
            val params = MusicUtil.arithmetic(mapOf("csrf_token" to ""))
            val resp = post(url = "http://music.163.com/weapi/toplist/detail",
                    params = params,
                    headers = mapOf(
                            "Accept" to "*/*",
                            "Host" to "music.163.com",
                            "Referer" to "http://music.163.com",
                            "Cookie" to "appver=2.0.2",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                    ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.text
                        .replace("\"tracks\":null,", "")
                        .replace("\"tracks\": null,", "")
                val songList = JSONObject(radioData)
                        .getJSONArray("list")
                val tops = songList.map {
                    val topObj = it as JSONObject
                    val topReap = MusicTop()
                    topReap.site = "netease"
                    topReap.name = topObj.getString("name")
                    topReap.topId = topObj.getInt("id").toString()
                    topReap.comment = topObj.getString("description")
                    topReap.pic = topObj.getString("coverImgUrl")
                    topReap
                }
                MusicResp.success(data = tops)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = post(url = "http://music.163.com/api/cloudsearch/pc",
                    params = mapOf("s" to key, "offset" to "${page * num - num}", "limit" to "$num", "type" to "1"),
                    headers = mapOf(
                            "Accept" to "*/*",
                            "Host" to "music.163.com",
                            "Referer" to "http://music.163.com",
                            "Cookie" to "appver=2.0.2",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                    ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONObject("result")
                        .getJSONArray("songs")

                val songs = songList.future {
                    val song = it as JSONObject

                    val songId = song.getLong("id").toString()
                    val albumName = song.getJSONObject("al").getString("name")

                    val pic = song.getJSONObject("al").get("picUrl")

                    val author = song.getJSONArray("ar").joinToString(",") {
                        (it as JSONObject).getString("name")
                    }
                    Song(
                            site = "netease",
                            link = "http://music.163.com/#/song?id=$songId",
                            songid = songId,
                            title = song.getString("name"),
                            author = author,
                            url = "http://music.163.com/song/media/outer/url?id=$songId.mp3",
                            lrc = getLrcById(songId),
                            pic = "$pic?param=300x300",
                            albumName = albumName
                    )
                }
                MusicResp.success(data = songs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun getSongById(songIds: List<String>): MusicResp<List<Song>> {

        val songId = songIds.joinToString(",")

        return try {
            val songResp = post(url = "http://music.163.com/api/song/detail",
                    params = mapOf("id" to songId, "ids" to "[$songId]"),
                    headers = mapOf(
                            "Accept" to "*/*",
                            "Host" to "music.163.com",
                            "Referer" to "http://music.163.com",
                            "Cookie" to "appver=2.0.2",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                    ))
            if (songResp.statusCode != 200) {
                MusicResp.failure(code = songResp.statusCode, msg = "请求失败")
            } else {
                val songInfo = songResp.jsonObject
                val songList = songInfo
                        .getJSONArray("songs")
                val songs = songList.map {
                    val song = it as JSONObject
                    val id = song.getLong("id").toString()
                    val albumName = song.getJSONObject("album").getString("name")
                    val pic = song.getJSONObject("album").getString("picUrl")
                    val author = song.getJSONArray("artists").joinToString(",") {
                        (it as JSONObject).getString("name")
                    }
                    Song(
                            site = "netease",
                            link = "http://music.163.com/#/song?id=$id",
                            songid = id,
                            title = song.getString("name"),
                            author = author,
                            url = "http://music.163.com/song/media/outer/url?id=$id.mp3",
                            lrc = getLrcById(id),
                            pic = "$pic?param=300x300",
                            albumName = albumName
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
        val radioLrcUrls = "http://music.163.com/api/song/lyric?id=$songId&lv=1&kv=1&tv=-1"
        return try {
            val songResp = get(url = radioLrcUrls,
                    timeout = 5.0,
                    headers = mapOf("Referer" to "http://music.163.com",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
            return if (songResp.statusCode == 200) {
                val lrcJson = songResp.jsonObject
                lrcJson.getJSONObject("lrc").getString("lyric")
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