package xyz.yhsj.kmusic.impl

import xyz.yhsj.json.JSONObject
import xyz.yhsj.khttp.get
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.utils.future

/**
 * 酷狗解析
 */
object KugouImpl : Impl {
    /**
     * 根据类型,获取歌曲排行榜
     */
    override fun getSongTop(topType: String, page: Int, num: Int): String {
        //http://mobilecdn.kugou.com/api/v3/rank/list?plat=0&withsong=1
        //http://m.kugou.com/rank/info/6666&json=true
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun search(key: String, page: Int, num: Int): MusicResp<List<Song>> {
        return try {
            val resp = get(url = "http://mobilecdn.kugou.com/api/v3/search/song?keyword=$key&format=json&page=$page&pagesize=$num"
                    , headers = mapOf("Referer" to "http://m.kugou.com/v2/static/html/search.html",
                    "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"
            ))
            if (resp.statusCode != 200) {
                MusicResp.failure(code = resp.statusCode, msg = "请求失败")
            } else {
                val radioData = resp.jsonObject

                val songList = radioData
                        .getJSONObject("data")
                        .getJSONArray("info")


                //酷狗不支持在详情获取专辑名称
                val albumNames = HashMap<String, String>()
                //获取歌曲ID
                val songIds = songList.map {
                    val hash = (it as JSONObject).getString("320hash")
                    val albumName = it.getString("album_name")
                    val tmpHash = if (hash.isNullOrEmpty()) {
                        it.getString("hash")
                    } else {
                        hash
                    }
                    albumNames[tmpHash.toUpperCase()] = albumName
                    tmpHash
                }
                //添加专辑名称
                val musicData = getSongById(songIds)
                        .apply {
                            data?.map {
                                it.albumName = albumNames[it.songid?.toUpperCase()] ?: ""
                                it
                            }
                        }
                musicData

            }
        } catch (e: Exception) {
            e.printStackTrace()
            MusicResp.failure(msg = e.message)
        }
    }

    override fun getSongById(songIds: List<String>): MusicResp<List<Song>> {
        //使用携程并发加载
        val songs =
                songIds.future { songId ->
                    try {
                        val songResp = get(url = "http://m.kugou.com/app/i/getSongInfo.php?cmd=playInfo&hash=$songId",
                                headers = mapOf("Referer" to "http://m.kugou.com/play/info/$songId",
                                        "User-Agent" to "Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1"))
                        if (songResp.statusCode != 200) {
                            Song(
                                    type = "kugou",
                                    code = songResp.statusCode,
                                    msg = "网络异常",
                                    songid = songId)
                        } else {
                            val songInfo = songResp.jsonObject
//                    val errcode = songInfo.getInt("errcode")
//                    val error = songInfo.getString("error")
                            val url = songInfo.getString("url")
                            if (url.isNullOrEmpty()) {
                                val privilege = songInfo.getInt("privilege")
                                Song(
                                        type = "kugou",
                                        code = 403,
                                        msg = if (privilege == 1) "源站反馈此音频需要付费" else "找不到可用的播放地址",
                                        songid = songId)
                            } else {
                                val radioSongId = songInfo.getString("hash")
                                val albumImg = songInfo.getString("album_img").replace("{size}", "150")
                                val imgUrl = songInfo.getString("imgUrl").replace("{size}", "150")

                                Song(
                                        type = "kugou",
                                        link = "http://www.kugou.com/song/#hash=$radioSongId",
                                        songid = radioSongId,
                                        title = songInfo.getString("songName"),
                                        author = songInfo.getString("singerName"),
                                        url = url,
                                        lrc = getLrcById(radioSongId),
                                        pic = if (albumImg.isEmpty()) imgUrl else albumImg
//                                albumName = song.getString("albumName")
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Song(
                                type = "kugou",
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
            val url = "http://m.kugou.com/app/i/krc.php?cmd=100&timelength=999999&hash=$songId"
            val songResp = get(url = url,
                    timeout = 5.0,
                    headers = mapOf("Referer" to "http://m.kugou.com/play/info/$songId",
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