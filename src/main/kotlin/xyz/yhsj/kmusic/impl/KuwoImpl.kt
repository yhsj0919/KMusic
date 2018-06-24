package xyz.yhsj.kmusic.impl

import xyz.yhsj.json.JSONObject
import xyz.yhsj.json.XML
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.future


/**
 * 酷我接口
 */
object KuwoImpl : Impl {
    /**
     * 根据类型,获取歌曲排行榜详情
     */
    override fun getSongTopDetail(topType: String, page: Int, num: Int): MusicResp<List<Song>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 获取歌曲排行榜
     */
    override fun getSongTop(): MusicResp<List<MusicTop>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**@param key 关键字
     * @param page 页数
     * 搜索
     */
    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "http://search.kuwo.cn/r.s?ft=music&itemset=web_2013&rn=$num&rformat=json&encoding=utf8&all=$key&pn=${page - 1}"
                    , headers = mapOf("Referer" to "http://player.kuwo.cn/webmusic/play",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                        .getJSONArray("abslist")
                val songIds = songList.map {
                    (it as JSONObject).getString("MUSICRID").replace("MUSIC_", "")
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
     * 根据id获取歌曲，部分接口支持获取多个，id用“，”分开
     */
    override fun getSongById(songIds: List<String>): MusicResp<List<Song>> {
        //使用携程并发加载
        val songs: List<Song> =
                songIds.future { songId ->
                    try {
                        val songResp = get(url = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_$songId",
                                headers = mapOf("Referer" to "http://player.kuwo.cn/webmusic/play",
                                        "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
                        if (songResp.statusCode != 200) {
                            Song(
                                    site = "kuwo",
                                    code = songResp.statusCode,
                                    msg = "网络异常",
                                    songid = songId)
                        } else {
                            //先把xml转成json，&是关键字，解析的时候会报错，先转义
                            val songInfo = XML.toJSONObject(songResp.text
                                    .replace("&", "%26")
                                    .replace("<Song>", "")
                                    .replace("</Song>", "")
                            )

                            val radioSongId = songInfo.getLong("music_id").toString()

                            val url = "http://${songInfo.getString("mp3dl")}/resource/${songInfo.getString("mp3path")}"

                            val albumImg = songInfo.getString("artist_pic240")
                            val imgUrl = songInfo.getString("artist_pic")

                            Song(
                                    site = "kuwo",
                                    link = "http://www.kuwo.cn/yinyue/$radioSongId",
                                    songid = radioSongId,
                                    title = songInfo.getString("name"),
                                    author = songInfo.getString("singer"),
                                    url = url,
                                    lrc = getLrcById(radioSongId),
                                    pic = if (albumImg.isEmpty()) imgUrl else albumImg,
                                    albumName = songInfo.getString("special")
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Song(
                                site = "kuwo",
                                code = 500,
                                msg = e.message ?: "未知异常",
                                songid = songId,
                                title = "N/A",
                                author = "N/A",
                                albumName = "N/A"
                        )
                    }
                }
                        .filter { it.code == 200 }

        return MusicResp.success(data = songs)
    }

    /**
     * 根据id获取歌词，部分接口需要传入完整url，由网站确定
     */
    override fun getLrcById(songId: String): String {
        return try {
            val url = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=$songId"
            val songResp = get(url = url,
                    timeout = 5.0,
                    headers = mapOf("Referer" to "http://m.kuwo.cn/yinyue/$songId",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
            if (songResp.statusCode == 200) {
                val songInfo = songResp.jsonObject
                val lrcList = songInfo.getJSONObject("data").getJSONArray("lrclist")
                val lrc = lrcList.joinToString("\n") {
                    val lrcLine = it as JSONObject
                    "[${lrcLine.getString("time").lrcTime()}]${lrcLine.getString("lineLyric")}"
                }
                lrc
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
     * 格式化歌词时间
     */
    private fun String.lrcTime(): String {
        return try {
            val times = this.split(".")
            val secondTmp = times[0].toInt()
            val millisecond = times[1].formatZero()
            val minute = (secondTmp.toInt() / 60).toString().formatZero()
            val second = (secondTmp % 60).toString().formatZero()

            "$minute:$second.$millisecond"
        } catch (e: Exception) {
            "00:$this"
        }
    }

    /**
     * 补0
     */
    private fun String.formatZero() = if (this.length < 2) {
        "0$this"
    } else {
        this.substring(0, 2)
    }


}