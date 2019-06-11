package com.beesocial.unijobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.models.CheckNetwork;
import com.beesocial.unijobs.models.DefaultResponse;
import com.beesocial.unijobs.models.LoginResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.models.UserLogin;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;
import com.infideap.atomic.Atom;
import com.infideap.atomic.FutureCallback;

import okhttp3.Headers;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    DefaultResponse resposta = new DefaultResponse();
    UserLogin model_obj;
    User userComplete;
    CheckNetwork checkNetwork;
    Snackbar snackbar;
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
        callBackend(v, email, password);
        
    }

    private void callBackend(final View v, String email, String password) {
        model_obj = new UserLogin(email, password);

        Atom.with(LoginActivity.this)
                .load("https://micro-unijobs.now.sh/api/auth/user", Atom.DELETE_METHOD)
                .setJsonPojoBody(model_obj)
                .as(LoginResponse.class)
                .setCallback(new FutureCallback<LoginResponse>() {
                    @Override
                    public void onCompleted(Exception e, LoginResponse result) {

                        Headers.Builder a = new Headers.Builder();
                        String foi = "Bearer " + result.getToken();
                        a.add("Authorization", foi);
                        Atom.with(LoginActivity.this)
                                .load("https://micro-unijobs.now.sh/api/user")
                                .setHeader(a.build())
                                .as(DefaultResponse.class)
                                .setCallback(new FutureCallback<DefaultResponse>() {
                                    @Override
                                    public void onCompleted(Exception e, DefaultResponse result) {
                                        userComplete = new User(result.getId(), result.getEmail(), result.getName(), result.getImage(), result.getPhoneNumber(), result.getFacebook());
                                        //Log.d("respostaLogin", userComplete.getEmail());
                                        SharedPrefManager.getInstance(LoginActivity.this).saveUser(userComplete);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
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
