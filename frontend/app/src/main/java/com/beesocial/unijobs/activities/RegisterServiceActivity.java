package com.beesocial.unijobs.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.astritveliu.boom.Boom;
import com.beesocial.unijobs.R;
import com.beesocial.unijobs.api.Api;
import com.beesocial.unijobs.models.ServiceRegister;
import com.beesocial.unijobs.models.ServiceResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.pd.chocobar.ChocoBar;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ImageView registerServiceImageView;
    private RadioGroup registerServiceRadioGroup;
    User user;
    private EditText registerServiceTitulo;
    private EditText registerServiceDescricao;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    String encodedImage = null;
    com.github.ybq.android.spinkit.SpinKitView spinKitView;
    private RadioButton registerServiceRadioOferta, registerServiceRadioProcura;
    private Button button;

    private void serviceRegister(final View v) {
        String titulo = registerServiceTitulo.getText().toString().trim();
        String desc = registerServiceDescricao.getText().toString().trim();
        String isOffer;


        if (titulo.isEmpty()) {
            registerServiceTitulo.setError("Campo necessário");
            registerServiceTitulo.requestFocus();
            return;
        }
        else if (titulo.length() < 5) {
            registerServiceDescricao.setError("Titulo muito curto");
            registerServiceDescricao.requestFocus();
            return;
        }

        if (desc.isEmpty()) {
            registerServiceDescricao.setError("Campo necessário");
            registerServiceDescricao.requestFocus();
            return;
        }
        else if (desc.length() < 5) {
            registerServiceDescricao.setError("Descrição muito curta");
            registerServiceDescricao.requestFocus();
            return;
        }

        if (registerServiceRadioProcura.isChecked()) {
            isOffer = "false";
        } else {
            isOffer = "true";
        }


        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.VISIBLE);
        callBackend(v, titulo, desc, isOffer);
        //callBackend2(v, email, name, password, facebook, phoneNumber);
    }

    private void callBackend(final View v, String titulo, String desc, String isOffer) {
        ServiceRegister service = new ServiceRegister(titulo, isOffer, encodedImage, desc);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://unijobs-service.now.sh/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        Api client = retrofit.create(Api.class);
        Call<ServiceResponse> calltargetResponse = client.createService("Bearer " + user.getToken(), service);
        calltargetResponse.enqueue(new Callback<ServiceResponse>() {
                                       @Override
                                       public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                                           spinKitView.setVisibility(View.GONE);
                                           ServiceResponse sResponse = response.body();
                                           if (response.isSuccessful()) {
                                               ChocoBar.builder().setBackgroundColor(Color.parseColor("#004E8D"))
                                                       .setTextSize(18)
                                                       .setTextColor(Color.parseColor("#AAFFFFFF"))
                                                       .setTextTypefaceStyle(Typeface.ITALIC)
                                                       .setText("Serviço cadastrado!")
                                                       .setMaxLines(4)
                                                       .centerText()
                                                       .setActionText("UniJobs")
                                                       .setActionTextColor(Color.parseColor("#AAFFFFFF"))
                                                       .setActionTextSize(20)
                                                       .setActionTextTypefaceStyle(Typeface.BOLD)
                                                       .setIcon(R.mipmap.ic_launcher)
                                                       .setActivity(RegisterServiceActivity.this)
                                                       .setDuration(ChocoBar.LENGTH_LONG)
                                                       .build()
                                                       .show();
                                           } else {
                                               ChocoBar.builder().setView(v)
                                                       .setText("Erro ao cadastrar serviço")
                                                       .setDuration(ChocoBar.LENGTH_LONG)
                                                       .setActionText(android.R.string.ok)
                                                       .red()
                                                       .show();
                                           }
                                       }

                                       @Override
                                       public void onFailure(Call<ServiceResponse> call, Throwable t) {
                                           spinKitView.setVisibility(View.GONE);
                                           ChocoBar.builder().setView(v)
                                                   .setText("Erro na conexão com o servidor, tente novamente")
                                                   .setDuration(ChocoBar.LENGTH_LONG)
                                                   .setActionText(android.R.string.ok)
                                                   .red()
                                                   .show();
                                           //System.out.println("merda");
                                       }
                                   }
        );
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        toolbar = findViewById(R.id.register_service_toolbar);
        toolbar.setTitle("Cadastrar Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerServiceImageView = findViewById(R.id.imageViewService);
        registerServiceRadioGroup = findViewById(R.id.radioGroup);
        registerServiceRadioOferta = findViewById(R.id.radioButtonOferta);
        registerServiceRadioProcura = findViewById(R.id.radioButtonDemanda);
        registerServiceTitulo = findViewById(R.id.editTextTitulo);
        registerServiceDescricao = findViewById(R.id.editTextDescricao);
        button = findViewById(R.id.buttonRegisterService);
        new Boom(button);
        button.setOnClickListener(this);
        registerServiceImageView.setOnClickListener(this);

        user = SharedPrefManager.getInstance(this).getUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonRegisterService:
                serviceRegister(v);
                break;
            case R.id.imageViewService:
                Options options = Options.init()
                        .setRequestCode(100)
                        .setCount(1)
                        .setFrontfacing(true)
                        .setImageQuality(ImageQuality.LOW)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start(RegisterServiceActivity.this, options);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            returnValue = imageData.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            UCrop uCrop = UCrop.of(Uri.parse("file://" + returnValue.get(0)), Uri.parse("file://" + returnValue.get(0).substring(0, returnValue.get(0).length() - 4) + "crop" + returnValue.get(0).substring(returnValue.get(0).length() - 4)))
                    .withAspectRatio(16, 9)
                    .withMaxResultSize(1920, 1080);
            uCrop.start(RegisterServiceActivity.this);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(imageData);

            bitmap = BitmapFactory.decodeFile(resultUri.getPath());


            Glide.with(this).load(bitmap).fitCenter().into(registerServiceImageView);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(imageData);
        }
    }
}