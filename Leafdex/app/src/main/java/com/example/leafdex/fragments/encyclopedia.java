package com.example.leafdex.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leafdex.Chat_users;
import com.example.leafdex.Encyclopedia;
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
    private List<String> mImages, search_mImages;
    private List<String> mComName, search_mComName;
    private List<String> mSciName, search_mSciName;
    private EncycAdapter encycAdapter;
    private EncycAdapter.EncycAdapterViewClickListener listener, search_listener;
    private List<Integer> search_position;
    private ProgressDialog mProgressDialog;

    private LinearLayoutManager mLayoutManager;
    private boolean loading = true, fromSearch = false;
    private String oldestPostId;
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
        view = inflater.inflate(R.layout.fragment_encyclopedia, container, false);
        Bundle bundle = getArguments();
        mComName = new ArrayList<>();
        mSciName = new ArrayList<>();
        mImages = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.enc_rv_feeds);
        setOnClickListener();
        encycAdapter = new EncycAdapter(getActivity(), mComName, mSciName, mImages, listener);
        oldestPostId = null;

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    if (loading && !fromSearch) {
                        if (!recyclerView.canScrollVertically(1)) {
                            loading = false;
                            Log.v("pagination", "Eto yon: " + oldestPostId);
                            FetchFromDB("");
                        }
                    }
                }
            }
        });
        mRecyclerView.setAdapter(encycAdapter);

        plants = new ArrayList<ArrayList<String>>();
        FetchFromDB("");

        EditText search_items = view.findViewById(R.id.enc_search_items);
        search_items.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String searchText = search_items.getText().toString();
                    searchItems(searchText);
                }
                return false;
            }
        });
                /*.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String searchText = search_items.getText().toString();
                    searchItems(searchText);
                }
                return handled;
            }
        });

                 */


        search_items.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().length() == 0){
                    FetchFromDB("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return view;
    }

    private void FetchFromDB(String searchText){
        loading = true;
        plants.clear();
        Query query;

        if(!searchText.equals("")){
            query = reference.child("Plants");
        } else {
            if(oldestPostId == null){
                query = reference.child("Plants").limitToFirst(10);
                fromSearch = false;

                mComName = new ArrayList<>();
                mSciName = new ArrayList<>();
                mImages = new ArrayList<>();

                encycAdapter = new EncycAdapter(getActivity(), mComName, mSciName, mImages, listener);
                mRecyclerView.setAdapter(encycAdapter);
            } else {
                query = reference.child("Plants").orderByKey().startAfter(oldestPostId).limitToFirst(10);
            }
        }

        Log.d("POSTS", "Lol: " + searchText + " nice " + oldestPostId);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Searching Database...");
        mProgressDialog.setCancelable(false);

        if(!searchText.equals("")){
            mProgressDialog.show();
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        ArrayList<String> plant = new ArrayList<String>();
                        plant.add(childDataSnapshot.getKey()); //Plant key
                        plant.add(childDataSnapshot.child("common_name").getValue().toString()); //Plant Common name
                        plant.add(childDataSnapshot.child("scientific_name").getValue().toString()); //Plant Sci Name
                        plant.add(childDataSnapshot.child("image_url").getValue().toString()); //Plant Image URL
                        if(searchText.equals("")){
                            oldestPostId = childDataSnapshot.getKey();
                        }
                        plants.add(plant);
                    }

                    if(!searchText.equals("")){
                        for(ArrayList<String> childPosts : plants){
                            if(!childPosts.isEmpty()){
                                if(childPosts.get(2).toLowerCase().matches(searchText.toLowerCase() + "(.*)")) {
                                    search_mComName.add(childPosts.get(1));
                                    search_mSciName.add(childPosts.get(2));
                                    search_mImages.add(childPosts.get(3));
                                    search_position.add(Integer.parseInt(childPosts.get(0)));
                                }
                            }
                        }
                        encycAdapter = new EncycAdapter(getActivity(), search_mComName, search_mSciName, search_mImages, search_listener);
                        mRecyclerView.setAdapter(encycAdapter);
                        mProgressDialog.dismiss();
                    } else {
                        for(ArrayList<String> childPosts : plants){
                            if(!childPosts.isEmpty()){
                                mComName.add(childPosts.get(1));
                                mSciName.add(childPosts.get(2));
                                mImages.add(childPosts.get(3));
                            }
                        }
                        encycAdapter.notifyDataSetChanged();
                    }
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
    }

    private void setOnClickListener() {
        listener = new EncycAdapter.EncycAdapterViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), Encyclopedia.class);
                intent.putExtra("comName", mComName.get(position));
                intent.putExtra("sciName", mSciName.get(position));
                getActivity().startActivity(intent);
            }
        };
        search_listener = new EncycAdapter.EncycAdapterViewClickListener() {
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
        search_mComName = new ArrayList<>();
        search_mSciName = new ArrayList<>();
        search_mImages = new ArrayList<>();
        search_position = new ArrayList<>();
        oldestPostId = null;

        fromSearch = true;
        FetchFromDB(s);

        /*
        for(int i = 0; i < mComName.size(); i++) {
            if(mSciName.get(i).toLowerCase().matches(s.toLowerCase() + "(.*)")) {
                search_mComName.add(mComName.get(i));
                search_mSciName.add(mSciName.get(i));
                search_mImages.add(mImages.get(i));
                search_position.add(i);
            }
        }
         */

        //encycAdapter = new EncycAdapter(getActivity(), search_mComName, search_mSciName, search_mImages, search_listener);
        //mRecyclerView.setAdapter(encycAdapter);
    }
}