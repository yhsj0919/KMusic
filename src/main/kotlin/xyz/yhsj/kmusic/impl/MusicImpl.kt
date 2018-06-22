package xyz.yhsj.kmusic.impl


import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.site.MusicSite
import xyz.yhsj.kmusic.utils.future
import java.util.*

/**
 * 统一音乐接口
 */
object MusicImpl {
    fun search(key: String, page: Int = 1, num: Int = 10, site: MusicSite = MusicSite.QQ) = when (site) {
        MusicSite.BAIDU -> BaiduImpl
        MusicSite.QQ -> QQImpl
        MusicSite.NETEASE -> NeteaseImpl
        MusicSite.XIAMI -> XiamiImpl
        MusicSite.KUGOU -> KugouImpl
        MusicSite.KUWO -> KuwoImpl
        MusicSite.MIGU -> MiguImpl
    }.search(key, page, num)

    fun getSongById(songIds: List<String>, site: MusicSite) = when (site) {
        MusicSite.BAIDU -> BaiduImpl
        MusicSite.QQ -> QQImpl
        MusicSite.NETEASE -> NeteaseImpl
        MusicSite.XIAMI -> XiamiImpl
        MusicSite.KUGOU -> KugouImpl
        MusicSite.KUWO -> KuwoImpl
        MusicSite.MIGU -> MiguImpl
    }.getSongById(songIds)

    fun getLrcById(songId: String, site: MusicSite) = when (site) {
        MusicSite.BAIDU -> BaiduImpl
        MusicSite.QQ -> QQImpl
        MusicSite.NETEASE -> NeteaseImpl
        MusicSite.XIAMI -> XiamiImpl
        MusicSite.KUGOU -> KugouImpl
        MusicSite.KUWO -> KuwoImpl
        MusicSite.MIGU -> MiguImpl
    }.getLrcById(songId)

    /**
     * 获取所有
     */
    fun searchAll(key: String, page: Int = 1, num: Int = 1): MusicResp<List<Song>> {

        val results =
                arrayOf(MusicSite.BAIDU, MusicSite.QQ, MusicSite.NETEASE, MusicSite.XIAMI, MusicSite.KUGOU, MusicSite.KUWO, MusicSite.MIGU)
                        .future {
                            MusicImpl.search(key, page, num, it)
                        }
                        .filter { it.code == 200 }
                        .flatMap {
                            it.data!!
                        }
        return MusicResp.success(data = results)
    }
}












