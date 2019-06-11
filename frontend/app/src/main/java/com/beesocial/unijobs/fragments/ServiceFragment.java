package com.beesocial.unijobs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.adapters.ServicesAdapter;
import com.beesocial.unijobs.models.ServiceResponse;
import com.example.downzlibrary.DownZ;
import com.example.downzlibrary.ListnerInterface.HttpListener;
import com.google.gson.Gson;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ServiceFragment extends Fragment {
    List<ServiceResponse> responseVector;
    private String[] dataset;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter servicesAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static ServiceFragment newInstance(int index) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index + 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int tipoServico = getArguments().getInt(ARG_SECTION_NUMBER);
        //SharedPrefManager.getInstance(getActivity()).saveTab(tipoServico);
        callBackend(tipoServico);
        layoutManager = new LinearLayoutManager(getActivity());


    }

    private void callBackend(int index) {
        String URL;
        if (index == 1) {
            URL = "https://micro-unijobs-service.felipetiagodecarli.now.sh/api/service?isOffer=true";
        } else {

            URL = "https://micro-unijobs-service.felipetiagodecarli.now.sh/api/service?isOffer=false";
        }

        DownZ
                .from(getContext()) //context
                .load(DownZ.Method.GET, URL)
                .asJsonArray()    //asJsonArray() or asJsonObject() or asXml() can be used depending on need
                .setCallback(new HttpListener<org.json.JSONArray>() {
                    @Override
                    public void onRequest() {
                        //System.out.println("bla-bla");
                        //On Beginning of request

                    }

                    @Override
                    public void onResponse(org.json.JSONArray data) {
                        if (data != null) {
                            String dataString = data.toString();
                            Gson gson = new Gson();
                            ServiceResponse responseList[] = gson.fromJson(dataString, ServiceResponse[].class);
                            String names[] = new String[responseList.length];
                            String desc[] = new String[responseList.length];
                            String img[] = new String[responseList.length];
                            for (int i = 0; i < responseList.length; i++) {
                                names[i] = responseList[i].getName();
                                desc[i] = responseList[i].getDescription();
                                img[i] = responseList[i].getImage();
                            }
                            servicesAdapter = new ServicesAdapter(getContext(), names, desc, img);
                            recyclerView.setAdapter(servicesAdapter);

                        }
                    }

                    @Override
                    public void onError() {
                        //System.out.println("bla-bla");
                        //do something when there is an error

                    }

                    @Override
                    public void onCancel() {
                        //System.out.println("bla-bla");
                        //do something when request cancelled

                    }
                });
    }

    /*private void callBackend(int index) {
        Call<List<ServiceResponse>> call;
        if (index == 1) {
            call = RetrofitClient
                    .getInstance(2).getApi().getServiceOfferTrue();
        } else {
            call = RetrofitClient
                    .getInstance(2).getApi().getServiceOfferFalse();
        }

        call.enqueue(new Callback<List<ServiceResponse>>() {
            @Override
            public void onResponse(Call<List<ServiceResponse>> calltargetResponce, retrofit2.Response<List<ServiceResponse>> response3) {
                List<ServiceResponse> responseList = response3.body();
                String names[] = new String[responseList.size()];
                String desc[] = new String[responseList.size()];
                String img[] = new String[responseList.size()];
                for (int i = 0; i < responseList.size(); i++) {
                    names[i] = responseList.get(i).getName();
                    desc[i] = responseList.get(i).getDescription();
                    img[i] = responseList.get(i).getImage();
                }
                servicesAdapter = new ServicesAdapter(getContext(), names, desc, img);
                recyclerView.setAdapter(servicesAdapter);
            }

            @Override
            public void onFailure(Call<List<ServiceResponse>> calltargetResponce, Throwable t) {

            }
        });
    }*/

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_service, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        return root;
    }
}