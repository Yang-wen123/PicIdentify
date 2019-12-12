package com.example.left.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.example.left.APPInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
/*
*
* 创建于2019/11/8
*
* */
public class GetBitmapByte {
    //bitmap转换成byte
    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        }        catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
    public String imageToBase64(Bitmap bitmap1) {
        //以防解析错误之后bitmap为null
        if (bitmap1 == null)
            return "解析异常";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //将bitmap进行压缩
        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //转换成byte数组
        byte[] bytes = outputStream.toByteArray();
        APPInfo.str1 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return APPInfo.str1;
    }
    public Bitmap base64ToImage(String text) {
        byte[] bytes = Base64.decode(text, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
