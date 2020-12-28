package xyz.yhsj.kmusic


import io.ktor.client.request.*
import io.ktor.client.request.forms.*

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.streams.*
import xyz.yhsj.kmusic.utils.FormParams
import xyz.yhsj.kmusic.utils.md5
import xyz.yhsj.kmusic.utils.params
import xyz.yhsj.kmusic.utils.plus
import java.io.File
import java.io.FileInputStream
import java.util.*


object BaiduMusic {
    /**
     * 欢迎页
     */
    suspend inline fun <reified T> openScreen(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params = params)
        return baiduMusic.get(url = "/ad/openscreen?$params&sign=$sign")
    }

    /**
     * 首页
     */
    suspend inline fun <reified T> index(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/index?$params&sign=$sign")
    }

    /**
     * 专辑信息
     * @param albumAssetCode 专辑Id
     */
    suspend inline fun <reified T> albumInfo(albumAssetCode: String): T {
        val params = "albumAssetCode=$albumAssetCode&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/album/info?$params&sign=$sign")
    }

    /**
     * 专辑列表，最新专辑
     * @param page 从1开始
     * @param size 默认20
     */
    suspend inline fun <reified T> albumList(page: Int = 1, size: Int = 20): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/album/list?$params&sign=$sign")
    }

    /**
     * 新歌推荐
     * @param page 从1开始
     * @param size 默认20
     */
    suspend inline fun <reified T> songList(page: Int = 1, size: Int = 20): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/song/list?$params&sign=$sign")
    }


    /**
     * 歌曲信息(播放地址。Vip歌曲只能获取30秒)
     * @param tsId 歌曲Id
     */
    suspend inline fun <reified T> songInfo(tsId: String, authorization: String = ""): T {
        val params = "TSID=$tsId&rate=320&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/song/tracklink?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 歌曲下载
     * @param tsId 歌曲Id
     */
    suspend inline fun <reified T> songDownload(tsId: String, authorization: String = ""): T {
        val params = "TSID=$tsId&rate=320&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/song/download?$params&sign=$sign") {
            header("authorization", "/access_token $authorization")
        }
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
        return baiduMusic.get(url = "/artist/list?$params&sign=$sign")
    }


    /**
     * 歌手详情
     * @param artistCode 歌手Id
     */
    suspend inline fun <reified T> artistInfo(artistCode: String): T {
        val params = "artistCode=$artistCode&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/artist/info?$params&sign=$sign")
    }

    /**
     * 歌手热门歌曲
     * @param artistCode 歌手Id
     */
    suspend inline fun <reified T> artistSong(artistCode: String, page: Int = 1, size: Int = 20): T {
        val params = "artistCode=$artistCode&pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/artist/song?$params&sign=$sign")
    }

    /**
     * 歌手热门专辑
     * @param artistCode 歌手Id
     */
    suspend inline fun <reified T> artistAlbum(artistCode: String, page: Int = 1, size: Int = 20): T {
        val params = "artistCode=$artistCode&pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/artist/album?$params&sign=$sign")
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
        return baiduMusic.get(url = "/search?$params&sign=$sign")
    }


    /**
     * 搜索热词
     * @param word 搜索关键词
     */
    suspend inline fun <reified T> searchSug(word: String): T {
        val params = "timestamp=${Date().time}&word=$word"
        val sign = sign(params)
        return baiduMusic.get(url = "/search/sug?$params&sign=$sign")
    }


    /**
     * 榜单分类
     */
    suspend inline fun <reified T> bdCategory(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/bd/category?$params&sign=$sign")
    }

    /**
     * 榜单列表
     */
    suspend inline fun <reified T> bdList(bdid: String, page: Int = 1, size: Int = 20): T {
        val params = "bdid=$bdid&pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/bd/list?$params&sign=$sign")
    }


    /**
     * 歌单分类
     */
    suspend inline fun <reified T> trackListCategory(): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/tracklist/category?$params&sign=$sign")
    }

    /**
     * 歌单分类详情
     */
    suspend inline fun <reified T> trackListList(subCateId: String? = null, page: Int = 1, size: Int = 20): T {
        val params = if (subCateId == null) {
            "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        } else {
            "pageNo=$page&pageSize=$size&subCateId=$subCateId&timestamp=${Date().time}"
        }

        val sign = sign(params)
        return baiduMusic.get(url = "/tracklist/list?$params&sign=$sign")
    }


    /**
     * 歌单详情、推荐歌单
     */
    suspend inline fun <reified T> trackListInfo(id: String, page: Int = 1, size: Int = 20): T {
        val params = "id=$id&pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/tracklist/info?$params&sign=$sign")
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     */
    suspend inline fun <reified T> sendSms(phone: String): T {
        val form = FormParams {
            this["phone"] = phone
            this["randstr"] = "@arV"
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/oauth/send_sms", formParameters = params)
    }

    /**
     * 登录
     * @param phone 手机号
     * @param code 验证码
     * NjVhNTMzM2QyZWEyZTlhOTI5OTJiMjZiNWE2YTkwMjY=
     */
    suspend inline fun <reified T> login(phone: String, code: String): T {
        val form = FormParams {
            this["code"] = code
            this["phone"] = phone
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/oauth/login", formParameters = params)
    }


    /**
     * 登出
     */
    suspend inline fun <reified T> logout(authorization: String): T {
        val form = FormParams {
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/account/logout", formParameters = params) {
            header("authorization", "/access_token $authorization")
        }
    }

    /**
     * 账户信息
     */
    suspend inline fun <reified T> accountInfo(authorization: String): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/account/info?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }


    /**
     * 修改账户信息
     * @param age 年龄
     * @param avatar 头像（可为空，是一个完整的图片路径，上传方式请查看说明文档）
     * @param birth 生日
     * @param nickname 昵称
     * @param sex 性别（0，女，1，男）
     */
    suspend inline fun <reified T> changeAccountInfo(
        age: String,
        avatar: String? = null,
        birth: String,
        nickname: String,
        sex: String,
        authorization: String
    ): T {
        val form = FormParams {
            this["age"] = age
            if (!avatar.isNullOrEmpty()) {
                this["avatar"] = avatar
            }
            this["birth"] = birth
            this["nickname"] = nickname
            this["sex"] = sex
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/account/info?$params&sign=$sign", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }


    /**
     * 账户歌曲列表(喜欢的歌曲？)
     */
    suspend inline fun <reified T> accountSongList(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/account/songlist?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 账户收藏等信息
     */
    suspend inline fun <reified T> accountAmount(authorization: String): T {
        val params = "timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/account/amount?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }


    /**
     * 签到
     */
    suspend inline fun <reified T> userSignin(authorization: String): T {
        val form = FormParams {
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/user/points/signin", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }


    /**
     * 收藏的歌曲
     */
    suspend inline fun <reified T> favoriteSong(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/favorite/song?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 收藏歌曲
     */
    suspend inline fun <reified T> favoriteSongCreate(code: String, authorization: String): T {
        val form = FormParams {
            this["code"] = code
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/favorite/song/create", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 删除收藏歌曲
     */
    suspend inline fun <reified T> favoriteSongDelete(code: String, authorization: String): T {
        val form = FormParams {
            this["code"] = code
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/favorite/song/delete", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 收藏的歌单
     */
    suspend inline fun <reified T> favoriteTrackList(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/favorite/tracklist?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 收藏歌单
     */
    suspend inline fun <reified T> favoriteTrackListCreate(code: String, authorization: String): T {
        val form = FormParams {
            this["code"] = code
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/favorite/tracklist/create", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 删除收藏歌单
     */
    suspend inline fun <reified T> favoriteTrackListDelete(code: String, authorization: String): T {
        val form = FormParams {
            this["code"] = code
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/favorite/tracklist/delete", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 收藏的歌手
     */
    suspend inline fun <reified T> favoriteArtist(page: Int = 1, size: Int = 20, authorization: String): T {
        val params = "pageNo=$page&pageSize=$size&timestamp=${Date().time}"
        val sign = sign(params)
        return baiduMusic.get(url = "/favorite/artist?$params&sign=$sign") {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 收藏歌手
     */
    suspend inline fun <reified T> favoriteArtistCreate(code: String, authorization: String): T {
        val form = FormParams {
            this["code"] = code
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/favorite/artist/create", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
    }

    /**
     * 删除收藏歌手
     */
    suspend inline fun <reified T> favoriteArtistDelete(code: String, authorization: String): T {
        val form = FormParams {
            this["code"] = code
            this["timestamp"] = "${Date().time}"
        }
        val sign = sign(form.params())
        val params = form.plus {
            this["sign"] = sign
        }
        return baiduMusic.form(url = "/favorite/artist/delete", formParameters = params) {
            header("authorization", "access_token $authorization")
        }
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
    val resp = BaiduMusic.songDownload<String>(
        tsId = "T10053430210",
        authorization = "NjVhNTMzM2QyZWEyZTlhOTI5OTJiMjZiNWE2YTkwMjY="
    )

    println(resp)

}