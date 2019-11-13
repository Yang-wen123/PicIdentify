package com.example.left;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.TextView;

public class ShowDialog {
    private final Activity activity;
    ReadAndTake readandtake =new ReadAndTake();
    Context context;
    public ShowDialog(Context context, Activity activity){
        this.context=context;
        this.activity=activity;
    }
    public void ShowDialog(View view){
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);        //初始化控件
        APPInfo.cam = (TextView) inflate.findViewById(R.id.cam);
        APPInfo.pic = (TextView) inflate.findViewById(R.id.pic);
        APPInfo.cancel = (TextView) inflate.findViewById(R.id.cancel);
        APPInfo.cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readandtake.TakePhoto(context,activity);
                dialog.dismiss();
                APPInfo.iscommon=0;
            }
        });
        APPInfo.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readandtake.ReadPhoto(context,activity);
                dialog.dismiss();
                APPInfo.iscommon=0;
            }
        });
        APPInfo.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 100;//设置Dialog距离底部的距离//将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
}
