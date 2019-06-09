package com.beesocial.unijobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.activities.ServiceDetailActivity;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    private String[] dataset;
    private Context context;

    public ServicesAdapter(Context context, String[] dataset) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_text_view, parent, false);

        return new ServiceViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.serviceTitleTextView.setText(dataset[position]);
    }

    @Override
    public int getItemCount() {
        return dataset.length;
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView serviceImageView;
        TextView serviceTitleTextView;
        TextView serviceDescriptionTextView;
        private ServicesAdapter adapter;

        public ServiceViewHolder(@NonNull View itemView, ServicesAdapter adapter) {
            super(itemView);

            this.adapter = adapter;

            serviceImageView = itemView.findViewById(R.id.service_image_view);
            serviceTitleTextView = itemView.findViewById(R.id.service_title_text_view);
            serviceDescriptionTextView = itemView.findViewById(R.id.service_description_text_view);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String current = dataset[getAdapterPosition()];

            Intent intent = new Intent(context, ServiceDetailActivity.class);
            intent.putExtra("service_title", current);
            context.startActivity(intent);
        }
    }
}