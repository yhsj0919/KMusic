package xyz.yhsj.kmusic.impl


import xyz.yhsj.json.JSONArray
import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song

/**
 * 解析
 */
object QQImpl : Impl {
    /**
     * 根据类型,获取歌曲排行榜详情
     * top 巅峰榜
     * global 全球榜
     *
     * https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?date=2018-06-22&topid=4&type=top&song_begin=0&song_num=10&format=jsonp&inCharset=utf8&outCharset=utf-8
     * 巅峰MV
     * https://c.y.qq.com/mv/fcgi-bin/fcg_musicshow_mvtoplist.fcg?format=jsonp&g_tk=5381&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&needNewCode=0&listid=mainland_musicshow_mvtoplist_current_new
     */
    override fun getSongTopDetail(topId: String, topType: String, topKey: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?date=$topKey&topid=$topId&type=$topType&song_begin=${(page - 1) * num}&song_num=$num&format=jsonp&inCharset=utf8&outCharset=utf-8"
                    , headers = mapOf("Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
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
            val resp = get(url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_opt.fcg?page=index&format=html&v8debug=1"
                    , headers = mapOf("Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
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
            val resp = get(url = "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?cr=1&p=$page&n=$num&format=json&w=$key"
                    , headers = mapOf("Referer" to "http://m.y.qq.com",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONObject("data")
                        .getJSONObject("song")
                        .getJSONArray("list")

                val songIds = songList.map {
                    (it as JSONObject).getString("songmid")
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
            val vkeyResp = get(url = "http://base.music.qq.com/fcgi-bin/fcg_musicexpress.fcg?json=3&guid=5150825362&format=json",
                    headers = mapOf("Referer" to "http://m.y.qq.com",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))

            val vkeyData = if (vkeyResp.statusCode == 200) {
                vkeyResp.jsonObject
            } else {
                null
            }
            val songResp = get(url = "http://c.y.qq.com/v8/fcg-bin/fcg_play_single_song.fcg?format=json&songmid=$songId",
                    headers = mapOf("Referer" to "http://m.y.qq.com",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))

            if (songResp.statusCode != 200) {
                MusicResp.failure(code = songResp.statusCode, msg = "请求失败")
            } else {
                val songInfo = songResp.jsonObject

                val songDatas = songInfo.getJSONArray("data")

                val songs = songDatas.map {
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
        val radioLrcUrls = "http://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric.fcg?format=json&nobase64=1&songtype=0&callback=c&songmid=$songId"
        return try {
            val songResp = get(url = radioLrcUrls,
                    timeout = 5.0,
                    headers = mapOf("Referer" to "http://m.y.qq.com",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
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

        return "http://dl.stream.qqmusic.qq.com/M800$songId.mp3?vkey=$key&guid=5150825362&fromtag=1"
    }

    //去除歌词里的转义字符
    private fun String.unescapeHtml() =
            this.replace("&#10;", "\n")
                    .replace("&#13;", "\r")
                    .replace("&#32;", " ")
                    .replace("&#40;", "(")
                    .replace("&#41;", ")")
                    .replace("&#45;", "-")
                    .replace("&#46;", ".")
                    .replace("&#58;", ":")
                    .replace("&#64;", "@")
                    .replace("&#124;", "|")


}