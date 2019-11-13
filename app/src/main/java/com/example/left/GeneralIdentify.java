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

public class GeneralIdentify {

    Context context;
    public GeneralIdentify(Context context){
        this.context=context;
    }
    //需要lib文件
    public AipImageClassify aipImageClassify;
    public JSONObject json;
    //bitmap转二进制
    private GetBitmapByte getBitmapByte = new GetBitmapByte();
    //通用场景识别键值对
    private String generaldescription=null,generalresult_num=null,generalroot=null,generalimage_url=null,generalkeyword=null,generalbaike_url=null,generalscore=null;
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
            if(generalkeyword==null &&generalscore==null){
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle(APPInfo.InterError).setMessage(APPInfo.InterErrorDetail).create().show();
            }else {
                String[] mitems1 = {"识别到的场景数：" +generalresult_num,"称谓：" +generalroot ,"可能性：" + generalscore,
                        "百科链接：" +generalbaike_url,"图片链接：" + generalimage_url, "介绍：" +generaldescription};
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);
                alertDialog1.setTitle("通用场景识别结果").setItems(mitems1, null).create().show();
            }
        }
    };
}
