package xyz.yhsj.kmusic.utils

import java.io.ByteArrayOutputStream
import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * 非对称加密 RSA总结
 * 秘钥对：   公钥和私钥，必须由系统生成并且存储起来
 * 特点：
 *     可逆，加解密：   公钥加密，私钥解密；私钥加密，公钥解密
 *     加解密速度慢，要分段加密和分段解密
 *     公钥互换： 两个组织或者人互相交换公钥
 *
 * 应用场景：数字签名（结合消息摘要实现),校验数据完整性
 */
object RSA {
    /**
     * 算法
     */
    const val CRYPT_ALGORITHM = "RSA"

    /**
     * Cipher对象
     */
    private val cipher: Cipher = Cipher.getInstance(CRYPT_ALGORITHM)

    /**
     * 加密的时候  不能大于117个字节，否则会报异常：javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
     */
    private const val ENCRYPT_MAX_SIZE = 117

    /**
     * 解密的时候  不能大于128个字节，否则会报异常：javax.crypto.IllegalBlockSizeException: Data must not be longer than 128 bytes
     */
    private const val DECRYPT_MAX_SIZE = 128


    /**
     * 使用私钥解密
     * @param encryptContent 要解密的内容
     * @param privateKey privateKey：RSA私钥
     */
    fun decryptByPrivateKey(encryptContent: String, privateKey: PrivateKey): String {
        return segmentedDecrypt(encryptContent, privateKey)
    }

    /**
     * 使用公钥解密
     * @param encryptContent 要解密的内容
     * @param publicKey publicKey：RSA公钥
     */
    fun decryptByPublicKey(encryptContent: String, publicKey: PublicKey): String {
        return segmentedDecrypt(encryptContent, publicKey)
    }

    /**
     * 使用私钥加密
     * @param originContent 要加密的内容
     * @param privateKey 秘钥：RSA私钥
     */
    fun encryptByPrivateKey(originContent: String, privateKey: PrivateKey): String {
        return segmentedEncrypt(originContent, privateKey)
    }

    /**
     * 使用公钥加密
     * @param originContent 要加密的内容
     * @param publicKey 秘钥：RSA公钥
     */
    fun encryptByPublicKey(originContent: String, publicKey: PublicKey): String {
        return segmentedEncrypt(originContent, publicKey)
    }

    /**
     * 分段加密
     * @param originContent 要加密的内容
     * @param key 秘钥：RSA公钥或者RSA私钥
     */
    private fun segmentedEncrypt(originContent: String, key: Key): String {
        // 初始化cipher
        cipher.init(Cipher.ENCRYPT_MODE, key)
        // 分段加密
        val byteArray = originContent.toByteArray()
        var temp: ByteArray?
        // 当前位置偏移量
        var offset = 0
        // 输出流
        val byteArrayOutputStream = ByteArrayOutputStream()
        // 如果没有加密完，则一直加密
        while (byteArray.size - offset > 0) {
            // 每次加密不能大于117个字节，否则会报异常：javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
            if (byteArray.size - offset > ENCRYPT_MAX_SIZE) {
                // 剩余部分大于117个字节
                // 加密完整的117个字节
                temp = cipher.doFinal(byteArray, offset, ENCRYPT_MAX_SIZE)
                // 重新计算偏移的位置
                offset += ENCRYPT_MAX_SIZE
            } else {
                // 计算最后一块不足117个字节的那块
                temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                // 重新计算偏移的位置
                offset = byteArray.size
            }
            // 存储到临时缓冲区
            byteArrayOutputStream.write(temp)
        }
        // 关闭输出流
        byteArrayOutputStream.close()
        // 3、Base64编码 加密后的内容
        return Base64.encode(byteArrayOutputStream.toByteArray())
    }

    /**
     * 分段解密
     * @param encryptContent 要解密的内容
     * @param key 秘钥：RSA公钥或者RSA私钥
     */
    private fun segmentedDecrypt(encryptContent: String, key: Key): String {
        // 初始化cipher
        cipher.init(Cipher.DECRYPT_MODE, key)
        // 使用Base64解编码
        val byteArray = Base64.decode(encryptContent)

        //分段解密
        var temp: ByteArray?
        // 当前位置偏移量
        var offset = 0
        // 输出流
        val byteArrayOutputStream = ByteArrayOutputStream()
        // 如果没有解密完，则一直解密
        while (byteArray.size - offset > 0) {
            // 每次解密的时候  不能大于128个字节，否则会报异常：javax.crypto.IllegalBlockSizeException: Data must not be longer than 128 bytes
            if (byteArray.size - offset > DECRYPT_MAX_SIZE) {
                // 剩余部分大于128个字节
                // 解密完整的128个字节
                temp = cipher.doFinal(byteArray, offset, DECRYPT_MAX_SIZE)
                // 重新计算偏移的位置
                offset += DECRYPT_MAX_SIZE
            } else {
                // 计算最后一块不足128个字节的那块
                temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                // 重新计算偏移的位置
                offset = byteArray.size
            }
            // 存储到临时缓冲区
            byteArrayOutputStream.write(temp)
        }
        //关闭输出流
        byteArrayOutputStream.close()
        // 3、解密后的内容
        return String(byteArrayOutputStream.toByteArray())
    }
}

fun main(args: Array<String>) {

//    //秘钥对生成器
//    val pairGenerator = KeyPairGenerator.getInstance(RSACryptUtil.CRYPT_ALGORITHM)
//    // 秘钥对
//    val keyPair = pairGenerator.genKeyPair();
//    val publicKey = keyPair.public
//    val privateKey = keyPair.private
//
//    println("publicKey = ${Base64Util.encode(publicKey.encoded)} ")
//    println("privateKey = ${Base64Util.encode(privateKey.encoded)}")

    // 保存秘钥对
    val publicKeyStr =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcAy2jnNa1ST6RnLEsTyewfijqACw5IJpZ39RLmhiegv4A+v5amksJP6X8e8Hs1AAttibinofgL/ACC0+bXDvtPRmctXACeoksQ+9p+V8JHfluHCLYFBNoF8ag0Uyqf2i7aRZ5Cjhg1p3EPFWBpF4gQmY4/6/kJRCJY4+pkhHNnQIDAQAB"
    val privateKeyStr =
        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJwDLaOc1rVJPpGcsSxPJ7B+KOoALDkgmlnf1EuaGJ6C/gD6/lqaSwk/pfx7wezUAC22JuKeh+Av8AILT5tcO+09GZy1cAJ6iSxD72n5Xwkd+W4cItgUE2gXxqDRTKp/aLtpFnkKOGDWncQ8VYGkXiBCZjj/r+QlEIljj6mSEc2dAgMBAAECgYB5VnN5i7Ll3ih+oaqedpW8y/JQRIAgkj3Z5p67igbZybF527xVhSkU8V7MZwhlhGD7LG9D096Du2Tkgh2RDIRCAIhLW3t2E4ecH1mvjEd1tAM9O8L71xhqFv7QKW+AYrqz9BjnVY0GQ7vtjVMm2FcaXkwO/gauliPyZJgP3zSj9QJBANdJZr8jL1fomvM0knsQreEOQf/VRu1A5T8q6rKRskQCbZJes7HF+wZwMRWd185CjEItO02XQ+mx0prld0lCaesCQQC5hCax57Rl2CxPDFyk43vFZ4WyPM3T4AK0uSWz7zL70JYEf5KGovxnJv5zK73jelA0pRut60zUa/7oorNYZvyXAkEArWqkCD1YapClGFl10QoLdmlcwq7TNlP7FhoE+higp2XpS2lbiEz5OyNudoIyDyTOBJSY25SIkRu7kShcITgNRwJBAIAjQmLxiw4pgXlcLYWKxwvz1Enna7fj65qOo/cjyhBa47PZtgie/T32lhxQ40kDS/EA9hxsAVZb/ecLslrmT6MCQBklolO3g3lhlzOMnXFgnIJUWYtKMWTUX1LklIGYot8+UnFUwhpHEU7IAjaF2jA1NZgjfN2Xfh/eTkOZSdOeWJQ="

    // 字符串转换成秘钥对对象
    val keyFactory = KeyFactory.getInstance(RSA.CRYPT_ALGORITHM)
    //下面的代码会报异常： java.security.spec.InvalidKeySpecException: Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys
//    val privateKey = keyFactory.generatePrivate(SecretKeySpec(privateKeyStr.toByteArray(), RSACryptUtil.CRYPT_ALGORITHM))

    //下面的代码会报异常： java.security.InvalidKeyException: invalid key format
//    val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKeyStr.toByteArray()))

    // 下面的代码  可以生成privateKey对象
    val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(Base64.decode(privateKeyStr)))


    //下面的代码会报异常： java.security.spec.InvalidKeySpecException: Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys
//    val publicKey = keyFactory.generatePublic(SecretKeySpec(publicKeyStr.toByteArray(), RSACryptUtil.CRYPT_ALGORITHM))

    //下面的代码会报异常：java.security.spec.InvalidKeySpecException: java.security.InvalidKeyException: invalid key format
//    val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(publicKeyStr.toByteArray()))

    // 下面的代码  可以生成publicKey对象
    val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(Base64.decode(publicKeyStr)))


    // 原文
    val originContent = "我喜欢编程,我喜欢编程,我喜欢编程,我喜欢编程,我喜欢编程我喜欢编程我喜欢编程我喜欢编程," +
            "我喜欢编程,我喜欢编程我喜欢编程我喜欢编程我喜欢编" + "我喜欢编程,我喜欢编程我喜欢编程我喜欢编程我喜欢编" +
            "我喜欢编程,我喜欢编程我喜欢编程我喜欢编程我喜欢编" + "我喜欢编程,我喜欢编程我喜欢编程我喜欢编程我喜欢编"

    println("\n==============================RSA私钥加密======================================================")
    // 秘文
    val rsaPrivateKeyEncryptContent = RSA.encryptByPrivateKey(originContent, privateKey)
    println("\n原文为：【${originContent}】 \nRSA私钥加密后的内容为：【${rsaPrivateKeyEncryptContent}】")


    println("\n==============================RSA公钥解密======================================================")

    // 解密  私钥加密后的秘文
    val rsaPublicKeyDecryptContent = RSA.decryptByPublicKey(rsaPrivateKeyEncryptContent, publicKey)
    println("\nRSA私钥加密后的内容为：【${rsaPrivateKeyEncryptContent}】\n RSA公钥解密后的内容为 ${rsaPublicKeyDecryptContent}")

    println("\n*************************************************************************************************")
    println("*************************************************************************************************")
    println("*************************************************************************************************")
    println("*************************************************************************************************")

    println("\n==============================RSA公钥加密======================================================")

    // 秘文
    val rsaPublicKeyEncryptContent = RSA.encryptByPublicKey(originContent, publicKey)
    println("\n原文为：【${originContent}】 \nRSA公钥加密后的内容为：【${rsaPublicKeyEncryptContent}】")

    println("\n==============================RSA私钥解密======================================================")

    // 解密  公钥加密后的秘文
    val rsaPrivateKeyDecryptContent = RSA.decryptByPrivateKey(rsaPublicKeyEncryptContent, privateKey)
    println("\nRSA公钥加密后的内容为：【${rsaPublicKeyEncryptContent}】\n RSA私钥解密后的内容为 ${rsaPrivateKeyDecryptContent}")
}