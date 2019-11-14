package com.example.left;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.baidu.aip.imageclassify.AipImageClassify;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;

public class Identify {

    Context context;
    public Identify(Context context){
        this.context=context;
    }
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
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

    public void Generalidentify() {
        if (APPInfo.bitmap==null) {
            Toast.makeText(context,  APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
            //安卓系统不允许网络环境在主线程工作，新建子线程重写Run方法
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
    //通用场景识别、匿名Handler子类
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //响应布局内容
            if (APPInfo.netid == 1) {
            if(generalkeyword==null &&generalscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                String[] mitems1 = {"识别到的场景数：" +generalresult_num,"称谓：" +generalroot ,"可能性：" + generalscore,
                        "百科链接：" +generalbaike_url,"图片链接：" + generalimage_url, "介绍：" +generaldescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
            }
        } else if (APPInfo.netid == 0) {
            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
            alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
        }
        }
    };
    public void Plantidentify() {
        if (APPInfo.bitmap == null) {
            Toast.makeText(context,  APPInfo.Please,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
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
            if (APPInfo.netid ==1 ) {
            if(plantname==null &&plantscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                String[] mitems1 = {"植物名称：" + plantname, "可能性：" + plantscore,"百科链接：" +plantbaike_url,"介绍：" + plantdescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
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
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
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
            if (APPInfo.netid == 1) {
                if (animalname == null && animalscore == null) {
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
                } else {
                    String[] mitems1 = {"动物名称：" + animalname, "可能性：" + animalscore, "百科链接：" + animalbaike_url,
                            "介绍：" + animaldescription};
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
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
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
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
            if(APPInfo.netid==1){
            if(carname==null &&carscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                String[] mitems1 = {"车辆名称：" + carname, "可能性：" + carscore,"年份：" +caryear,"百科链接：" +carbaike_url,"介绍：" + cardescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
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
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
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
            if(APPInfo.netid==1){
            if(cookname==null &&cookprobability==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                String[] mitems1 = {"菜品名称：" + cookname, "可能性：" + cookprobability,"是否含有卡路里：" +cookhas_calorie,
                        "卡路里含量：" +cookcalorie,"百科链接：" +cookbaike_url,"介绍：" + cookdescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
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
        } else {
            Toast.makeText(context,  APPInfo.Wait,Toast.LENGTH_SHORT).show();
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
            if(APPInfo.netid==1){
            if(logoname==null &&logoprobability==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
            }else {
                String[] mitems1 = {"商标数量：" + logoresult_num,"商标名称：" + logoname, "可能性：" + logoprobability,"种类：" +logotype};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
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
        } else {
            Toast.makeText(context, APPInfo.Wait,Toast.LENGTH_SHORT).show();
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
            if(APPInfo.netid==1){
                if(coinname==null &&coinhasdetail==null){
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setMessage(APPInfo.dialogmsg).create().show();
                }else {
                    String[] mitems1 = {"货币名称：" + coinname, "数量：" +coinhasdetail,"发行地：" +coincurrencyCode,
                        "年份：" +coinyear,"价值：" +coincurrencyDenomination};
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                    alertDialog1.setTitle(APPInfo.dialogtitle).setItems(mitems1, null).create().show();
                }
            }else if(APPInfo.netid==0){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }
        }
    };
}
