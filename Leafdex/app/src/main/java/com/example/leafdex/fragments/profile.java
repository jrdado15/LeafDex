package com.example.leafdex.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.leafdex.Home;
import com.example.leafdex.R;
import com.example.leafdex.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profile extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button save;
    private Button cancel;
    private View view;
    private ImageView pic;
    private Uri newPicUri;
    private String downloadURL;
    private EditText fname, lname, email, contact, birthdate;
    private Spinner sex;
    private String userID, choice, samePic;
    private String uimageURL, ufname, ulname, uemail, ucontact, usex, ubirthdate;
    private DatePickerDialog datePickerDialog;

    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ProgressDialog mProgressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public profile() {
        // Required empty public constructor
    }

    public profile(Home home) {
        mProgressDialog = new ProgressDialog(home);
        mProgressDialog.setMessage("Updating user profile...");
        mProgressDialog.setCancelable(false);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profile.
     */
    // TODO: Rename and change types and number of parameters
    public static profile newInstance(String param1, String param2) {
        profile fragment = new profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userID = user.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        save = (Button) view.findViewById(R.id.btn_save);
        cancel = (Button) view.findViewById(R.id.btn_cancel);
        pic = (ImageView) view.findViewById(R.id.imageView);
        fname = (EditText) view.findViewById(R.id.input_first_name);
        lname = (EditText) view.findViewById(R.id.input_last_name);
        email = (EditText) view.findViewById(R.id.input_emailAddress);
        contact = (EditText) view.findViewById(R.id.input_phone_number);
        sex = (Spinner) view.findViewById(R.id.input_Sex);
        birthdate = (EditText) view.findViewById(R.id.input_birthDate);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null) {
                    uimageURL = userProfile.imageURL;
                    ufname = userProfile.fname;
                    ulname = userProfile.lname;
                    uemail = userProfile.email;
                    ucontact = userProfile.contact;
                    usex = userProfile.sex;
                    ubirthdate = userProfile.birthdate;

                    Glide.with(getActivity()).load(uimageURL).into(pic);
                    fname.setText(ufname);
                    lname.setText(ulname);
                    email.setText(uemail);
                    contact.setText(ucontact);
                    if(usex.equals("Male")) {
                        sex.setSelection(0);
                    } else {
                        sex.setSelection(1);
                    }
                    birthdate.setText(ubirthdate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        KeyListener keyListener = email.getKeyListener();
        email.setKeyListener(null);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelChanges();
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(adapter);
        sex.setOnItemSelectedListener(this);

        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        birthdate.setText((month+1) + "/" + day + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        samePic = "true";

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        choice = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void saveChanges() {
        String sfname = fname.getText().toString().trim();
        String slname = lname.getText().toString().trim();
        String scontact = contact.getText().toString().trim();
        String ssex = choice;
        String sbirthdate = birthdate.getText().toString();

        if(sfname.isEmpty()) {
            fname.setError("First name is required!");
            fname.requestFocus();
            return;
        }

        if(slname.isEmpty()) {
            lname.setError("Last name is required!");
            lname.requestFocus();
            return;
        }

        if(scontact.isEmpty()) {
            contact.setError("Contact number is required!");
            contact.requestFocus();
            return;
        }

        if(sbirthdate.isEmpty()) {
            birthdate.setError("Contact number is required!");
            birthdate.requestFocus();
            return;
        }

        if(scontact.length() < 11) {
            contact.setError("Please provide valid contact number.");
            contact.requestFocus();
            return;
        }

        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);
        StorageReference ref2 = storageReference.child("images/" + randomKey);
        mProgressDialog.show();

        if(samePic.equals("false")) {
            ref.putFile(newPicUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(uimageURL);
                                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        downloadURL = uri.toString();

                                        HashMap hashMap = new HashMap();
                                        hashMap.put("imageURL", downloadURL);
                                        hashMap.put("fname", sfname);
                                        hashMap.put("lname", slname);
                                        hashMap.put("contact", scontact);
                                        hashMap.put("sex", ssex);
                                        hashMap.put("birthdate", sbirthdate);

                                        reference.child(userID).updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Updated successfully.", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
                                                    getActivity().startActivity(intent);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Failed to update.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Failed to remove old picture. Please try again.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity(), "Failed to upload picture. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("fname", sfname);
            hashMap.put("lname", slname);
            hashMap.put("contact", scontact);
            hashMap.put("sex", ssex);
            hashMap.put("birthdate", sbirthdate);

            reference.child(userID).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Updated successfully.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
                        getActivity().startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed to update.", Toast.LENGTH_LONG).show();
                    }
                });
        }
    }

    private void cancelChanges() {
        Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
        getActivity().startActivity(intent);
    }

    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    newPicUri = data.getData();
                    pic.setImageURI(newPicUri);
                    samePic = "false";
                }
            }
        }
    );
}