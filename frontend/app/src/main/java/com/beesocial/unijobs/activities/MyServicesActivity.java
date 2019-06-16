package com.beesocial.unijobs.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.adapters.ServicesAdapter;
import com.beesocial.unijobs.models.ServiceResponse;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.infideap.atomic.Atom;
import com.infideap.atomic.FutureCallback;
import com.pd.chocobar.ChocoBar;

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
        User user = SharedPrefManager.getInstance(this).getUser();
        callBackend(user.getId());
    }

    private void callBackend(String userID) {
        String URL = "https://unijobs-service.now.sh/api/service?createdBy=" + userID;

        Atom.with(this)
                .load(URL)
                .as(ServiceResponse[].class)
                .setCallback(new FutureCallback<ServiceResponse[]>() {
                    @Override
                    public void onCompleted(Exception e, ServiceResponse[] result) {
                        if (e == null) {
                            if (result != null) {
                                String names[] = new String[result.length];
                                String desc[] = new String[result.length];
                                String img[] = new String[result.length];
                                String id[] = new String[result.length];
                                for (int i = 0, j = result.length - 1; i < result.length; i++, j--) {
                                    names[j] = result[i].getName();
                                    desc[j] = result[i].getDescription();
                                    img[j] = result[i].getImage();
                                    id[j] = result[i].getId();
                                }
                                adapter = new ServicesAdapter(MyServicesActivity.this, names, desc, img, id);

                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(layoutManager);
                            } else {
                                String names[] = null;
                                String desc[] = null;
                                String img[] = null;
                                String id[] = null;
                                adapter = new ServicesAdapter(MyServicesActivity.this, names, desc, img, id);

                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(layoutManager);
                            }

                        } else {
                            ChocoBar.builder().setActivity(MyServicesActivity.this)
                                    .setText("Erro na conexão com o servidor, por favor, tente novamente")
                                    .setDuration(ChocoBar.LENGTH_LONG)
                                    .setActionText(android.R.string.ok)
                                    .red()
                                    .show();
                        }
                    }
                });
    }
}
