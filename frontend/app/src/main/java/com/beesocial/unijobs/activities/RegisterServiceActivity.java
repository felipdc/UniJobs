package com.beesocial.unijobs.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beesocial.unijobs.R;

public class RegisterServiceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView registerServiceImageView;
    private RadioGroup registerServiceRadioGroup;
    private EditText registerServiceTitulo;
    private EditText registerServiceDescricao;

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
        registerServiceTitulo = findViewById(R.id.editTextTitulo);
        registerServiceDescricao = findViewById(R.id.editTextDescricao);
    }
}
