package com.example.left.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.left.APPInfo;
import com.example.left.DBHelper;
import com.example.left.R;
import com.example.left.User;
import com.example.left.Utils.ReadAndTake;
import org.w3c.dom.Text;

public class ShowDialog {
    private final Activity activity;
    ReadAndTake readandtake =new ReadAndTake();
    Context context;
    DBHelper mhelper;
    DBHelper.DBOpenHelper dbhelper;
    SQLiteDatabase db;
    View inflate,v0;
    EditText nickname,signature,rename;
    Button ensure,cancel,clear,ensure1,cancel1,clear1;
    public ShowDialog(Context context, Activity activity){
        this.context=context;
        this.activity=activity;
    }
    public void initlayout(){
        //数据库
        mhelper=new DBHelper(context);
        dbhelper=new DBHelper.DBOpenHelper(context);
        db = dbhelper.getReadableDatabase();
        inflate = LayoutInflater.from(context).inflate(R.layout.infodialog_layout, null);
        //头像昵称签名
        nickname=inflate.findViewById(R.id.usernickname);
        signature=inflate.findViewById(R.id.usersignature);
        ensure=inflate.findViewById(R.id.ensure);
        cancel=inflate.findViewById(R.id.cancel);
        clear=inflate.findViewById(R.id.clear);
        //历史记录|收藏的布局
        v0 = LayoutInflater.from(context).inflate(R.layout.rename_dialog, null);
        rename=v0.findViewById(R.id.rename);
        ensure1=v0.findViewById(R.id.ensure1);
        cancel1=v0.findViewById(R.id.cancel1);
        clear1=v0.findViewById(R.id.clear1);
    }
    public void ShowHeadDialog(View view){
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        //填充对话框的布局
        View inflate2 = LayoutInflater.from(context).inflate(R.layout.headdialog_layout, null);
        //初始化控件
        APPInfo.cam = (TextView) inflate2.findViewById(R.id.cam);
        APPInfo.pic = (TextView) inflate2.findViewById(R.id.pic);
        APPInfo.cancel = (TextView) inflate2.findViewById(R.id.cancel);
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
    public void ShowinfoDialog(View view){
        initlayout();
        final Dialog dialog = new Dialog(context);
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nickname.getText().length()>=0&&nickname.getText().length()<=8&&signature.getText().length()>=0&&signature.getText().length()<=45){
                    User u=new User(nickname.getText().toString(),signature.getText().toString());
                    mhelper.addNickname(u);
                    APPInfo.nickname.setText(nickname.getText());
                    APPInfo.signature.setText(signature.getText());
                    dialog.dismiss();
                }
                if(nickname.getText().length()>=0&&nickname.getText().length()<=8){
                    if(APPInfo.signature.getText().length()>0){
                        String s=APPInfo.signature.getText().toString();
                        User u=new User(nickname.getText().toString(),s);
                        mhelper.addNickname(u);
                        APPInfo.nickname.setText(nickname.getText());
                        APPInfo.signature.setText(s);
                        dialog.dismiss();
                    }else{
                        User u=new User(nickname.getText().toString(),signature.getText().toString());
                        mhelper.addNickname(u);
                        APPInfo.nickname.setText(nickname.getText());
                        APPInfo.signature.setText(signature.getText());
                        dialog.dismiss();
                    }
                }else if(signature.getText().length()>=0&&signature.getText().length()<=45){
                    if(APPInfo.nickname.getText().toString().length()>0){
                        String a=APPInfo.nickname.getText().toString();
                        User u=new User(a,signature.getText().toString());
                        mhelper.addNickname(u);
                        APPInfo.nickname.setText(a);
                        APPInfo.signature.setText(signature.getText());
                        dialog.dismiss();
                    }else{
                        User u=new User(nickname.getText().toString(),signature.getText().toString());
                        mhelper.addNickname(u);
                        APPInfo.nickname.setText(nickname.getText());
                        APPInfo.signature.setText(signature.getText());
                        dialog.dismiss();
                    }
                }else if(nickname.getText().length()>8){
                    Toast.makeText(context, "昵称过长，请删减", Toast.LENGTH_SHORT).show();
                }else if(signature.getText().length()>50){
                    Toast.makeText(context, "签名过长，请删减", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname.setText("");
                signature.setText("");
            }
        });
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 1000;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
    public void ShowCollectionDialog(View view){
        final Dialog dialog = new Dialog(context);
        initlayout();
        ensure1.setOnClickListener(new View.OnClickListener() {
            private static final String TAG ="" ;
            @Override
            public void onClick(View v) {
                if(APPInfo.layoutdialog){
                    User u=new User(APPInfo.toppos,rename.getText().toString());
                    //Log.d(TAG, APPInfo.pos+"onClick: "+APPInfo.toppos);
                    mhelper.updateBm(u);
                    APPInfo.texttop.setText("文件名:"+rename.getText());
                    if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.TOP)){
                        User uer=new User(1,APPInfo.imagetop,rename.getText().toString(),APPInfo.toppos);
                        mhelper.updatetop(uer);
                    }
                    Log.d(TAG, APPInfo.toppos+"onClick: "+rename.getText());
                }else {
                    User u=new User(APPInfo.pos,rename.getText().toString());
                    //Log.d(TAG, APPInfo.pos+"onClick: "+APPInfo.toppos);
                    mhelper.updateBm(u);
                    if(APPInfo.pos==APPInfo.toppos){
                        APPInfo.texttop.setText("文件名:"+rename.getText());
                        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.TOP)){
                            User uer=new User(1,APPInfo.imagetop,rename.getText().toString(),APPInfo.toppos);
                            mhelper.updatetop(uer);
                        }
                        Log.d(TAG, "onClick: "+rename.getText());
                    }

                }

                dialog.dismiss();
            }
        });
        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename.setText("");
            }
        });
        dialog.setContentView(v0);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 1000;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    public void ShowHistoryDialog(View view) {
        final Dialog dialog = new Dialog(context);
        initlayout();
        ensure1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    User u=new User(Integer.parseInt(APPInfo.historyid),rename.getText().toString());
                    mhelper.UpdateHistory(u);
                dialog.dismiss();
            }
        });
        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename.setText("");
            }
        });
        dialog.setContentView(v0);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 1000;
        dialogWindow.setAttributes(lp);
        dialog.show();
    }
}
