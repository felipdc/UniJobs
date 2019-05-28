package com.beesocial.unijobs.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.api.Api;
import com.beesocial.unijobs.api.RetrofitClient;
import com.beesocial.unijobs.models.CheckNetwork;
import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.LoginResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.models.UserLogin;
import com.beesocial.unijobs.models.UserRegister;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    UserRegister userRegister;
    UserLogin userLogin;
    User userComplete;
    CheckNetwork checkNetwork;
    Snackbar snackbar;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    String encodedImage;
    private EditText editTextEmail, editTextPassword, editTextName, editTextImage;
    private CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextImage = findViewById(R.id.editTextImage);
        profile = findViewById(R.id.imageProfileLogin);

        findViewById(R.id.buttonSignUp).setOnClickListener(this);
//        findViewById(R.id.textViewLogin).setOnClickListener(this);
        findViewById(R.id.editTextImage).setOnClickListener(this);

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
        String imageText = editTextImage.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Campo necessário");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("O email precisa ser válido");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Campo necessário");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("A senha tem que ter ao mínimo 6 caracteres");
            editTextPassword.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            editTextName.setError("Campo necessário");
            editTextName.requestFocus();
            return;
        }

//        if (imageText.isEmpty()) {
//            editTextImage.setError("Campo necessário");
//            editTextImage.requestFocus();
//            return;
//        }

        //chamada para criar o usuario
        callBackend(v, email, name, password);
    }

    private void callBackend(final View v, String email, String name, String password) {
        userRegister = new UserRegister(email, name, password, encodedImage);
        userLogin = new UserLogin(email, password);

        Call<DefaultResponse> call = RetrofitClient
                .getInstance().getApi().createUser(userRegister);
        call.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                try {
                    DefaultResponse defaultResponse = response.body();

                    defaultResponse.getEmail(); // linha que checa se a resposta tem os campos certos, se nao tiver, cai no catch e dai verifica o porque de ter dado errado


                    Call<LoginResponse> call2 = RetrofitClient
                            .getInstance().getApi().userLogin(userLogin);
                    call2.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call2, Response<LoginResponse> response2) {
                            LoginResponse loginResponse = response2.body();

                            //Toast.makeText(RegisterActivity.this, loginResponse.getToken(), Toast.LENGTH_LONG).show();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://micro-unijobs-user.felipetiagodecarli.now.sh/api/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            Api client = retrofit.create(Api.class);
                            Call<DefaultResponse> calltargetResponce = client.getUser(loginResponse.getToken());
                            calltargetResponce.enqueue(new Callback<DefaultResponse>() {
                                @Override
                                public void onResponse(Call<DefaultResponse> calltargetResponce, retrofit2.Response<DefaultResponse> response3) {
                                    DefaultResponse UserResponse = response3.body();
                                    userComplete = new User(UserResponse.getId(), UserResponse.getEmail(), UserResponse.getName(), UserResponse.getImage(), UserResponse.getPassword());

                                    SharedPrefManager.getInstance(RegisterActivity.this).saveUser(userComplete);

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(Call<DefaultResponse> calltargetResponce, Throwable t) {
                                    snackbar = Snackbar
                                            .make(v, "Erro na conexão com o servidor, tente novamente", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    Log.d("erroUnijobs", t.getMessage());
                                }
                            });

                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call2, Throwable t) {
                            snackbar = Snackbar
                                    .make(v, "Erro na conexão com o servidor, tente novamente", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            Log.d("erroUnijobs", t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    checkNetwork = new CheckNetwork();
                    if (checkNetwork.haveNetworkConnection(RegisterActivity.this)) {
                        if (response.code() == 403) {
                            snackbar = Snackbar
                                    .make(v, "Email já cadastrado", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    } else {
                        snackbar = Snackbar
                                .make(v, "Sem conexão com a internet", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                snackbar = Snackbar
                        .make(v, "Erro na conexão com o servidor, tente novamente", Snackbar.LENGTH_LONG);
                snackbar.show();
                Log.d("erroUnijobs", t.getMessage());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            returnValue = imageData.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            bitmap = BitmapFactory.decodeFile(returnValue.get(0));
            editTextImage.setText(returnValue.get(0));

            profile.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignUp:
                userSignUp(v);
                break;
//            case R.id.textViewLogin:
//                startActivity(new Intent(this, LoginActivity.class));
//                break;
            case R.id.editTextImage:

                /*FishBun.with(RegisterActivity.this)
                        .setImageAdapter(new GlideAdapter())
                        .setMaxCount(1)
                        .setMinCount(1)
                        .startAlbum();*/

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
