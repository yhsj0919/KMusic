package xyz.yhsj.kmusic.impl

import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.DecodeKaiserMatrix

/**
 * 虾米解析
 */
object XiamiImpl : Impl {
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

        /** [{
        id: 101,
        name: "虾米音乐榜",
        logo: "//gw.alicdn.com/tps/i1/T19LocFghXXXXsGF3s-640-640.png",
        gmt_modify: i
        }, {
        id: 103,
        name: "虾米原创榜",
        logo: "//gw.alicdn.com/tps/i1/T1qMgSFlxkXXXsGF3s-640-640.png",
        gmt_modify: i
        }, {
        id: 1,
        name: "Hito中文排行榜",
        logo: "//img.alicdn.com/tps/TB1RTkfNVXXXXXdXFXXXXXXXXXX-290-290.png",
        gmt_modify: i
        }, {
        id: 2,
        name: "香港劲歌金榜",
        logo: "//img.alicdn.com/tps/TB1GMQvNVXXXXbwXXXXXXXXXXXX-290-290.png",
        gmt_modify: i
        }, {
        id: 3,
        name: "英国UK单曲榜",
        logo: "//img.alicdn.com/tps/TB11FsrNVXXXXabXpXXXXXXXXXX-290-290.png",
        gmt_modify: i
        }, {
        id: 4,
        name: "Billboard单曲榜",
        logo: "//img.alicdn.com/tps/TB1EqgvNVXXXXbPXXXXXXXXXXXX-290-290.png",
        gmt_modify: i
        }, {
        id: 5,
        name: "Oricon公信单曲榜",
        logo: "//img.alicdn.com/tps/TB17O3cNVXXXXa0XFXXXXXXXXXX-290-290.png",
        gmt_modify: i
        }, {
        id: 6,
        name: "M-net综合数据周榜",
        logo: "//img.alicdn.com/tps/TB1K.ErNVXXXXXcXpXXXXXXXXXX-290-290.png",
        gmt_modify: i
        }, {
        id: 106,
        name: "陌陌试听榜",
        logo: "//img.alicdn.com/tps/TB1nUn_NVXXXXX4XVXXXXXXXXXX-330-330.png",
        gmt_modify: i
        }, {
        id: 31,
        name: "音乐风云榜",
        logo: "//img.alicdn.com/tps/TB1nmf7NVXXXXbFXVXXXXXXXXXX-330-330.png",
        gmt_modify: i
        }, {
        id: 10011,
        name: "微信分享榜",
        logo: "//img.alicdn.com/tps/TB1mrUbNVXXXXaFXVXXXXXXXXXX-330-330.png",
        gmt_modify: i
        }, {
        id: 10012,
        name: "微博分享榜",
        logo: "//img.alicdn.com/tps/TB1h1oaNVXXXXXrXVXXXXXXXXXX-330-330.png",
        gmt_modify: i
        }, {
        id: 10013,
        name: "大虾试听榜",
        logo: "//img.alicdn.com/tps/TB1tK7cNVXXXXXgXVXXXXXXXXXX-330-330.png",
        gmt_modify: i
        }, {
        id: 10014,
        name: "歌单收录榜",
        logo: "//img.alicdn.com/tps/TB1KQseNVXXXXbfXFXXXXXXXXXX-330-330.png",
        gmt_modify: i
        }]**/

//http://api.xiami.com/web?v=2.0&app_key=1&id=101&page=2&limit=20&_ksTS=1529740218689_96&callback=jsonp97&r=rank/song-list
//        return try {
//            val resp = get(url = "http://api.xiami.com/web?v=2.0&app_key=1&id=101&page=1&limit=10&r=rank/song-list"
//                    , headers = mapOf("Referer" to "http://m.xiami.com",
//                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
//            ))
//            if (resp.statusCode != 200) {
//                println("请求失败")
//                "请求失败"
//            } else {
//                val radioData = resp.text
//
//
//                radioData
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            println("请求失败")
//            "请求失败"
//        }
        TODO("")

    }

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
                            site = "xiami",
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