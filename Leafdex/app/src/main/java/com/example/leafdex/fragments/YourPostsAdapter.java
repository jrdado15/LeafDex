package com.example.leafdex.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.leafdex.Home;
import com.example.leafdex.R;
import com.example.leafdex.fragments.parsers.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class YourPostsAdapter extends RecyclerView.Adapter<YourPostsAdapter.YourPostsViewHolder>{
    private static Context context;
    private static List<String> postIDs;
    private static List<String> titles;
    private static List<String> images;
    private static String imageURL;
    private static DatabaseReference reference;
    private static FirebaseStorage storage;
    private static EditClickListener listener;

    public YourPostsAdapter(Context context, List<String> postIDs, List<String> titles, List<String> images, EditClickListener listener){
        this.context = context;
        this.postIDs = postIDs;
        this.titles = titles;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public YourPostsAdapter.YourPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.grid_item_own_post, parent,  false);
        return new YourPostsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull YourPostsAdapter.YourPostsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.user_post_item_textView.setText(titles.get(position));
        //holder.user_post_item_image.setImageResource(images.get(position));
        Glide.with(context).load(images.get(position)).into(holder.user_post_item_image);
        holder.position = position;
        /* ITO YUNG NAG-OOVERLAP
        holder.user_post_item_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                your_posts_edit fragment = new your_posts_edit();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frag_your_posts, fragment).addToBackStack(null).commit();
            }
        });
        */
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public interface EditClickListener {
        void onClick(View v, int position);
    }

    public static class YourPostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView user_post_item_image;
        TextView  user_post_item_textView;
        Button user_post_item_button2, user_post_item_button4;
        int position;
        public YourPostsViewHolder(@NonNull View itemView) {
            super(itemView);

            user_post_item_textView = itemView.findViewById(R.id.user_post_item_textView);
            user_post_item_image = itemView.findViewById(R.id.user_post_item_image);
            user_post_item_button2 = itemView.findViewById(R.id.button2);
            user_post_item_button4 = itemView.findViewById(R.id.button4);

            user_post_item_button2.setOnClickListener(this);

            user_post_item_button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String postID = postIDs.get(position);
                    Log.d("TAG", postID);

                    reference = FirebaseDatabase.getInstance().getReference("Posts");
                    storage = FirebaseStorage.getInstance();

                    reference.child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Post post = snapshot.getValue(Post.class);
                            if (post != null) {
                                imageURL = post.imageURL;
                                storage.getReferenceFromUrl(imageURL).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                reference.child(postID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_LONG).show();
                                                                } else {
                                                                    Toast.makeText(context, "Failed to delete.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to delete image. Please try again.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
