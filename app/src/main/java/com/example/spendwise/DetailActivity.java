package com.example.spendwise;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;


import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

public class DetailActivity extends AppCompatActivity {

    private MaterialTextView detail_name;
    private AppCompatImageView detail_IMG;
    private MaterialTextView detail_date;
    private String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViews();
        initViews();

    }

    private void findViews() {
        detail_name = findViewById(R.id.detail_name);
        detail_IMG = findViewById(R.id.detail_IMG);
        detail_date = findViewById(R.id.detail_date);

    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            detail_date.setText(bundle.getString("Date"));
            detail_name.setText(bundle.getString("Category"));
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detail_IMG);

        }
    }
}