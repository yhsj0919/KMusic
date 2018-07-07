package xyz.yhsj.kmusic

import xyz.yhsj.kmusic.impl.MiguImpl
import xyz.yhsj.kmusic.impl.NeteaseImpl

fun main(args: Array<String>) {

    println("请输入关键字")
    val songName = readLine()


    val startTime = System.currentTimeMillis()
    println(startTime)

//    val resp = NeteaseImpl.getSongTopDetail("3779629", page = 1, num = 10)
//
//    println(System.currentTimeMillis() - startTime)
//
//    println(resp.msg)
//    resp.data?.forEach {
//        println(it.site)
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




    val tops = NeteaseImpl.getSongTop()

    println(tops.msg)
    tops.data?.forEach {
        println(it.code)
        println(it.topId)
        println(it.topType)
        println(it.topKey)
        println(it.msg)
        println(it.name)
        println(it.pic)
        println(it.comment)
        println("")
        println("")
    }


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