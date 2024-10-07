package com.search.demo;

import java.security.MessageDigest;

/**
 * @author yangzijian
 * @date 2019/2/18
 * @des  hash265 16位加密
 * @modify
 **/
public class Util {
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public static String getSHA256(String data) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte[] byteData = md.digest();
            sb.append(bytesToHex(byteData));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return String.valueOf(hexChars);
    }
}
