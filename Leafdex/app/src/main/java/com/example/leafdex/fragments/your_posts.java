package com.example.leafdex.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leafdex.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link your_posts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class your_posts extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference userReference;
    private DatabaseReference reference;

    private String userID;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private RecyclerView mRecyclerView;
    private List<String> titles;
    private List<String> mImages;
    private YourPostsAdapter yourPostsAdapter;

    public your_posts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment your_posts.
     */
    // TODO: Rename and change types and number of parameters
    public static your_posts newInstance(String param1, String param2) {
        your_posts fragment = new your_posts();
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
        reference = FirebaseDatabase.getInstance().getReference(); //Root
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users"); //Users Parent
        reference = FirebaseDatabase.getInstance().getReference(); //Root
        userID = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ArrayList<ArrayList<String>> posts = new ArrayList<ArrayList<String>>();
        view = inflater.inflate(R.layout.fragment_your_posts, container, false);

        Query query = reference.child("Posts").orderByChild("userID").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    //retrieval of user's posts
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        ArrayList<String> post = new ArrayList<String>();
                        post.add(childDataSnapshot.getKey()); //post key -- 0
                        post.add(childDataSnapshot.child("comName").getValue().toString()); //post plant name -- 1
                        post.add(childDataSnapshot.child("desc").getValue().toString()); //post plant description -- 2
                        post.add(childDataSnapshot.child("imageURL").getValue().toString()); //post plant image -- 3
                        posts.add(post);
                    }

                    //Addition of user's posts into recyclerview
                    Bundle bundle = getArguments();
                    titles = new ArrayList<>();
                    mImages = new ArrayList<>();
                    mRecyclerView = view.findViewById(R.id.rv_your_posts);
                    yourPostsAdapter = new YourPostsAdapter(getActivity(), titles, mImages);

                    // TODO: GANITO KUMUHA VALUES @CJ
                    for(ArrayList<String> childPosts : posts){
                        if(!childPosts.isEmpty()){
                            titles.add(childPosts.get(1));
                            mImages.add(childPosts.get(3));
                        }
                    }

                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setAdapter(yourPostsAdapter);
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
}