package xyz.yhsj.kmusic.utils;

import xyz.yhsj.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 网易云音乐算法
 * Created by xuzijia
 * 2017/12/25 9:07
 */
public class MusicUtil {

    /**
     * 获取网易云反爬虫->加密参数(params,encSecKey)
     *
     * @param data 请求参数
     * @return Map ->params,encSecKey
     * @throws Exception
     */
    public static Map<String, String> arithmetic(Map<String, Object> data) throws Exception {
        //将参数转成json字符串
        String text = new JSONObject(data).toString();
        //私钥，随机16位字符串（自己可改）
        String secKey = "cd859f54539b24b7";
        String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
        String nonce = "0CoJUm6Qyw8W8jud";
        String pubKey = "010001";
        //2次AES加密，得到params
        String params = encrypt(encrypt(text, nonce), secKey);
        StringBuilder stringBuffer = new StringBuilder(secKey);
        //逆置私钥
        secKey = stringBuffer.reverse().toString();
        String hex = Hex.byte2HexStr(secKey.getBytes());
        BigInteger bigInteger1 = new BigInteger(hex, 16);
        BigInteger bigInteger2 = new BigInteger(pubKey, 16);
        BigInteger bigInteger3 = new BigInteger(modulus, 16);
        //RSA加密计算
        BigInteger bigInteger4 = bigInteger1.pow(bigInteger2.intValue()).remainder(bigInteger3);
        String encSecKey = Hex.byte2HexStr(bigInteger4.toByteArray());
        //字符填充
        encSecKey = zfill(encSecKey, 256);
        Map result = new HashMap();
        result.put("params", params);
        result.put("encSecKey", encSecKey);
        return result;
    }


    /**
     * AES加密
     *
     * @param text
     * @param secKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String text, String secKey) throws Exception {
        byte[] raw = secKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // "算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    //字符填充
    public static String zfill(String result, int n) {
        if (result.length() >= n) {
            result = result.substring(result.length() - n, result.length());
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = n; i > result.length(); i--) {
                stringBuilder.append("0");
            }
            stringBuilder.append(result);
            result = stringBuilder.toString();
        }
        return result;
    }

    /**
     * md5算法
     *
     * @param pwd 密码
     * @return
     */
    public static String md5(String pwd) {
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] bs = digest.digest(pwd.getBytes());
            String hexString = "";
            for (byte b : bs) {
                int temp = b & 255;
                if (temp < 16 && temp >= 0) {
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}