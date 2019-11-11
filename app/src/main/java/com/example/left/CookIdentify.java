package com.example.left;

import android.app.AlertDialog;
import android.content.Context;
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

public class CookIdentify {
    //菜品识别键值对
    private String cookname=null,cookcalorie=null,cookhas_calorie=null,cookprobability=null,cookbaike_url=null,cookdescription=null;
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();
    Context context;
    public CookIdentify(Context context){
        this.context=context;
    }
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
            if(cookname==null &&cookprobability==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }else {
                String[] mitems1 = {"菜品名称：" + cookname, "可能性：" + cookprobability,"是否含有卡路里：" +cookhas_calorie,
                        "卡路里含量：" +cookcalorie,"百科链接：" +cookbaike_url,"介绍：" + cookdescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
            }
        }
    };
}
