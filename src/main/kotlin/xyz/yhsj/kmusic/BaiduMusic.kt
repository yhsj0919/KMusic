package xyz.yhsj.kmusic


import khttp.get
import khttp.post
import org.json.JSONObject
import xyz.yhsj.kmusic.utils.HttpMethod
import xyz.yhsj.kmusic.utils.md5
import xyz.yhsj.kmusic.utils.obj
import xyz.yhsj.kmusic.utils.params
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


object BaiduMusic {
    const val baseUrl = "https://api-qianqian.taihe.com/v1"
    val header = hashMapOf(
        "app-version" to "v8.2.3.1",
        "from" to "android",
        "user-agent" to "Mozilla/5.0 (Linux; U; Android 8.0.0; zh-cn; MI 5 Build/OPR1.170623.032) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
    )

    inline fun <reified T> baidu(
        url: String,
        method: HttpMethod = HttpMethod.GET,
        params: LinkedHashMap<String, String> = LinkedHashMap(),
        authorization: String? = null
    ): T {
        authorization?.let {
            header["authorization"] = "/access_token $authorization"
        }
        if (!params.containsKey("timestamp")) {
            params["timestamp"] = "${Date().time}"
        }
        val sign = sign(params.params())
        params["sign"] = sign

        return if (method == HttpMethod.POST) {
            post(url = "$baseUrl/$url", params = params, headers = header).obj()
        } else {
            get(url = "$baseUrl/$url", params = params, headers = header).obj()
        }
    }


    /**
     * 欢迎页
     */
    inline fun <reified T> openScreen(): T {
        return baidu(url = "/ad/openscreen")
    }

    /**
     * 首页
     */
    inline fun <reified T> index(): T {
        return baidu(url = "/index")
    }

    /**
     * 专辑信息
     * @param albumAssetCode 专辑Id
     */
    inline fun <reified T> albumInfo(albumAssetCode: String): T {
        val params = linkedMapOf("albumAssetCode" to albumAssetCode)
        return baidu(url = "/album/info", params = params)
    }

    /**
     * 专辑列表，最新专辑
     * @param page 从1开始
     * @param size 默认20
     */
    inline fun <reified T> albumList(page: Int = 1, size: Int = 20): T {
        val params = linkedMapOf("pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/album/list", params = params)
    }

    /**
     * 新歌推荐
     * @param page 从1开始
     * @param size 默认20
     */
    inline fun <reified T> songList(page: Int = 1, size: Int = 20): T {
        val params = linkedMapOf("pageNo" to "$page", "pageSize" to "$size")

        return baidu(url = "/song/list", params = params)
    }


    /**
     * 歌曲信息(播放地址。Vip歌曲只能获取30秒)
     * @param tsId 歌曲Id
     */
    inline fun <reified T> songInfo(tsId: String, authorization: String = ""): T {
        val params = linkedMapOf("TSID" to tsId, "rate" to "320")
        return baidu(url = "/song/tracklink", params = params, authorization = authorization)
    }

    /**
     * 歌曲下载
     * @param tsId 歌曲Id
     */
    inline fun <reified T> songDownload(tsId: String, authorization: String = ""): T {
        val params = linkedMapOf("TSID" to tsId, "rate" to "320")
        return baidu(url = "/song/download", params = params, authorization = authorization)
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
    inline fun <reified T> artistList(
        artistGender: String? = null,
        artistRegion: String? = null,
        page: Int = 1,
        size: Int = 20
    ): T {
        val params = LinkedHashMap<String, String>()
        if (!artistGender.isNullOrEmpty()) {
            params["artistGender"] = artistGender
        }
        if (!artistRegion.isNullOrEmpty()) {
            params["artistRegion"] = artistRegion
        }
        if (params.isNotEmpty()) {
            params["pageNo"] = "$page"
            params["pageSize"] = "$size"
        }

        return baidu(url = "/artist/list", params = params)
    }


    /**
     * 歌手详情
     * @param artistCode 歌手Id
     */
    inline fun <reified T> artistInfo(artistCode: String): T {
        val params = linkedMapOf("artistCode" to artistCode)
        return baidu(url = "/artist/info", params = params)
    }

    /**
     * 歌手热门歌曲
     * @param artistCode 歌手Id
     */
    inline fun <reified T> artistSong(artistCode: String, page: Int = 1, size: Int = 20): T {
        val params = linkedMapOf("artistCode" to artistCode, "pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/artist/song", params = params)
    }

    /**
     * 歌手热门专辑
     * @param artistCode 歌手Id
     */
    inline fun <reified T> artistAlbum(artistCode: String, page: Int = 1, size: Int = 20): T {
        val params = linkedMapOf("artistCode" to artistCode, "pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/artist/album", params = params)
    }

    /**
     * 搜索
     * @param type 0全部、1单曲、3专辑、2歌手
     * 0没有分页，其他有 pageNo、pageSize
     * @param word 搜索关键词
     */
    inline fun <reified T> search(type: Int, word: String, page: Int = 1, size: Int = 20): T {
        val params = if (type != 0) {
            linkedMapOf(
                "timestamp" to "${Date().time}",
                "type" to "$type",
                "word" to word,
            )
        } else {
            linkedMapOf(
                "pageNo" to "$page",
                "pageSize" to "$size",
                "timestamp" to "${Date().time}",
                "type" to "$type",
                "word" to word,
            )
        }
        return baidu(url = "/search", params = params)
    }


    /**
     * 搜索热词
     * @param word 搜索关键词
     */
    inline fun <reified T> searchSug(word: String): T {
        val params = linkedMapOf("timestamp" to "${Date().time}", "word" to word)
        return baidu(url = "/search/sug", params = params)
    }


    /**
     * 榜单分类
     */
    inline fun <reified T> bdCategory(): T {
        return baidu(url = "/bd/category")
    }

    /**
     * 榜单列表
     */
    inline fun <reified T> bdList(bdid: String, page: Int = 1, size: Int = 20): T {
        val params = linkedMapOf("bdid" to bdid, "pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/bd/list", params = params)
    }


    /**
     * 歌单分类
     */
    inline fun <reified T> trackListCategory(): T {
        return baidu(url = "/tracklist/category")
    }

    /**
     * 歌单分类详情
     */
    inline fun <reified T> trackListList(subCateId: String? = null, page: Int = 1, size: Int = 20): T {
        val params = if (subCateId == null) {
            linkedMapOf("pageNo" to "$page", "pageSize" to "$size")
        } else {
            linkedMapOf("pageNo" to "$page", "pageSize" to "$size", "subCateId" to subCateId)
        }
        return baidu(url = "/tracklist/list", params = params)
    }


    /**
     * 歌单详情、推荐歌单
     */
    inline fun <reified T> trackListInfo(id: String, page: Int = 1, size: Int = 20): T {
        val params = linkedMapOf("id" to id, "pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/tracklist/info", params = params)
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     */
    inline fun <reified T> sendSms(phone: String): T {
        val params = linkedMapOf(
            "phone" to phone,
            "randstr" to "@arV",
        )
        return baidu(url = "/oauth/send_sms", params = params, method = HttpMethod.POST)
    }

    /**
     * 登录
     * @param phone 手机号
     * @param code 验证码
     * NjVhNTMzM2QyZWEyZTlhOTI5OTJiMjZiNWE2YTkwMjY=
     */
    inline fun <reified T> login(phone: String, code: String): T {
        val params = linkedMapOf(
            "code" to code,
            "phone" to phone,
        )
        return baidu(url = "/oauth/login", params = params, method = HttpMethod.POST)
    }


    /**
     * 登出
     */
    inline fun <reified T> logout(authorization: String): T {
        return baidu(url = "/account/logout", method = HttpMethod.POST, authorization = authorization)
    }

    /**
     * 账户信息
     */
    inline fun <reified T> accountInfo(authorization: String): T {
        return baidu(url = "/account/info", authorization = authorization)
    }


    /**
     * 修改账户信息
     * @param age 年龄
     * @param avatar 头像（可为空，是一个完整的图片路径，上传方式请查看说明文档）
     * @param birth 生日
     * @param nickname 昵称
     * @param sex 性别（0，女，1，男）
     */
    inline fun <reified T> changeAccountInfo(
        age: String,
        avatar: String? = null,
        birth: String,
        nickname: String,
        sex: String,
        authorization: String
    ): T {
        val params = LinkedHashMap<String, String>()
        params["age"] = age
        if (!avatar.isNullOrEmpty()) {
            params["avatar"] = avatar
        }
        params["birth"] = birth
        params["nickname"] = nickname
        params["sex"] = sex
        return baidu(url = "/account/info", method = HttpMethod.POST, params = params, authorization = authorization)
    }


    /**
     * 账户歌曲列表(喜欢的歌曲？)
     */
    inline fun <reified T> accountSongList(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = linkedMapOf("pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/account/songlist", params = params, authorization = authorization)
    }

    /**
     * 账户收藏等信息
     */
    inline fun <reified T> accountAmount(authorization: String): T {
        return baidu(url = "/account/amount", authorization = authorization)
    }


    /**
     * 签到
     */
    inline fun <reified T> userSignin(authorization: String): T {
        return baidu(url = "/user/points/signin", method = HttpMethod.POST, authorization = authorization)
    }


    /**
     * 收藏的歌曲
     */
    inline fun <reified T> favoriteSong(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = linkedMapOf("pageNo" to "$page", "pageSize" to "$size")
        return baidu(url = "/favorite/song", params = params, authorization = authorization)
    }

    /**
     * 收藏歌曲
     */
    inline fun <reified T> favoriteSongCreate(code: String, authorization: String): T {
        val params = linkedMapOf("code" to code)
        return baidu(
            url = "/favorite/song/create",
            params = params,
            method = HttpMethod.POST,
            authorization = authorization
        )
    }

    /**
     * 删除收藏歌曲
     */
    inline fun <reified T> favoriteSongDelete(code: String, authorization: String): T {
        val params = linkedMapOf("code" to code)
        return baidu(
            url = "/favorite/song/delete",
            params = params,
            method = HttpMethod.POST,
            authorization = authorization
        )
    }

    /**
     * 收藏的歌单
     */
    inline fun <reified T> favoriteTrackList(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = linkedMapOf("pageNo" to "$page", "pageSize" to "$size")

        return baidu(url = "/favorite/tracklist", params = params, authorization = authorization)
    }

    /**
     * 收藏歌单
     */
    inline fun <reified T> favoriteTrackListCreate(code: String, authorization: String): T {
        val params = linkedMapOf("code" to code)
        return baidu(
            url = "/favorite/tracklist/create",
            params = params,
            method = HttpMethod.POST,
            authorization = authorization
        )
    }

    /**
     * 删除收藏歌单
     */
    inline fun <reified T> favoriteTrackListDelete(code: String, authorization: String): T {
        val params = linkedMapOf("code" to code)
        return baidu(
            url = "/favorite/tracklist/delete",
            params = params,
            method = HttpMethod.POST,
            authorization = authorization
        )
    }

    /**
     * 收藏的歌手
     */
    inline fun <reified T> favoriteArtist(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = linkedMapOf("pageNo" to "$page", "pageSize" to "$size")

        return baidu(url = "/favorite/artist", params = params, authorization = authorization)
    }

    /**
     * 收藏歌手
     */
    inline fun <reified T> favoriteArtistCreate(code: String, authorization: String): T {
        val params = linkedMapOf("code" to code)
        return baidu(
            url = "/favorite/artist/create",
            params = params,
            method = HttpMethod.POST,
            authorization = authorization
        )
    }

    /**
     * 删除收藏歌手
     */
    inline fun <reified T> favoriteArtistDelete(code: String, authorization: String): T {
        val params = linkedMapOf("code" to code)
        return baidu(
            url = "/favorite/artist/delete",
            params = params,
            method = HttpMethod.POST,
            authorization = authorization
        )
    }

    /**
     * 签名算法
     */
    fun sign(params: String): String {
        val secret = "0b50b02fd0d73a9c4c8c3a781c30845f"
        return "$params$secret".md5()
    }
}


fun main() {
    val resp = BaiduMusic.accountInfo<JSONObject>(
//        tsId = "T10053430210",
        authorization = "NjVhNTMzM2QyZWEyZTlhOTI5OTJiMjZiNWE2YTkwMjY="
    )

    println(resp)

}