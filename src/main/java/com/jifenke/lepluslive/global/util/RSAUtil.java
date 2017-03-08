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

  /**
   * 银商公钥
   */
  private static String
      pubYS =
      "30819f300d06092a864886f70d010101050003818d0030818902818100c60738ffae05b8902661cc8c72dbdaa9de6a98f69575f2118aeafd77d91989f4363119dcc8c2109d94fbdc48873305c67e3a742ea9879555ab573bb95cd4c5200cf680146812405c134f3f89c283eb9fec75013b358c3818b9c1055737cd25a811e38ba714b22167520fe4d9f6f3dc29deeea9f59b583a59ad426c08e1d1dc3f0203010001";

  /**
   * 积分客私钥
   */
  private static String
      pri =
      "30820278020100300d06092a864886f70d0101010500048202623082025e02010002818100e3b7f8a022be8ffa8057b7c6277c4bb60b2ede048b90d4df5b676d41e437255d976436a77b551a963fc12cff0847c5206a7e7c62be256011e5b4365300df584d18733f43b3d4be99d3ba455a47f51931999a2fe93e0dc79bc386a37e9b9a2e0bf95672c2d57ad74b7b7ca6c604e0fced2b08f874e0604e8b80aa30270cab3211020301000102818100b54ec1321ef7c3031c34bb29963eacb960e60bf76a780245642d511c696c4edfd9adfca614d889b69f445b632360a15f51eb2b762316f70ac8ed5763770a5cee645fb0809239f614fe7de41955bd3a46cefb9081b8ff98a362526b54ab6f40088b4fa2243816e38d20225f81fb44cf80dfe9ca00e0c05eeee4c790542af9cd01024100fd63abf258ca666f1568d0da8438b04b4a2ee04eb8e323e0449f6dd63c0f5b2fe759b3397595e2f15486d0551a48166118330bbefaadce4676eb251a0b7d7ec9024100e61097750f00ce316eba28755acab74275caf67198918fb875838c850927fa716a3cbc74d121a284b810be937b1dfac083452f7aad4f7051945757af70615509024100f11a20e75553294ab38d6b8530e09be6d7ec68a49f4a09be00adce9e55c1aadb970004083d79645a8b3a480b62198ba9d193e8ae4cea8a67040a54e046649ff902400a7b730b2855d70f358651d596ff697e23b3cdaabb52d6838707295dde5eedf040f0e77d43120f3f90cb628381b689f83f82e578e77099237e68c45bfff8cf5102410094362e41dce4db21360819efec1193070c4e77172735946cb77024af26bbf78ce2e6a3f6c823d18e8b6fd08c6b0d53ab2d4cf284240d2b49e585f474121e88e8";

  /**
   * RSA银商公钥加密
   *
   * @param src 原字符串
   * @return 加密后字符串
   */
  public static String encode(String src) {
    try {
      X509EncodedKeySpec
          x509EncodedKeySpec =
          new X509EncodedKeySpec(MHex.decodeHex(pubYS.toCharArray()));
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
    } catch (Exception e) {

    }

    return null;
  }

  /**
   * RSA积分客私钥解密
   *
   * @param str 被加密字符串
   * @return 已解密字符串
   */
  public static String decode(String str) {
    try {
      PKCS8EncodedKeySpec
          pkcs8EncodedKeySpec =
          new PKCS8EncodedKeySpec(MHex.decodeHex(pri.toCharArray()));
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
    } catch (Exception e) {

    }

    return null;
  }

  /**
   * SHA1WithRSA 私钥签名
   *
   * @param str 原字符串
   * @return 被签名字符串
   */
  public static String sign(String str) {
    try {
      PKCS8EncodedKeySpec
          pkcs8EncodedKeySpec =
          new PKCS8EncodedKeySpec(MHex.decodeHex(pri.toCharArray()));
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
      Signature signature = Signature.getInstance("SHA1WithRSA");
      signature.initSign(privateKey);
      signature.update(str.getBytes());
      byte[] result = signature.sign();
      return MHex.encodeHexString(result);
    } catch (Exception e) {

    }
    return null;
  }

  /**
   * SHA1WithRSA 验证签名
   *
   * @param str     原字符串
   * @param signStr 签名后字符串
   * @return 验证结果
   */
  public static boolean testSign(String str, String signStr) {
    try {
      X509EncodedKeySpec
          x509EncodedKeySpec =
          new X509EncodedKeySpec(MHex.decodeHex(pubYS.toCharArray()));
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
      Signature signature = Signature.getInstance("SHA1WithRSA");
      signature.initVerify(publicKey);
      signature.update(str.getBytes());
      byte[] encryptedData = MHex.decodeHex(signStr.toCharArray());
      boolean bool = signature.verify(encryptedData);

      return bool;
    } catch (Exception e) {

    }
    return false;
  }

}
