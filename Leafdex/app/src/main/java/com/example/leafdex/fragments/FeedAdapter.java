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
import com.example.leafdex.Product;
import com.example.leafdex.R;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    private ArrayList<Product> productsList;
    private Context context;
    private List<String> images;
    private FeedAdapterViewClickListener listener;
    
    public FeedAdapter(Context context, ArrayList<Product> productsList,  List<String> images,FeedAdapterViewClickListener listener){
        this.context = context;
        this.productsList = productsList;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_item, parent,  false);
                return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        String product_name = productsList.get(position).getProduct();
        holder.feed_textView.setText(product_name);
        //holder.feed_imageView.setImageResource(images.get(position));
        Glide.with(context).load(images.get(position)).into(holder.feed_imageView);
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
    
    public interface FeedAdapterViewClickListener{
        void onClick(View v, int position);
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView feed_imageView;
        TextView feed_textView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feed_imageView = itemView.findViewById(R.id.feed_item_image);
            feed_textView = itemView.findViewById(R.id.feed_item_textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
