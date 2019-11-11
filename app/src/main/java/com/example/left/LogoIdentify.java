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

public class LogoIdentify {
    //logo商标识别键值对
    private String logoresult_num=null,logotype=null,logoprobability,logoname;
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();
    Context context;
    public LogoIdentify(Context context){
        this.context=context;
    }
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
            if(logoname==null &&logoprobability==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }else {
                String[] mitems1 = {"商标数量：" + logoresult_num,"商标名称：" + logoname, "可能性：" + logoprobability,"种类：" +logotype};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
            }
        }
    };
}
