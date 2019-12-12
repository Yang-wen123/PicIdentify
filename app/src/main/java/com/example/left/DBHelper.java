package com.example.left;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    public static final String DB_name="database.db";
    public static final String TABLE_NAME="user";
    public static final String NICKNAME_TABLE="nicknametable";
    public static final String COLLECTION_LOCATION="location";
    public static final String COLLECTION="collection";
    public static final String HISTORY="history";
    public static final String TOP="top";
    public static final int db_version=1;
    public static final String ID="_id";
    public static final String NICKNAME="nickname";
    public static final String SIGNATURE="signature";
    public static final String ImageRes="imageres";
    public static final String LASTX="lastx";
    public static final String LASTY="lasty";
    private static final String TAG ="btn" ;
    public static final String BITMAP="bm";
    public static final String FILEName="filename";
    public static final String TOPPOS="toppos";
    public static final String IMAGE="image";
    public static final String HISTORYNAME="historyname";
    public static final String SCORE="score";
    public static final String BAIKEURL="baikeurl";
    public static final String IMAGEURL="imageurl";
    public static final String DESCRIPTION="description";
    private DBOpenHelper helper;
    public SQLiteDatabase db;
    User uesr=new User();
    Context context;

    public static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, DB_name, null, db_version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // 建表
            String sql = "CREATE TABLE " + TABLE_NAME + "(" + ID + " integer primary key autoincrement, " +
                     ImageRes + " varchar " +")";
            db.execSQL(sql);
            String sqlnickname = "CREATE TABLE " + NICKNAME_TABLE + "(" + ID + " integer primary key autoincrement, " +
                    NICKNAME + " varchar, " +SIGNATURE + " varchar " +")";
            db.execSQL(sqlnickname);
            String sqllocation = "CREATE TABLE " + COLLECTION_LOCATION + "(" + ID + " integer primary key autoincrement, " +
                    LASTX + " int, " +LASTY + " int " +")";
            db.execSQL(sqllocation);
            String sqlcollection = "CREATE TABLE " + COLLECTION + "(" + ID + " integer primary key autoincrement, " +
                    BITMAP + " varchar, " + FILEName + " varchar " +")";
            db.execSQL(sqlcollection);
            String sqltop = "CREATE TABLE " + TOP + "(" + ID + " integer primary key autoincrement, " +
                    BITMAP + " varchar, " + FILEName + " varchar, "+ TOPPOS + " int " +")";
            db.execSQL(sqltop);
            String sqlhistory = "CREATE TABLE " + HISTORY + "(" + ID + " integer primary key autoincrement, " +
                    IMAGE + " varchar, " + HISTORYNAME + " varchar, "+ SCORE + " varchar, "+ BAIKEURL + " varchar, " + IMAGEURL + " varchar, "+ DESCRIPTION + " varchar "+")";
            db.execSQL(sqlhistory);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
            db.execSQL("drop table if exists " + NICKNAME_TABLE);
            onCreate(db);
            db.execSQL("drop table if exists " + COLLECTION_LOCATION);
            onCreate(db);
            db.execSQL("drop table if exists " + COLLECTION);
            onCreate(db);
            db.execSQL("drop table if exists " + TOP);
            onCreate(db);
            db.execSQL("drop table if exists " + HISTORY);
            onCreate(db);
        }
        public static boolean HaveData(SQLiteDatabase db,String tablename){
            Cursor cursor;
            boolean a=false;
            cursor = db.rawQuery("select name from sqlite_master where type='table' ", null);
            while(cursor.moveToNext()){
                //遍历出表名
                String name = cursor.getString(0);
                if(name.equals(tablename)) {
                    a=true;
                }
            }if(a) {
                cursor=db.query(tablename,null,null,null,null,null,null);
                //检查是不是空表
                if(cursor.getCount()>0)
                    return true;
                else
                    return false;
            } else
                return false;
        }
    }
    public DBHelper(Context context){
        this.context=context;
        this.helper=new DBOpenHelper(context);
        this.db=this.helper.getReadableDatabase();//获得可写的数据库
    }
    public void addImage(User user){
        ContentValues values=new ContentValues();
        values.put(ImageRes, user.getImageresouce());
        db.insert(TABLE_NAME, null, values);
        Toast.makeText(context, "更换成功", Toast.LENGTH_SHORT).show();
    }
    public void addBm(User user){
        ContentValues values=new ContentValues();
        values.put(BITMAP, user.getNickname());
        values.put(FILEName, user.getSignature());
        db.insert(COLLECTION, null, values);
        Log.d(TAG, user.getX()+"addBm: "+  user.getSignature());
        Toast.makeText(context, "图片已保存", Toast.LENGTH_SHORT).show();
    }
    //添加历史记录
    public void addHistory(User user){
        ContentValues values=new ContentValues();
        values.put(IMAGE, user.getNickname());
        values.put(HISTORYNAME, user.getSignature());
        values.put(SCORE, user.getFilename());
        values.put(BAIKEURL, user.getImageresouce());
        values.put(IMAGEURL, user.getImage());
        values.put(DESCRIPTION, user.getDescription());
        db.insert(HISTORY, null, values);
        Log.d(TAG, user.getX()+"addBm: "+  user.getSignature());
        Toast.makeText(context, "信息已保存至历史记录，可前往查看", Toast.LENGTH_SHORT).show();
    }
    //删除历史记录
    public void deleteHistory(int id) {
        db.delete(HISTORY,ID + "=?",new String[]{ id + ""});
        Log.d(TAG, "deleteUser: "+id);
        Toast.makeText(context, "已删除，若界面无反应请点击刷新", Toast.LENGTH_SHORT).show();
    }
    //重命名
    public void UpdateHistory(User user){
        ContentValues values=new ContentValues();
        values.put(ID, user.getId());
        values.put(HISTORYNAME, user.getSignature());
        db.update(HISTORY,values,ID + "=?",new String[]{user.getId() + ""});
        Log.d(TAG, user.getId()+"addBm: "+  user.getSignature());
        Toast.makeText(context, "更改成功，若界面无反应请点击刷新", Toast.LENGTH_SHORT).show();
    }
    //置顶内容
    public void addTop(User user){
        ContentValues values=new ContentValues();
        values.put(BITMAP, user.getNickname());
        values.put(FILEName, user.getSignature());
        values.put(TOPPOS,user.getX());
        Log.d(TAG, user.getSignature()+"addTop: "+APPInfo.toppos);
        db.insert(TOP, null, values);
        Toast.makeText(context, "已置顶", Toast.LENGTH_SHORT).show();
    }
    //置顶
    public void updatetop(User user){
        ContentValues values=new ContentValues();
        values.put(ID, user.getId());
        values.put(BITMAP, user.getNickname());
        values.put(FILEName, user.getSignature());
        values.put(TOPPOS,user.getX());
        Log.d(TAG, user.getId()+"updateBm: "+user.getSignature());
        db.update(TOP,values,ID + "=?",new String[]{user.getId() + ""});
    }
    //修改收藏
    public void updateBm(User user){
        ContentValues values=new ContentValues();
        values.put(ID, user.getId());
        values.put(FILEName, user.getSignature());
        Log.d(TAG, user.getId()+"updateBm: "+user.getSignature());
        Toast.makeText(context, "更改成功，若界面无反应请点击刷新", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "updateBm: "+user.getId());
        db.update(COLLECTION,values,ID + "=?",new String[]{user.getId() + ""});
    }
    //删除收藏
    public void deleteUser(int id){
        db.delete(COLLECTION,ID + "=?",new String[]{ id + ""});
        Log.d(TAG, "deleteUser: "+id);
        Toast.makeText(context, "已删除，若界面无反应请点击刷新", Toast.LENGTH_SHORT).show();
    }

    //昵称和个性签名
    public void addNickname(User user){
        ContentValues values=new ContentValues();
        values.put(NICKNAME, user.getNickname());
        values.put(SIGNATURE, user.getSignature());
        db.insert(NICKNAME_TABLE, null, values);
    }
    //按钮位置
    public void addLocation(User user){
        ContentValues values=new ContentValues();
        values.put(LASTX, user.getX());
        values.put(LASTY, user.getY());
        /*Toast.makeText(context, "轨迹已记录", Toast.LENGTH_SHORT).show();*/
        Log.d(TAG, user.getX()+"\n"+user.getY());
        db.insert(COLLECTION_LOCATION, null, values);
    }

}
