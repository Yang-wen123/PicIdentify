package com.example.left;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Layout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private int id=0;
    //植物识别键值对
    private String plantname=null,plantscore=null,plantbaike_url=null,plantdescription=null;
    //动物识别键值对
    private String animalname=null,animalscore=null,animalbaike_url=null,animaldescription=null;
    //通用场景识别键值对
    private String generaldescription=null,generalresult_num=null,generalroot=null,generalimage_url=null,generalkeyword=null,generalbaike_url=null,generalscore=null;
    //车型识别键值对
    private String caryear=null,carbaike_url=null,cardescription=null,carname=null,carscore=null;
    //菜品识别键值对
    private String cookname=null,cookcalorie=null,cookhas_calorie=null,cookprobability=null,cookbaike_url=null,cookdescription=null;
    //logo商标识别键值对
    private String logoresult_num=null,logotype=null,logoprobability,logoname;
    //货币识别键值对
    public String coinhasdetail=null,coincurrencyCode=null,coincurrencyDenomination=null,coinname=null,coinyear=null;
    //触发器
    public Handler handler,handler1,handler2,handler3,handler4,handler5,handler6;
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();
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
                ReadPhoto();
            }
        });
        graph_fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePhoto();
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
                Generalidentify();
                //通用场景识别、匿名Handler子类
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(generalkeyword==null &&generalscore==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"识别到的场景数：" +generalresult_num,"称谓：" +generalroot ,"可能性：" + generalscore,
                                    "百科链接：" +generalbaike_url,"图片链接：" + generalimage_url, "介绍：" +generaldescription};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("通用场景识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };
                break;
            case 1:
                Plantidentify();
                //植物识别、匿名Handler子类
                handler1 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(plantname==null &&plantscore==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"植物名称：" + plantname, "可能性：" + plantscore,"百科链接：" +plantbaike_url,"介绍：" + plantdescription};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };
                break;
            case 2:
                Animalidentify();
                //动物识别、匿名Handler子类
                handler2 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(animalname==null &&animalscore==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"动物名称：" + animalname, "可能性：" + animalscore,"百科链接：" +animalbaike_url,
                                    "介绍：" + animaldescription};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };

                break;
            case 3:
                Caridentify();
                //车型识别、匿名Handler子类
                handler3 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(carname==null &&carscore==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"车辆名称：" + carname, "可能性：" + carscore,"年份：" +caryear,"百科链接：" +carbaike_url,"介绍：" + cardescription};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };
                break;
            case 4:
                Cookidentify();
                //车型识别、匿名Handler子类
                handler4 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(cookname==null &&cookprobability==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"菜品名称：" + cookname, "可能性：" + cookprobability,"是否含有卡路里：" +cookhas_calorie,
                                    "卡路里含量：" +cookcalorie,"百科链接：" +cookbaike_url,"介绍：" + cookdescription};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };
                break;
            case 5:
                Logoidentify();
                //logo商标识别、匿名Handler子类
                handler5 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(logoname==null &&logoprobability==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"商标数量：" + logoresult_num,"商标名称：" + logoname, "可能性：" + logoprobability,"种类：" +logotype};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };
                break;
            case 6:
                Coinidentify();
                //货币识别、匿名Handler子类
                handler6 = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //响应布局内容
                        if(coinname==null &&coinhasdetail==null){
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
                        }else {
                            String[] mitems1 = {"货币名称：" + coinname, "数量：" +coinhasdetail,"发行地：" +coincurrencyCode,
                                    "年份：" +coinyear,"价值：" +coincurrencyDenomination};
                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                            alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
                        }
                    }
                };
                break;
        }
    }
    public void Generalidentify() {
        if (APPInfo.bitmap==null) {
            Toast.makeText(this,  APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            //安卓系统不允许网络环境在主线程工作，新建子线程重写Run方法
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    //获取Acess_token
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY,APPInfo.SECRET_KEY);
                    // 可选：设置网络连接参数
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
                    //aipImageClassify.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
                    //aipImageClassify.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");//返回百科信息的结果数，默认不返回
                    //JSON解析数据
                    JSONObject res = aipImageClassify.advancedGeneral(content, options);
                    try {
                        //解析数组
                        generalresult_num=res.optString("result_num");
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        generalscore=jsonArray.optJSONObject(0).optString("score");
                        generalroot=jsonArray.optJSONObject(0).optString("root");
                        generaldescription=jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("description");
                        generalbaike_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("baike_url");
                        generalimage_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("image_url");
                        generalkeyword = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("keyword");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //子线程发送数据
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler.sendMessage(message);
                }
            }).start();
        }
    }
    public void Plantidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(this,  APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");
                    JSONObject res = aipImageClassify.plantDetect(content, options);
                    try {
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        plantname=jsonArray.optJSONObject(0).optString("name");
                        plantscore=jsonArray.optJSONObject(0).optString("score");
                        plantdescription=jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("description");
                        plantbaike_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("baike_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler1.sendMessage(message);
                }
            }).start();
        }
    }
    public void Animalidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(this,  APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");
                    JSONObject res = aipImageClassify.animalDetect(content, options);
                    try {
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        animalname=jsonArray.optJSONObject(0).optString("name");
                        animalscore=jsonArray.optJSONObject(0).optString("score");
                        animaldescription=jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("description");
                        animalbaike_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("baike_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler2.sendMessage(message);
                }
            }).start();
        }
    }

    public void Caridentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(this, APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");
                    JSONObject res = aipImageClassify.carDetect(content, options);
                    try {
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        carname=jsonArray.optJSONObject(0).optString("name");
                        carscore=jsonArray.optJSONObject(0).optString("score");
                        caryear=jsonArray.optJSONObject(0).optString("year");
                        cardescription=jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("description");
                        carbaike_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("baike_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler3.sendMessage(message);
                }
            }).start();
        }
    }
    public void Cookidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(this,  APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");
                    JSONObject res = aipImageClassify.dishDetect(content, options);
                    try {
                        //解析数组
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        cookname=jsonArray.optJSONObject(0).optString("name");
                        cookprobability=jsonArray.optJSONObject(0).optString("probability");
                        cookcalorie=jsonArray.optJSONObject(0).optString("calorie");
                        cookhas_calorie=jsonArray.optJSONObject(0).optString("has_calorie");
                        cookdescription=jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("description");
                        cookbaike_url = jsonArray.optJSONObject(0).getJSONObject("baike_info").optString("baike_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler4.sendMessage(message);
                }
            }).start();
        }
    }
    public void Logoidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(this, APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                    // 可选：设置网络连接参数
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");
                    JSONObject res = aipImageClassify.logoSearch(content, options);
                    try {
                        logoresult_num=res.optString("result_num");
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        logoname=jsonArray.optJSONObject(0).optString("name");
                        logoprobability=jsonArray.optJSONObject(0).optString("probability");
                        logotype=jsonArray.optJSONObject(0).optString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler5.sendMessage(message);
                }
            }).start();
        }
    }
    public void Coinidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(this, APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, APPInfo.Wait,Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    byte[] content = getBitmapByte.getBitmapByte(APPInfo.bitmap);
                    aipImageClassify = new AipImageClassify(APPInfo.APP_ID, APPInfo.API_KEY, APPInfo.SECRET_KEY);
                    aipImageClassify.setConnectionTimeoutInMillis(2000);
                    aipImageClassify.setSocketTimeoutInMillis(6000);
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("baike_num", "5");
                    JSONObject res = aipImageClassify.currency(content, options);
                    try {
                        JSONArray jsonArray = new JSONArray(res.optString("result"));
                        coinname=jsonArray.optJSONObject(0).optString("currencyName");
                        coinhasdetail=jsonArray.optJSONObject(0).optString("hasdetail");
                        coincurrencyCode=jsonArray.optJSONObject(0).optString("currencyCode");
                        coinyear=jsonArray.optJSONObject(0).optString("year");
                        coincurrencyDenomination=jsonArray.optJSONObject(0).optString("currencyDenomination");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int UPDATE_TEXT=1;
                    Message message = new Message();
                    message.what = UPDATE_TEXT;
                    handler6.sendMessage(message);
                }
            }).start();
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
                TakePhoto();
                dialog.dismiss();
                APPInfo.iscommon=0;
            }
        });
        APPInfo.pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadPhoto();
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

    public void ReadPhoto() {
        //获取动态权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,APPInfo.IMAGE_TYPE);
        startActivityForResult(intent,APPInfo.RESULT_IMAGE);
        APPInfo.iscommon=1;
    }
    //拍照
    public void TakePhoto() {
        //          获取本地时间，作为图片的名字
        SimpleDateFormat format = new SimpleDateFormat(APPInfo.imageyear);
        Date curDate = new Date(System.currentTimeMillis());
        APPInfo.str = format.format(curDate);
        //创建一个新的文件夹            //如果存在则删除，不存在则创建
        File outputImage = new File(getExternalCacheDir(), APPInfo.str+".jpg");
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
            APPInfo.ImageUri = FileProvider.getUriForFile(this,
                    "com.example.left.fileprovider", outputImage);
        } else {
            APPInfo.ImageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //传递保存路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, APPInfo.ImageUri);
        startActivityForResult(intent, APPInfo.TAKE_PHOTO);
        APPInfo.iscommon=1;
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
