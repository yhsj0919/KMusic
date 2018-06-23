package xyz.yhsj.kmusic

import xyz.yhsj.kmusic.impl.MusicImpl
import xyz.yhsj.kmusic.impl.XiamiImpl

fun main(args: Array<String>) {

    println("请输入关键字")
//    val songName = readLine()


    val startTime = System.currentTimeMillis()
    println(startTime)

//    val resp = MusicImpl.searchAll(songName ?: "", page = 1, num = 10)

    println(System.currentTimeMillis() - startTime)

//    println(resp.msg)
//    resp.data?.forEach {
//        println(it.type)
//        println(it.code)
//        println(it.msg)
//        println(it.title)
//        println(it.songid)
//        println(it.link)
//        println(it.author)
//        println(it.pic)
//        println(it.url)
////        println(it.lrc)
//        println("")
//        println("")
//    }


    val sss = XiamiImpl.getSongTop("", 1, 1)

    println(sss)


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

    println(System.currentTimeMillis() - startTime)


}