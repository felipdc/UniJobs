package com.beesocial.unijobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beesocial.unijobs.R;
import com.beesocial.unijobs.activities.EditServiceActivity;
import com.beesocial.unijobs.activities.MyServicesActivity;
import com.beesocial.unijobs.activities.ServiceDetailActivity;
import com.beesocial.unijobs.storage.SharedPrefManager;
import com.bumptech.glide.Glide;
import com.pd.chocobar.ChocoBar;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder> {

    private String[] names, desc, img, id, phones, facebookLinks;
    private Context context;

    public ServicesAdapter(Context context, String[] names, String[] desc, String[] img, String[] id) {
        this.names = names;
        this.desc = desc;
        this.img = img;
        this.context = context;
        this.id = id;
    }

    public ServicesAdapter(Context context, String[] names, String[] desc, String[] img, String[] id, String[] facebookLinks, String[] phones) {
        this.names = names;
        this.desc = desc;
        this.img = img;
        this.context = context;
        this.id = id;
        this.phones = phones;
        this.facebookLinks = facebookLinks;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_card_view, parent, false);

        return new ServiceViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        holder.serviceTitleTextView.setText(names[position]);
        holder.serviceDescriptionTextView.setText(desc[position]);
        String encodedImage = img[position];
        if (encodedImage != null && encodedImage!="-1") {
            final String pureBase64Encoded = encodedImage.substring(encodedImage.indexOf(",") + 1);
            final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            //Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Glide.with(holder.itemView).load(decodedBytes).centerCrop().into(holder.serviceImageView);
            //holder.serviceImageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ServicesAdapter adapter;

        ImageView serviceImageView;
        TextView serviceTitleTextView;
        TextView serviceDescriptionTextView;

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
            if (SharedPrefManager.getInstance(context).isLoggedIn()) {
                String currentName = names[getAdapterPosition()];
                String currentDesc = desc[getAdapterPosition()];
                String currentImg = img[getAdapterPosition()];
                String currentId = id[getAdapterPosition()];
                String phone = phones[getAdapterPosition()];
                String facebookLink = facebookLinks[getAdapterPosition()];

                Intent intent;
                if (context instanceof MyServicesActivity) {
                    intent = new Intent(context, EditServiceActivity.class);
                    intent.putExtra("service_id", currentId);
                } else {
                    intent = new Intent(context, ServiceDetailActivity.class);
                    intent.putExtra("phone", phone);
                    intent.putExtra("facebook_link", facebookLink);
                }

                intent.putExtra("service_title", currentName);
                intent.putExtra("service_desc", currentDesc);

                if (currentImg != null) {
                    final String pureBase64Encoded = currentImg.substring(currentImg.indexOf(",") + 1);
                    SharedPrefManager.getInstance(context.getApplicationContext()).saveId(pureBase64Encoded);
                    //intent.putExtra("service_img", pureBase64Encoded);
                } else {
                    SharedPrefManager.getInstance(context.getApplicationContext()).saveId("-1");
                }
                context.startActivity(intent);
            } else {
                ChocoBar.builder().setView(v)
                        .setText("Você precisa estar autenticado para visualizar os detalhes!")
                        .setDuration(ChocoBar.LENGTH_LONG)
                        .setActionText(android.R.string.ok)
                        .red()
                        .show();
            }
        }
    }
}