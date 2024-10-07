package com.pasc.business.search.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @date 2019/6/17
 * @des
 * @modify
 **/
public class IoUtil {
    public static byte[] raw2Bytes(Context context,int rawRes){
        InputStream inputStream =context. getResources ().openRawResource (rawRes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
        byte[] bytes = new byte[1024];
        try {
            int n=0;
            while ((n=inputStream.read (bytes)) != -1) {
                outputStream.write (bytes,0,n);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }finally {
            try {
                inputStream.close ();
                outputStream.close ();
            }catch (Exception e){}
        }
        final byte[] allB = outputStream.toByteArray ();
        return allB;
    }
}
