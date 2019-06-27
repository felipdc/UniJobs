package com.beesocial.unijobs.activities;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;

public class ServiceDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView detailServiceImageView;
    private TextView detailServiceTitleTextView;
    private ImageButton detailServiceLikeImageView;
    private TextView detailServiceDescriptionTextView;
    private TextView detailServiceContactInfoTextView;

    private String servicoTitle, servicoDesc, servicoImgString, facebookLink, phone;
    private Bitmap servicoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        toolbar = findViewById(R.id.description_toolbar);
        toolbar.setTitle("Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailServiceImageView = findViewById(R.id.detail_service_image_view);
        detailServiceTitleTextView = findViewById(R.id.detail_service_title_text_view);
        detailServiceLikeImageView = findViewById(R.id.detail_service_like_button);
        detailServiceDescriptionTextView = findViewById(R.id.detail_service_description_text_view);
        detailServiceContactInfoTextView = findViewById(R.id.detail_service_contact_info);

        servicoTitle = getIntent().getStringExtra("service_title");
        servicoDesc = getIntent().getStringExtra("service_desc");
        servicoImgString = SharedPrefManager.getInstance(this).getId();
        if (servicoImgString != null && servicoImgString != "-1") {
            try {
                final String pureBase64Encoded = servicoImgString.substring(servicoImgString.indexOf(",") + 1);
                final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);

                Glide.with(this).load(decodedBytes).centerCrop().into(detailServiceImageView);
            }catch (Exception e){
                Glide.with(this).load(R.drawable.ic_info).centerCrop().into(detailServiceImageView);
            }
            //detailServiceImageView.setImageBitmap(bitmap);
        }
        detailServiceTitleTextView.setText(servicoTitle);
        detailServiceDescriptionTextView.setText(servicoDesc);

        String contact = facebookLink + '\n' + phone;
        detailServiceContactInfoTextView.setText(contact);
    }

}

