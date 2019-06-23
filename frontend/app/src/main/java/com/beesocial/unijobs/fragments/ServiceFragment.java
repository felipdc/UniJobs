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
import com.beesocial.unijobs.models.ErrorResponse;
import com.beesocial.unijobs.models.ServiceResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iammert.library.ui.multisearchviewlib.MultiSearchView;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A placeholder fragment containing a simple view.
 */
public class ServiceFragment extends Fragment {
    List<ServiceResponse> responseVector;
    private String[] dataset;
    com.iammert.library.ui.multisearchviewlib.MultiSearchView searchView;

    private static final String ARG_SECTION_NUMBER = "section_number";

    com.github.ybq.android.spinkit.SpinKitView spinKitView;
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
                    .createInstance(2).getApi().getServiceOfferTrue();
        } else {
            call = RetrofitClient
                    .createInstance(2).getApi().getServiceOfferFalse();
        }

        call.enqueue(new Callback<List<ServiceResponse>>() {
            @Override
            public void onResponse(Call<List<ServiceResponse>> calltargetResponce, retrofit2.Response<List<ServiceResponse>> response) {
                List<ServiceResponse> responseList = response.body();
                if (response.isSuccessful()) {
                    spinKitView = getView().findViewById(R.id.spin_kit);
                    spinKitView.setVisibility(View.GONE);
                    if (!responseList.isEmpty()) {
                        String names[] = new String[responseList.size()];
                        String desc[] = new String[responseList.size()];
                        String img[] = new String[responseList.size()];
                        String id[] = new String[responseList.size()];
                        for (int i = 0, j = responseList.size() - 1; i < responseList.size(); i++, j--) {
                            names[j] = responseList.get(i).getName();
                            desc[j] = responseList.get(i).getDescription();
                            img[j] = responseList.get(i).getImage();
                            id[j] = responseList.get(i).getId();
                        }
                        searchView.setVisibility(View.VISIBLE);
                        servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id);
                        recyclerView.setAdapter(servicesAdapter);
                        searchList(names, desc, img, id);
                    } else {
                        ChocoBar.builder().setActivity(getActivity())
                                .setText("Erro na conexão com o servidor, por favor, tente novamente")
                                .setDuration(ChocoBar.LENGTH_LONG)
                                .setActionText(android.R.string.ok)
                                .red()
                                .show();
                    }
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ErrorResponse>() {
                    }.getType();
                    ErrorResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                    ChocoBar.builder().setActivity(getActivity())
                            .setText(errorResponse.getErr())
                            .setDuration(ChocoBar.LENGTH_LONG)
                            .setActionText(android.R.string.ok)
                            .red()
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceResponse>> calltargetResponce, Throwable t) {
                ChocoBar.builder().setActivity(getActivity())
                        .setText("Erro na conexão com o servidor, por favor, tente novamente")
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();
            }
        });
    }

    private void searchList(String[] names, String[] desc, String[] img, String[] id) {

        searchView.setSearchViewListener(new MultiSearchView.MultiSearchViewListener() {
            //tem jeito melhor de fazer essa pesquisa, porem, nao tenho tempo para parar e pensar como ser mais otimizada.
            @Override
            public void onTextChanged(int j, @NotNull CharSequence charSequence) {
                Boolean yes;
                ArrayList<String> resultNames = new ArrayList<String>();
                ArrayList<String> resultDesc = new ArrayList<String>();
                ArrayList<String> resultImg = new ArrayList<String>();
                ArrayList<String> resultId = new ArrayList<String>();

                for (int i = 0; i < names.length; i++) {
                    yes = names[i].contains(charSequence.toString());
                    if (yes) {
                        resultNames.add(names[i]);
                        resultDesc.add(desc[i]);
                        resultImg.add(img[i]);
                        resultId.add(id[i]);
                    }
                }

                if (charSequence.length() == 0) {
                    servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id);
                    recyclerView.setAdapter(servicesAdapter);
                }

                String[] returnNames = resultNames.toArray(new String[resultNames.size()]);
                String[] returnDesc = resultDesc.toArray(new String[resultDesc.size()]);
                String[] returnImg = resultImg.toArray(new String[resultImg.size()]);
                String[] returnId = resultId.toArray(new String[resultId.size()]);

                servicesAdapter = new ServicesAdapter(getContext(), returnNames, returnDesc, returnImg, returnId);
                recyclerView.setAdapter(servicesAdapter);
            }

            @Override
            public void onSearchComplete(int j, @NotNull CharSequence charSequence) {
                Boolean yes;
                ArrayList<String> resultNames = new ArrayList<String>();
                ArrayList<String> resultDesc = new ArrayList<String>();
                ArrayList<String> resultImg = new ArrayList<String>();
                ArrayList<String> resultId = new ArrayList<String>();

                for (int i = 0; i < names.length; i++) {
                    yes = names[i].contains(charSequence.toString());
                    if (yes) {
                        resultNames.add(names[i]);
                        resultDesc.add(desc[i]);
                        resultImg.add(img[i]);
                        resultId.add(id[i]);
                    }
                }

                if (charSequence.length() == 0) {
                    servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id);
                    recyclerView.setAdapter(servicesAdapter);
                }

                String[] returnNames = resultNames.toArray(new String[resultNames.size()]);
                String[] returnDesc = resultDesc.toArray(new String[resultDesc.size()]);
                String[] returnImg = resultImg.toArray(new String[resultImg.size()]);
                String[] returnId = resultId.toArray(new String[resultId.size()]);

                servicesAdapter = new ServicesAdapter(getContext(), returnNames, returnDesc, returnImg, returnId);
                recyclerView.setAdapter(servicesAdapter);
            }

            @Override
            public void onSearchItemRemoved(int j) {

            }

            @Override
            public void onItemSelected(int j, @NotNull CharSequence charSequence) {

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
        searchView = root.findViewById(R.id.searchView);
        //searchView.setQueryHint("Pesquisar");

        return root;
    }
}