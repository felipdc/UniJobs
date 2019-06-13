package com.beesocial.unijobs.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.beesocial.unijobs.R;

public class EditServiceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView registerServiceImageView;
    private RadioGroup registerServiceRadioGroup;
    private EditText registerServiceTitulo;
    private EditText registerServiceDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        toolbar = findViewById(R.id.edit_service_toolbar);
        toolbar.setTitle("Editar Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registerServiceImageView = findViewById(R.id.edit_service_imageViewService);
        registerServiceRadioGroup = findViewById(R.id.edit_service_radioGroup);
        registerServiceTitulo = findViewById(R.id.edit_service_editTextTitulo);
        registerServiceDescricao = findViewById(R.id.edit_service_editTextDescricao);
    }
}
