package com.beesocial.unijobs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.adapters.SectionsPagerAdapter;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView imageProfile;
    User user;
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(" ");
        FloatingActionButton fab = findViewById(R.id.fab);
        //FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPrefManager.getInstance(getBaseContext()).isLoggedIn()){
                    Intent intent = new Intent(getBaseContext(), RegisterServiceActivity.class);
                    startActivity(intent);
                }
            }
        });


        imageProfile = findViewById(R.id.imageProfile);
        imageProfile.setOnClickListener(v -> {
            Intent intentProfile = new Intent(this, ProfileActivity.class);
            startActivity(intentProfile);
        });
    }

    @Override
    protected void onStart() {
        StringBuilder stringBuilder = new StringBuilder();
        super.onStart();

        user = SharedPrefManager.getInstance(this).getUser();
        Handler handler = new Handler();
                /*stringBuilder.append("Olá ");
        stringBuilder.append(user.getName());
        nav_nome.setTitle(stringBuilder);*/
        handler.removeCallbacksAndMessages(null);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
            MenuItem action_logout = menu.findItem(R.id.action_logout);
            MenuItem action_my_offers = menu.findItem(R.id.action_my_offers);

            action_logout.setVisible(false);
            action_my_offers.setVisible(false);
        }
        else {
            String encodedImage = user.getImage();
            MenuItem action_profile = menu.findItem(R.id.action_profile);

            if (encodedImage == null) {
                Glide.with(this).load(R.drawable.ic_emoji).fitCenter().dontAnimate().into(imageProfile);
            }
            else {
                final String pureBase64Encoded = encodedImage.substring(encodedImage.indexOf(",") + 1);
                final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                Glide.with(this).load(decodedBytes).placeholder(R.drawable.ic_loading).fitCenter().dontAnimate().into(imageProfile);
            }

            action_profile.setActionView(imageProfile);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                if(!SharedPrefManager.getInstance(this).isLoggedIn()){
                    SharedPrefManager.getInstance(this).clear();
                    Intent intentLogin = new Intent(this, LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;

            case R.id.action_logout:
                SharedPrefManager.getInstance(this).clear();

                //se nao for mandar para o login activity depois do logout, tem que pelo menos mandar de volta pra main activity, pra recarregar as informacoes, se nao vai parecer que o usuario ainda esta instanciado.
                Intent intentMain = new Intent(this, MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentMain);
                break;
            case R.id.action_my_offers:
                Intent intentMyOffers = new Intent(this, MyServicesActivity.class);
                startActivity(intentMyOffers);
        }

        return super.onOptionsItemSelected(item);
    }
}
