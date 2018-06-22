package xyz.yhsj.kmusic.entity

//返回的消息
data class MusicResp<T>(
        var code: Int = 200,

        var msg: String = "操作成功",
        val data: T? = null
) {
    companion object {
        fun <T> success(msg: String = "操作成功", data: T?) = MusicResp(msg = msg, data = data)
        fun <T> failure(code: Int = 500, msg: String?) = MusicResp<T>(code = code, msg = msg ?: "未知异常")
    }
}