package com.example.leafdex.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.leafdex.Home;
import com.example.leafdex.Product;
import com.example.leafdex.Product_info;
import com.example.leafdex.R;
import com.example.leafdex.fragments.parsers.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private String filePath, comName, desc, userID;
    private Uri filePathUri;

    private View view;
    private ImageView postIV;
    private EditText comNameET, descET;
    private Button postBtn1, postBtn2;

    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    //START NG RECYCLER VIEW PARA SA FEEDS XD
    private RecyclerView mRecyclerView;
    private ArrayList<Product> productList;
    private List<Integer> mImages;
    private FeedAdapter feedAdapter;
    private FeedAdapter.FeedAdapterViewClickListener listener;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = getArguments();
        productList = new ArrayList<>();
        mImages = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.rv_feeds);
        setOnClickListener();
        feedAdapter = new FeedAdapter(getActivity(), productList, mImages, listener);
        //LAGAY SA ARRAY TYM
        mImages.add(R.drawable.sample);
        mImages.add(R.drawable.sample);
        mImages.add(R.drawable.sample);
        mImages.add(R.drawable.sample);
        setProductList();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(feedAdapter);
        if(bundle != null) {
            filePath = bundle.getString("filePath");
            comName = bundle.getString("comName");
        }
        if(filePath != null && comName != null) {
            view = inflater.inflate(R.layout.fragment_home_post, container, false);
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            postIV = (ImageView) view.findViewById(R.id.postImageView);
            comNameET = (EditText) view.findViewById(R.id.postEditText1);
            descET = (EditText) view.findViewById(R.id.postEditText2);
            postBtn1 = (Button) view.findViewById(R.id.postButton1);
            postBtn2 = (Button) view.findViewById(R.id.postButton2);
            Log.d("TAG", filePath);
            filePathUri = Uri.parse("file:///" + filePath);
            postIV.setImageURI(filePathUri);
            comNameET.setText(comName);
            KeyListener keyListener = comNameET.getKeyListener();
            comNameET.setKeyListener(null);
            postBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    desc = descET.getText().toString().trim();
                    if(desc.isEmpty()) {
                        descET.setError("Plant description is required!");
                        descET.requestFocus();
                        return;
                    }
                    final String randomKey = UUID.randomUUID().toString();
                    StorageReference ref = storageReference.child("posts/" + randomKey);
                    ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setMessage("Posting...");
                    mProgressDialog.show();
                    mProgressDialog.setCancelable(false);
                    ref.putFile(filePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadURL = uri.toString();
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        userID = user.getUid();
                                        Post post = new Post(downloadURL, comName, desc, userID);
                                        String key = FirebaseDatabase.getInstance().getReference("Posts").push().getKey();
                                        FirebaseDatabase.getInstance().getReference("Posts").child(key)
                                                .setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mProgressDialog.dismiss();
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Posted successfully.", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
                                                    getActivity().startActivity(intent);
                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to post. Please try again.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getActivity(), "Failed to upload image. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        });
                }
            });
            postBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
                    getActivity().startActivity(intent);
                }
            });
        }

        return view;
    }

    private void setProductList(){
        productList.add(new Product("Rubber plant"));
        productList.add(new Product("Mango tree"));
        productList.add(new Product("Money plant"));
        productList.add(new Product("String beans"));
    }


    private void setOnClickListener() {
        listener = new FeedAdapter.FeedAdapterViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), Product_info.class);
                intent.putExtra("product_name", productList.get(position).getProduct());
                getActivity().startActivity(intent);
            }
        };
    }
}