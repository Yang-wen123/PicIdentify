package com.example.left;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.left.Adapter.CollectionAdapter;
import com.example.left.Adapter.HistoryAdapter;
import com.example.left.Utils.GetBitmapByte;
import com.example.left.Utils.ShowDialog;

import java.lang.reflect.Field;
import java.util.Locale;


public class History extends BaseActivity implements TextToSpeech.OnInitListener{
    private static final String TAG = "history";
    DBHelper mhelper;
    DBHelper.DBOpenHelper dbhelper;
    SQLiteDatabase db;
    Cursor cursor;
    ListView lv;
    HistoryAdapter historyAdapter;
    String filename,score,baikeurl,imageurl,description;
    private TextToSpeech tts;
    private GetBitmapByte getBB=new GetBitmapByte();
    private ShowDialog showDialog=new ShowDialog(this,this);
    Identify identify=new Identify(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initview();
    }

    private void initview() {
        mhelper=new DBHelper(this);
        dbhelper=new DBHelper.DBOpenHelper(this);
        db = dbhelper.getReadableDatabase();
        tts = new TextToSpeech(History.this.getApplicationContext(),this);
        lv=findViewById(R.id.hislist);
        ShowLv();
    }

    private void ShowLv() {
        if(DBHelper.DBOpenHelper.HaveData(db,DBHelper.HISTORY)){
            cursor = db.rawQuery("select * from history", null);
            try{
                historyAdapter = new HistoryAdapter(this, cursor);
                historyAdapter.notifyDataSetChanged();
                lv.setAdapter(historyAdapter);
                lv.setSelector(R.color.colorAccent);
                clicklv();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(History.this,"历史记录出错啦，请退出重试",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(History.this,"暂无历史记录，快去识别图片吧--->",Toast.LENGTH_SHORT).show();
        }
    }

    private void clicklv() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //把cursor移动到指定行
                cursor.moveToPosition(position);
                filename=cursor.getString(cursor.getColumnIndex("historyname"));
                score=cursor.getString(cursor.getColumnIndex("score"));
                baikeurl=cursor.getString(cursor.getColumnIndex("baikeurl"));
                imageurl=cursor.getString(cursor.getColumnIndex("imageurl"));
                description=cursor.getString(cursor.getColumnIndex("description"));
                AlertDialog.Builder dialog = new AlertDialog.Builder(History.this);
                dialog.setTitle("信息")
                        .setMessage("名称："+filename+"\n可能性："+score+"\n百科链接："+baikeurl+"\n图片链接："+imageurl+"\n介绍："+description)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                canCloseDialog(dialog,true);
                            }
                        });
                dialog.create().show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, final long id) {
                cursor.moveToPosition(position);
                APPInfo.historyid=cursor.getString(cursor.getColumnIndex("_id"));
                filename=cursor.getString(cursor.getColumnIndex("historyname"));
                score=cursor.getString(cursor.getColumnIndex("score"));
                baikeurl=cursor.getString(cursor.getColumnIndex("baikeurl"));
                imageurl=cursor.getString(cursor.getColumnIndex("imageurl"));
                description=cursor.getString(cursor.getColumnIndex("description"));
                AlertDialog.Builder dialog = new AlertDialog.Builder(History.this);
                final String[] item={"查看详情","分享","重命名","删除"};
                dialog.setTitle("请选择操作")
                        .setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (item[which].equals("查看详情")) {
                                    AlertDialog.Builder dialogz = new AlertDialog.Builder(History.this);
                                    final String[] item1={"名称："+filename,"可能性："+score,"百科链接"+baikeurl,"图片链接"+imageurl,"介绍："+description};
                                    dialogz.setTitle("信息")
                                           .setItems(item1, new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialog, int which) {
                                                   if (item1[which].equals("名称："+filename)) {
                                                       // 条件不成立不能关闭 AlertDialog 窗口
                                                       try {
                                                           tts.speak(filename, TextToSpeech.QUEUE_FLUSH, null);
                                                           canCloseDialog(dialog, false);
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                   }else if (item1[which].equals("可能性："+score)) {
                                                       // 条件不成立不能关闭 AlertDialog 窗口
                                                       try {
                                                           tts.speak("可能性："+score, TextToSpeech.QUEUE_FLUSH, null);
                                                           canCloseDialog(dialog, false);
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                   }
                                                   else if (item1[which].equals("百科链接"+baikeurl)) {
                                                       // 条件不成立不能关闭 AlertDialog 窗口
                                                       try {
                                                           canCloseDialog(dialog, false);
                                                           Uri uri=Uri.parse(baikeurl);
                                                           Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                                                           startActivity(intent);
                                                           identify.stopTTS();
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                   }else if (item1[which].equals("图片链接"+imageurl)) {
                                                       // 条件不成立不能关闭 AlertDialog 窗口
                                                       try {
                                                           canCloseDialog(dialog, false);
                                                           Uri uri=Uri.parse(imageurl);
                                                           Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                                                           startActivity(intent);
                                                           identify.stopTTS();
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                   }else if (item1[which].equals("介绍："+description)) {
                                                       // 条件不成立不能关闭 AlertDialog 窗口
                                                       try {
                                                           tts.speak(description, TextToSpeech.QUEUE_FLUSH, null);
                                                           canCloseDialog(dialog, false);
                                                       } catch (Exception e) {
                                                           e.printStackTrace();
                                                       }
                                                   }
                                               }
                                           })
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    identify.stopTTS();
                                                    canCloseDialog(dialog,true);
                                                }
                                            });
                                    dialogz.create().show();
                                    canCloseDialog(dialog,false);
                                }  else if (item[which].equals("分享")) {
                                    canCloseDialog(dialog, false);
                                    Intent intent=new Intent(Intent.ACTION_SEND);
                                    intent.setType("text/plain")
                                            .putExtra(Intent.EXTRA_TEXT,"快来看看我的图片识别结果吧-->"+imageurl);
                                    startActivity(intent);
                                }  else if (item[which].equals("重命名")) {
                                    showDialog.ShowHistoryDialog(view);
                                    canCloseDialog(dialog, true);
                                    ShowLv();
                                }else if (item[which].equals("删除")) {
                                    mhelper.deleteHistory(Integer.parseInt(APPInfo.historyid));
                                    canCloseDialog(dialog, true);
                                    ShowLv();
                                } else {
                                    canCloseDialog(dialog, false);
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
        return R.layout.activity_history;
    }

    @Override
    public void onInit(int status) {

    }

}
