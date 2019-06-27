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
import com.beesocial.unijobs.api.RetrofitClient;
import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.ErrorResponse;
import com.beesocial.unijobs.models.ServiceResponse;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pd.chocobar.ChocoBar;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ImageView detailServiceImageView;
    private TextView detailServiceTitleTextView;
    private ImageButton detailServiceLikeImageView;
    private TextView detailServiceDescriptionTextView;
    private TextView detailServiceContactInfoTextView;

    private String servicoTitle, servicoDesc, servicoImgString, servicoId, servicoCreated;
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
        servicoId = getIntent().getStringExtra("service_id");
        servicoCreated = getIntent().getStringExtra("service_created");
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

        callBackend(servicoCreated);
    }

    private void callBackend(String id){
        Call<DefaultResponse> call2 = RetrofitClient.createInstance(1).getApi().getUserById(id);
        Call<DefaultResponse> call = RetrofitClient.getInstance(1).getApi().getUserById(id);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if(response.isSuccessful()) {
                    DefaultResponse responseUser = response.body();

                }
                else{
                    Gson gson = new Gson();
                    Type type = new TypeToken<ErrorResponse>() {
                    }.getType();
                    ErrorResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                    ChocoBar.builder().setActivity(ServiceDetailActivity.this)
                            .setText(errorResponse.getErr())
                            .setDuration(ChocoBar.LENGTH_LONG)
                            .setActionText(android.R.string.ok)
                            .red()
                            .show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                ChocoBar.builder().setActivity(ServiceDetailActivity.this)
                        .setText(t.getMessage())
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();
            }
        });
    }

}

