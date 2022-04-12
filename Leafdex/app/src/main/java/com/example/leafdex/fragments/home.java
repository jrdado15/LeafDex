package com.example.leafdex.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    private String filePath, comName, price, desc, userID;
    private Uri filePathUri;
    private ArrayList<ArrayList<String>> posts;

    private View view;
    private ImageView postIV;
    private EditText priceET, descET;
    private TextView comNameET;
    private Button postBtn1, postBtn2;

    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;

    //START NG RECYCLER VIEW PARA SA FEEDS XD
    private RecyclerView mRecyclerView;
    private List<String> mImages;
    private ArrayList<Product> productList;
    private List<String> mPrices;
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
        feedAdapter = new FeedAdapter(getActivity(), productList, mImages, listener, mPrices);

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
                        post.add(childDataSnapshot.child("desc").getValue().toString()); //post plant description
                        post.add(childDataSnapshot.child("imageURL").getValue().toString()); //post plant image
                        post.add(childDataSnapshot.child("userID").getValue().toString()); //post user
                        post.add(childDataSnapshot.child("dateTime").getValue().toString()); //post date and time
                        post.add(childDataSnapshot.child("price").getValue().toString()); //post price
                        posts.add(post);
                    }

                    //Addition of posts into recycler view
                    for(ArrayList<String> childPosts : posts){
                        if(!childPosts.isEmpty()){
                            productList.add(new Product(childPosts.get(1)));
                            mImages.add(childPosts.get(3));
                            mPrices.add(childPosts.get(6));
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
                //do whatever
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("POSTS", "Error found: " + error.toString());
            }
        });

        if(bundle != null) {
            filePath = bundle.getString("filePath");
            comName = bundle.getString("comName");
        }
        if(filePath != null && comName != null) {
            view = inflater.inflate(R.layout.fragment_home_post, container, false);
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            postIV = (ImageView) view.findViewById(R.id.postImageView);
            comNameET = (TextView) view.findViewById(R.id.tv_post_plant);
            priceET = (EditText) view.findViewById(R.id.et_post_price);
            descET = (EditText) view.findViewById(R.id.et_post_description);
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
                    price = priceET.getText().toString().trim();
                    desc = descET.getText().toString().trim();
                    if(price.isEmpty()) {
                        priceET.setError("Plant price is required!");
                        priceET.requestFocus();
                        return;
                    }
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
                                        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
                                        String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
                                        String timeDate = currentTime + " - " + currentDate;
                                        Log.d("TAG", timeDate);
                                        Post post = new Post(downloadURL, comName, desc, userID, timeDate, price);
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

    private void setOnClickListener() {
        listener = new FeedAdapter.FeedAdapterViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), Product_info.class);
                intent.putExtra("product_key", posts.get(position).get(0));
                Log.d("POSTS", posts.get(position).get(0));
                getActivity().startActivity(intent);
            }
        };
    }
}