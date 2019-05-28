package com.beesocial.unijobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.models.User;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MenuItem nav_nome;
    CircleImageView imageProfile;
    User user;
    View header;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        header = navigationView.getHeaderView(0);
        //nav_nome = menu.findItem(R.id.nav_nome);
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
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);

        imageProfile = header.findViewById(R.id.imageProfile);

        if(SharedPrefManager.getInstance(this).isLoggedIn()) {
            String encodedImage = user.getImage();
            if (encodedImage == null) {
                Glide.with(this).load(R.drawable.ic_emoji).fitCenter().dontAnimate().into(imageProfile);
                return true;
            }
            final String pureBase64Encoded = encodedImage.substring(encodedImage.indexOf(",") + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Glide.with(this).load(decodedBytes).placeholder(R.drawable.ic_loading).fitCenter().dontAnimate().into(imageProfile);
        }
        else{
            Glide.with(this).load(R.drawable.ic_emoji).fitCenter().dontAnimate().into(imageProfile);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_perfil) {
            if(!SharedPrefManager.getInstance(this).isLoggedIn()){
                SharedPrefManager.getInstance(this).clear();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {
                //vai para o perfil
            }
        } else if (id == R.id.nav_serv) {

        } else if (id == R.id.nav_not) {

        } else if (id == R.id.nav_config) {

        } else if (id == R.id.nav_logout){
            if(SharedPrefManager.getInstance(this).isLoggedIn()){
                SharedPrefManager.getInstance(this).clear();

                //se nao for mandar para o login activity depois do logout, tem que pelo menos mandar de volta pra main activity, pra recarregar as informacoes, se nao vai parecer que o usuario ainda esta instanciado.
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
