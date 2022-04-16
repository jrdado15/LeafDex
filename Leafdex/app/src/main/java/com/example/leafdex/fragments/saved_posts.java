package com.example.leafdex.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leafdex.Product_info;
import com.example.leafdex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link saved_posts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class saved_posts extends Fragment {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;
    private View view;
    private RecyclerView mRecyclerView;
    private List<String> postIDs;
    private List<String> titles;
    private List<String> prices;
    private List<String> mImages;
    private SavedPostsAdapter savedPostsAdapter;
    private SavedPostsAdapter.SavedClickListener listener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public saved_posts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment saved_posts.
     */
    // TODO: Rename and change types and number of parameters
    public static saved_posts newInstance(String param1, String param2) {
        saved_posts fragment = new saved_posts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ArrayList<ArrayList<String>> posts = new ArrayList<ArrayList<String>>();
        view =  inflater.inflate(R.layout.fragment_saved_posts, container, false);

        reference.child("Saved").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null) {
                    posts.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        reference.child("Posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot ss) {
                                for(DataSnapshot ds : ss.getChildren()) {
                                    if(ds.getKey().equals(dataSnapshot.getKey())) {
                                        ArrayList<String> post = new ArrayList<String>();
                                        post.add(ds.getKey()); //post key -- 0
                                        post.add(ds.child("comName").getValue().toString()); //post plant name -- 1
                                        post.add(ds.child("price").getValue().toString()); //post plant price -- 2
                                        post.add(ds.child("imageURL").getValue().toString()); //post plant image -- 3
                                        posts.add(post);

                                        postIDs = new ArrayList<>();
                                        titles = new ArrayList<>();
                                        prices = new ArrayList<>();
                                        mImages = new ArrayList<>();
                                        setOnClickListener();
                                        mRecyclerView = view.findViewById(R.id.rv_saved_posts);
                                        savedPostsAdapter = new SavedPostsAdapter(getActivity(), postIDs, titles, prices, mImages, listener);

                                        for(ArrayList<String> childPosts : posts){
                                            if(!childPosts.isEmpty()){
                                                postIDs.add(childPosts.get(0));
                                                titles.add(childPosts.get(1));
                                                prices.add(childPosts.get(2));
                                                mImages.add(childPosts.get(3));
                                            }
                                        }

                                        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
                                        mRecyclerView.setHasFixedSize(true);
                                        mRecyclerView.setAdapter(savedPostsAdapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Log.d("POSTS", "No posts found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("POSTS", "Error found: " + error.toString());
            }
        });

        return view;
    }

    private void setOnClickListener() {
        listener = new SavedPostsAdapter.SavedClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity(), Product_info.class);
                intent.putExtra("product_key", postIDs.get(position));
                intent.putExtra("signal", "saved_posts");
                startActivity(intent);
            }
        };
    }
}