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

/**
 * A placeholder fragment containing a simple view.
 */
public class ServiceFragment extends Fragment {

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
        if (tipoServico == 1) {
            // Fazer requisicao para servicos oferecidos
            servicesAdapter = new ServicesAdapter(getContext(), new String[]{"Ofereco 1", "Ofereco 2", "Ofereco 3", "Ofereco 4", "Ofereco 5"});
        } else if (tipoServico == 2) {
            // Fazer requisicao para servicos oferecidos
            servicesAdapter = new ServicesAdapter(getContext(), new String[]{"Demando 1", "Demando 2", "Demando 3", "Demando 4"});
        }

        layoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_service, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(servicesAdapter);

        return root;
    }
}