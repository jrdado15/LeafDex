package com.example.leafdex.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leafdex.Product;
import com.example.leafdex.Product_info;
import com.example.leafdex.R;
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
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<ArrayList<String>> posts;

    private View view;

    private DatabaseReference reference;

    private RecyclerView mRecyclerView;
    private ArrayList<Product> productList, search_productList;
    private List<String> mImages, search_mImages;
    private List<String> mPrices, search_mPrices;
    private FeedAdapter feedAdapter;
    private FeedAdapter.FeedAdapterViewClickListener listener, search_listener;
    private List<Integer> search_position;

    public home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = getArguments();
        productList = new ArrayList<>();
        mImages = new ArrayList<>();
        mPrices = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_feeds);
        setOnClickListener();
        feedAdapter = new FeedAdapter(getActivity(), productList, mImages, mPrices, listener);

        posts = new ArrayList<ArrayList<String>>();
        Query query = reference.child("Posts");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    //Retrieval of data
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        ArrayList<String> post = new ArrayList<String>();
                        post.add(childDataSnapshot.getKey()); //post key
                        post.add(childDataSnapshot.child("comName").getValue().toString()); //post plant name
                        post.add(childDataSnapshot.child("imageURL").getValue().toString()); //post plant image
                        post.add(childDataSnapshot.child("price").getValue().toString()); //post price
                        posts.add(post);
                    }

                    //Addition of posts into recycler view
                    for(ArrayList<String> childPosts : posts){
                        if(!childPosts.isEmpty()){
                            productList.add(new Product(childPosts.get(1)));
                            mImages.add(childPosts.get(2));
                            mPrices.add(childPosts.get(3));
                        }
                    }
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setAdapter(feedAdapter);
                } else {
                    Log.d("POSTS", "No posts found.");
                }
            }

            public void onSuccess(@NonNull Void T) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("POSTS", "Error found: " + error.toString());
            }
        });

        EditText search_items = view.findViewById(R.id.search_items);
        search_items.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchItems(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void setOnClickListener() {
        listener = new FeedAdapter.FeedAdapterViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), Product_info.class);
                intent.putExtra("product_key", posts.get(position).get(0));
                intent.putExtra("signal", "home");
                getActivity().startActivity(intent);
            }
        };
        search_listener = new FeedAdapter.FeedAdapterViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), Product_info.class);
                intent.putExtra("product_key", posts.get(search_position.get(position)).get(0));
                intent.putExtra("signal", "home");
                getActivity().startActivity(intent);
            }
        };
    }

    private void searchItems(String s) {
        search_productList = new ArrayList<>();
        search_mImages = new ArrayList<>();
        search_mPrices = new ArrayList<>();
        search_position = new ArrayList<>();
        for(int i = 0; i < productList.size(); i++) {
            if(productList.get(i).getProduct().toLowerCase().matches(s.toLowerCase() + "(.*)")) {
                search_productList.add(productList.get(i));
                search_mImages.add(mImages.get(i));
                search_mPrices.add(mPrices.get(i));
                search_position.add(i);
            }
        }
        feedAdapter = new FeedAdapter(getActivity(), search_productList, search_mImages, search_mPrices, search_listener);
        mRecyclerView.setAdapter(feedAdapter);
    }
}