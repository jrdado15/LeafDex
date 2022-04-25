package com.example.leafdex;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URL;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int IMG_TYPE_LEFT = 2;
    public static final int IMG_TYPE_RIGHT = 3;

    private Context mContext;
    private List<Chat> mChat;
    private String product_key, plant_name;

    private FirebaseUser user;

    public ChatAdapter(Context mContext, List<Chat> mChats) {
        this.mContext = mContext;
        this.mChat = mChats;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else if(viewType == IMG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right_image, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else if(viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left_image, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        if(checkURL(chat.getMessage())) {
            Glide.with(mContext).load(chat.getMessage()).into(holder.show_image);
        } else if(checkFrom(chat.getMessage())) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            String[] split = chat.getMessage().split("\\s+");
            if(split[2].charAt(split[2].length() - 1) == ')') {
                product_key = split[2].substring(0, split[2].length() - 1);
                plant_name = split[1];
            } else {
                product_key = split[3].substring(0, split[3].length() - 1);
                plant_name = split[1] + " " + split[2];
            }
            String master = chat.getMessage();
            int start1 = master.indexOf(product_key) - 1;
            int stop1 = start1 + 21;
            StringBuilder builder = new StringBuilder(master);
            builder.delete(start1, stop1);
            int start2 = builder.toString().indexOf(plant_name);
            int stop2 = start2 + plant_name.length();
            SpannableString spannableString = new SpannableString(builder.toString());
            spannableString.setSpan(new UnderlineSpan(), start2, stop2, 0);
            if(chat.getReceiver().equals(user.getUid())) {
                holder.show_message.setText(spannableString);
            } else {
                holder.show_message.setText(builder.toString());
            }
            holder.show_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(checkFrom(chat.getMessage()) && product_key.charAt(0) == '-' &&
                            chat.getReceiver().equals(user.getUid())) {
                        Intent intent = new Intent(mContext, Product_info.class);
                        intent.putExtra("product_key", product_key);
                        intent.putExtra("signal", "from");
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            holder.show_message.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView show_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            show_image = itemView.findViewById(R.id.show_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(user.getUid())) {
            if(checkURL(mChat.get(position).getMessage())) {
                return IMG_TYPE_RIGHT;
            }
            return MSG_TYPE_RIGHT;
        } else {
            if(checkURL(mChat.get(position).getMessage())) {
                return IMG_TYPE_LEFT;
            }
            return MSG_TYPE_LEFT;
        }
    }

    public boolean checkURL(String str) {
        try {
            new URL(str).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkFrom(String str) {
        int index = str.indexOf("FROM");
        if(index != -1) {
            return true;
        } else {
            return false;
        }
    }
}