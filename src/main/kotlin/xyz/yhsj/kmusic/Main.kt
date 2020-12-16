package xyz.yhsj.kmusic

import xyz.yhsj.kmusic.impl.*
import xyz.yhsj.kmusic.site.MusicSite

fun main(args: Array<String>) {

    println("请输入关键字")
    val songName = readLine()


    val startTime = System.currentTimeMillis()
    println(startTime)

    val resp = KMusic.search(site = MusicSite.BAIDU,key = songName!!, num = 3,)

    println(System.currentTimeMillis() - startTime)

    println(resp.msg)
    resp.data?.forEach {
        println(it.site)
        println(it.code)
        println(it.msg)
        println(it.title)
        println(it.songid)
        println(it.link)
        println(it.author)
        println(it.pic)
        println(it.url)
//        println(it.lrc)
        println("")
        println("")
    }


//    val tops = NeteaseImpl.getSongTop()

//    println(tops.msg)
//    tops.data?.forEach {
//        println(it.code)
//        println(it.topId)
//        println(it.topType)
//        println(it.topKey)
//        println(it.msg)
//        println(it.name)
//        println(it.pic)
//        println(it.comment)
//        println("")
//        println("")
//    }


//    val sss = NeteaseImpl.getSongById("432506345")
//
//    println(sss.msg)
//    sss.data?.forEach {
//        println(it.title)
//        println(it.songid)
//        println(it.link)
//        println(it.author)
//        println(it.pic)
//        println(it.url)
//        println(it.lrc)
//        println("")
//        println("")
//    }


//    val resp = QQImpl.searchAlbum(key = songName!!, num = 15)
//    println(resp.msg)
//    resp.data?.forEach {
//        println(it.site)
//        println(it.code)
//        println(it.msg)
//        println(it.albumName)
//        println(it.albumMID)
//        println(it.albumPic)
//        println(it.singerName)
//        println(it.publicTime)
//        println(it.song_count)
//
//        println("")
//        println("")
//    }

//    val resp = QQImpl.getAlbumById("001L7UIu3GXVtT")
//    println(resp.msg)
//
//    println(resp.data?.site)
//    println(resp.data?.code)
//    println(resp.data?.msg)
//    println(resp.data?.name)
//    println(resp.data?.mid)
//    println(resp.data?.pic)
//    println(resp.data?.singerName)
//    println(resp.data?.publicTime)
//    println(resp.data?.songCount)
////    println(resp.data?.desc)
//    println(resp.data?.company)
//    println(resp.data?.lan)
//    println(resp.data?.genre)
//    resp.data?.list?.forEach {
//        println("")
//        println(it.title)
//        println(it.url)
//
//    }

    println("")
    println("")


    println(System.currentTimeMillis() - startTime)

}