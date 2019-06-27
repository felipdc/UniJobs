package com.beesocial.unijobs.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.astritveliu.boom.Boom;
import com.beesocial.unijobs.R;
import com.beesocial.unijobs.api.Api;
import com.beesocial.unijobs.models.CheckConnection;
import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.LoginResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.models.UserLogin;
import com.beesocial.unijobs.models.UserRegister;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.android.material.snackbar.Snackbar;
import com.infideap.atomic.Atom;
import com.infideap.atomic.FutureCallback;
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


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    User userComplete;
    Snackbar snackbar;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    String encodedImage = null, token;
    com.github.ybq.android.spinkit.SpinKitView spinKitView;
    private EditText editTextEmail, editTextPassword, editTextName, editTextPhone, editTextFacebook;
    private CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        profile = findViewById(R.id.imageProfileLogin);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextFacebook = findViewById(R.id.editTextFacebook);

        Button button = findViewById(R.id.buttonSignUp);
        button.setOnClickListener(this);
        new Boom(button);
        profile.setOnClickListener(this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void userSignUp(final View v) {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhone.getText().toString().trim();
        String facebook = editTextFacebook.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Campo necessário");
            editTextEmail.requestFocus();
            return;
        }
        else if (email.length() <= 10) {
            // checks "ab@ab.com" emails
            editTextEmail.setError("Há algo de errado em seu email");
            editTextEmail.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("O email precisa ser válido");
            editTextEmail.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()) {
            editTextFacebook.setError("Campo necessário");
            editTextFacebook.requestFocus();
            return;
        }
        else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            editTextPhone.setError("O telefone precisa ser válido");
            editTextPhone.requestFocus();
            return;
        }

        if (!Patterns.WEB_URL.matcher(facebook).matches()) {
            editTextFacebook.setError("O link do perfil do Facebook precisa ser válido");
            editTextFacebook.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Campo necessário");
            editTextPassword.requestFocus();
            return;
        }
        else if (password.length() < 6) {
            editTextPassword.setError("A senha tem que ter ao mínimo 6 caracteres");
            editTextPassword.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            editTextName.setError("Campo necessário");
            editTextName.requestFocus();
            return;
        }


        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.VISIBLE);
        //chamada para criar o usuario
        callBackend(v, email, name, password, facebook, phoneNumber);
    }

    private void callBackend(final View v, String email, String name, String password, String facebook, String phoneNumber) {
        UserRegister userRegister = new UserRegister(email, name, encodedImage, facebook, password, phoneNumber);
        final UserLogin userLogin = new UserLogin(email, password);

        Atom.with(RegisterActivity.this)
                .load("https://unijobs-user.now.sh/api/user", Atom.POST_METHOD)
                .setJsonPojoBody(userRegister)
                //.setBody(requestString) //Plain String
                .as(DefaultResponse.class)
                .setCallback(new FutureCallback<DefaultResponse>() {
                    @Override
                    public void onCompleted(Exception e, DefaultResponse result) {
                        //System.out.println("");
                        if (e == null) {
                            Atom.with(RegisterActivity.this)
                                    .load("https://unijobs-user.now.sh/api/auth/user", Atom.POST_METHOD)
                                    .setJsonPojoBody(userLogin)
                                    .as(LoginResponse.class)
                                    .setCallback(new FutureCallback<LoginResponse>() {

                                        @Override
                                        public void onCompleted(Exception e, LoginResponse result) {

                                            Retrofit retrofit = new Retrofit.Builder()
                                                    .baseUrl("https://unijobs-user.now.sh/api/")
                                                    .addConverterFactory(GsonConverterFactory.create()).build();
                                            Api client = retrofit.create(Api.class);
                                            Call<DefaultResponse> calltargetResponse = client.getUser(result.getToken());
                                            token = result.getToken();
                                            calltargetResponse.enqueue(new Callback<DefaultResponse>() {
                                                @Override
                                                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                                                    DefaultResponse UserResponse = response.body();
                                                    spinKitView.setVisibility(View.GONE);
                                                    userComplete = new User(UserResponse.getId(), UserResponse.getEmail(), UserResponse.getName(), UserResponse.getImage(), UserResponse.getPhoneNumber(), UserResponse.getFacebook());
                                                    userComplete.setToken(token);
                                                    SharedPrefManager.getInstance(RegisterActivity.this).saveUser(userComplete);

                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);

                                                }

                                                @Override
                                                public void onFailure(Call<DefaultResponse> call, Throwable t) {
                                                    spinKitView.setVisibility(View.GONE);
                                                    ChocoBar.builder().setView(v)
                                                            .setText("Erro na conexão com o servidor, tente novamente")
                                                            .setDuration(ChocoBar.LENGTH_LONG)
                                                            .setActionText(android.R.string.ok)
                                                            .red()
                                                            .show();
                                                }
                                            });
                                        }
                                    });
                        } else {
                            spinKitView.setVisibility(View.GONE);
                            CheckConnection checkConnection = new CheckConnection();
                            checkConnection.checkStuffAtom(v, RegisterActivity.this, e);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            returnValue = imageData.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            UCrop uCrop = UCrop.of(Uri.parse("file://" + returnValue.get(0)), Uri.parse("file://" + returnValue.get(0).substring(0, returnValue.get(0).length() - 4) + "crop" + returnValue.get(0).substring(returnValue.get(0).length() - 4)))
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(1000, 1000);
            uCrop.start(RegisterActivity.this);

            bitmap = BitmapFactory.decodeFile(returnValue.get(0));

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(imageData);

            bitmap = BitmapFactory.decodeFile(resultUri.getPath());


            Glide.with(this).load(bitmap).fitCenter().into(profile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(imageData);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignUp:
                userSignUp(v);
                break;

            case R.id.imageProfileLogin:
                Options options = Options.init()
                        .setRequestCode(100)
                        .setCount(1)
                        .setFrontfacing(true)
                        .setImageQuality(ImageQuality.LOW)
                        .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT);
                Pix.start(RegisterActivity.this, options);
                break;
        }
    }

}
