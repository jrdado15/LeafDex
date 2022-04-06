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

public class YourPostsAdapter extends RecyclerView.Adapter<YourPostsAdapter.YourPostsViewHolder>{
    private Context context;
    private List<String> titles;
    private List<String> images;

    public YourPostsAdapter(Context context, List<String> titles, List<String> images){
        this.context = context;
        this.titles = titles;
        this.images = images;
    }


    @NonNull
    @Override
    public YourPostsAdapter.YourPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_item_own_post, parent,  false);
        return new YourPostsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull YourPostsAdapter.YourPostsViewHolder holder, int position) {
        holder.user_post_item_textView.setText(titles.get(position));
        //holder.user_post_item_image.setImageResource(images.get(position));
        Glide.with(context).load(images.get(position)).into(holder.user_post_item_image);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class YourPostsViewHolder extends RecyclerView.ViewHolder{
        ImageView user_post_item_image;
        TextView  user_post_item_textView;
        public YourPostsViewHolder(@NonNull View itemView) {
            super(itemView);

            user_post_item_textView = itemView.findViewById(R.id.user_post_item_textView);
            user_post_item_image = itemView.findViewById(R.id.user_post_item_image);
        }
    }
}
