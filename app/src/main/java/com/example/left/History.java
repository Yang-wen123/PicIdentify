package com.example.left;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.Bundle;
import android.widget.*;


public class History extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toast.makeText(this,APPInfo.Personal,Toast.LENGTH_SHORT).show();

    }
}
