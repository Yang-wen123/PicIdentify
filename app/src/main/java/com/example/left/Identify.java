package com.example.left;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import com.baidu.aip.imageclassify.AipImageClassify;
import com.example.left.Utils.GetBitmapByte;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

public class Identify implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    DBHelper mhelper;
    DBHelper.DBOpenHelper dbhelper;
    SQLiteDatabase db;
    User user;
    Context context;
    public Identify(Context context){
        this.context=context;
    }
    //需要lib文件
    public AipImageClassify aipImageClassify;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();

    //通用场景识别键值对
    private String generaldescription=null,generalresult_num=null,generalroot=null,generalimage_url=null,generalkeyword=null,generalbaike_url=null,generalscore=null;
    //植物识别键值对
    private String plantname=null,plantscore=null,plantbaike_url=null,plantdescription=null;
    //动物识别键值对
    private String animalname=null,animalscore=null,animalbaike_url=null,animaldescription=null;
    //车型识别键值对
    private String caryear=null,carbaike_url=null,cardescription=null,carname=null,carscore=null;
    //菜品识别键值对
    private String cookname=null,cookcalorie=null,cookhas_calorie=null,cookprobability=null,cookbaike_url=null,cookdescription=null;
    //logo商标识别键值对
    private String logoresult_num=null,logotype=null,logoprobability,logoname;
    //货币识别键值对
    public String coinhasdetail=null,coincurrencyCode=null,coincurrencyDenomination=null,coinname=null,coinyear=null;
    public void initsql(){
        mhelper=new DBHelper(context);
        dbhelper=new DBHelper.DBOpenHelper(context);
        db = dbhelper.getReadableDatabase();
    }
    public void Generalidentify() {
        tts = new TextToSpeech(context.getApplicationContext(),this);
        if (APPInfo.bitmap==null) {
            Toast.makeText(context,  APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            //安卓系统不允许网络环境在主线程工作，新建子线程重写Run方法
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
                        }
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }*/
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
                    options.put("baike_num", "5");//返回百科信息的结果数，默认0不返回
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
    //通用场景识别、匿名Handler子类
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if (APPInfo.netid == 1) {
            if(generalkeyword==null &&generalscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                generaldialog();
            }
        } else if (APPInfo.netid == 0) {
            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
        }
        }
    };
    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Plantidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context,  APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
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
    //植物识别、匿名Handler子类
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if (APPInfo.netid ==1 ) {
            if(plantname==null &&plantscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                plantdialog();
                /*String[] mitems1 = {"植物名称：" + plantname, "可能性：" + plantscore,"百科链接：" +plantbaike_url,"介绍：" + plantdescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();*/
            }
        } else if (APPInfo.netid == 0) {
            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
        }
        }
    };
    public void Animalidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context,  APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
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
    //动物识别、匿名Handler子类
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if (APPInfo.netid == 1) {
                if (animalname == null && animalscore == null) {
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
                } else {
                    animaldialog();
                    /*String[] mitems1 = {"动物名称：" + animalname, "可能性：" + animalscore, "百科链接：" + animalbaike_url,
                            "介绍：" + animaldescription};
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();*/
                }
            } else if (APPInfo.netid == 0) {
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }
        }
    };
    public void Caridentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context, APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
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
    //车型识别、匿名Handler子类
    Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if(APPInfo.netid==1){
            if(carname==null &&carscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                cardialog();
                /*String[] mitems1 = {"车辆名称：" + carname, "可能性：" + carscore,"年份：" +caryear,"百科链接：" +carbaike_url,"介绍：" + cardescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();*/
            }
        }else if(APPInfo.netid==0){
            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
            alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
        }
        }
    };
    public void Cookidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context,  APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
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
    //车型识别、匿名Handler子类
    Handler handler4 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if(APPInfo.netid==1){
            if(cookname==null &&cookprobability==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                cookdialog();
               /* String[] mitems1 = {"菜品名称：" + cookname, "可能性：" + cookprobability,"是否含有卡路里：" +cookhas_calorie,
                        "卡路里含量：" +cookcalorie,"百科链接：" +cookbaike_url,"介绍：" + cookdescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();*/
            }
        }else if(APPInfo.netid==0){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }
        }
    };
    public void Logoidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context, APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
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
    //logo商标识别、匿名Handler子类
    Handler handler5 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if(APPInfo.netid==1){
            if(logoname==null &&logoprobability==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                logodialog();
               /* String[] mitems1 = {"商标数量：" + logoresult_num,"商标名称：" + logoname, "可能性：" + logoprobability,"种类：" +logotype};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();*/
            }
            }else if(APPInfo.netid==0){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }
        }
    };
    public void Coinidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context, APPInfo.Please,Toast.LENGTH_SHORT).show();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
        } else {
            Toast.makeText(context, APPInfo.Wait,Toast.LENGTH_SHORT).show();
            scanning();
            APPInfo.mViewHorizontal.setAnimation( APPInfo.mAnimation);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(APPInfo.imagePath!=null) {
                            //在这里提取缓冲流赋值为bitmap
                            APPInfo.bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(APPInfo.imagePath)));
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
    //货币识别、匿名Handler子类
    Handler handler6 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            APPInfo.mViewHorizontal.clearAnimation();
            APPInfo.mViewHorizontal.setVisibility(View.GONE);
            if(APPInfo.netid==1){
                if(coinname==null &&coinhasdetail==null){
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
                }else {
                    coindialog();
                }
            }else if(APPInfo.netid==0){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }
        }
    };
    private void generaldialog(){
        initsql();
        final String[] mitems1 = {"称谓：" +generalroot ,"可能性：" + generalscore,
                "百科链接：" +generalbaike_url,"图片链接：" + generalimage_url, "介绍(可播放)：" +generaldescription};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,generalroot,generalscore,generalbaike_url,generalimage_url,generaldescription);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        canCloseDialog(dialog, true);
                        stopTTS();
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("百科链接：" +generalbaike_url)) {
                    // 条件不成立不能关闭 AlertDialog 窗口
                    try {
                        canCloseDialog(dialog, false);
                        Uri uri=Uri.parse(generalbaike_url);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        context.startActivity(intent);
                        stopTTS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mitems1[which].equals("图片链接：" + generalimage_url)) {
                    // 条件不成立不能关闭 AlertDialog 窗口
                    try {
                        Uri uri=Uri.parse(generalimage_url);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        context.startActivity(intent);
                        canCloseDialog(dialog, false);
                        stopTTS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mitems1[which].equals("可能性：" + generalscore)) {
                    canCloseDialog(dialog, false);
                    stopTTS();
                }
                else if (mitems1[which].equals("称谓：" + generalroot)) {
                    canCloseDialog(dialog, false);
                    stopTTS();
                }else if (mitems1[which].equals("介绍(可播放)：" +generaldescription)) {
                    tts.speak(generaldescription, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }
                else {
                    stopTTS();
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }

    private void plantdialog(){
        final String[] mitems1 = {"植物名称：" + plantname, "可能性：" + plantscore,"百科链接：" +plantbaike_url,"介绍：" + plantdescription};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,plantname,plantscore,plantbaike_url,null,plantdescription);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        canCloseDialog(dialog, true);
                        stopTTS();
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("百科链接：" +plantbaike_url)) {
                    // 条件不成立不能关闭 AlertDialog 窗口
                    try {
                        canCloseDialog(dialog, false);
                        Uri uri=Uri.parse(plantbaike_url);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        context.startActivity(intent);
                        stopTTS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }  else if (mitems1[which].equals("植物名称：" + plantname)) {
                    stopTTS();
                    canCloseDialog(dialog, false);
                }else if (mitems1[which].equals("可能性：" + plantscore)) {
                    stopTTS();
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("介绍：" +plantdescription)) {
                    tts.speak(plantdescription, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }
                else {
                    stopTTS();
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }
    private void animaldialog(){
        final String[] mitems1 = {"动物名称：" + animalname, "可能性：" + animalscore, "百科链接：" + animalbaike_url,
                "介绍：" + animaldescription};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,animalname,animalscore,animalbaike_url,null,animaldescription);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        canCloseDialog(dialog, true);
                        stopTTS();
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("百科链接：" +animalbaike_url)) {
                    // 条件不成立不能关闭 AlertDialog 窗口
                    try {
                        canCloseDialog(dialog, false);
                        Uri uri=Uri.parse(animalbaike_url);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        context.startActivity(intent);
                        stopTTS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mitems1[which].equals("动物名称：" + animalname)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("可能性：" + animalscore)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("介绍：" +animaldescription)) {
                    tts.speak(animaldescription, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                } else {
                    stopTTS();
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }
    private void cardialog(){
        final String[] mitems1 = {"车辆名称：" + carname, "可能性：" + carscore,"年份：" +caryear,"百科链接：" +carbaike_url,
                "介绍：" + cardescription};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,carname,carscore,carbaike_url,null,cardescription);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        canCloseDialog(dialog, true);
                        stopTTS();
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("百科链接：" +carbaike_url)) {
                    // 条件不成立不能关闭 AlertDialog 窗口
                    try {
                        canCloseDialog(dialog, false);
                        Uri uri=Uri.parse(carbaike_url);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        context.startActivity(intent);
                        stopTTS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mitems1[which].equals("车辆名称：" + carname)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("可能性：" + carscore)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("介绍：" +animaldescription)) {
                    tts.speak(animaldescription, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }else if (mitems1[which].equals("年份：" +caryear)) {
                    tts.speak(caryear, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                } else {
                    stopTTS();
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }
    private void cookdialog(){
        final String[] mitems1 = {"菜品名称：" + cookname, "可能性：" + cookprobability,"是否含有卡路里：" +cookhas_calorie,
                "卡路里含量：" +cookcalorie,"百科链接：" +cookbaike_url,"介绍：" + cookdescription};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,cookname,cookprobability,cookbaike_url,null,cookdescription);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        stopTTS();
                        canCloseDialog(dialog, true);
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("百科链接：" +cookbaike_url)) {
                    // 条件不成立不能关闭 AlertDialog 窗口
                    try {
                        canCloseDialog(dialog, false);
                        Uri uri=Uri.parse(cookbaike_url);
                        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mitems1[which].equals("菜品名称：" + cookname)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("可能性：" + cookprobability)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("卡路里含量：" +cookcalorie)) {
                    tts.speak(cookcalorie, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }else if (mitems1[which].equals("是否含有卡路里：" +cookhas_calorie)) {
                    tts.speak("是否含有卡路里：" +cookhas_calorie, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("介绍：" + cookdescription)) {
                    tts.speak(cookdescription, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }else {
                    stopTTS();
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }
    private void logodialog(){
        final String[] mitems1 = {"商标数量：" + logoresult_num,"商标名称：" + logoname, "可能性：" + logoprobability,"种类：" +logotype};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,logoname,logoprobability,null,null,logotype);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        stopTTS();
                        canCloseDialog(dialog, true);
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("商标数量：" + logoresult_num)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("商标名称：" + logoname)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("可能性：" + logoprobability)) {
                    tts.speak("可能性：" + logoprobability, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }else if (mitems1[which].equals("种类：" +logotype)) {
                    tts.speak("种类：" +logotype, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                } else {
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }
    private void coindialog(){
        final String[] mitems1 = {"货币名称：" + coinname, "数量：" +coinhasdetail,"发行地：" +coincurrencyCode,
                "年份：" +coinyear,"价值：" +coincurrencyDenomination};
        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
        alertDialog1.setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                            String b=getBitmapByte.imageToBase64(APPInfo.bitmap);
                            user=new User(b,coinname,coinhasdetail,null,null,coincurrencyDenomination);
                            mhelper.addHistory(user);
                        }else{
                            Toast.makeText(context,"图片过大，暂时不支持保存",Toast.LENGTH_SHORT).show();
                        }
                        stopTTS();
                        canCloseDialog(dialog, true);
                    }
                })
                .setTitle(APPInfo.dialogtitle).setItems(mitems1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mitems1[which].equals("价值：" +coincurrencyDenomination)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("货币名称：" + coinname)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("数量：" +coinhasdetail)) {
                    canCloseDialog(dialog, false);
                } else if (mitems1[which].equals("发行地：" +coincurrencyCode)) {
                    tts.speak(animaldescription, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                }else if (mitems1[which].equals("年份：" +coinyear)) {
                    tts.speak(caryear, TextToSpeech.QUEUE_FLUSH, null);
                    canCloseDialog(dialog, false);
                } else {
                    stopTTS();
                    canCloseDialog(dialog, true);
                }
            }
        });
        alertDialog1.create().show();
    }
    //线条扫描动画
    public static void scanning() {
        APPInfo.mViewHorizontal.setVisibility(View.VISIBLE);
        APPInfo.mAnimation = new TranslateAnimation(0, 0, 0, APPInfo.imageView.getHeight()+300);
        APPInfo.mAnimation.start();
        APPInfo.mAnimation.setDuration(500);
        APPInfo.mAnimation.setRepeatCount(-1);
        APPInfo.mAnimation.setRepeatMode(Animation.RESTART);
        APPInfo.mAnimation.setInterpolator(new LinearInterpolator());
        APPInfo.mAnimation.setFillAfter(true);
        APPInfo.mAnimation.setFillBefore(true);

    }
    @Override
    public void onInit(int status) {
        // 判断是否转化成功
        if (status == TextToSpeech.SUCCESS){
            //默认设定语言为中文，原生的android貌似不支持中文。
            int result = tts.setLanguage(Locale.CHINESE);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(context, "播放失败", Toast.LENGTH_SHORT).show();
            }else{
                //不支持中文就将语言设置为英文
                tts.setLanguage(Locale.US);
            }
        }
    }
    //关闭语言播放
    public void stopTTS() {
        if ( tts  != null) {
            tts .shutdown();
            tts .stop();
            tts = null;
        }
    }
}
