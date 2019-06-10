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
import com.beesocial.unijobs.api.RetrofitClient;
import com.beesocial.unijobs.models.ServiceResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

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
    }

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