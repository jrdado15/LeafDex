package com.example.leafdex.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.leafdex.R;

import java.util.List;

public class EncycAdapter extends RecyclerView.Adapter<EncycAdapter.EncycViewHolder> {
    private Context context;
    private List<String> comName;
    private List<String> sciName;
    private List<String> images;
    private EncycAdapterViewClickListener listener;


    public EncycAdapter(Context context, List<String> comName, List<String> sciName, List<String> images, EncycAdapterViewClickListener listener){
        this.context = context;
        this.comName = comName;
        this.sciName = sciName;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EncycViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_item_encyclopedia, parent,  false);
        return new EncycViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EncycViewHolder holder, int position) {
        holder.enc_comName.setText(comName.get(position));
        holder.enc_sciName.setText(sciName.get(position));
        Glide.with(context).load(images.get(position)).into(holder.enc_image);
    }

    @Override
    public int getItemCount() {
        return comName.size();
    }

    public interface EncycAdapterViewClickListener{
        void onClick(View v, int position);
    }

    public class EncycViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView enc_comName;
        TextView enc_sciName;
        ImageView enc_image;

        public EncycViewHolder(@NonNull View itemView) {
            super(itemView);
            enc_comName = itemView.findViewById(R.id.enc_comName);
            enc_sciName = itemView.findViewById(R.id.enc_sciName);
            enc_image = itemView.findViewById(R.id.enc_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
