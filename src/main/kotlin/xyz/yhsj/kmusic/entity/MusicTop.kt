package xyz.yhsj.kmusic.entity

import java.io.Serializable

/**
 * 歌曲排行榜
 */
data class MusicTop(
        var code: Int? = 200,
        var msg: String = "操作成功",

        //来源网站
        var site: String? = null,
        //排行榜名称
        var name: String? = null,
        //排行榜ID
        var topId: String? = null,
        //类型
        var topType: String? = null,
        //排行榜key
        var topKey: String? = null,
        //排行榜描述
        var comment: String? = null,
        //封面
        var pic: String? = null,
        //预览歌曲
        var songs: List<Song> = ArrayList()
) : Serializable {
    companion object {
        fun failure(code: Int = 500, msg: String?) = Song(code = code, msg = msg ?: "未知异常")

    }
}