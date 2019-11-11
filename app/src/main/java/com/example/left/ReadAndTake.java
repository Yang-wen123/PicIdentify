package com.example.left;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadAndTake {
    public void ReadPhoto(Context context, Activity activity) {
        //获取动态权限
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,APPInfo.IMAGE_TYPE);
        activity.startActivityForResult(intent,APPInfo.RESULT_IMAGE);
        APPInfo.iscommon=1;
    }
    public void TakePhoto(Context context, Activity activity) {
        //          获取本地时间，作为图片的名字
        SimpleDateFormat format = new SimpleDateFormat(APPInfo.imageyear);
        Date curDate = new Date(System.currentTimeMillis());
        APPInfo.str = format.format(curDate);
        //创建一个新的文件夹            //如果存在则删除，不存在则创建
        File outputImage = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
            outputImage = new File(context.getExternalCacheDir(), APPInfo.str+".jpg");
        }
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //判断系统版本 是 < 7.0  或是 > 7.0
        if (Build.VERSION.SDK_INT >= 24) {
            //authority与Mainifest中provider的authority保持一致
            APPInfo.ImageUri = FileProvider.getUriForFile(context,
                    "com.example.left.fileprovider", outputImage);
        } else {
            APPInfo.ImageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //传递保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, APPInfo.ImageUri);
      activity.startActivityForResult(intent, APPInfo.TAKE_PHOTO);
        APPInfo.iscommon=1;
    }

}
