package xyz.yhsj.kmusic.utils

import java.net.URLDecoder

/**
 * 凯撒方阵解析
 */
object DecodeKaiserMatrix {
    fun decode(str: String): String? {
        if (str.isEmpty()) return null

        val num = Integer.valueOf(str[0].toString())
        val realStr = str.substring(1)
        val step = realStr.length / num
        val duo = realStr.length % num
        var start = 0
        val strList = (0 until num).map {
            val end = if (it < duo) {
                start + step + 1
            } else {
                start + step
            }
            val tmpStr = realStr.substring(start, end)
            start = end
            tmpStr
        }
        return makeSense(strList)
    }

    private fun makeSense(matrix: List<String>): String? {
        val sb = StringBuilder()
        for (index in 0 until matrix[0].length) {
            for (aMatrix in matrix) {
                if (aMatrix.length - 1 >= index)
                    sb.append(aMatrix[index])
            }
        }
        return try {
            val tmp = URLDecoder.decode(sb.toString(), "UTF-8")
            tmp.replace("\\^".toRegex(), "0")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

//fun main(args: Array<String>) {
//    val str = "9%8eE%5%1123y1-9c9f%2.t%5E2157F%7%6dd75Fx%2E3F%17a3254cabE%i2F%41546uD%E7b7%c2aF1228E1.t15-f6%5Fm21F8%16mh5E%7f5Emi5522577p_2%594E%1.%218E443k75E61c52n55%61_6%e2E-6c8E"
//    println(DecodeKaiserMatrix.decode(str))
//}