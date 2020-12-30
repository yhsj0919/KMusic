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

}

suspend fun main() {
    val resp = KuwoMusic.bangList<String>()
    println(resp)
}