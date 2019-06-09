package com.beesocial.unijobs.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beesocial.unijobs.R;

public class ServiceDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView detailServiceImageView;
    private TextView detailServiceTitleTextView;
    private ImageButton detailServiceLikeImageView;
    private TextView detailServiceDescriptionTextView;
    private TextView detailServiceContactInfoTextView;

    private String servico;

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

        servico = getIntent().getStringExtra("service_title");

        detailServiceTitleTextView.setText(servico);
    }
}