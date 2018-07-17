package xyz.yhsj.kmusic.utils

object Hex {
    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    fun str2HexStr(str: String): String {

        val chars = "0123456789ABCDEF".toCharArray()
        val sb = StringBuilder("")
        val bs = str.toByteArray()
        var bit: Int

        for (i in bs.indices) {
            bit = bs[i].toInt() and 0x0f0 shr 4
            sb.append(chars[bit])
            bit = bs[i].toInt() and 0x0f
            sb.append(chars[bit])
            sb.append(' ')
        }
        return sb.toString().trim { it <= ' ' }
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return String 对应的字符串
     */
    fun hexStr2Str(hexStr: String): String {
        val str = "0123456789ABCDEF"
        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexStr.length / 2)
        var n: Int

        for (i in bytes.indices) {
            n = str.indexOf(hexs[2 * i]) * 16
            n += str.indexOf(hexs[2 * i + 1])
            bytes[i] = (n and 0xff).toByte()
        }
        return String(bytes)
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    fun byte2HexStr(b: ByteArray): String {
        var stmp: String
        val sb = StringBuilder("")
        for (n in b.indices) {
            stmp = Integer.toHexString(b[n].toInt() and 0xFF)
            sb.append(if (stmp.length == 1) "0$stmp" else stmp)
            //			sb.append(" ");
        }
        return sb.toString().toUpperCase().trim { it <= ' ' }
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    fun hexStr2Bytes(src: String): ByteArray {
        var m: Int
        var n: Int
        val l = src.length / 2
        println(l)
        val ret = ByteArray(l)
        for (i in 0 until l) {
            m = i * 2 + 1
            n = m + 1
            ret[i] = java.lang.Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n))!!
        }
        return ret
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @param strText 全角字符串
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    @Throws(Exception::class)
    fun strToUnicode(strText: String): String {
        var c: Char
        val str = StringBuilder()
        var intAsc: Int
        var strHex: String
        for (i in 0 until strText.length) {
            c = strText[i]
            intAsc = c.toInt()
            strHex = Integer.toHexString(intAsc)
            if (intAsc > 128)
                str.append("\\u$strHex")
            else
            // 低位在前面补00
                str.append("\\u00$strHex")
        }
        return str.toString()
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    fun unicodeToString(hex: String): String {
        val t = hex.length / 6
        val str = StringBuilder()
        for (i in 0 until t) {
            val s = hex.substring(i * 6, (i + 1) * 6)
            // 高位需要补上00再转
            val s1 = s.substring(2, 4) + "00"
            // 低位直接转
            val s2 = s.substring(4)
            // 将16进制的string转为int
            val n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16)
            // 将int转换为字符
            val chars = Character.toChars(n)
            str.append(String(chars))
        }
        return str.toString()
    }
}
