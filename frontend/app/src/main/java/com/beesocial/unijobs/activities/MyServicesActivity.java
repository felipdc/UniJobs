package com.beesocial.unijobs.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.adapters.ServicesAdapter;

public class MyServicesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServicesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);

        Toolbar toolbar = findViewById(R.id.myoffers_toolbar);
        toolbar.setTitle("Meus Serviços");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.myoffers_recyclerView);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ServicesAdapter(this, new String[] {"Oferta 1", "Oferta2"}, new String[] {"Desc 1", "Desc 2"}, new String[] {"", ""});

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}
