package com.beesocial.unijobs.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.api.Api;
import com.beesocial.unijobs.models.ServiceRegister;
import com.beesocial.unijobs.models.ServiceResponse;
import com.beesocial.unijobs.models.User;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView registerServiceImageView;
    private RadioGroup registerServiceRadioGroup;
    private EditText registerServiceTitulo;
    private EditText registerServiceDescricao;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    String encodedImage = null;
    User user, userComplete;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        toolbar = findViewById(R.id.edit_service_toolbar);
        button = findViewById(R.id.edit_service_buttonRegisterService);
        toolbar.setTitle("Editar Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerServiceImageView = findViewById(R.id.edit_service_imageViewService);
        registerServiceRadioGroup = findViewById(R.id.edit_service_radioGroup);
        registerServiceTitulo = findViewById(R.id.edit_service_editTextTitulo);
        registerServiceDescricao = findViewById(R.id.edit_service_editTextDescricao);
    }

    private void serviceModify(View v) {
        String titulo = registerServiceTitulo.getText().toString().trim();
        String desc = registerServiceDescricao.getText().toString().trim();
        String isOffer;
        int id = registerServiceRadioGroup.getCheckedRadioButtonId();

        if (id == 2131296483) {
            isOffer = "false";
        } else {
            isOffer = "true";
        }
        //falta o ID do servico e checar se o int id ta certo
        callBackend(v, titulo, desc, isOffer, "");
    }

    private void callBackend(final View v, String titulo, String desc, String isOffer, String id) {
        ServiceRegister service = new ServiceRegister(titulo, isOffer, encodedImage, desc);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://unijobs-service.now.sh/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        Api client = retrofit.create(Api.class);
        Call<List<ServiceResponse>> calltargetResponse = client.updateService("Bearer " + user.getToken(), service);
        calltargetResponse.enqueue(new Callback<List<ServiceResponse>>() {
            @Override
            public void onResponse(Call<List<ServiceResponse>> call, Response<List<ServiceResponse>> response) {
                //System.out.println("merda");

            }

            @Override
            public void onFailure(Call<List<ServiceResponse>> call, Throwable t) {
                //System.out.println("merda");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegisterService:
                serviceModify(v);
                break;
            case R.id.imageViewService:
                Options options = Options.init()
                        .setRequestCode(100)
                        .setCount(1)
                        .setFrontfacing(true)
                        .setImageQuality(ImageQuality.LOW)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start(EditServiceActivity.this, options);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            returnValue = imageData.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            bitmap = BitmapFactory.decodeFile(returnValue.get(0));


            Glide.with(this).load(bitmap).fitCenter().into(registerServiceImageView);

            //registerServiceImageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
}
