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
    private Context context;
    private ArrayList<Product> productsList;
    private List<String> images;
    private List<String> prices;
    private FeedAdapterViewClickListener listener;

    
    public FeedAdapter(Context context, ArrayList<Product> productsList, List<String> images, List<String> prices, FeedAdapterViewClickListener listener){
        this.context = context;
        this.productsList = productsList;
        this.images = images;
        this.prices = prices;
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
        Glide.with(context).load(images.get(position)).into(holder.feed_imageView);
        holder.feed_price_textView.setText("₱" + prices.get(position));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
    
    public interface FeedAdapterViewClickListener{
        void onClick(View v, int position);
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView feed_textView;
        ImageView feed_imageView;
        TextView feed_price_textView;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            feed_textView = itemView.findViewById(R.id.feed_item_textView);
            feed_imageView = itemView.findViewById(R.id.feed_item_image);
            feed_price_textView = itemView.findViewById(R.id.feed_price_textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}
