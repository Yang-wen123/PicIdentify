package com.example.left;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView();
    }

    @Override
    protected int getContentView() {
        ts("ssss");
        return R.layout.activity_test;
    }

   /* @Override
    protected void TS(String msg) {
        super.TS(msg);
    }*/
}
