package xyz.yhsj.kmusic.entity

/**
 * 专辑
 */
data class Album(
        var code: Int? = 200,
        var msg: String = "操作成功",

        //来源网站
        var site: String? = null,
        //专辑名称
        var name: String? = null,
        //专辑ID
        var mid: String? = null,
        //专辑图片
        var pic: String? = null,
        //歌手名字
        var singerName: String? = null,
        //专辑时间
        var publicTime: String? = null,
        //歌曲数量
        var songCount: Long? = null,
        //专辑描述
        var desc: String? = null,
        //公司
        var company: String? = null,
        //语言
        var lan: String? = null,
        //类别
        var genre: String? = null,
        //歌曲
        val list: ArrayList<Song> = ArrayList()

)