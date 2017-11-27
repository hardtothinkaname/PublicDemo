package com.alibaba.idst.nlsdemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.idst.R;


public class MainActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void AsrDemo(View view){
        Intent intent = new Intent(this, PublicRealtimeActivity.class);
        startActivity(intent);
    }

}