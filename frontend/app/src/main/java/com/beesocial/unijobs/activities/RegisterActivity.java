package com.beesocial.unijobs.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.astritveliu.boom.Boom;
import com.beesocial.unijobs.R;
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
import com.infideap.atomic.Atom;
import com.infideap.atomic.FutureCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Headers;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    User userComplete;
    CheckNetwork checkNetwork;
    Snackbar snackbar;
    ArrayList<String> returnValue;
    Bitmap bitmap;
    String encodedImage = null;
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

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("O email precisa ser válido");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
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

        if (phoneNumber.isEmpty()) {
            editTextFacebook.setError("Campo necessário");
            editTextFacebook.requestFocus();
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

        //chamada para criar o usuario
        callBackend(v, email, name, password, facebook, phoneNumber);
        //callBackend2(v, email, name, password, facebook, phoneNumber);
    }

    private void callBackend(final View v, String email, String name, String password, String facebook, String phoneNumber) {
        UserRegister userRegister = new UserRegister(email, name, encodedImage, facebook, password, phoneNumber);
        final UserLogin userLogin = new UserLogin(email, password);

        Atom.with(RegisterActivity.this)
                .load("https://micro-unijobs.now.sh/api/user", Atom.POST_METHOD)
                .setJsonPojoBody(userRegister)
                //.setBody(requestString) //Plain String
                .as(DefaultResponse.class)
                .setCallback(new FutureCallback<DefaultResponse>() {
                    @Override
                    public void onCompleted(Exception e, DefaultResponse result) {

                        Atom.with(RegisterActivity.this)
                                .load("https://micro-unijobs.now.sh/api/auth/user", Atom.POST_METHOD)
                                .setJsonPojoBody(userLogin)
                                //.setBody(requestString) //Plain String
                                .as(LoginResponse.class)
                                .setCallback(new FutureCallback<LoginResponse>() {

                                    @Override
                                    public void onCompleted(Exception e, LoginResponse result) {

                                        Headers.Builder a = new Headers.Builder();
                                        String foi = "Bearer " + result.getToken();
                                        a.add("Authorization", foi);


                                        Atom.with(RegisterActivity.this)
                                                .load("https://micro-unijobs.now.sh/api/user")
                                                .setHeader(a.build())
                                                .as(DefaultResponse.class)
                                                .setCallback(new FutureCallback<DefaultResponse>() {
                                                    @Override
                                                    public void onCompleted(Exception e, DefaultResponse result) {
                                                        System.out.println("caralho foi filha da puta");
                                                        SharedPrefManager.getInstance(RegisterActivity.this).saveUser(userComplete);
                                                        userComplete = new User(result.getId(), result.getEmail(), result.getName(), result.getImage(), result.getPhoneNumber(), result.getFacebook());
                                                        //Log.d("respostaLogin", userComplete.getEmail());
                                                        SharedPrefManager.getInstance(RegisterActivity.this).saveUser(userComplete);

                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    /*private void callBackend2(final View v, String email, String name, String password, String facebook, String phoneNumber) {
        UserRegister userRegister = new UserRegister(email, name, encodedImage, facebook, password, phoneNumber);
        //userLogin = new UserLogin(email, password);
        DownZ
                .from(this) //context
                .load(DownZ.Method.POST, "https://micro-unijobs.now.sh/api/user")
                .asJsonObject()    //asJsonArray() or asJsonObject() or asXml() can be used depending on need
                .setCallback(new HttpListener<JSONObject>() {
                    @Override
                    public void onRequest() {
                        //System.out.println("bla-bla");
                        //On Beginning of request

                    }

                    @Override
                    public void onResponse(JSONObject data) {
                        if (data != null) {
                            String dataString = data.toString();
                            Gson gson = new Gson();
                            DefaultResponse response = gson.fromJson(dataString, DefaultResponse.class);

                        }
                    }

                    @Override
                    public void onError() {
                        //System.out.println("bla-bla");
                        //do something when there is an error

                    }

                    @Override
                    public void onCancel() {
                        //System.out.println("bla-bla");
                        //do something when request cancelled

                    }
                });
    }*/

    /*
        private void callBackend(final View v, String email, String name, String password, String facebook, String phoneNumber) {
            userRegister = new UserRegister(email, name, password, phoneNumber, encodedImage, facebook);
            userLogin = new UserLogin(email, password);

            Call<DefaultResponse> call = RetrofitClient
                    .createInstance(3).getApi().createUser(userRegister);
            call.enqueue(new Callback<DefaultResponse>() {
                @Override
                public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                    try {
                        DefaultResponse defaultResponse = response.body();

                        String s=defaultResponse.getEmail(); // linhas que checam se a resposta tem os campos certos, se nao tiver, cai no catch e dai verifica o porque de ter dado errado
                        s.length();

                        Call<LoginResponse> call2 = RetrofitClient
                                .getInstance(1).getApi().userLogin(userLogin);
                        call2.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call2, Response<LoginResponse> response2) {
                                final LoginResponse loginResponse = response2.body();
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
                                        userComplete = new User(UserResponse.getId(), UserResponse.getEmail(), UserResponse.getName(), UserResponse.getImage(), UserResponse.getPhoneNumber().toString(), UserResponse.getFacebook());
                                        userComplete.setToken(loginResponse.getToken());
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
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

            returnValue = imageData.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            bitmap = BitmapFactory.decodeFile(returnValue.get(0));

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