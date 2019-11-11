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

public class CoinIdentify {
    //货币识别键值对
    public String coinhasdetail=null,coincurrencyCode=null,coincurrencyDenomination=null,coinname=null,coinyear=null;
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();
    Context context;
    public CoinIdentify(Context context){
        this.context=context;
    }
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
            if(coinname==null &&coinhasdetail==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle( APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }else {
                String[] mitems1 = {"货币名称：" + coinname, "数量：" +coinhasdetail,"发行地：" +coincurrencyCode,
                        "年份：" +coinyear,"价值：" +coincurrencyDenomination};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
            }
        }
    };
}
