package com.example.left.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.left.R;
import com.example.left.Utils.GetBitmapByte;

public class HistoryAdapter extends CursorAdapter {
    private LayoutInflater layoutInflater;
    private GetBitmapByte getBB = new GetBitmapByte();

    public HistoryAdapter(Context context, Cursor c) {
        super(context, c);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.collection_listview, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        try {
            String B = cursor.getString(cursor.getColumnIndex("image"));
            String filename = cursor.getString(cursor.getColumnIndex("historyname"));
            TextView tv = view.findViewById(R.id.collection_tv);
            tv.setText("名称：" + filename);
            ImageView image = view.findViewById(R.id.collection_image);
            Bitmap b = getBB.base64ToImage(B);
            image.setImageBitmap(b);
            //Log.d(TAG, B+"bindView: "+filename);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
        }

    }
}