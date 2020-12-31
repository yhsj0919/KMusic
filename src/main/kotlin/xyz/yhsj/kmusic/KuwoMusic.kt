package xyz.yhsj.kmusic

import io.ktor.client.request.*

object KuwoMusic {

    /**
     * 首页
     */
    suspend inline fun <reified T> index(): T {
        return client.get(
            url = "http://www.kuwo.cn/pc/index/info"
        ) {
            parameter("apiVer", "1")
        }
    }


    /**
     * 搜索
     * @param type 0全部、1单曲、2歌手,3专辑、4歌单、5MV，6lrc，7电台
     * 0没有分页，其他有 pageNo、pageSize
     * @param word 搜索关键词
     */
    suspend inline fun <reified T> search(type: Int, word: String, page: Int = 1, size: Int = 10): T {

        val ft = when (type) {
            0 -> "music"
            1 -> "music"
            2 -> "artist"
            3 -> "album"
            4 -> "playlist"
            5 -> "music"
            6 -> "music"
            7 -> "recordlist"
            else -> ""
        }

        return client.get(url = "https://search.kuwo.cn/r.s?") {
            if (type == 6) {
                parameter("lrccontent", word)
                parameter("client", "kt")
            } else {
                parameter("all", word)
            }
            if (type == 5) {
                parameter("hasmkv", "1")
            }
            parameter("pn", "${page - 1}")
            parameter("rn", "$size")
            parameter("ft", ft)
            parameter("rformat", "json")
            parameter("encoding", "utf8")
            parameter("pcjson", "1")
            parameter("vipver", "MUSIC_9.1.1.2_BCS2")
        }
    }

    /**
     * 歌单详情
     */
    suspend inline fun <reified T> playListInfo(pid: String, page: Int = 1, size: Int = 10): T {
        return client.get(
            url = "http://nplserver.kuwo.cn/pl.svc"
        ) {
            parameter("op", "getlistinfo")
            parameter("pid", pid)
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("encode", "utf8")
            parameter("keyset", "pl2012")
            parameter("vipver", "MUSIC_9.1.1.2_BCS2")
            parameter("newver", "1")
        }
    }

    /**
     * 歌手信息
     */
    suspend inline fun <reified T> artistinfo(artistId: String, page: Int = 1, size: Int = 10): T {
        return client.get(
            url = "https://search.kuwo.cn/r.s"
        ) {
            parameter("stype", "artistinfo")
            parameter("encoding", "encoding")
            parameter("artistid", artistId)
            parameter("pcjson", "1")
        }
    }

    /**
     * 歌手单曲（按时间排序）
     */
    suspend inline fun <reified T> artistMusic(artistId: String, page: Int = 1, size: Int = 10): T {
        return client.get(
            url = "https://search.kuwo.cn/r.s"
        ) {
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("stype", "artist2music")
            parameter("encoding", "utf8")
            parameter("artistid", artistId)
            parameter("pcjson", "1")
            parameter("sortby", "1")
            parameter("vipver", "MUSIC_9.1.1.2_BCS2")
        }
    }

    /**
     * 歌手MV
     */
    suspend inline fun <reified T> artistMV(artistId: String, page: Int = 1, size: Int = 10): T {
        return client.get(
            url = "https://search.kuwo.cn/r.s"
        ) {
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("stype", "mvlist")
            parameter("encoding", "encoding")
            parameter("artistid", artistId)
            parameter("pcjson", "1")
            parameter("sortby", "1")
            parameter("vipver", "MUSIC_9.1.1.2_BCS2")
        }
    }

    /**
     * 歌手分类
     * 用于歌手的分类查询
     */
    suspend inline fun <reified T> artistType(): T {
        return client.get(
            url = "http://mobi.kuwo.cn/mobiweb.s"
        ) {
            parameter("type", "artist_cat_list")
        }
    }

    /**
     * 歌手列表
     * @see artistType
     * @param region 地区
     * @param gender 性别
     * @param genre 类型
     * @param prefix 首字母
     * @param order 排序 hot热度 soar飙升
     */
    suspend inline fun <reified T> artistList(
        region: Int = 0,
        gender: Int = 0,
        genre: Int = 0,
        prefix: String = "",
        order: String = "hot",
        page: Int = 1,
        size: Int = 10
    ): T {
        return client.get(
            url = "http://mobi.kuwo.cn/mobiweb.s"
        ) {
            parameter("type", "pc_artist_list")
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("region", region)
            parameter("gender", gender)
            parameter("genre", genre)
            parameter("prefix", prefix)
            parameter("order", order)
        }
    }

    /**
     * 排行榜列表
     */
    suspend inline fun <reified T> bangList(): T {
        return client.get(url = "http://wapi.kuwo.cn/api/pc/bang/list")
    }


    /**
     * 榜单详情列表
     * @param id id
     */
    suspend inline fun <reified T> bangInfo(sourceId: String, page: Int = 1, size: Int = 10): T {
        return client.get(
            url = "http://kbangserver.kuwo.cn/ksong.s"
        ) {
            parameter("from", "pc")
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("fmt", "json")
            parameter("type", "bang")
            parameter("id", sourceId)
            parameter("isbang", "1")
        }
    }

    /**
     * 首页推荐标签
     * 热门歌单标签
     */
    suspend inline fun <reified T> playListRcmTagList(): T {
        return client.get(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmTagList")
    }

    /**
     * 热门歌单标签
     */
    suspend inline fun <reified T> playListTagList(): T {
        return client.get(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagList")
    }

    /**
     * 热门歌单
     */
    suspend inline fun <reified T> playListRcm(order: String = "new", page: Int = 1, size: Int = 10): T {
        return client.get(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList") {
            parameter("pn", page)
            parameter("rn", size)
            parameter("order", order)
        }
    }

    /**
     * 歌单,根据tagId获取
     *
     * digest==1000使用这个接口
     *
     */
    suspend inline fun <reified T> playListByTag1000(id: String, page: Int = 1, size: Int = 10): T {
        return client.get(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList") {
            parameter("pn", page)
            parameter("rn", size)
            parameter("id", id)
        }
    }

    /**
     * 专区,根据tagId获取
     *
     * digest==43使用这个接口
     * digest=4 调用歌手详情
     * digest=8 专辑详情
     * digest=9 调用电台
     *
     */
    suspend inline fun <reified T> playListZQ43(id: String): T {
        return client.get(url = "http://mobileinterfaces.kuwo.cn/er.s") {
            parameter("type", "get_pc_qz_data")
            parameter("prod", "pc")
            parameter("id", id)
        }
    }

    /**
     * 专区详情
     *
     * digest==13使用这个接口
     *
     */
    suspend inline fun <reified T> playListZQInfo13(albumid: String, page: Int = 1, size: Int = 10): T {
        return client.get(url = "http://mobi.kuwo.cn/mobi.s") {
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("albumid", albumid)
            parameter("type", "pc_music_by_album")
            parameter("encoding", "utf8")
            parameter("vipver", "MUSIC_9.1.1.2_BCS2")
            parameter("newver", "1")
            parameter("f", "web")
            parameter("epaor", "1")
        }
    }

    /**
     * 专区详情
     *
     * digest==5使用这个接口
     *
     */
    suspend inline fun <reified T> playListZQInfo5(id: String, page: Int = 1, size: Int = 10): T {
        return client.get(url = "http://qukudata.kuwo.cn/q.k") {
            parameter("pn", page - 1)
            parameter("rn", size)
            parameter("node", id)
            parameter("op", "query")
            parameter("cont", "ninfo")
            parameter("fmt", "json")
            parameter("src", "mbox")
            parameter("level", "2")
        }
    }

    /**
     *热门MV
     *
     */
    suspend inline fun <reified T> mvRcm(): T {
        return client.get(url = "http://wapi.kuwo.cn/openapi/v1/pc/mv/index/rcm")
    }

    /**
     * MV分类列表
     *
     */
    suspend inline fun <reified T> mvList(catId: String = "", page: Int = 1, size: Int = 10): T {
        return client.get(url = "http://wapi.kuwo.cn/openapi/v1/pc/mv/index/cat") {
            parameter("pn", page)
            parameter("rn", size)
            parameter("catId", catId)

        }
    }

    /**
     * MV分类列表
     *
     */
    suspend inline fun <reified T> mvInfo(id: String): T {
        return client.get(url = "http://m.kuwo.cn/newh5app/api/mobile/v1/video/info/$id") {
            parameter("source", "7")
        }
    }

    suspend inline fun <reified T> mvInfo2(id: String): T {
        return client.get(url = "http://kuwo.cn/url") {
            parameter("rid", id)
            parameter("format", "mp4|mkv")
            parameter("type", "convert_url")
        }
    }

    /**
     * 歌曲详情
     */
    suspend inline fun <reified T> songInfo(id: String): T {
        return client.get(url = "http://m.kuwo.cn/newh5/singles/songinfoandlrc") {
            parameter("musicId", id)
            parameter("httpsStatus", "1")
        }
    }

    /**
     * 歌曲播放地址
     */
    suspend inline fun <reified T> songUrl(id: String,br:String="320kmp3"): T {
        return client.get(url = "https://kuwo.cn/url") {
            parameter("rid", id)
            parameter("format", "mp3")
            parameter("response", "url")
            parameter("type", "convert_url3")
            parameter("br", br)
            parameter("from", "web")
        }
    }


}

suspend fun main() {
    val resp = KuwoMusic.songInfo<String>("154087086")
    println(resp)
}