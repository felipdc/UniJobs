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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.astritveliu.boom.Boom;
import com.beesocial.unijobs.R;
import com.beesocial.unijobs.api.Api;
import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.models.UserRegister;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextName, editTextPhone, editTextFacebook;
    private CircleImageView profile;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    String encodedImage = null;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextEmail = findViewById(R.id.profile_editTextEmail);
        editTextPassword = findViewById(R.id.profile_editTextPassword);
        editTextName = findViewById(R.id.profile_editTextName);
        profile = findViewById(R.id.profile_imageProfileLogin);
        editTextPhone = findViewById(R.id.profile_editTextPhone);
        editTextFacebook = findViewById(R.id.profile_editTextFacebook);

        Button button = findViewById(R.id.profile_save);
        button.setOnClickListener(this);
        new Boom(button);
        profile.setOnClickListener(this);
    }

    private void editProfile(final View v) {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String facebook = editTextFacebook.getText().toString().trim();

        callBackend(v, email, password, name, phone, facebook);
    }

    private void callBackend(final View v, String email, String password, String name, String phone, String facebook) {
        user = SharedPrefManager.getInstance(this).getUser();
        UserRegister changes = new UserRegister(email, name, encodedImage, facebook, password, phone);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://unijobs-user.now.sh/api/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        Api client = retrofit.create(Api.class);
        Call<DefaultResponse> calltargetResponse = client.updateUser("Bearer " + user.getToken(), changes);
        calltargetResponse.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                //DefaultResponse UpdateResponse = response.body();
                //UpdateResponse.getDate();
                System.out.println("merda");
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                System.out.println("merda");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_save:
                editProfile(v);
                break;

            case R.id.profile_imageProfileLogin:
                Options options = Options.init()
                        .setRequestCode(100)
                        .setCount(1)
                        .setFrontfacing(true)
                        .setImageQuality(ImageQuality.LOW)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start(ProfileActivity.this, options);
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


            Glide.with(this).load(bitmap).fitCenter().dontAnimate().into(profile);

            //registerServiceImageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
}
