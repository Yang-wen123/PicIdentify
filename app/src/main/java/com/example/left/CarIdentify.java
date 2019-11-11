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

public class CarIdentify {
    //车型识别键值对
    private String caryear=null,carbaike_url=null,cardescription=null,carname=null,carscore=null;
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();
    Context context;
    public CarIdentify(Context context){
        this.context=context;
    }
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
            if(carname==null &&carscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }else {
                String[] mitems1 = {"车辆名称：" + carname, "可能性：" + carscore,"年份：" +caryear,"百科链接：" +carbaike_url,"介绍：" + cardescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle("识别结果").setItems(mitems1, null).create().show();
            }
        }
    };
}
