package xyz.yhsj.kmusic


import xyz.yhsj.kmusic.utils.md5
import java.lang.StringBuilder
import java.util.*


object BaiduMusic {
    /**
     * 欢迎页
     */
    suspend inline fun <reified T> openScreen(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params = params)
        return baiduMusic.get(urlString = "/ad/openscreen?$params&sign=$sign")
    }

    /**
     * 首页
     */
    suspend inline fun <reified T> index(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "/index?$params&sign=$sign")
    }

    /**
     * 专辑信息
     * @param albumAssetCode 专辑Id
     */
    suspend inline fun <reified T> albumInfo(albumAssetCode: String): T {
        val params = "albumAssetCode=$albumAssetCode&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "album/info?$params&sign=$sign")
    }

    /**
     * 专辑列表，最新专辑
     * @param page 从1开始
     * @param size 默认20
     */
    suspend inline fun <reified T> albumList(page: Int = 1, size: Int = 20): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "album/list?$params&sign=$sign")
    }

    /**
     * 新歌推荐
     * @param page 从1开始
     * @param size 默认20
     */
    suspend inline fun <reified T> songList(page: Int = 1, size: Int = 20): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "song/list?$params&sign=$sign")
    }


    /**
     * 歌曲信息
     * @param tsId 歌曲Id
     */
    suspend inline fun <reified T> songInfo(tsId: String): T {
        val params = "TSID=$tsId&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "song/tracklink?$params&sign=$sign")
    }


    /**
     * 歌手列表
     * @param page 从1开始
     * @param size 默认20
     * @param artistGender 性别(男、女、组合、乐队),值是写死的;
     * 这个不传不分页;
     * 这个参数必须在地区之前，不然获取不到值
     * @param artistRegion 地区(内地、港台、欧美、韩国、日本、其他),值是写死的,这个不传不分页
     */
    suspend inline fun <reified T> artistList(
        artistGender: String? = null,
        artistRegion: String? = null,
        page: Int = 1,
        size: Int = 20
    ): T {
        val params = StringBuilder()
        if (!artistGender.isNullOrEmpty()) {
            params.append("artistGender=$artistGender&")
        }
        if (!artistRegion.isNullOrEmpty()) {
            params.append("artistRegion=$artistRegion&")
        }
        if (params.isNotEmpty()) {
            params.append("pageNo=$page&pageSize=$size&")
        }
        params.append("timestamp=${Date().time}")
        val sign = sign(params.toString())
        return baiduMusic.get(urlString = "artist/list?$params&sign=$sign")
    }


    /**
     * 歌手详情
     * @param artistCode 歌手Id
     */
    suspend inline fun <reified T> artistInfo(artistCode: String): T {
        val params = "artistCode=$artistCode&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "artist/info?$params&sign=$sign")
    }

    /**
     * 歌手热门歌曲
     * @param artistCode 歌手Id
     */
    suspend inline fun <reified T> artistSong(artistCode: String, page: Int = 1, size: Int = 20): T {
        val params = "artistCode=$artistCode&pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "artist/song?$params&sign=$sign")
    }

    /**
     * 歌手热门专辑
     * @param artistCode 歌手Id
     */
    suspend inline fun <reified T> artistAlbum(artistCode: String, page: Int = 1, size: Int = 20): T {
        val params = "artistCode=$artistCode&pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "artist/album?$params&sign=$sign")
    }

    /**
     * 搜索
     * @param type 0全部、1单曲、3专辑、2歌手
     * 0没有分页，其他有 pageNo、pageSize
     * @param word 搜索关键词
     */
    suspend inline fun <reified T> search(type: Int, word: String, page: Int = 1, size: Int = 20): T {
        val params = if (type != 0) {
            "timestamp=${Date().time}&type=$type&word=$word"
        } else {
            "pageNo=$page&pageSize=$size&timestamp=${Date().time}&type=$type&word=$word"
        }
        val sign = sign(params)
        return baiduMusic.get(urlString = "search?$params&sign=$sign")
    }


    /**
     * 搜索热词
     * @param word 搜索关键词
     */
    suspend inline fun <reified T> searchSug(word: String): T {
        val params = "timestamp=${Date().time}&word=$word"
        val sign = sign(params)
        return baiduMusic.get(urlString = "search/sug?$params&sign=$sign")
    }


    /**
     * 榜单分类
     */
    suspend inline fun <reified T> bdCategory(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "bd/category?$params&sign=$sign")
    }

    /**
     * 榜单列表
     */
    suspend inline fun <reified T> bdList(bdid: String, page: Int = 1, size: Int = 20): T {
        val params = "bdid=257851&pageNo=1&pageSize=20&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(urlString = "bd/list?$params&sign=$sign")
    }


    /**
     * 签名算法
     */
    fun sign(params: String): String {
        val secret = "0b50b02fd0d73a9c4c8c3a781c30845f"
        return "$params$secret".md5()
    }

}


suspend fun main() {
    val resp = BaiduMusic.bdList<String>(bdid = "257851")

    println(resp)

}