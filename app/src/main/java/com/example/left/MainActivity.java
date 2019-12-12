package com.example.left;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import com.example.left.Utils.CircleDrawable;
import com.example.left.Utils.GetBitmapByte;
import com.example.left.Utils.ReadAndTake;
import com.example.left.Utils.ShowDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "activity";
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private int id=0;
    private ShowDialog showDialog=new ShowDialog(this,this);
    private ReadAndTake readandtake =new ReadAndTake();
    private GetBitmapByte getBB=new GetBitmapByte();
    private Identify identify =new Identify(this);
    int Ra;
    private Toolbar toolbar;
    DBHelper mhelper;
    DBHelper.DBOpenHelper dbhelper;
    SQLiteDatabase db;
    private boolean clickormove = true; //点击或拖动，点击为true，拖动为false
    private boolean longormove = true;//长按或拖动，长按为true，拖动为false
    private int downX, downY; //按下时的X，Y坐标
    private boolean hasMeasured = false; //ViewTree是否已被测量过，是为true，否为false
    private View content; //界面的ViewTree
    private int screenWidth,screenHeight; //ViewTree的宽和高
    TranslateAnimation animation;
    FloatingActionButton collection_fab;
    int lastX, lastY; // 记录移动的最后的位置
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ///APPInfo.bitmap=null;
        initView();
    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initView() {
        mhelper=new DBHelper(this);
        dbhelper=new DBHelper.DBOpenHelper(this);
        db = dbhelper.getReadableDatabase();
        FloatingActionButton gallery_fab = (FloatingActionButton) findViewById(R.id.gallery_fab);
        FloatingActionButton graph_fab = (FloatingActionButton) findViewById(R.id.graph_fab);
        FloatingActionButton identify_fab = (FloatingActionButton) findViewById(R.id.identify_fab);
        collection_fab = (FloatingActionButton) findViewById(R.id.collection_fab);
        translate();
        //collection_fab = (FloatingActionButton) findViewById(R.id.collection_fab);
        APPInfo.imageView= (ImageView) findViewById(R.id.initimage);
        APPInfo.tv=findViewById(R.id.warning);
        APPInfo.tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                //判断手机系统的版本
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    //intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                    //intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    //intent = new Intent(android.provider.Settings.ACTION_APN_SETTINGS);
                    intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                startActivity(intent);
            }
        });
        APPInfo.mViewHorizontal=findViewById(R.id.line_horizontal);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter); //注册广播接收器

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

        initwindow();
        collection_fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ea = event.getAction();//获取事件类型
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下事件
                        clickormove=true;
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX = lastX;
                        downY = lastY;
                        break;
                    case MotionEvent.ACTION_MOVE: // 拖动事件
                        // 移动中动态设置位置
                        clickormove=false;
                        int x=(int) event.getRawX();
                        int y=(int) event.getRawY();
                        int dx = x - lastX;//位移量X
                        int dy = y - lastY;//位移量Y
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {
                            //left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            //right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 0) {
                            //top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > screenHeight) {
                            //bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        v.layout(left, top, right, bottom);//按钮重画
                        // 记录当前的位置
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        User u=new User(lastX,lastY);
                        mhelper.addLocation(u);
                        break;
                    case MotionEvent.ACTION_UP: // 弹起事件
                        //判断点击/长按/滑动
                        if (Math.abs((int) (event.getRawX() - downX)) > 5
                                || Math.abs((int) (event.getRawY() - downY)) > 5){
                            clickormove = false;
                            longormove=false;
                        } else{
                            longormove=true;
                            clickormove = true;
                        }
                        break;
                }
                return false;
            }
        });
        collection_fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(longormove){
                    Intent intent1 = new Intent(MainActivity.this,Collection.class);
                    startActivity(intent1);
                }
                return true;
            }
        });
        collection_fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickormove){
                    if(APPInfo.bitmap!=null){
                        SimpleDateFormat dateformat = new SimpleDateFormat(APPInfo.imageyear);
                        Date curDate = new Date(System.currentTimeMillis());
                        APPInfo.str = dateformat.format(curDate);
                        Toast.makeText(MainActivity.this,dateformat.format(curDate),Toast.LENGTH_SHORT).show();
                        String s=getBB.imageToBase64(APPInfo.bitmap);
                        //Log.d(TAG, "onClick: "+APPInfo.str);
                        /*Cursor cursor = db.rawQuery("select * from collection", null);
                        String B=cursor.getString(cursor.getColumnIndex("bm"));*/
                        User u=new User(s,APPInfo.str);
                        mhelper.addBm(u);
                    }else{
                        Toast.makeText(MainActivity.this,"请添加图片",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        initdraw();
        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.TABLE_NAME)){
            @SuppressLint("Recycle") Cursor c=db.rawQuery("select * from user where _id=(select max(_id) from user)",null);
            int i=0;
            if (i == 0)  {
                //确定游标位置
                c.moveToFirst();
                i++;
            }
            else {
                c.moveToNext();
            }
            try {
            /*Toast.makeText(MainActivity.this, "已读取", Toast.LENGTH_SHORT).show();
            Log.d(TAG, c.getString(c.getColumnIndex("imageres")));*/
                String b1=c.getString(c.getColumnIndex("imageres"));
                Bitmap bb=getBB.base64ToImage(b1);
                Drawable dj= new BitmapDrawable(bb);
                CircleDrawable circleDrawable = new CircleDrawable(dj, MainActivity.this, 55);
                toolbar.setNavigationIcon(circleDrawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Resources resources = MainActivity.this.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.generallay);
            CircleDrawable circleDrawable = new CircleDrawable(drawable, MainActivity.this, 55);
            toolbar.setNavigationIcon(circleDrawable);
        }
    }
    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //初始化侧边栏
    public void initdraw(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch (id){
            case 0:
                toolbar.setTitle("通用识别");
                break;
            case 1:
                toolbar.setTitle("植物识别");
                break;
            case 2:
                toolbar.setTitle("动物识别");
                break;
            case 3:
                toolbar.setTitle("车型识别");
                break;
            case 4:
                toolbar.setTitle("菜品识别");
                break;
            case 5:
                toolbar.setTitle("商标识别");
                break;
            case 6:
                toolbar.setTitle("货币识别");
                break;
        }
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayUseLogoEnabled(false);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                initinfomation(drawerView);
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                drawer.openDrawer(GravityCompat.START);
                initinfomation(view);
            }
        });
        disableNavigationViewScrollbars(navigationView);
    }
    //侧边栏控件
    public void initinfomation(final View view){
        APPInfo.headview=findViewById(R.id.headView);
        APPInfo.nickname=findViewById(R.id.nickname);
        APPInfo.signature=findViewById(R.id.signature);
        APPInfo.signature.setMovementMethod(ScrollingMovementMethod.getInstance());
        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.NICKNAME_TABLE)||DBHelper.DBOpenHelper.HaveData(db,DBHelper.TABLE_NAME)){
            @SuppressLint("Recycle") Cursor c=db.rawQuery("select * from user where _id=(select max(_id) from user)",null);
            @SuppressLint("Recycle") Cursor c1=db.rawQuery("select * from nicknametable where _id=(select max(_id) from nicknametable)",null);
            int i=0;
            if (i == 0)  {
                //确定游标位置
                c.moveToFirst();
                c1.moveToFirst();
                i++;
            }
            else {
                c.moveToNext();
                c1.moveToNext();
            }
            try {
            /*Toast.makeText(MainActivity.this, "已读取", Toast.LENGTH_SHORT).show();
            Log.d(TAG, c.getString(c.getColumnIndex("imageres")));*/
                if(c.getString(c.getColumnIndex("imageres"))!=null){
                    String b1=c.getString(c.getColumnIndex("imageres"));
                    APPInfo.headview.setImageBitmap(getBB.base64ToImage(b1));
                }else{

                }
            } catch (Exception e) {
                e.printStackTrace();
            }try {
                if(c1.getString(c1.getColumnIndex("nickname"))!=null){
                    String b2=c1.getString(c1.getColumnIndex("nickname"));
                    Log.d(TAG, b2);
                    APPInfo.nickname.setText(b2);
                }else{

                }
            } catch (Exception e) {
                e.printStackTrace();
            }try {
                if(c1.getString(c1.getColumnIndex("signature"))!=null){
                    String b3=c1.getString(c1.getColumnIndex("signature"));
                    Log.d(TAG, b3);
                    APPInfo.signature.setText(b3);
                }else{

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //更换头像
        APPInfo.headview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.ShowHeadDialog(view);
            }
        });
        //更换昵称
        APPInfo.nickname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.ShowinfoDialog(view);
            }
        });
        //更换个性签名
        APPInfo.signature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.ShowinfoDialog(view);
            }
        });
    }
   /* @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }*/

    //定义一个判断网络的广播
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, APPInfo.isopen,Toast.LENGTH_SHORT).show();
                APPInfo.tv.setVisibility(View.GONE);
                APPInfo.netid=1;
            }
            else {
                APPInfo.tv.setVisibility(View.VISIBLE);

                APPInfo.netid=0;
            }
        }
    }
    //注销广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        unregisterReceiver(networkChangeReceiver);
//        Log.d(TAG, "onDestroy: ");
    }
    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
    //调整收藏按钮位置
    public void translate(){
        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.COLLECTION_LOCATION)){
            @SuppressLint("Recycle") Cursor cursor=db.rawQuery("select * from location where _id=(select max(_id) from location)",null);
            int k=0;
            if (k == 0)  {
                cursor.moveToFirst();
                k++;
            }
            else {
                cursor.moveToNext();
            }
            try {
                lastX=Integer.parseInt(cursor.getString(cursor.getColumnIndex("lastx")));
                lastY=Integer.parseInt(cursor.getString(cursor.getColumnIndex("lasty")));
                collection_fab.setX(lastX-150);
                collection_fab.setY(lastY-225);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    //初始化屏幕属性
    private void initwindow() {
        content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);//获取界面的ViewTree根节点View
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取显示屏属性
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        ViewTreeObserver vto = content.getViewTreeObserver();//获取ViewTree的监听器
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // TODO Auto-generated method stub
                if(!hasMeasured)
                {
                    screenHeight = content.getMeasuredHeight();//获取ViewTree的高度
                    hasMeasured = true;//设置为true，使其不再被测量。
                }
                return true;//如果返回false，界面将为空。
            }
        });
    }

    //随机数
    public void radom(){
        Random random=new Random();
        Ra=random.nextInt((10-5)+5);
    }
    //识别模块
    @SuppressLint("HandlerLeak")
    private void IdentifyPhoto(int id) {
        switch (id){
            case 0:
                identify.Generalidentify();
                break;
            case 1:
                identify.Plantidentify();
                break;
            case 2:
                identify.Animalidentify();
                break;
            case 3:
                identify.Caridentify();
                break;
            case 4:
                identify.Cookidentify();
                break;
            case 5:
                identify.Logoidentify();
                break;
            case 6:
                identify.Coinidentify();
                break;
        }
    }
    //返回和退出
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //创建工具栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //工具栏事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                if(APPInfo.daynight==1){
                    APPInfo.daynight =0;
                    setEnableNightMode(true);//日间-->夜间
                }else {
                    APPInfo.daynight =1;
                    setEnableNightMode(false);//夜间-->日间
                }
                break;
            case R.id.Exit:
                finish();//退出
                break;
            case R.id.introduction:
                //使用手册
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                alertDialog1.setTitle(APPInfo.Use).setMessage(APPInfo.UseIntroduction).create().show();
                break;
            case R.id.live_identify:
                //实时识别模式
                Intent intent=new Intent(MainActivity.this, LiveIdentifyActivity.class);
                startActivity(intent);
                break;
            case R.id.hist:
                Intent intent1 = new Intent(MainActivity.this,History.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //侧边栏事件监听
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.generalidentify:
                id=0;
                initdraw();
                APPInfo.iscommon=1;//标记摄像头和识别按钮
                APPInfo.imageView.setBackgroundResource(R.drawable.generallay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.plantidentify:
                id=1;APPInfo.iscommon=1;
                initdraw();
                APPInfo.imageView.setBackgroundResource(R.drawable.plantlay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.animalidentify:
                id=2;APPInfo.iscommon=1;
                initdraw();
                APPInfo.imageView.setBackgroundResource(R.drawable.animallay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.caridentify:
                id=3;APPInfo.iscommon=1;
                initdraw();
                APPInfo.imageView.setBackgroundResource(R.drawable.carlay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.cookidentify:
                id=4;APPInfo.iscommon=1;
                initdraw();
                APPInfo.imageView.setBackgroundResource(R.drawable.cooklay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.logoidentify:
                id=5;APPInfo.iscommon=1;
                initdraw();
                APPInfo.imageView.setBackgroundResource(R.drawable.logolay);
                if(APPInfo.bitmap!=null){
                    APPInfo.imageView.setBackgroundColor(0x00000000);
                }
                break;
            case R.id.coinidentify:
                id=6;APPInfo.iscommon=1;
                initdraw();
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
                    //查询data数据
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
                // 获取相机返回的数据，并转换为Bitmap图片格式
                try {
                    if(APPInfo.iscommon==1){
                        APPInfo.bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(APPInfo.ImageUri));
                       /* APPInfo.info = ""+APPInfo.ImageUri;
                        APPInfo.index = "2";*/
                    }else if(APPInfo.iscommon==0){
                        APPInfo.headbitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(APPInfo.ImageUri));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if(APPInfo.iscommon==0){
                Toast.makeText(this, APPInfo.Successhead, Toast.LENGTH_SHORT).show();
                APPInfo.headview= findViewById(R.id.headView);
                APPInfo.headview.setImageBitmap(APPInfo.headbitmap);
                APPInfo.iscommon=1;
                Drawable d= new BitmapDrawable(APPInfo.headbitmap);
                CircleDrawable circleDrawable = new CircleDrawable(d, MainActivity.this, 55);
                Toolbar toolbar=findViewById(R.id.toolbar);
                toolbar.setNavigationIcon(circleDrawable);//绘制圆形图片
                User u=new User(getBB.imageToBase64(APPInfo.headbitmap));
                mhelper.addImage(u);
            }else if(APPInfo.iscommon==1){
                Toast.makeText(this, APPInfo.Success, Toast.LENGTH_SHORT).show();
                APPInfo.imageView.setBackgroundColor(0x00000000);//清除背景
                APPInfo.imageView.setImageBitmap(APPInfo.bitmap);//显示图片
                //Log.d(TAG, "onActivityResult: "+APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight());
                //判断图片大小是否能存入数据库
                if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()>1200000){
                    Toast.makeText(this, "暂不支持存放大图，更换小图即可收藏", Toast.LENGTH_SHORT).show();
                    collection_fab.setVisibility(View.GONE);
                }
                if(APPInfo.bitmap.getWidth()*APPInfo.bitmap.getHeight()<1200000){
                    collection_fab.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
