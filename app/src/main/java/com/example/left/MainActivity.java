package com.example.left;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.baidu.aip.imageclassify.AipImageClassify;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int id=0;

    ReadAndTake readandtake =new ReadAndTake();
    GeneralIdentify generalIdentify=new GeneralIdentify(this);
    PlantIdentify plantIdentify=new PlantIdentify(this);
    AnimalIdentify animalIdentify=new AnimalIdentify(this);
    CarIdentify carIdentify=new CarIdentify(this);
    CookIdentify cookIdentify=new CookIdentify(this);
    LogoIdentify logoIdentify=new LogoIdentify(this);
    CoinIdentify coinIdentify=new CoinIdentify(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化菜单栏，新版按钮，侧边栏界面，侧边栏事件及设置监听
        APPInfo.bitmap=null;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initView() {
        FloatingActionButton gallery_fab = (FloatingActionButton) findViewById(R.id.gallery_fab);
        FloatingActionButton graph_fab = (FloatingActionButton) findViewById(R.id.graph_fab);
        FloatingActionButton identify_fab = (FloatingActionButton) findViewById(R.id.identify_fab);
        APPInfo.imageView= (ImageView) findViewById(R.id.initimage);
        APPInfo.textview=(TextView)findViewById(R.id.textview);
        gallery_fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                readandtake.ReadPhoto(MainActivity.this,MainActivity.this);
            }
        });
        graph_fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                readandtake.TakePhoto(MainActivity.this,MainActivity.this);
            }
        });
        identify_fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentifyPhoto(id);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void IdentifyPhoto(int id) {
        switch (id){
            case 0:
                generalIdentify.Generalidentify();
                break;
            case 1:
                plantIdentify.Plantidentify();
                break;
            case 2:
                animalIdentify.Animalidentify();
                break;
            case 3:
                carIdentify.Caridentify();
                break;
            case 4:
                cookIdentify.Cookidentify();
                break;
            case 5:
                logoIdentify.Logoidentify();
                break;
            case 6:
                coinIdentify.Coinidentify();
                break;
        }
    }

    //侧边栏
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //工具栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //工具栏事件监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Toast.makeText(this, "暂无设置选项", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Exit:
                finish();
                break;
            case R.id.introduction:
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                alertDialog1.setTitle(APPInfo.Use).setMessage(APPInfo.UseIntroduction).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //侧边栏事件监听
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.generalidentify:
                id=0;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.GeneralIden);
                APPInfo.textview.setTextColor(0xda5894af);
                APPInfo.imageView.setBackgroundResource(R.drawable.generallay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.plantidentify:
                id=1;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.PlantIden);
                APPInfo.textview.setTextColor(0xff669900);
                APPInfo.imageView.setBackgroundResource(R.drawable.plantlay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.animalidentify:
                id=2;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.AnimalIden);
                APPInfo.textview.setTextColor(0xffff8800);
                APPInfo.imageView.setBackgroundResource(R.drawable.animallay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.caridentify:
                id=3;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.CarIden);
                APPInfo.textview.setTextColor(0xffffbb33);
                APPInfo.imageView.setBackgroundResource(R.drawable.carlay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.cookidentify:
                id=4;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.CookIden);
                APPInfo.textview.setTextColor(0xf01a2014);
                APPInfo.imageView.setBackgroundResource(R.drawable.cooklay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.logoidentify:
                id=5;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.LogoIden);
                APPInfo.textview.setTextColor(0x76541230);
                APPInfo.imageView.setBackgroundResource(R.drawable.logolay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.coinidentify:
                id=6;APPInfo.iscommon=1;
                APPInfo.textview.setText(APPInfo.CoinIden);
                APPInfo.textview.setTextColor(0xff66aacc);
                APPInfo.imageView.setBackgroundResource(R.drawable.coinlay);
                if(APPInfo.bitmap!=null){
                APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.history:
                APPInfo.iscommon=1;
                Intent intent = new Intent(MainActivity.this,History.class);
                startActivity(intent);
                break;
            case R.id.collection:
                APPInfo.iscommon=1;
                Intent intent1 = new Intent(MainActivity.this,Collection.class);
                startActivity(intent1);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void headclick(View view){
        ShowDialog(view);
    }
    public void ShowDialog(View view){
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);        //初始化控件
        APPInfo.cam = (TextView) inflate.findViewById(R.id.cam);
        APPInfo.pic = (TextView) inflate.findViewById(R.id.pic);
        APPInfo.cancel = (TextView) inflate.findViewById(R.id.cancel);
        APPInfo.cam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                readandtake.TakePhoto(MainActivity.this,MainActivity.this);
                dialog.dismiss();
                APPInfo.iscommon=0;
            }
        });
        APPInfo.pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                readandtake.ReadPhoto(MainActivity.this,MainActivity.this);
                dialog.dismiss();
                APPInfo.iscommon=0;
            }
        });
        APPInfo.cancel.setOnClickListener(new OnClickListener() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==APPInfo.RESULT_IMAGE&&data!=null){
                if (data == null) {
                    Toast.makeText(this, APPInfo.Cancel1, Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    //相册
                    //通过获取当前应用的contentResolver对象来查询返回的data数据
                    Cursor cursor= getContentResolver().query(Objects.requireNonNull(data.getData()),null,null,null,null);
                    cursor.moveToFirst();//将cursor指针移动到数据首行
                    APPInfo.imagePath = cursor.getString(cursor.getColumnIndex("_data")); //获取字段名为_data的数据
                    cursor.close(); //销毁cursor对象，释放资源
                    if(APPInfo.bitmap!=null) {
                        Toast.makeText(this, APPInfo.Success, Toast.LENGTH_SHORT).show();
                    }
                    if(APPInfo.iscommon==1) {
                    APPInfo.bitmap= BitmapFactory.decodeFile(APPInfo.imagePath); //将图片路径所得图片类型转换成bitmap
                    }else if(APPInfo.iscommon==0) {
                    APPInfo.headbitmap= BitmapFactory.decodeFile(APPInfo.imagePath);
                    }
                }
            }else {
                //拍照
                if(APPInfo.iscommon==1) {
                    Toast.makeText(this, APPInfo.Success, Toast.LENGTH_SHORT).show();
                }else if(APPInfo.iscommon==0)
                    Toast.makeText(this, APPInfo.Successhead, Toast.LENGTH_SHORT).show();
                // 获取相机返回的数据，并转换为Bitmap图片格式
                try {
                    if(APPInfo.iscommon==1){
                    APPInfo.bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(APPInfo.ImageUri));
                        APPInfo.info = ""+APPInfo.ImageUri;
                        APPInfo.index = "2";
                    }else if(APPInfo.iscommon==0){
                    APPInfo.headbitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(APPInfo.ImageUri));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if(APPInfo.iscommon==0){
            APPInfo.headview= findViewById(R.id.headView);
            APPInfo.headview.setImageBitmap(APPInfo.headbitmap);
            APPInfo.iscommon=1;
            }else if(APPInfo.iscommon==1){
            APPInfo.imageView.setBackgroundColor(0x00000000);//清除背景
            APPInfo.imageView.setImageBitmap(APPInfo.bitmap);//显示图片
            }
        }
    }
}
