package com.android.mapsampleapplication.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.mapsampleapplication.R;
import com.android.mapsampleapplication.utils.Utils;

public class HomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        if (savedInstanceState == null) {
            Utils.makeFragmentTransaction(this,R.id.fragment,new MapFragment());
        }
    }
}
