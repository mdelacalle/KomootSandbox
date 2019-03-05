package com.mdelacalle.komootsandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);


        ImageView errorImage = (ImageView) findViewById(R.id.error_image);
        Glide.with(ErrorActivity.this).load(R.drawable.error).into(errorImage);


    }
}
