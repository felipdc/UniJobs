package com.beesocial.unijobs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.adapters.ServicesAdapter;
import com.beesocial.unijobs.api.RetrofitClient;
import com.beesocial.unijobs.models.ErrorResponse;
import com.beesocial.unijobs.models.ServiceResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    MultiSearchView searchView;
    int y=0;
    private static final String ARG_SECTION_NUMBER = "section_number";

    com.github.ybq.android.spinkit.SpinKitView spinKitView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter servicesAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipe;
    int tipoServico=1;
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
        tipoServico = getArguments().getInt(ARG_SECTION_NUMBER);
        //SharedPrefManager.getInstance(getActivity()).saveTab(tipoServico);
        callBackend(tipoServico);
        layoutManager = new LinearLayoutManager(getActivity());

    }

    private void callBackend(int index) {

        Call<List<ServiceResponse>> call;
        Call<List<ServiceResponse>> call2 = RetrofitClient.createInstance(2).getApi().getServiceOfferFalse();
        if (index == 1) {
            call = RetrofitClient
                    .getInstance(2).getApi().getServiceOfferTrue();
        } else {
            call = RetrofitClient
                    .getInstance(2).getApi().getServiceOfferFalse();
        }

        call.enqueue(new Callback<List<ServiceResponse>>() {
            @Override
            public void onResponse(Call<List<ServiceResponse>> calltargetResponce, retrofit2.Response<List<ServiceResponse>> response) {

                List<ServiceResponse> responseList = response.body();
                if (response.isSuccessful()) {

                    spinKitView.setVisibility(View.GONE);

                        String names[] = new String[responseList.size()];
                        String desc[] = new String[responseList.size()];
                        String img[] = new String[responseList.size()];
                        String id[] = new String[responseList.size()];
                        String created[] = new String[responseList.size()];
                        for (int i = 0, j = responseList.size() - 1; i < responseList.size(); i++, j--) {
                            names[j] = responseList.get(i).getName();
                            desc[j] = responseList.get(i).getDescription();
                            img[j] = responseList.get(i).getImage();
                            id[j] = responseList.get(i).getId();
                            created[j] = responseList.get(i).getCreatedBy();
                        }
                        swipe.setRefreshing(false);
                        servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id, created);
                        recyclerView.setAdapter(servicesAdapter);
                        //searchView.setVisibility(View.VISIBLE);
                        searchList(names, desc, img, id, created);

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
                        .setText(t.getMessage())
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();
            }
        });
    }

    private void searchList(String[] names, String[] desc, String[] img, String[] id, String[] created) {

        searchView.setSearchViewListener(new MultiSearchView.MultiSearchViewListener() {
            @Override
            public void onTextChanged(int j, @NotNull CharSequence charSequence) {
                Boolean yes;
                ArrayList<String> resultNames = new ArrayList<String>();
                ArrayList<String> resultDesc = new ArrayList<String>();
                ArrayList<String> resultImg = new ArrayList<String>();
                ArrayList<String> resultId = new ArrayList<String>();
                ArrayList<String> resultCreated = new ArrayList<String>();

                if (charSequence.toString().isEmpty()) {
                    servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id, created);
                    recyclerView.setAdapter(servicesAdapter);
                }

                for (int i = 0; i < names.length; i++) {
                    yes = names[i].contains(charSequence);
                    if (yes) {
                        resultNames.add(names[i]);
                        resultDesc.add(desc[i]);
                        resultImg.add(img[i]);
                        resultId.add(id[i]);
                        resultCreated.add(created[i]);
                    }
                }

                String[] returnNames = resultNames.toArray(new String[resultNames.size()]);
                String[] returnDesc = resultDesc.toArray(new String[resultDesc.size()]);
                String[] returnImg = resultImg.toArray(new String[resultImg.size()]);
                String[] returnId = resultId.toArray(new String[resultId.size()]);
                String[] returnCreated = resultCreated.toArray(new String[resultCreated.size()]);

                servicesAdapter = new ServicesAdapter(getContext(), returnNames, returnDesc, returnImg, returnId, returnCreated);
                recyclerView.setAdapter(servicesAdapter);

            }

            @Override
            public void onSearchComplete(int j, @NotNull CharSequence charSequence) {
                Boolean yes;
                ArrayList<String> resultNames = new ArrayList<String>();
                ArrayList<String> resultDesc = new ArrayList<String>();
                ArrayList<String> resultImg = new ArrayList<String>();
                ArrayList<String> resultId = new ArrayList<String>();
                ArrayList<String> resultCreated = new ArrayList<String>();

                if (charSequence.toString().isEmpty()) {
                    servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id, created);
                    recyclerView.setAdapter(servicesAdapter);
                }

                for (int i = 0; i < names.length; i++) {
                    yes = names[i].contains(charSequence);
                    if (yes) {
                        resultNames.add(names[i]);
                        resultDesc.add(desc[i]);
                        resultImg.add(img[i]);
                        resultId.add(id[i]);
                        resultCreated.add(created[i]);
                    }
                }

                String[] returnNames = resultNames.toArray(new String[resultNames.size()]);
                String[] returnDesc = resultDesc.toArray(new String[resultDesc.size()]);
                String[] returnImg = resultImg.toArray(new String[resultImg.size()]);
                String[] returnId = resultId.toArray(new String[resultId.size()]);
                String[] returnCreated = resultCreated.toArray(new String[resultCreated.size()]);

                servicesAdapter = new ServicesAdapter(getContext(), returnNames, returnDesc, returnImg, returnId, returnCreated);
                recyclerView.setAdapter(servicesAdapter);
            }

            @Override
            public void onSearchItemRemoved(int j) {
                servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id, created);
                recyclerView.setAdapter(servicesAdapter);
            }

            @Override
            public void onItemSelected(int j, @NotNull CharSequence charSequence) {
                Boolean yes;
                ArrayList<String> resultNames = new ArrayList<String>();
                ArrayList<String> resultDesc = new ArrayList<String>();
                ArrayList<String> resultImg = new ArrayList<String>();
                ArrayList<String> resultId = new ArrayList<String>();
                ArrayList<String> resultCreated = new ArrayList<String>();

                if (charSequence.toString().isEmpty()) {
                    servicesAdapter = new ServicesAdapter(getContext(), names, desc, img, id, created);
                    recyclerView.setAdapter(servicesAdapter);
                }

                for (int i = 0; i < names.length; i++) {
                    yes = names[i].contains(charSequence);
                    if (yes) {
                        resultNames.add(names[i]);
                        resultDesc.add(desc[i]);
                        resultImg.add(img[i]);
                        resultId.add(id[i]);
                        resultCreated.add(created[i]);
                    }
                }

                String[] returnNames = resultNames.toArray(new String[resultNames.size()]);
                String[] returnDesc = resultDesc.toArray(new String[resultDesc.size()]);
                String[] returnImg = resultImg.toArray(new String[resultImg.size()]);
                String[] returnId = resultId.toArray(new String[resultId.size()]);
                String[] returnCreated = resultCreated.toArray(new String[resultCreated.size()]);

                servicesAdapter = new ServicesAdapter(getContext(), returnNames, returnDesc, returnImg, returnId, returnCreated);
                recyclerView.setAdapter(servicesAdapter);
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
        spinKitView = root.findViewById(R.id.spin_kit);
        //searchView.setQueryHint("Pesquisar");
        swipe = root.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callBackend(tipoServico);
            }
        });

        FloatingActionButton fab2 = root.findViewById(R.id.fab2);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.a);
        fadeIn.setDuration(300);
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.b);
        fadeOut.setDuration(300);
        LinearLayout layout = root.findViewById(R.id.linearSearchView);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(y==0) {
                    layout.setVisibility(View.VISIBLE);
                    layout.startAnimation(fadeIn);
                    y++;
                }
                else{
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            layout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    layout.startAnimation(fadeOut);

                    //
                    y--;
                }
            }
        });

        return root;
    }
}