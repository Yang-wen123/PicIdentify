package com.example.left;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.LongDef;
import com.example.left.Adapter.CollectionAdapter;
import com.example.left.Utils.GetBitmapByte;
import com.example.left.Utils.ShowDialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collection extends BaseActivity {
    private static final String TAG ="collection" ;
    Cursor cursor;
    DBHelper mhelper;
    DBHelper.DBOpenHelper dbhelper;
    SQLiteDatabase db;
    ListView lv;
    CollectionAdapter adapter;
    String name;
    LinearLayout layout;
    ImageView imagetop;

    private GetBitmapByte getBB=new GetBitmapByte();
    private ShowDialog showDialog=new ShowDialog(this,this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initview();
    }

    private void initview() {
        mhelper=new DBHelper(this);
        dbhelper=new DBHelper.DBOpenHelper(this);
        db = dbhelper.getReadableDatabase();
        layout=findViewById(R.id.toplayout);
        imagetop=findViewById(R.id.topimage);
        APPInfo.texttop=findViewById(R.id.toptext);
        lv=findViewById(R.id.collist);
        ShowLv();
    }
    //以listview形式展示收藏记录
    private void ShowLv() {
        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.COLLECTION)){
            cursor = db.rawQuery("select * from collection", null);
            try{
                adapter = new CollectionAdapter(this, cursor);
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
                lv.setSelector(R.color.colorAccent);
                clicklv();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            Toast.makeText(Collection.this,"暂无收藏记录，快去收藏图片吧--->",Toast.LENGTH_SHORT).show();
        }
        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.TOP)){
            Cursor c=db.rawQuery("select * from top where _id ="+1, null);
            int i=0;
            if (i == 0)  {
                //确定游标位置
                c.moveToFirst();
            }
            else {
                c.moveToNext();
            }
            final String name=c.getString(c.getColumnIndex("filename"));
            final String B=c.getString(c.getColumnIndex("bm"));
            final String P=c.getString(c.getColumnIndex("toppos"));
            APPInfo.toppos=Integer.parseInt(P);
            Log.d(TAG, P+"initview: ");
            if(APPInfo.toppos==0){
                layout.setVisibility(View.GONE);
            }else {
                layout.setVisibility(View.VISIBLE);
                APPInfo.texttop.setText("文件名:"+name);
                APPInfo.imagetop=B;
                imagetop.setImageBitmap(getBB.base64ToImage(B));
                APPInfo.texttop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Collection.this);
                        final String[] item={"分享","查看","重命名","取消置顶"};
                        dialog.setTitle("请选择操作")
                                .setItems(item, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (item[which].equals("分享")) {
                                            canCloseDialog(dialog, false);
                                            Intent intent=new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain")
                                                    .putExtra(Intent.EXTRA_TEXT,name);
                                            startActivity(intent);
                                        } else if (item[which].equals("查看")) {
                                            final AlertDialog.Builder dialogz = new AlertDialog.Builder(Collection.this);
                                            dialogz.setTitle("信息")
                                                    .setMessage(name+"\n所在行"+APPInfo.toppos)
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            canCloseDialog(dialog, true);
                                                        }
                                                    });
                                            dialogz.create().show();
                                            canCloseDialog(dialog, false);
                                        }else if (item[which].equals("重命名")) {
                                            APPInfo.layoutdialog=true;
                                            showDialog.ShowCollectionDialog(v);
                                            ShowLv();
                                            canCloseDialog(dialog, true);
                                        }else if (item[which].equals("取消置顶")) {
                                            layout.setVisibility(View.GONE);
                                            APPInfo.toppos=0;
                                            User u=new User(1,B,name,APPInfo.toppos);
                                            mhelper.updatetop(u);
                                            canCloseDialog(dialog, true);
                                        } else {
                                            canCloseDialog(dialog, true);
                                        }
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        canCloseDialog(dialog, true);
                                    }
                                });
                        dialog.create().show();
                    }
                });
            }

        }else{
            layout.setVisibility(View.GONE);
        }
    }

    private void clicklv() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把cursor移动到指定行
                cursor.moveToPosition(position);
                name=cursor.getString(cursor.getColumnIndex("filename"));
                AlertDialog.Builder dialog = new AlertDialog.Builder(Collection.this);
                dialog.setTitle("信息")
                        .setMessage(name)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.create().show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                cursor.moveToPosition(position);
                name=cursor.getString(cursor.getColumnIndex("filename"));
                APPInfo.collectionid=cursor.getString(cursor.getColumnIndex("_id"));
                Log.d(TAG, "onItemLongClick: "+APPInfo.collectionid);
                APPInfo.pos=Integer.parseInt(APPInfo.collectionid);
                final String B=cursor.getString(cursor.getColumnIndex("bm"));
                AlertDialog.Builder dialog = new AlertDialog.Builder(Collection.this);
                final String[] item={"分享","置顶","重命名","删除"};
                dialog.setTitle("请选择操作")
                        .setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (item[which].equals("分享")) {
                                    canCloseDialog(dialog, false);
                                    Intent intent=new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain")
                                            .putExtra(Intent.EXTRA_TEXT,name);
                                    startActivity(intent);
                                } else if (item[which].equals("置顶")) {
                                    layout.setVisibility(View.VISIBLE);
                                    APPInfo.texttop.setText("文件名："+name);
                                    imagetop.setImageBitmap(getBB.base64ToImage(B));
                                    APPInfo.toppos=Integer.parseInt(APPInfo.collectionid);
                                    if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.TOP)){
                                        User u=new User(1,B,name,APPInfo.toppos);
                                        mhelper.updatetop(u);
                                    }else{
                                        User u=new User(B,name,APPInfo.toppos);
                                        mhelper.addTop(u);
                                    }
                                    ShowLv();
                                    Log.d(TAG, "onClick: "+APPInfo.toppos);
                                    canCloseDialog(dialog, true);
                                } else if (item[which].equals("重命名")) {
                                    APPInfo.layoutdialog=false;
                                    Log.d(TAG, APPInfo.toppos+"onClick: "+APPInfo.pos);
                                    showDialog.ShowCollectionDialog(view);
                                    canCloseDialog(dialog, true);
                                    ShowLv();
                                }else if (item[which].equals("删除")) {
                                    if(APPInfo.pos==APPInfo.toppos){
                                        AlertDialog.Builder dialogx = new AlertDialog.Builder(Collection.this);
                                        dialogx.setTitle("警告")
                                                .setMessage("当前项为指定内容，若要删除请点击确定按钮")
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        canCloseDialog(dialog,true);
                                                    }
                                                })
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        APPInfo.toppos=0;
                                                        User u=new User(1,B,name,APPInfo.toppos);
                                                        mhelper.updatetop(u);
                                                        Log.d(TAG, APPInfo.pos+"onClick2: "+APPInfo.toppos);
                                                        ShowLv();
                                                        canCloseDialog(dialog,true);
                                                    }
                                                });
                                        dialogx.create().show();
                                    }else{
                                        mhelper.deleteUser(Integer.parseInt(APPInfo.collectionid));
                                        canCloseDialog(dialog, true);
                                        ShowLv();
                                        Log.d(TAG, APPInfo.pos+"onClick: "+APPInfo.toppos);
                                    }
                                } else {
                                    canCloseDialog(dialog, true);
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                canCloseDialog(dialog, true);
                            }
                        });
                dialog.create().show();
                return true;
            }
        });
    }
    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //创建工具栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection, menu);
        return true;
    }
    //工具栏事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                ShowLv();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected int getContentView() {
        return R.layout.activity_collection;
    }
}
