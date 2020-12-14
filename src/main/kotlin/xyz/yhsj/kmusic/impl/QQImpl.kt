package xyz.yhsj.kmusic.impl


import xyz.yhsj.json.JSONArray
import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.Album
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.future

/**
 * 解析
 */
object QQImpl : Impl {
    /**
     *根据ID获取专辑详情
     * @param albumId 专辑ID
     * https://c.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?albummid=000gCu9F4cwL7S&g_tk=5381&jsonpCallback=albuminfoCallback&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0
     */
    override fun getAlbumById(albumId: String): MusicResp<Album> {
        return try {
            val resp = get(
                url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?albummid=$albumId", headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject

                val albumResp = radioData
                    .getJSONObject("data")
                val album = Album(
                    site = "qq",
                    name = albumResp.getString("name"),
                    mid = albumResp.getString("mid"),
                    pic = "https://y.gtimg.cn/music/photo_new/T002R300x300M000${albumResp.getString("mid")}.jpg?max_age=2592000",
                    singerName = albumResp.getString("singername"),
                    publicTime = albumResp.getString("aDate"),
                    songCount = albumResp.getLong("total_song_num"),
                    desc = albumResp.getString("desc"),
                    company = albumResp.getString("company"),
                    lan = albumResp.getString("lan"),
                    genre = albumResp.getString("genre")
                )

                val songIds = albumResp.getJSONArray("list")
                    .map { (it as JSONObject).getString("songmid") }

                album.list.addAll(getSongById(songIds).data ?: emptyList())

                MusicResp(data = album)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    /**
     * @param key 关键字
     * @param page 页数
     * 搜索专辑
     */
    override fun searchAlbum(key: String, page: Int, num: Int): MusicResp<List<Album>> {
        return try {
            //https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&searchid=43060341428840891&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=20&w=%E8%96%9B%E4%B9%8B%E8%B0%A6&g_tk=5381&jsonpCallback=MusicJsonCallback6790904757407503&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0
            //https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&remoteplace=txt.yqq.center&searchid=53629173608275471&aggr=0&catZhida=1&lossless=0&sem=10&t=8&p=1&n=30&w=%E8%96%9B%E4%B9%8B%E8%B0%A6&g_tk=5381&jsonpCallback=MusicJsonCallback6651207653165634&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0

            //t=8为 专辑搜索  t=0为单曲搜索
            val resp = get(
                url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?cr=1&p=$page&n=$num&format=json&w=$key&t=8",
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject

                val albumList = radioData
                    .getJSONObject("data")
                    .getJSONObject("album")
                    .getJSONArray("list")

                val albums = albumList.map {
                    val album = (it as JSONObject)


                    Album(
                        site = "qq",
                        name = album.getString("albumName"),
                        mid = album.getString("albumMID"),
                        pic = album.getString("albumPic"),
                        singerName = album.getString("singerName"),
                        publicTime = album.getString("publicTime"),
                        songCount = album.getLong("song_count")
                    )
                }
                MusicResp(data = albums)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    /**
     * 根据类型,获取歌曲排行榜详情
     * top 巅峰榜
     * global 全球榜
     *
     * https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?date=2018-06-22&topid=4&type=top&song_begin=0&song_num=10&format=jsonp&inCharset=utf8&outCharset=utf-8
     * 巅峰MV
     * https://c.y.qq.com/mv/fcgi-bin/fcg_musicshow_mvtoplist.fcg?format=jsonp&g_tk=5381&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&needNewCode=0&listid=mainland_musicshow_mvtoplist_current_new
     */
    override fun getSongTopDetail(
        topId: String,
        topType: String,
        topKey: String,
        page: Int,
        num: Int
    ): MusicResp<List<Song>> {
        return try {
            val resp = get(
                url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?date=$topKey&topid=$topId&type=$topType&song_begin=${(page - 1) * num}&song_num=$num&format=jsonp&inCharset=utf8&outCharset=utf-8",
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject

                val songList = radioData
                    .getJSONArray("songlist")

                val songIds = songList.map {
                    (it as JSONObject).getJSONObject("data").getString("songmid")
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
     * https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_opt.fcg?page=index&format=html&v8debug=1
     * 暂时过滤掉MV
     */
    override fun getSongTop(): MusicResp<List<MusicTop>> {
        return try {
            val resp = get(
                url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_opt.fcg?page=index&format=html&v8debug=1",
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.text
                val topList = radioData.replace("jsonCallback(", "")
                    .replace("])", "]")

                val songList = JSONArray(topList)

                val tops = songList
                    .flatMap {
                        (it as JSONObject).getJSONArray("List")
                    }.map {
                        val topObj = it as JSONObject
                        val topReap = MusicTop()
                        topReap.site = "qq"
                        topReap.name = topObj.getString("ListName")
                        topReap.topId = topObj.getInt("topID").toString()
                        topReap.topKey = topObj.getString("update_key")
                        topReap.topType = when (topObj.getInt("type")) {
                            0 -> "top"
                            1 -> "global"
                            else -> ""
                        }
                        topReap.comment = topObj.getString("ListName")
                        topReap.pic = topObj.getString("pic")
                        topReap
                    }
                    //暂时过滤掉MV榜
                    .filter { !it.topType.isNullOrEmpty() }
                MusicResp.success(data = tops)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            //https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&searchid=43060341428840891&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=20&w=%E8%96%9B%E4%B9%8B%E8%B0%A6&g_tk=5381&jsonpCallback=MusicJsonCallback6790904757407503&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0
            //https://c.y.qq.com/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&remoteplace=txt.yqq.center&searchid=53629173608275471&aggr=0&catZhida=1&lossless=0&sem=10&t=8&p=1&n=30&w=%E8%96%9B%E4%B9%8B%E8%B0%A6&g_tk=5381&jsonpCallback=MusicJsonCallback6651207653165634&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0

            //t=8为 专辑搜索  t=0为单曲搜索
            val resp = get(
                url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?cr=1&p=$page&n=$num&format=json&w=$key&t=0",
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                    .getJSONObject("data")
                    .getJSONObject("song")
                    .getJSONArray("list")

                val songs = songList.map {
                    val songInfo = (it as JSONObject)
                    val songmid = songInfo.getString("songmid")
                    val albummid = songInfo.getString("albummid")
                    Song(
                        site = "qq",
                        link = "http://y.qq.com/n/yqq/song/$songmid.html",
                        songid = songmid,
                        title = songInfo.getString("songname"),
                        author = songInfo.getJSONArray("singer")
                            .joinToString(",") { (it as JSONObject).getString("name") },
                        url = getSongUrl(songmid),
                        lrc = getLrcById(songmid),
                        pic = "https://y.gtimg.cn/music/photo_new/T002R300x300M000${albummid}_1.jpg?max_age=2592000",
                        albumName = songInfo.getString("albumname")
                    )


                }
                return MusicResp.success(data = songs)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun getSongById(songIds: List<String>): MusicResp<List<Song>> {
        val songId = songIds.joinToString(",")

        return try {
            val vkeyResp = get(
                url = "https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?json=3&guid=5150825362&format=json",
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )

            val vkeyData = if (vkeyResp.statusCode == 200) {

                println(vkeyResp.text)

                vkeyResp.jsonObject
            } else {
                null
            }
            val songResp = get(
                url = "http://c.y.qq.com/v8/fcg-bin/fcg_play_single_song.fcg?format=json&songmid=$songId",
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )

            if (songResp.statusCode != 200) {
                MusicResp.failure(code = songResp.statusCode, msg = "请求失败")
            } else {
                val songInfo = songResp.jsonObject

                val songDatas = songInfo.getJSONArray("data")

                val songs = songDatas.future {
                    val songData = it as JSONObject

                    val songUrl = songInfo.getJSONObject("url")

                    val mid = songData.getString("mid")

                    val radioAuthors = songData.getJSONArray("singer").joinToString(",") {
                        (it as JSONObject).getString("title")
                    }

                    val radioLrc = getLrcById(mid)

                    val radioMusic = if (vkeyData != null && !vkeyData.isNull("key")) {
                        generateQQmusicUrl(mid, vkeyData.getString("key"))
                    } else {
                        "http://${songUrl.getString(songData.getString("id")).replace("ws", "dl")}"
                    }

                    val radioAlbum = if (!songData.isNull("album") && !songData.getJSONObject("album").isNull("mid")) {
                        val albumId = songData.getJSONObject("album").getString("mid")
                        "http://y.gtimg.cn/music/photo_new/T002R300x300M000$albumId.jpg"
                    } else {
                        ""
                    }

                    val albumName = if (!songData.isNull("album") && !songData.getJSONObject("album").isNull("name")) {
                        songData.getJSONObject("album").getString("name")
                    } else {
                        ""
                    }
                    Song(
                        site = "qq",
                        link = "http://y.qq.com/n/yqq/song/$mid.html",
                        songid = mid,
                        title = songData.getString("title"),
                        author = radioAuthors,
                        url = radioMusic,
                        lrc = radioLrc,
                        pic = radioAlbum,
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
        val radioLrcUrls =
            "http://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric.fcg?format=json&nobase64=1&songtype=0&callback=c&songmid=$songId"
        return try {
            val songResp = get(
                url = radioLrcUrls,
                timeout = 5.0,
                headers = mapOf(
                    "Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            return if (songResp.statusCode == 200) {
                val lrcStr = songResp.text
                val lrcJson = JSONObject(lrcStr.substring(2, lrcStr.length - 1))
                lrcJson.getString("lyric").unescapeHtml()
            } else {
                "[00:00:00]此歌曲可能没有歌词"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("获取歌词出现异常")
            "[00:00:00]此歌曲可能没有歌词"
        }
    }

    /**
     * 生成url
     */
    private fun generateQQmusicUrl(songId: String, key: String): String {
//        val quality = arrayListOf("M800", "M500")
//
//        val urls = quality.map {
//            "http://dl.stream.qqmusic.qq.com/$it$songId.mp3?vkey=$key&guid=5150825362&fromtag=1"
//        }.joinToString(" , ")

        return "http://dl.stream.qqmusic.qq.com/M500$songId.mp3?vkey=$key&guid=5150825362&fromtag=1"
    }

    //去除歌词里的转义字符
    private fun String.unescapeHtml() =
        this.replace("&#10;", "\n")
            .replace("&#13;", "\r")
            .replace("&#32;", " ")
            .replace("&#39;", "'")
            .replace("&#40;", "(")
            .replace("&#41;", ")")
            .replace("&#45;", "-")
            .replace("&#46;", ".")
            .replace("&#58;", ":")
            .replace("&#64;", "@")
            .replace("&#95;", "_")
            .replace("&#124;", "|")

    /**
     * 根据ID获取播放地址
     *
     */

    fun getSongUrl(songId: String): String {
        //"https://u.y.qq.com/cgi-bin/musicu.fcg?data={\"req\":{\"module\":\"CDN.SrfCdnDispatchServer\",\"method\":\"GetCdnDispatch\",\"param\":{\"guid\":\"3982823384\",\"calltype\":0,\"userip\":\"\"}},\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"3982823384\",\"songmid\":[\"003COGf722WjdV\"],\"songtype\":[0],\"uin\":\"0\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":0,\"format\":\"json\",\"ct\":24,\"cv\":0}}",
        return try {
            val songResp = get(
                url = "https://u.y.qq.com/cgi-bin/musicu.fcg?data={\"req\":{\"module\":\"CDN.SrfCdnDispatchServer\",\"method\":\"GetCdnDispatch\",\"param\":{\"guid\":\"3982823384\",\"calltype\":0,\"userip\":\"\"}},\"req_0\":{\"module\":\"vkey.GetVkeyServer\",\"method\":\"CgiGetVkey\",\"param\":{\"guid\":\"3982823384\",\"songmid\":[\"$songId\"],\"songtype\":[0],\"uin\":\"0\",\"loginflag\":1,\"platform\":\"20\"}},\"comm\":{\"uin\":0,\"format\":\"json\",\"ct\":24,\"cv\":0}}",
                timeout = 5.0,
                headers = mapOf(
                    "Referer" to "http://y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (songResp.statusCode == 200) {
                val songInfo = songResp.jsonObject

                val host = songInfo.getJSONObject("req").getJSONObject("data").getJSONArray("freeflowsip").getString(0)


                val url = songInfo.getJSONObject("req_0").getJSONObject("data").getJSONArray("midurlinfo")
                    .getJSONObject(0).getString("purl")

                if (host.isNullOrEmpty() || url.isNullOrEmpty()) {
                    ""
                } else {
                    host + url
                }

            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("获取Url出现异常")
            ""
        }
    }
}