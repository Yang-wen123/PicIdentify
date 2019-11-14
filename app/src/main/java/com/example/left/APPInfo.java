package com.example.left;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

public class APPInfo {
    //id、ak、sk在百度ai创建应用获取
    public static final String APP_ID = "17670498";
    public static final String API_KEY = "HeRRSgi5FLfF73m7FoGAyX3e";
    public static final String SECRET_KEY = "Ig9y7h7OLvyTTGGS91GPcg8tPZIhTv5M";

    //设置返回码：标识本地图库和摄像头
    public static final int RESULT_IMAGE=100;
    public static final int TAKE_PHOTO=2;
    //设置MIME码：表示image所有格式的文件均可
    public static final String IMAGE_TYPE="image/*";
    public static int daynight=1;       //标记日间/夜间模式,1为日间，0为夜间
    public static int netid=1;          //1有网，0没网

    //初始化属性
    public static Uri ImageUri;
    public static ImageView imageView;      //主界面 initimage
    public static String str;                               //图片名
    public static String imageyear="yyyy年MM月dd日HH:mm:ss";//图片保存命名格式
    public static ImageView headview;       //头像
    public static TextView textview;        //主界面text
    public static Bitmap bitmap;            //主界面bitmap
    public static Bitmap headbitmap;        //头像的bitmap
    public static String imagePath=null;    //图片路径
    public static TextView cam,pic,cancel;  //更换头像对话框的实例化对象
    public static int iscommon = 1;         //头像和主界面标签的识别码
    public static String index = "";		//	作为拍照还是相册的标识
    public static String info = "";		//	uri变成字符串
    public static TextView tv;

    //中文
    public static String Use="使用指南";
    public static String UseIntroduction = "此软件通过从本地读取图库和摄像头拍摄来对图片进行识别，" +
            "左侧菜单栏可滑动托出，其中有更多精彩内容哦！";
    public static String InterError="网络环境异常！！";
    public static String InterErrorDetail="网络连接超时，请检查您的网络设置！！！";
    public static String Please="请先添加图片";
    public static String Wait="识别中，请稍后……";
    public static String Success="图片获取成功";
    public static String Successhead="头像更换成功";
    public static String UnSuccesshead="您选择的图片路径不存在，头像更换失败";
    public static String Cancel1="未选择图片";
    public static String Cancel2="无内容";
    public static String PlantIden="植物识别";
    public static String AnimalIden="动物识别";
    public static String GeneralIden="通用识别";
    public static String CarIden="车型识别";
    public static String CookIden="菜品识别";
    public static String LogoIden="商标识别";
    public static String CoinIden="货币识别";
    public static String NeverUse="您还未曾使用过此软件";
    public static String Personal="此软件为个人版，暂不支持访问历史记录，点击返回即可退出该界面";
    public static String Personalcollection="此软件为个人版，暂不支持收藏图片，点击返回即可退出该界面";
    public static String isopen="网络已打开";
    public static String dialogtitle="识别结果";
    public static String dialogmsg="未知";
}
