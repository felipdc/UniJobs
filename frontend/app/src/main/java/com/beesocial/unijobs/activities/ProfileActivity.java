package com.beesocial.unijobs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.astritveliu.boom.Boom;
import com.beesocial.unijobs.R;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword, editTextName, editTextPhone, editTextFacebook;
    private CircleImageView profile;

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_save:
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
}
