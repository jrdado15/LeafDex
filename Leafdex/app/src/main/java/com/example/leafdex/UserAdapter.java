package com.example.leafdex;

import android.content.Context;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUser;
    private static UserClickListener listener;
    private String last_chat;
    private List<String> userList;

    public UserAdapter(Context mContext, List<User> mUser, UserClickListener listener, List<String> userList) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.listener = listener;
        this.userList = userList;
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
        lastChat(userList.get(position), holder.user_last_chat);
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
        public TextView user_last_chat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            user_name = itemView.findViewById(R.id.user_name);
            // onstatus = itemView.findViewById(R.id.user_onstatus);
            // offstatus = itemView.findViewById(R.id.user_offstatus);
            user_last_chat = itemView.findViewById(R.id.user_last_chat);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    private void lastChat(String userID, TextView user_last_chat) {
        last_chat = "default";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Chat chat = datasnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(user.getUid()) && chat.getSender().equals(userID) ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(user.getUid())) {
                        last_chat = chat.getMessage();
                    }
                }
                switch(last_chat) {
                    case "default":
                        user_last_chat.setText("No message");
                        break;
                    default:
                        user_last_chat.setText(last_chat);
                        break;
                }
                last_chat = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}