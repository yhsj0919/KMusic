package xyz.yhsj.kmusic


import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.Song
import xyz.yhsj.kmusic.impl.*
import xyz.yhsj.kmusic.site.MusicSite
import xyz.yhsj.kmusic.utils.future

/**
 * 统一音乐接口
 */
object KMusic {
    /**
     * 搜索歌曲.默认在QQ音乐搜索
     */
    @JvmStatic
    fun search(key: String, page: Int = 1, num: Int = 10, site: MusicSite = MusicSite.QQ) = when (site) {
        MusicSite.BAIDU -> BaiduImpl
        MusicSite.QQ -> QQImpl
        MusicSite.NETEASE -> NeteaseImpl
//        MusicSite.XIAMI -> XiamiImpl
        MusicSite.KUGOU -> KugouImpl
        MusicSite.KUWO -> KuwoImpl
        MusicSite.MIGU -> MiguImpl
    }.search(key, page, num)

    /**
     * 根据歌曲ID获取歌曲详情,支持批量获取
     */
    @JvmStatic
    fun getSongById(songIds: List<String>, site: MusicSite) = when (site) {
        MusicSite.BAIDU -> BaiduImpl
        MusicSite.QQ -> QQImpl
        MusicSite.NETEASE -> NeteaseImpl
//        MusicSite.XIAMI -> XiamiImpl
        MusicSite.KUGOU -> KugouImpl
        MusicSite.KUWO -> KuwoImpl
        MusicSite.MIGU -> MiguImpl
    }.getSongById(songIds)

    /**
     * 根据歌曲ID获取歌词,支持批量获取
     */
    @JvmStatic
    fun getLrcById(songId: String, site: MusicSite) = when (site) {
        MusicSite.BAIDU -> BaiduImpl
        MusicSite.QQ -> QQImpl
        MusicSite.NETEASE -> NeteaseImpl
//        MusicSite.XIAMI -> XiamiImpl
        MusicSite.KUGOU -> KugouImpl
        MusicSite.KUWO -> KuwoImpl
        MusicSite.MIGU -> MiguImpl
    }.getLrcById(songId)

    /**
     * 在全部7个网站搜索歌曲.持续时间,以几个网站中最长的为准
     */
    @JvmStatic
    fun searchAll(key: String, page: Int = 1, num: Int = 1): MusicResp<List<Song>> {

        val results =
                arrayOf(MusicSite.BAIDU, MusicSite.QQ, MusicSite.NETEASE, MusicSite.KUGOU, MusicSite.KUWO, MusicSite.MIGU)
                        .future {
                            search(key, page, num, it)
                        }
                        .filter { it.code == 200 }
                        .flatMap {
                            it.data!!
                        }
        return MusicResp.success(data = results)
    }
}












