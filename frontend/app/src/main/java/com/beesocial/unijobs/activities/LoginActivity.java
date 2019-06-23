package com.beesocial.unijobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.astritveliu.boom.Boom;
import com.beesocial.unijobs.R;
import com.beesocial.unijobs.api.Api;
import com.beesocial.unijobs.api.RetrofitClient;
import com.beesocial.unijobs.models.CheckConnection;
import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.LoginResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.models.UserLogin;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.pd.chocobar.ChocoBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    DefaultResponse resposta = new DefaultResponse();
    UserLogin model_obj;
    User userComplete;
    com.github.ybq.android.spinkit.SpinKitView spinKitView;
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.textViewRegister).setOnClickListener(this);

        Button button = findViewById(R.id.buttonLogin);
        new Boom(button);
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


    private void userLogin(final View v) {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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
        spinKitView = findViewById(R.id.spin_kit);
        spinKitView.setVisibility(View.VISIBLE);
        callBackend(v, email, password);
        
    }

    private void callBackend(final View v, String email, String password) {
        model_obj = new UserLogin(email, password);

        Call<LoginResponse> call = RetrofitClient
                .createInstance(1).getApi().userLogin(model_obj);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (response.isSuccessful()) {
                    loginResponse.setToken(loginResponse.getToken());
                    //Toast.makeText(LoginActivity.this, loginResponse.getToken(), Toast.LENGTH_LONG).show();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://unijobs-user.now.sh/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Api client = retrofit.create(Api.class);
                    Call<DefaultResponse> calltargetResponse = client.getUser("Bearer " + loginResponse.getToken());
                    calltargetResponse.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> calltargetResponse, retrofit2.Response<DefaultResponse> responsee) {
                            DefaultResponse UserResponse = responsee.body();
                            Log.d("respostaLogin", "Login ");
                            Log.d("respostaLogin", UserResponse.getEmail());
                            userComplete = new User(UserResponse.getId(), UserResponse.getEmail(), UserResponse.getName(), UserResponse.getImage(), UserResponse.getPhoneNumber(), UserResponse.getFacebook());
                            userComplete.setToken(loginResponse.getToken());
                            Log.d("respostaLogin", userComplete.getEmail());
                            SharedPrefManager.getInstance(LoginActivity.this).saveUser(userComplete);
                            spinKitView.setVisibility(View.GONE);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> calltargetResponse, Throwable t) {
                            spinKitView.setVisibility(View.GONE);
                            ChocoBar.builder().setView(v)
                                    .setText("Erro na conexão com o servidor, tente novamente")
                                    .setDuration(ChocoBar.LENGTH_LONG)
                                    .setActionText(android.R.string.ok)
                                    .red()
                                    .show();
                            Log.d("erroUnijobs", "erro call2 retrofit");
                        }
                    });
                } else {
                    spinKitView.setVisibility(View.GONE);
                    CheckConnection checkConnection = new CheckConnection();
                    checkConnection.checkStuff(v, LoginActivity.this, response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                spinKitView.setVisibility(View.GONE);
                ChocoBar.builder().setView(v)
                        .setText("Erro na conexão com o servidor, tente novamente")
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();
                Log.d("erroUnijobs", "erro call1 retrofit");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                userLogin(v);
                break;
            case R.id.textViewRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }


}
