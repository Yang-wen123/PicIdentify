package com.example.left;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class Collection extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Toast.makeText(this,APPInfo.Personalcollection,Toast.LENGTH_SHORT).show();

    }
}
