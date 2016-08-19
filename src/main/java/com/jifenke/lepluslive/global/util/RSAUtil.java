package com.jifenke.lepluslive.global.util;

import org.apache.commons.codec.binary.MHex;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {

    /** 银商公钥 */
    private static String pubYS = "" +
            "30819f300d06092a864886f70d010101050003818" +
            "d0030818902818100ea5b22fc7e4a47b424b484de" +
            "b463c91c869fc14b8afa697caef5513d7d6ff3fc9" +
            "28be7457e74fd5b67b0e5d1e26a58dfa6118310f4" +
            "3e43540f3d7e9a985c8de373c0e5c9e2ac8d7396a" +
            "4b57e6be3279a13f13b720c3f7fa2fa8ad2cc91c5" +
            "ea4587c608af96825597b6eaf316a1daeff906350" +
            "f9c18eef819592b8efcbb0536af0203010001";

    /** 积分客私钥 */
    private static String pri = "" +
            "30820278020100300d06092a864886f70d0101010" +
            "500048202623082025e02010002818100d42a42bb" +
            "229bc745071cbe1772b8f3db3d08423a834407798" +
            "124df3f061d94086f404898d27260d6caaaee1c5b" +
            "1b10726942bed5e045f489ebf553f78df45085265" +
            "4ebd5759c3ddb4f2b2e5cdeba850c7bd170baf716" +
            "f8f03971179963785c5f40e35293afb6cd69250f7" +
            "606056986714dc8569187ba117314fb3b11495d07" +
            "61020301000102818100c3da2733bca4f4f481b64" +
            "4bc3c89038334f9f4b933078fa6d852834b188821" +
            "690fd9d5aa4d4a8c9e7b04033e65d3bc0101ae3da" +
            "00da24635337e8a7e91aa20d0bab67cf07121126d" +
            "900252d0d8ed7fbe59d24d3392f495f1730e2e8b6" +
            "d12933a3a9466c12d2321ca754704285cb337c4fd" +
            "bcbaca38600ae1a77aed5e51892a69024100efec2" +
            "3c2fd391460b6d9f18dd6be28ccf1c7a15ec87ec0" +
            "13b4aca27b868a125c412dddcd581cb8f6d705ff5" +
            "be91942e1c04b6abc2daa89f996b4a318180d7193" +
            "024100e261f1e039800c65a376b5671d2f31fe1ce" +
            "76db8d17dd26a6194568a8a06c12f7fa4863a3a4d" +
            "257e2eb7a0ba8ad38a97304054ec8d7b354ec74d2" +
            "1cc4ac74bbb024100ce1c5218980baa3aefbac9ae" +
            "2d564d9ccaf4d1e7a85c634c4a81a85070233b64a" +
            "fc5e46f1d0c07c65eb0d350352f336ad714b30c18" +
            "3b12af2d4ddf72ba6f0a2b024030fed1c29daf40f" +
            "f9e694ac98e5f708a76e6ffb04866daeb7b430797" +
            "e457a1e80dd025dce4515e13f93874e8eab2ee03d" +
            "ac5919bc0e3319e6334266478aab991024100a3ac" +
            "9c5366f59a67566d35d6ab7a36dafe379e9cfbb35" +
            "e31387a40b1f492eceb0250a61dd4fe8b3390e34b" +
            "bf001ea6845304a8116fbbb80257195947a7937486";

    /**
     * RSA银商公钥加密
     * @param src 原字符串
     * @return  加密后字符串
     */
    public static String encode(String src){
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(MHex.decodeHex(pubYS.toCharArray()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            int inputLen = src.getBytes().length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > 117) {
                    cache = cipher.doFinal(src.getBytes(), offSet, 117);
                } else {
                    cache = cipher.doFinal(src.getBytes(), offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * 117;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return MHex.encodeHexString(encryptedData);
        }catch (Exception e){

        }

        return null;
    }

    /**
     * RSA积分客私钥解密
     * @param str    被加密字符串
     * @return  已解密字符串
     */
    public static String decode(String str){
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(MHex.decodeHex(pri.toCharArray()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedData = MHex.decodeHex(str.toCharArray());
            int inputLen2 = encryptedData.length;
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            int offSet2 = 0;
            byte[] cache2;
            int i2 = 0;
            // 对数据分段解密
            while (inputLen2 - offSet2 > 0) {
                if (inputLen2 - offSet2 > 128) {
                    cache2 = cipher.doFinal(encryptedData, offSet2, 128);
                } else {
                    cache2 = cipher.doFinal(encryptedData, offSet2, inputLen2 - offSet2);
                }
                out2.write(cache2, 0, cache2.length);
                i2++;
                offSet2 = i2 * 128;
            }
            byte[] decryptedData = out2.toByteArray();
            out2.close();

            return new String(decryptedData);
        }catch (Exception e){

        }

        return null;
    }

    /**
     * SHA1WithRSA 私钥签名
     * @param str    原字符串
     * @return  被签名字符串
     */
    public static String sign(String str){
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(MHex.decodeHex(pri.toCharArray()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(str.getBytes());
            byte[] result = signature.sign();
            return MHex.encodeHexString(result);
        }catch (Exception e){

        }
        return null;
    }

    /**
     * SHA1WithRSA 验证签名
     * @param str           原字符串
     * @param signStr       签名后字符串
     * @return  验证结果
     */
    public static boolean testSign(String str, String signStr){
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(MHex.decodeHex(pubYS.toCharArray()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            signature.update(str.getBytes());
            byte[] encryptedData = MHex.decodeHex(signStr.toCharArray());
            boolean bool = signature.verify(encryptedData);

            return bool;
        }catch (Exception e){

        }
        return false;
    }

}
