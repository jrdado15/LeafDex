package com.example.leafdex.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leafdex.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link your_posts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class your_posts extends Fragment {

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
    private List<Integer> mImages;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_your_posts, container, false);
        Bundle bundle = getArguments();
        titles = new ArrayList<>();
        mImages = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_your_posts);
        yourPostsAdapter = new YourPostsAdapter(getActivity(), titles, mImages);
        //LAGAY SA ARRAY TYM
        mImages.add(R.drawable.sample);
        mImages.add(R.drawable.sample);
        mImages.add(R.drawable.sample);
        mImages.add(R.drawable.sample);

        titles.add("GOMU GOMU NO ONE");
        titles.add("GOMU GOMU NO TWO");
        titles.add("GOMU GOMU NO THREE");
        titles.add("GOMU GOMU NO FOUR");

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(yourPostsAdapter);

        return view;
    }
}