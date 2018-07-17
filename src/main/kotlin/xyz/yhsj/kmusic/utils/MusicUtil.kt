package xyz.yhsj.kmusic.utils

import xyz.yhsj.json.JSONObject

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Base64
import java.util.HashMap

/**
 * 网易云音乐算法
 * Created by xuzijia
 * 2017/12/25 9:07
 */
object MusicUtil {

    /**
     * 获取网易云反爬虫->加密参数(params,encSecKey)
     *
     * @param data 请求参数
     * @return Map ->params,encSecKey
     * @throws Exception
     */
    @Throws(Exception::class)
    fun arithmetic(data: Map<String, Any>): Map<String, String> {
        //将参数转成json字符串
        val text = JSONObject(data).toString()
        //私钥，随机16位字符串（自己可改）
        var secKey = "cd859f54539b24b7"
        val modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7"
        val nonce = "0CoJUm6Qyw8W8jud"
        val pubKey = "010001"
        //2次AES加密，得到params
        val params = encrypt(encrypt(text, nonce), secKey)
        val stringBuffer = StringBuilder(secKey)
        //逆置私钥
        secKey = stringBuffer.reverse().toString()
        val hex = Hex.byte2HexStr(secKey.toByteArray())
        val bigInteger1 = BigInteger(hex, 16)
        val bigInteger2 = BigInteger(pubKey, 16)
        val bigInteger3 = BigInteger(modulus, 16)
        //RSA加密计算
        val bigInteger4 = bigInteger1.pow(bigInteger2.toInt()).remainder(bigInteger3)
        var encSecKey = Hex.byte2HexStr(bigInteger4.toByteArray())
        //字符填充
        encSecKey = zfill(encSecKey, 256)
        val result = HashMap<String, String>()
        result["params"] = params
        result["encSecKey"] = encSecKey
        return result
    }


    /**
     * AES加密
     *
     * @param text
     * @param secKey
     * @return
     * @throws Exception
     */
    @JvmStatic
    @Throws(Exception::class)
    fun encrypt(text: String, secKey: String): String {
        val raw = secKey.toByteArray()
        val skeySpec = SecretKeySpec(raw, "AES")
        // "算法/模式/补码方式"
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        val iv = IvParameterSpec("0102030405060708".toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    //字符填充
    fun zfill(res: String, n: Int): String {

        return if (res.length >= n) {
            res.substring(res.length - n, res.length)
        } else {
            val stringBuilder = StringBuilder()
            for (i in n downTo res.length + 1) {
                stringBuilder.append("0")
            }
            stringBuilder.append(res)
            stringBuilder.toString()
        }
    }

    /**
     * md5算法
     *
     * @param pwd 密码
     * @return
     */
    fun md5(pwd: String): String {
        try {
            val digest = MessageDigest.getInstance("md5")
            val bs = digest.digest(pwd.toByteArray())
            var hexString = ""
            for (b in bs) {
                val temp = b.toInt() and 255
                hexString = if (temp in 0..15) {
                    hexString + "0" + Integer.toHexString(temp)
                } else {
                    hexString + Integer.toHexString(temp)
                }
            }
            return hexString
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }

}