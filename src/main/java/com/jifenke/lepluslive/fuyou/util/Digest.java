package com.jifenke.lepluslive.fuyou.util;


import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by zhangwen on 2017/7/14.
 */
public class Digest {

  public static final String ENCODE = "UTF-8"; // UTF-8

  public static String hmacSign(String aValue) {
    try {
      byte[] input = aValue.getBytes();
      MessageDigest md = MessageDigest.getInstance("MD5");
      return ConvertUtils.toHex(md.digest(input));
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static String hmacSign(String aValue, String aKey, String encoding) {
    byte k_ipad[] = new byte[64];
    byte k_opad[] = new byte[64];
    byte keyb[];
    byte value[];
    try {
      keyb = aKey.getBytes(encoding);
      value = aValue.getBytes(encoding);
    } catch (UnsupportedEncodingException e) {
      keyb = aKey.getBytes();
      value = aValue.getBytes();
    }
    Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
    Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
    for (int i = 0; i < keyb.length; i++) {
      k_ipad[i] = (byte) (keyb[i] ^ 0x36);
      k_opad[i] = (byte) (keyb[i] ^ 0x5c);
    }

    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
    md.update(k_ipad);
    md.update(value);
    byte dg[] = md.digest();
    md.reset();
    md.update(k_opad);
    md.update(dg, 0, 16);
    dg = md.digest();
    return ConvertUtils.toHex(dg);
  }

  public static String hmacSign(String aValue, String aKey) {
    return hmacSign(aValue, aKey, ENCODE);
  }

}
