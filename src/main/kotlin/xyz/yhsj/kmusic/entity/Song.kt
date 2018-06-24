package xyz.yhsj.kmusic.entity

import java.io.Serializable

/**
 * 歌曲
 */
data class Song(
        var code: Int? = 200,
        var msg: String = "操作成功",

        //来源网站
        var site: String? = null,
        //歌曲地址
        var link: String? = null,
        //歌曲id
        var songid: String? = null,
        //歌名
        var title: String? = null,
        //演唱者
        var author: String? = null,
        //歌词
        var lrc: String? = null,
        //播放地址
        var url: String? = null,
        //封面
        var pic: String? = null,
        //专辑名
        var albumName: String? = null
) : Serializable {
    companion object {
        fun failure(code: Int = 500, msg: String?) = Song(code = code, msg = msg ?: "未知异常")

    }
}