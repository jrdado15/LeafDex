package com.example.leafdex.fragments;

import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leafdex.Encyclopedia;
import com.example.leafdex.Product;
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
 * Use the {@link encyclopedia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class encyclopedia extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<ArrayList<String>> plants;
    private ArrayList<String> lastPlant;

    private View view;

    private DatabaseReference reference;

    private RecyclerView mRecyclerView;
    private ArrayList<Product> encycList, search_encycList;
    private List<String> mImages, search_mImages;
    private List<String> mComName;
    private List<String> mSciName, search_mSciName;
    private FeedAdapter feedAdapter;
    private FeedAdapter.FeedAdapterViewClickListener listener, search_listener;
    private List<Integer> search_position;
    private ProgressDialog mProgressDialog;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    public encyclopedia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment encyclopedia.
     */
    // TODO: Rename and change types and number of parameters
    public static encyclopedia newInstance(String param1, String param2) {
        encyclopedia fragment = new encyclopedia();
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = getArguments();
        encycList = new ArrayList<>();
        mImages = new ArrayList<>();
        mComName = new ArrayList<>();
        mSciName = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_feeds);
        setOnClickListener();
        feedAdapter = new FeedAdapter(getActivity(), encycList, mImages, mSciName, listener);

        plants = new ArrayList<ArrayList<String>>();
        Query query = reference.child("Plants");//.limitToFirst(10);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Fetching data from database...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    mProgressDialog.dismiss();

                    //Retrieval of data
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        ArrayList<String> plant = new ArrayList<String>();
                        plant.add(childDataSnapshot.getKey()); //Plant key
                        plant.add(childDataSnapshot.child("common_name").getValue().toString()); //Plant Common name
                        plant.add(childDataSnapshot.child("scientific_name").getValue().toString()); //Plant Sci Name
                        plant.add(childDataSnapshot.child("image_url").getValue().toString()); //Plant Image URL
                        plants.add(plant);
                    }
                    //Collections.reverse(plants);
                    //Addition of posts into recycler view
                    for(ArrayList<String> childPosts : plants){
                        if(!childPosts.isEmpty()){
                            encycList.add(new Product(childPosts.get(1)));
                            mComName.add(childPosts.get(1));
                            mSciName.add(childPosts.get(2));
                            mImages.add(childPosts.get(3));
                        }
                    }
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    mRecyclerView.setAdapter(feedAdapter);
                    /*
                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if (dy > 0) { //check for scroll down
                                visibleItemCount = mLayoutManager.getChildCount();
                                totalItemCount = mLayoutManager.getItemCount();
                                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                                if (loading) {
                                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                        loading = false;
                                        Log.v("...", "Last Item Wow !");
                                    }
                                }
                            }
                        }
                    });
                    */
                } else {
                    mProgressDialog.dismiss();
                    Log.d("POSTS", "No posts found.");
                }
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
                Intent intent = new Intent(getActivity().getBaseContext(), Encyclopedia.class);
                intent.putExtra("comName", mComName.get(position));
                intent.putExtra("sciName", mSciName.get(position));
                getActivity().startActivity(intent);
            }
        };
        search_listener = new FeedAdapter.FeedAdapterViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), Encyclopedia.class);
                intent.putExtra("comName", plants.get(search_position.get(position)).get(1));
                intent.putExtra("sciName", plants.get(search_position.get(position)).get(2));
                getActivity().startActivity(intent);
            }
        };
    }

    private void searchItems(String s) {
        search_encycList = new ArrayList<>();
        search_mImages = new ArrayList<>();
        search_mSciName = new ArrayList<>();
        search_position = new ArrayList<>();
        for(int i = 0; i < encycList.size(); i++) {
            if(encycList.get(i).getProduct().toLowerCase().matches(s.toLowerCase() + "(.*)")) {
                search_encycList.add(encycList.get(i));
                search_mImages.add(mImages.get(i));
                search_mSciName.add(mSciName.get(i));
                search_position.add(i);
            }
        }
        feedAdapter = new FeedAdapter(getActivity(), search_encycList, search_mImages, search_mSciName, search_listener);
        mRecyclerView.setAdapter(feedAdapter);
    }
}