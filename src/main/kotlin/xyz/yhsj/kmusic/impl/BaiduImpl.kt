package xyz.yhsj.kmusic.impl


import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.Album
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.future


/**
 * 百度解析
 */

object BaiduImpl : Impl {
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
     * http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.billboard.billList&format=json&type=1&size=10&offset=0
     */
    override fun getSongTopDetail(topId: String, topType: String, topKey: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "http://musicapi.qianqian.com/v1/restserver/ting?method=baidu.ting.billboard.billList&format=json&type=$topId&size=$num&offset=${num * (page - 1)}"
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
                    topReap.topId = topObj.getInt("type").toString()
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
            //http://musicapi.taihe.com/v1/restserver/ting?from=webapp_music&format=json&method=baidu.ting.search.merge&query=%E8%96%9B%E4%B9%8B%E8%B0%A6&page_size=20&page_no=0&type=0,1,2,5,7
            val resp = get(url = "http://musicapi.taihe.com/v1/restserver/ting?method=baidu.ting.search.merge&format=json&query=$key&page_no=$page&page_size=$num"
                    , headers = mapOf("Referer" to "http://music.baidu.com/",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONObject("result")
                        .getJSONObject("song_info")
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


        val songs: List<Song> =
                songIds.future { songId ->
                    try {
                        //http://musicapi.taihe.com/v1/restserver/ting?format=json&from=webapp_music&method=baidu.ting.song.playAAC&songid=100575177
                        val songResp = get(url = "http://musicapi.taihe.com/v1/restserver/ting?format=json&from=webapp_music&method=baidu.ting.song.play&songid=$songId",
                                headers = mapOf("Referer" to "music.baidu.com/song/$songId",
                                        "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
                        if (songResp.statusCode != 200) {
                            Song(
                                    site = "baidu",
                                    code = songResp.statusCode,
                                    msg = "网络异常",
                                    songid = songId)
                        } else {

                            val songInfo = songResp.jsonObject

                            Song(
                                    site = "baidu",
                                    link = "http://music.baidu.com/song/$songId",
                                    songid = songId,
                                    title = songInfo.getJSONObject("songinfo").getString("title"),
                                    author = songInfo.getJSONObject("songinfo").getString("author"),
                                    url = songInfo.getJSONObject("bitrate").getString("file_link"),
                                    lrc = getLrcById(songInfo.getJSONObject("songinfo").getString("lrclink")),
                                    pic = songInfo.getJSONObject("songinfo").getString("pic_radio"),
                                    albumName = songInfo.getJSONObject("songinfo").getString("album_title")
                            )


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Song(
                                site = "baidu",
                                code = 500,
                                msg = e.message ?: "未知异常",
                                songid = songId
                        )
                    }

                }

        return MusicResp.success(data = songs)
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