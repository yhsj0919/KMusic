package xyz.yhsj.kmusic

import khttp.get
import org.json.JSONArray
import org.json.JSONObject
import xyz.yhsj.kmusic.utils.obj


object KuwoMusic {

    val header = hashMapOf(
        "user-agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4398.1 Safari/537.36"
    )

    inline fun <reified T> kuwo(
        url: String,
        params: LinkedHashMap<String, String> = LinkedHashMap()
    ): T {
        return get(url = url, params = params, headers = header).obj()
    }

    /**
     * 首页
     */
    inline fun <reified T> index(): T {
        val params = linkedMapOf("apiVer" to "1")
        return kuwo(url = "http://www.kuwo.cn/pc/index/info", params = params)
    }


    /**
     * 搜索
     * @param type 0全部、1单曲、2歌手,3专辑、4歌单、5MV，6lrc，7电台
     * 0没有分页，其他有 pageNo、pageSize
     * @param word 搜索关键词
     */
    inline fun <reified T> search(type: Int, word: String, page: Int = 1, size: Int = 10): T {
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

        val params = LinkedHashMap<String, String>()
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        if (type == 6) {
            params["lrccontent"] = word
            params["client"] = "kt"
        } else {
            params["all"] = word
        }
        if (type == 5) {
            params["hasmkv"] = "1"
        }

        params["ft"] = ft
        params["rformat"] = "json"
        params["encoding"] = "utf8"
        params["pcjson"] = "1"
        params["vipver"] = "MUSIC_9.1.1.2_BCS2"

        return kuwo(url = "https://search.kuwo.cn/r.s?", params = params)
    }

    /**
     * 歌单详情
     */
    inline fun <reified T> playListInfo(pid: String, page: Int = 1, size: Int = 10): T {

        val params = LinkedHashMap<String, String>()
        params["op"] = "getlistinfo"
        params["pid"] = pid
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["encode"] = "utf8"
        params["keyset"] = "pl2012"
        params["vipver"] = "MUSIC_9.1.1.2_BCS2"
        params["newver"] = "1"

        return kuwo(url = "http://nplserver.kuwo.cn/pl.svc", params = params)
    }

    /**
     * 歌手信息
     */
    inline fun <reified T> artistinfo(artistId: String): T {
        val params = LinkedHashMap<String, String>()

        params["stype"] = "artistinfo"
        params["encoding"] = "encoding"
        params["artistid"] = artistId
        params["pcjson"] = "1"

        return kuwo(url = "https://search.kuwo.cn/r.s", params = params)
    }

    /**
     * 歌手单曲（按时间排序）
     */
    inline fun <reified T> artistMusic(artistId: String, page: Int = 1, size: Int = 10): T {
        val params = LinkedHashMap<String, String>()
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["stype"] = "artist2music"
        params["encoding"] = "utf8"
        params["artistid"] = artistId
        params["pcjson"] = "1"
        params["sortby"] = "1"
        params["vipver"] = "MUSIC_9.1.1.2_BCS2"
        return kuwo(url = "https://search.kuwo.cn/r.s", params = params)
    }

    /**
     * 歌手MV
     */
    inline fun <reified T> artistMV(artistId: String, page: Int = 1, size: Int = 10): T {

        val params = LinkedHashMap<String, String>()
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["stype"] = "mvlist"
        params["encoding"] = "encoding"
        params["artistid"] = artistId
        params["pcjson"] = "1"
        params["sortby"] = "1"
        params["vipver"] = "MUSIC_9.1.1.2_BCS2"
        return kuwo(url = "https://search.kuwo.cn/r.s", params = params)
    }

    /**
     * 歌手分类
     * 用于歌手的分类查询
     */
    inline fun <reified T> artistType(): T {
        val params = linkedMapOf("type" to "artist_cat_list")
        return kuwo(url = "http://mobi.kuwo.cn/mobiweb.s", params = params)
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
    inline fun <reified T> artistList(
        region: Int = 0,
        gender: Int = 0,
        genre: Int = 0,
        prefix: String = "",
        order: String = "hot",
        page: Int = 1,
        size: Int = 10
    ): T {
        val params = LinkedHashMap<String, String>()
        params["type"] = "pc_artist_list"
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["region"] = "$region"
        params["gender"] = "$gender"
        params["genre"] = "$genre"
        params["prefix"] = prefix
        params["order"] = order

        return kuwo(url = "http://mobi.kuwo.cn/mobiweb.s", params = params)
    }

    /**
     * 排行榜列表
     */
    inline fun <reified T> bangList(): T {
        return kuwo(url = "http://wapi.kuwo.cn/api/pc/bang/list")
    }


    /**
     * 榜单详情列表
     * @param id id
     */
    inline fun <reified T> bangInfo(sourceId: String, page: Int = 1, size: Int = 10): T {
        val params = LinkedHashMap<String, String>()
        params["from"] = "pc"
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["fmt"] = "json"
        params["type"] = "bang"
        params["id"] = sourceId
        params["isbang"] = "1"
        return kuwo(url = "http://kbangserver.kuwo.cn/ksong.s", params = params)
    }

    /**
     * 首页推荐标签
     * 热门歌单标签
     */
    inline fun <reified T> playListRcmTagList(): T {
        return kuwo(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmTagList")
    }

    /**
     * 热门歌单标签
     */
    inline fun <reified T> playListTagList(): T {
        return kuwo(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagList")
    }

    /**
     * 热门歌单
     */
    inline fun <reified T> playListRcm(order: String = "new", page: Int = 1, size: Int = 10): T {
        val params = LinkedHashMap<String, String>()
        params["pn"] = "$page"
        params["rn"] = "$size"
        params["order"] = order
        return kuwo(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getRcmPlayList", params = params)
    }

    /**
     * 歌单,根据tagId获取
     *
     * digest==10000使用这个接口
     *
     */
    inline fun <reified T> playListByTag10000(id: String, page: Int = 1, size: Int = 10): T {

        val params = LinkedHashMap<String, String>()
        params["pn"] = "$page"
        params["rn"] = "$size"
        params["id"] = id
        return kuwo(url = "http://wapi.kuwo.cn/api/pc/classify/playlist/getTagPlayList", params = params)
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
    inline fun <reified T> playListZQ43(id: String): T {
        val params = LinkedHashMap<String, String>()
        params["type"] = "get_pc_qz_data"
        params["prod"] = "pc"
        params["id"] = id
        return kuwo(url = "http://mobileinterfaces.kuwo.cn/er.s", params = params)
    }

    /**
     * 专区详情
     *
     * digest==13使用这个接口
     *
     */
    inline fun <reified T> playListZQInfo13(albumId: String, page: Int = 1, size: Int = 10): T {
        val params = LinkedHashMap<String, String>()
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["albumid"] = albumId
        params["type"] = "pc_music_by_album"
        params["encoding"] = "utf8"
        params["vipver"] = "MUSIC_9.1.1.2_BCS2"
        params["newver"] = "1"
        params["f"] = "web"
        params["epaor"] = "1"
        return kuwo(url = "http://mobi.kuwo.cn/mobi.s", params = params)
    }

    /**
     * 专区详情
     *
     * digest==5使用这个接口
     *
     */
    inline fun <reified T> playListZQInfo5(id: String, page: Int = 1, size: Int = 10): T {

        val params = LinkedHashMap<String, String>()
        params["pn"] = "${page - 1}"
        params["rn"] = "$size"
        params["node"] = id
        params["op"] = "query"
        params["cont"] = "ninfo"
        params["fmt"] = "json"
        params["src"] = "mbox"
        params["level"] = "2"

        return kuwo(url = "http://qukudata.kuwo.cn/q.k", params = params)
    }

    /**
     *热门MV
     *
     */
    inline fun <reified T> mvRcm(): T {
        return kuwo(url = "http://wapi.kuwo.cn/openapi/v1/pc/mv/index/rcm")
    }

    /**
     * MV分类列表
     *
     */
    inline fun <reified T> mvList(catId: String = "", page: Int = 1, size: Int = 10): T {
        val params = LinkedHashMap<String, String>()
        params["pn"] = "$page"
        params["rn"] = "$size"
        params["catId"] = catId
        return kuwo(url = "http://wapi.kuwo.cn/openapi/v1/pc/mv/index/cat", params = params)
    }

    /**
     * MV详情
     *
     */
    inline fun <reified T> mvInfo(id: String): T {
        val params = LinkedHashMap<String, String>()
        params["source"] = "7"
        return kuwo(url = "http://m.kuwo.cn/newh5app/api/mobile/v1/video/info/$id", params = params)
    }

    inline fun <reified T> mvInfo2(id: String): T {

        val params = LinkedHashMap<String, String>()
        params["rid"] = id
        params["format"] = "mp4|mkv"
        params["type"] = "convert_url"

        return kuwo(url = "http://kuwo.cn/url", params = params)
    }

    /**
     * 歌曲详情
     */
    inline fun <reified T> songInfo(id: String): T {
        val params = LinkedHashMap<String, String>()
        params["musicId"] = id
        params["httpsStatus"] = "1"
        return kuwo(url = "http://m.kuwo.cn/newh5/singles/songinfoandlrc", params = params)
    }

    /**
     * 歌曲播放地址
     */
    inline fun <reified T> songUrl(id: String, br: String = "320kmp3"): T {
        val params = LinkedHashMap<String, String>()
        params["rid"] = id
        params["format"] = "mp3"
        params["response"] = "url"
        params["type"] = "convert_url3"
        params["br"] = br
        params["from"] = "web"

        return kuwo(url = "https://kuwo.cn/url", params = params)
    }


}

fun main() {

    val resp = KuwoMusic.songUrl<String>(
//        artistId = "947"
//    sourceId = "93"
        id = "161812299"
    )


//    val resp = KuwoMusic.songInfo<String>("154087086")
    println(resp)
}