package com.example.left;

import android.graphics.Bitmap;

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
}
