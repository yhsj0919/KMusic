package xyz.yhsj.kmusic.impl

import xyz.yhsj.kmusic.entity.Album
import xyz.yhsj.kmusic.entity.MusicResp
import xyz.yhsj.kmusic.entity.MusicTop
import xyz.yhsj.kmusic.entity.Song

interface Impl {
    /**@param key 关键字
     * @param page 页数
     * 搜索
     */
    fun search(key: String, page: Int = 1, num: Int = 10): MusicResp<List<Song>>

    /**
     * @param key 关键字
     * @param page 页数
     * 搜索专辑
     */
    fun searchAlbum(key: String, page: Int = 1, num: Int = 10): MusicResp<List<Album>>

    /**
     *根据ID获取专辑详情
     * @param albumId 专辑ID
     */
    fun getAlbumById(albumId: String): MusicResp<Album>

    /**
     * 根据id获取歌曲，部分接口支持获取多个，id用“，”分开
     */
    fun getSongById(songIds: List<String>): MusicResp<List<Song>>

    /**
     * 根据id获取歌词，部分接口需要传入完整url，由网站确定
     */
    fun getLrcById(songId: String): String

    /**
     * 获取歌曲排行榜
     */
    fun getSongTop(): MusicResp<List<MusicTop>>

    /**
     * 根据类型,获取歌曲排行榜详情
     */
    fun getSongTopDetail(topId: String, topType: String = "", topKey: String = "", page: Int = 1, num: Int = 10): MusicResp<List<Song>>


}