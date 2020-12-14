package xyz.yhsj.kmusic.impl

import xyz.yhsj.json.JSONObject
import xyz.yhsj.json.XML
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.Album
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.future
import java.util.*


/**
 * 酷我接口
 */
object KuwoImpl : Impl {
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
                url = "http://kbangserver.kuwo.cn/ksong.s?from=pc&fmt=json&type=bang&data=content&rn=$num&id=16&pn=${page - 1}",
                headers = mapOf(
                    "Referer" to "http://player.kuwo.cn/webmusic/play",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                    .getJSONArray("musiclist")
                val songIds = songList.map {
                    (it as JSONObject).getString("id")
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
     */
    override fun getSongTop(): MusicResp<List<MusicTop>> {
        val tops = arrayListOf(
            MusicTop(
                site = "kuwo",
                topId = "12",
                name = "Billboard榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/14/14/1481781261118_.png",
                comment = "Billboard榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "49",
                name = "iTunes音乐榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/15/15/1481781343391_.png",
                comment = "iTunes音乐榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "13",
                name = "英国UK榜",
                pic = "http://img4.kwcdn.kuwo.cn/star/upload/7/7/1481781590327_.png",
                comment = "英国UK榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "4",
                name = "台湾幽浮榜",
                pic = "http://img3.kwcdn.kuwo.cn/star/upload/12/12/1530184349244_.png",
                comment = "台湾幽浮榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "14",
                name = "韩国M-net榜",
                pic = "http://img4.kwcdn.kuwo.cn/star/upload/2/2/1481781405506_.png",
                comment = "韩国M-net榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "15",
                name = "日本公信榜",
                pic = "http://img2.kwcdn.kuwo.cn/star/upload/8/8/1481781468616_.png",
                comment = "日本公信榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "8",
                name = "香港电台榜",
                pic = "http://img3.kwcdn.kuwo.cn/star/upload/11/11/1512641164299_.jpg",
                comment = "香港电台榜"
            ),

            MusicTop(
                site = "kuwo",
                topId = "16",
                name = "酷我热歌榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/7/7/1444901362664_.jpg",
                comment = "酷我热歌榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "17",
                name = "酷我新歌榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/7/7/1444901569447_.jpg",
                comment = "酷我新歌榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "93",
                name = "酷我飙升榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/2/2/1530587832450_.png",
                comment = "酷我飙升榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "62",
                name = "酷我华语榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/14/14/1530587833022_.png",
                comment = "酷我华语榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "158",
                name = "潮流热歌榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/2/2/1531102994754_.png",
                comment = "潮流热歌榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "157",
                name = "老铁热歌榜",
                pic = "http://img3.kwcdn.kuwo.cn/star/upload/4/4/1531102994740_.png",
                comment = "老铁热歌榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "22",
                name = "酷我欧美榜",
                pic = "http://img3.kwcdn.kuwo.cn/star/upload/8/8/1444901107656_.jpg",
                comment = "酷我欧美榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "23",
                name = "酷我日韩榜",
                pic = "http://img2.kwcdn.kuwo.cn/star/upload/5/5/1444901436021_.jpg",
                comment = "酷我日韩榜"
            ),

            MusicTop(
                site = "kuwo",
                topId = "154",
                name = "酷我综艺榜",
                pic = "http://img2.kwcdn.kuwo.cn/star/upload/12/12/1530587833484_.png",
                comment = "酷我综艺榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "26",
                name = "经典怀旧榜",
                pic = "http://img2.kwcdn.kuwo.cn/star/upload/6/6/1530587833670_.png",
                comment = "经典怀旧榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "63",
                name = "网络神曲榜",
                pic = "http://img3.kwcdn.kuwo.cn/star/upload/6/6/1530587833558_.png",
                comment = "网络神曲榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "76",
                name = "夜店舞曲榜",
                pic = "http://img2.kwcdn.kuwo.cn/star/upload/2/2/1444906881618_.jpg",
                comment = "夜店舞曲榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "64",
                name = "热门影视榜",
                pic = "http://img3.kwcdn.kuwo.cn/star/upload/13/13/1530587834109_.png",
                comment = "热门影视榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "153",
                name = "网红新歌榜",
                pic = "http://img4.kwcdn.kuwo.cn/star/upload/12/12/1530587833948_.png",
                comment = "网红新歌榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "104",
                name = "酷我首发榜",
                pic = "http://img4.kwcdn.kuwo.cn/star/upload/12/12/1530587834348_.png",
                comment = "酷我首发榜"
            ),
            MusicTop(
                site = "kuwo",
                topId = "106",
                name = "酷我真声音",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/10/10/1530587834378_.png",
                comment = "酷我真声音"
            ),
            MusicTop(
                site = "kuwo",
                topId = "151",
                name = "腾讯音乐人",
                pic = "http://img2.kwcdn.kuwo.cn/star/upload/4/4/1530587834692_.png",
                comment = "腾讯音乐人"
            ),
            MusicTop(
                site = "kuwo",
                topId = "145",
                name = "单曲畅销榜",
                pic = "http://img1.kwcdn.kuwo.cn/star/upload/7/7/1530587834743_.png",
                comment = "单曲畅销榜"
            )
        )
        return MusicResp.success(data = tops)
    }

    /**@param key 关键字
     * @param page 页数
     * 搜索
     */
    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            //http://kuwo.cn/api/www/search/searchMusicBykeyWord?key=%E8%96%9B%E4%B9%8B%E8%B0%A6&pn=1&rn=30&httpsStatus=1&reqId=24020ad0-3ab4-11eb-8b50-cf8a98bef531
            val resp = get(
                url = "http://kuwo.cn/api/www/search/searchMusicBykeyWord?key=$key&pn=$page&rn=$num&httpsStatus=1&reqId=24020ad0-3ab4-11eb-8b50-cf8a98bef531",
                headers = mapOf(
                    "Referer" to "http://kuwo.cn/search/list?key=$key",
                    "Cookie" to "kw_token=EUOH79P2LLK",
                    "csrf" to "EUOH79P2LLK",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                    .getJSONObject("data")
                    .getJSONArray("list")

                val songs = songList.map {
                    val songInfo = (it as JSONObject)
                    Song(
                        site = "kuwo",
                        link = "http://kuwo.cn/play_detail/${songInfo.getInt("rid")}",
                        songid = songInfo.getInt("rid").toString(),
                        title = songInfo.getString("name", ""),
                        author = songInfo.getString("artist", ""),
                        url = getSongUrl(songInfo.getInt("rid").toString()),
                        lrc = getLrcById(songInfo.getInt("rid").toString()),
                        pic = songInfo.getString("pic", ""),
                        albumName = songInfo.getString("album", "")
                    )


                }
                return MusicResp.success(data = songs)

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
                    val songResp = get(
                        url = "http://player.kuwo.cn/webmusic/st/getNewMuiseByRid?rid=MUSIC_$songId",
                        headers = mapOf(
                            "Referer" to "http://player.kuwo.cn/webmusic/play",
                            "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                        )
                    )
                    if (songResp.statusCode != 200) {
                        Song(
                            site = "kuwo",
                            code = songResp.statusCode,
                            msg = "网络异常",
                            songid = songId
                        )
                    } else {
                        //先把xml转成json，&是关键字，解析的时候会报错，先转义
                        val songInfo = XML.toJSONObject(
                            songResp.text
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
            val songResp = get(
                url = url,
                timeout = 5.0,
                headers = mapOf(
                    "Referer" to "http://m.kuwo.cn/yinyue/$songId",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
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

    private fun JSONObject.getString(key: String, defValue: String = "") = if (this.isNull(key)) {
        defValue
    } else {
        this.getString(key)
    }


    /**
     * 根据ID获取播放地址
     * br=128kmp3 这个参数控制MP3的码率，这里可以提到256320
     *
     */
    fun getSongUrl(songId: String): String {
        //http://kuwo.cn/url?format=mp3&rid=157191563&response=url&type=convert_url3&br=128kmp3&from=web&t=1607584876054&httpsStatus=1&reqId=4a56fb61-3ab8-11eb-ac26-d7ac0c330124
        return try {
            val songResp = get(
                url = "http://kuwo.cn/url?format=mp3&rid=$songId&response=url&type=convert_url3&br=128kmp3&from=web&t=${Date().time}&httpsStatus=1&reqId=4a56fb61-3ab8-11eb-ac26-d7ac0c330124",
                timeout = 5.0,
                headers = mapOf(
                    "Referer" to "http://kuwo.cn",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
                )
            )
            if (songResp.statusCode == 200) {
                val songInfo = songResp.jsonObject
                songInfo.getString("url")
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