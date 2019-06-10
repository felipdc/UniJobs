package com.beesocial.unijobs.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beesocial.unijobs.R;

public class RegisterServiceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView RegisterServiceImageView;
    private RadioGroup RegisterServiceRadioGroup;
    private EditText RegisterServiceTitulo;
    private EditText RegisterServiceDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        toolbar = findViewById(R.id.register_service_toolbar);
        toolbar.setTitle("Cadastrar Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RegisterServiceImageView = findViewById(R.id.imageViewService);
        RegisterServiceRadioGroup = findViewById(R.id.radioGroup);
        RegisterServiceTitulo = findViewById(R.id.editTextTitulo);
        RegisterServiceDescricao = findViewById(R.id.editTextDescricao);
    }
}
