package com.example.veasnahan.locatepublic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


public class ProductItemActivity extends AppCompatActivity {
    private View parent_view;
    private RecyclerView recyclerView;
    private AdapterGridTwoLine mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        String s = getIntent().getStringExtra("itemname");
        Log.i ( "itemm", s );

        setContentView ( R.layout.activity_grid_two_line );

    }

}
