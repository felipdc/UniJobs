package com.beesocial.unijobs.models;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.pd.chocobar.ChocoBar;

import retrofit2.Response;

public class CheckConnection {
    public boolean haveNetworkConnection(Context mContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        } catch (Exception ex) {
            Log.d("netStatus", ex.getMessage());
        }
        return false;
    }

    public void checkStuff(View v, Context mContext, Response<LoginResponse> response) {
        CheckConnection checkConnection;
        checkConnection = new CheckConnection();
        if (checkConnection.haveNetworkConnection(mContext)) {
            if (response.code() == 404) {

                ChocoBar.builder().setView(v)
                        .setText("Email não cadastrado")
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();

            }
            if (response.code() == 400) {

                ChocoBar.builder().setView(v)
                        .setText("Senha incorreta")
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();

            }
        } else {

            ChocoBar.builder().setView(v)
                    .setText("Sem conexão com a internet")
                    .setDuration(ChocoBar.LENGTH_LONG)
                    .setActionText(android.R.string.ok)
                    .red()
                    .show();
        }
    }

    public void checkStuffAtom(View v, Context mContext, Exception e) {
        CheckConnection checkConnection;
        checkConnection = new CheckConnection();
        if (checkConnection.haveNetworkConnection(mContext)) {
            ChocoBar.builder().setView(v)
                    .setText(e.getCause().getMessage())
                    .setDuration(ChocoBar.LENGTH_LONG)
                    .setActionText(android.R.string.ok)
                    .red()
                    .show();
        } else {
            ChocoBar.builder().setView(v)
                    .setText("Sem conexão com a internet")
                    .setDuration(ChocoBar.LENGTH_LONG)
                    .setActionText(android.R.string.ok)
                    .red()
                    .show();
        }
    }
}
