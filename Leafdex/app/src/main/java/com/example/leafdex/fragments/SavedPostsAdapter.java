package com.example.leafdex.fragments;

import android.annotation.SuppressLint;
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

public class SavedPostsAdapter extends RecyclerView.Adapter<SavedPostsAdapter.SavedPostsViewHolder>{
    private static Context context;
    private static List<String> postIDs;
    private static List<String> titles;
    private static List<String> prices;
    private static List<String> images;
    private static SavedClickListener listener;

    public SavedPostsAdapter(Context context, List<String> postIDs, List<String> titles, List<String> prices, List<String> images, SavedClickListener listener){
        this.context = context;
        this.postIDs = postIDs;
        this.titles = titles;
        this.prices = prices;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavedPostsAdapter.SavedPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_item_saved_post, parent,  false);
        return new SavedPostsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedPostsAdapter.SavedPostsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.saved_post_item_textView.setText(titles.get(position));
        holder.saved_post_item_price.setText("₱" + prices.get(position));
        Glide.with(context).load(images.get(position)).into(holder.saved_post_item_image);
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public interface SavedClickListener {
        void onClick(View v, int position);
    }

    public static class SavedPostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView saved_post_item_textView;
        TextView saved_post_item_price;
        ImageView saved_post_item_image;
        int position;
        public SavedPostsViewHolder(@NonNull View itemView) {
            super(itemView);

            saved_post_item_textView = itemView.findViewById(R.id.saved_post_item_textView);
            saved_post_item_price = itemView.findViewById(R.id.saved_post_item_price);
            saved_post_item_image = itemView.findViewById(R.id.saved_post_item_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
