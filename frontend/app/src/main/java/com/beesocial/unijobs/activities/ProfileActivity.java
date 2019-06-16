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
import com.pd.chocobar.ChocoBar;
import com.yalantis.ucrop.UCrop;

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
                DefaultResponse dResponse = response.body();
                if (response.isSuccessful()) {
                    User user = SharedPrefManager.getInstance(ProfileActivity.this).getUser();
                    user.setEmail(dResponse.getEmail());
                    user.setFacebook(dResponse.getFacebook());
                    user.setImage(dResponse.getImage());
                    user.setName(dResponse.getName());
                    user.setphoneNumber(dResponse.getPhoneNumber());
                    SharedPrefManager.getInstance(ProfileActivity.this).saveUser(user);
                    //User user = new User(dResponse.getId(),dResponse.getName(),dResponse.getName(),dResponse.getImage(),dResponse.getPhoneNumber(),dResponse.getFacebook());
                    ChocoBar.builder().setBackgroundColor(Color.parseColor("#004E8D"))
                            .setTextSize(18)
                            .setTextColor(Color.parseColor("#AAFFFFFF"))
                            .setTextTypefaceStyle(Typeface.ITALIC)
                            .setText("Informações editadas!")
                            .setMaxLines(4)
                            .centerText()
                            .setActionText("UniJobs")
                            .setActionTextColor(Color.parseColor("#AAFFFFFF"))
                            .setActionTextSize(20)
                            .setActionTextTypefaceStyle(Typeface.BOLD)
                            .setIcon(R.mipmap.ic_launcher)
                            .setActivity(ProfileActivity.this)
                            .setDuration(ChocoBar.LENGTH_LONG)
                            .build()
                            .show();
                } else {
                    ChocoBar.builder().setView(v)
                            .setText("Erro ao editar perfil")
                            .setDuration(ChocoBar.LENGTH_LONG)
                            .setActionText(android.R.string.ok)
                            .red()
                            .show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                ChocoBar.builder().setView(v)
                        .setText("Erro na conexão com o servidor, tente novamente")
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();
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

            UCrop uCrop = UCrop.of(Uri.parse("file://" + returnValue.get(0)), Uri.parse("file://" + returnValue.get(0)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(1000, 1000);
            uCrop.start(ProfileActivity.this);

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(imageData);

            bitmap = BitmapFactory.decodeFile(resultUri.getPath());
            Glide.with(this).load(bitmap).fitCenter().dontAnimate().into(profile);

            //registerServiceImageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(imageData);
        }
    }
}
