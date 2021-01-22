//package xyz.yhsj.kmusic
//
//import io.ktor.client.request.*
//
//object KuGouMusic {
//    /**
//     * 排行榜
//     */
//    suspend inline fun <reified T> rankList(): T {
//        return client.get(
//            url = "http://m.kugou.com/rank/list&json=true"
//        )
//    }
//
//    /**
//     * 排行榜Tag
//     */
//    suspend inline fun <reified T> rankTag(rankId: String): T {
//        return client.get(
//            url = "https://gateway.kugou.com/api/v3/rank/vol"
//        ) {
//            header("x-router", "mobilecdn.kugou.com")
//            parameter("rankid", rankId)
//        }
//    }
//
//    /**
//     * 排行榜详情
//     */
//    suspend inline fun <reified T> rankInfo(rankId: String, volId: String, page: Int = 1, size: Int = 10): T {
//        return client.get(
//            url = "https://gateway.kugou.com/api/v3/rank/song"
//        ) {
//            header("x-router", "mobilecdn.kugou.com")
//            parameter("version", "10409")
//            parameter("page", page)
//            parameter("pagesize", size)
//            parameter("volid", volId)
//            parameter("rankid", rankId)
//        }
//    }
//
//    //http://trackercdnbj.kugou.com/i/v2/?cmd=24&hash=3bd5c05b9f8d082ba3c9425a1a712394&key=68662bee637e771cf5828de5d65b8dfe&pid=1&vipToken=&appid=1001&mid=4557f0209a4c0683b45fc04abf4c3cfc&version=8397&token=&vipType=0&userid=192125837&behavior=play&album_audio_id=32100650&IsFreePart=1&pidversion=3001&module=locallist&album_id=966846&area_code=1&cdnBackup=1
//
//}
//
//suspend fun main() {
//    val rasp = KuGouMusic.rankInfo<String>(rankId = "6666", volId = "")
//
//    println(rasp)
//}