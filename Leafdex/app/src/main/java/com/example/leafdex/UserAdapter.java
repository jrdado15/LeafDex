package com.example.leafdex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.leafdex.fragments.YourPostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUser;
    private static UserClickListener listener;

    public UserAdapter(Context mContext, List<User> mUser, UserClickListener listener) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = mUser.get(position);
        Glide.with(mContext).load(user.imageURL).into(holder.user_image);
        holder.user_name.setText(user.fname + " " + user.lname);
        /*
        if(user.status.equals("online")) {
            holder.onstatus.setVisibility(View.VISIBLE);
            holder.offstatus.setVisibility(View.GONE);
        } else {
            holder.onstatus.setVisibility(View.GONE);
            holder.offstatus.setVisibility(View.VISIBLE);
        }
        */
    }

    @Override
    public int getItemCount() { return mUser.size(); }

    public interface UserClickListener {
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CircleImageView user_image;
        public TextView user_name;
        // public CircleImageView onstatus;
        // public CircleImageView offstatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            // onstatus = itemView.findViewById(R.id.user_onstatus);
            // offstatus = itemView.findViewById(R.id.user_offstatus);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
