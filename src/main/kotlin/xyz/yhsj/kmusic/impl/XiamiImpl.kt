package xyz.yhsj.kmusic.impl

import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.khttp.structures.cookie.CookieJar
import xyz.yhsj.kmusic.entity.Album
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.DecodeKaiserMatrix
import xyz.yhsj.kmusic.utils.future
import xyz.yhsj.kmusic.utils.md5
import java.util.*

/**
 * 虾米解析
 */
object XiamiImpl : Impl {
    var cookie = CookieJar()
    var cookieStr = ""

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
     * http://api.xiami.com/web?v=2.0&app_key=1&id=101&page=2&limit=20&_ksTS=1529740218689_96&callback=jsonp97&r=rank/song-list
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
                url = "http://api.xiami.com/web?v=2.0&app_key=1&id=$topId&type=0&page=$page&limit=$num&_ksTS=${Date().time}_96&r=rank/song-list",
                headers = mapOf(
                    "Referer" to "http://m.xiami.com",
                    "User-Agent" to "Mozilla/5.0"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject
                val songList = radioData
                    .getJSONArray("data")
                val songIds = songList.map {
                    (it as JSONObject).getLong("song_id").toString()
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
                site = "xiami",
                topId = "101",
                name = "虾米音乐榜",
                pic = "https://gw.alicdn.com/tps/i1/T19LocFghXXXXsGF3s-640-640.png",
                comment = "虾米音乐榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "103",
                name = "虾米原创榜",
                pic = "https://gw.alicdn.com/tps/i1/T1qMgSFlxkXXXsGF3s-640-640.png",
                comment = "虾米原创榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "1",
                name = "Hito中文排行榜",
                pic = "https://img.alicdn.com/tps/TB1RTkfNVXXXXXdXFXXXXXXXXXX-290-290.png",
                comment = "Hito中文排行榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "2",
                name = "香港劲歌金榜",
                pic = "https://img.alicdn.com/tps/TB1GMQvNVXXXXbwXXXXXXXXXXXX-290-290.png",
                comment = "香港劲歌金榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "3",
                name = "英国UK单曲榜",
                pic = "https://img.alicdn.com/tps/TB11FsrNVXXXXabXpXXXXXXXXXX-290-290.png",
                comment = "英国UK单曲榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "4",
                name = "Billboard单曲榜",
                pic = "https://img.alicdn.com/tps/TB1EqgvNVXXXXbPXXXXXXXXXXXX-290-290.png",
                comment = "Billboard单曲榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "5",
                name = "Oricon公信单曲榜",
                pic = "https://img.alicdn.com/tps/TB1EqgvNVXXXXbPXXXXXXXXXXXX-290-290.png",
                comment = "Oricon公信单曲榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "6",
                name = "M-net综合数据周榜",
                pic = "https://img.alicdn.com/tps/TB1K.ErNVXXXXXcXpXXXXXXXXXX-290-290.png",
                comment = "M-net综合数据周榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "106",
                name = "陌陌试听榜",
                pic = "https://img.alicdn.com/tps/TB1nUn_NVXXXXX4XVXXXXXXXXXX-330-330.png",
                comment = "陌陌试听榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "31",
                name = "音乐风云榜",
                pic = "https://img.alicdn.com/tps/TB1nmf7NVXXXXbFXVXXXXXXXXXX-330-330.png",
                comment = "音乐风云榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "10011",
                name = "微信分享榜",
                pic = "https://img.alicdn.com/tps/TB1mrUbNVXXXXaFXVXXXXXXXXXX-330-330.png",
                comment = "微信分享榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "10012",
                name = "微博分享榜",
                pic = "https://img.alicdn.com/tps/TB1h1oaNVXXXXXrXVXXXXXXXXXX-330-330.png",
                comment = "微博分享榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "10013",
                name = "大虾试听榜",
                pic = "https://img.alicdn.com/tps/TB1tK7cNVXXXXXgXVXXXXXXXXXX-330-330.png",
                comment = "大虾试听榜"
            ),
            MusicTop(
                site = "xiami",
                topId = "10014",
                name = "歌单收录榜",
                pic = "https://img.alicdn.com/tps/TB1KQseNVXXXXbfXFXXXXXXXXXX-330-330.png",
                comment = "歌单收录榜"
            )
        )

        return MusicResp.success(data = tops)
    }

    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {

        return try {
//            if (!cookie.containsKey("xm_sg_tk")) {
//                getCookie()
//            }
//            val params = "{\"key\":\"$key\",\"pagingVO\":{\"page\":$page,\"pageSize\":$num}}"
//            val tk = getTK(api = "search/searchSongs", params = params)
            val resp = get(
                url = "https://api.xiami.com/web?&app_key=1&key=$key&limit=$num&page=$page&r=search/songs&v=2.0",
                cookies = cookie,
                headers = mapOf(
                    "Referer" to "https://www.xiami.com",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66"
                )
            )
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                println(resp.text)

                val radioData = resp.jsonObject
                val songList = radioData
                    .getJSONObject("data")
                    .getJSONArray("songs")
                val songs = songList.map {
                    val song = it as JSONObject
                    val songId = song.getLong("song_id").toString()
                    Song(
                        site = "xiami",
                        link = "http://www.xiami.com/song/$songId",
                        songid = songId,
                        title = song.getString("song_name"),
                        author = song.getString("artist_name"),
                        url = song.getString("listen_file"),
//                        lrc = getLrcById(song.getString("lyric")),
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
            val songResp = get(
                url = "http://www.xiami.com/song/playlist/id/$songId/type/0/cat/json",
                headers = mapOf(
                    "Referer" to "http://www.xiami.com",
                    "User-Agent" to "Mozilla/5.0"
                )
            )
            if (songResp.statusCode != 200) {
                MusicResp.failure(code = songResp.statusCode, msg = "请求失败")
            } else {
                val songInfo = songResp.jsonObject

                val songList = songInfo
                    .getJSONObject("data")
                    .getJSONArray("trackList")
                val songs = songList.future {
                    val song = it as JSONObject
                    val radioSongId = song.getString("song_id")
                    Song(
                        site = "xiami",
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
            val songResp = get(
                url = songId,
                timeout = 5.0,
                headers = mapOf(
                    "Referer" to "http://www.xiami.com",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36",
                )
            )
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

    fun parseCookie(cookie: String): CookieJar {
        val valueList = cookie.split("secure,").map(String::trim)
        val attributes =
            valueList
                .flatMap { it.split("httponly,").map(String::trim) }
                .map {
                    val k = it.split("=")[0].trim()
                    val v = it.substring(k.length + 1, it.length)
                    k to v
                }.toMap()
        return CookieJar(attributes)
    }


    fun getCookie() {
        val resp = get(
            url = "https://www.xiami.com", headers = mapOf(
                "Referer" to "http://img.xiami.com/static/swf/seiya/player.swf?v=${Date().time}",
                "User-Agent" to "Mozilla/5.0"
            )
        )
        cookieStr = resp.headers["set-cookie"] ?: ""
        cookie = parseCookie(resp.headers["set-cookie"] ?: "")
    }

    fun getTK(api: String, params: String): String {

        //                    "Cookie" to  " xm_sg_tk=ce1573ccfada2c4d438b29a3d7499655_1604209347226; xm_sg_tk.sig=vdhhrB8euKwEc9FXSx7_0VEhzY8DuyAknOn1NBkrIto;"

        return ("ce1573ccfada2c4d438b29a3d7499655_1604209347226" + "_xmMain_/api/${api}_" + params).md5()
    }


    /**
     * 根据ID获取播放地址
     *
     */

    fun getSongUrl(songId: String): String {
        //https://www.xiami.com/api/song/getPlayInfo?_q={"songIds":[2103056536]}&_s=8b9ae40212aebaa56080e07ecef04030
        return try {
            if (!cookie.containsKey("xm_sg_tk")) {
                getCookie()
            }
            val params = "{\"songIds\":[\"$songId\"]}"
            val tk = getTK(api = "song/getPlayInfo", params = params)
            val songResp = get(
                url = "https://www.xiami.com/api/search/searchSongs?_q=$params&_s=$tk",
                cookies = cookie,
                headers = mapOf(
                    "Referer" to "https://www.xiami.com/song/$songId",
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36"
                )
            )
            if (songResp.statusCode == 200) {
                val songInfo = songResp.jsonObject
                println(songInfo)

                return songInfo.getJSONObject("result")
                    .getJSONObject("data")
                    .getJSONArray("songPlayInfos")
                    .getJSONObject(0)
                    .getJSONArray("playInfos")
                    .getJSONObject(0)
                    .getString("listenFile")
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

// xm_sg_tk.sig=aTe73Wd7eOsZGWlIrxmCOSghN1EM3TfaC-r1hIC0PbE;
// path=/;
// expires=Wed, 27 Feb 2019 02:07:53 GMT;
// domain=.xiami.com;
// xm_sg_tk=bff15d950c322349f2910204f4b6d8b5_1551060473657;
// xmgid=fa460838-fdde-4f46-a30d-3c4446732e49;
