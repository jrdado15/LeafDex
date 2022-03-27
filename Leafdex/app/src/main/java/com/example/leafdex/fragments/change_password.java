package com.example.leafdex.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leafdex.Home;
import com.example.leafdex.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link change_password#newInstance} factory method to
 * create an instance of this fragment.
 */
public class change_password extends Fragment {

    private Button changePassword;
    private View view;
    private EditText oldPassword, password, confirmPassword;
    private String userEmail;

    private FirebaseUser user;
    private AuthCredential credential;

    private ProgressDialog mProgressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public change_password() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment change_password.
     */
    // TODO: Rename and change types and number of parameters
    public static change_password newInstance(String param1, String param2) {
        change_password fragment = new change_password();
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = user.getEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        changePassword = (Button) view.findViewById(R.id.btn_changePassword);
        oldPassword = (EditText) view.findViewById(R.id.cp_oldPassword);
        password = (EditText) view.findViewById(R.id.cp_password);
        confirmPassword = (EditText) view.findViewById(R.id.cp_confirmPassword);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePw();
            }
        });

        return view;
    }

    private void changePw() {
        String soldPassword = oldPassword.getText().toString().trim();
        String spassword = password.getText().toString().trim();
        String sconfirmPassword = confirmPassword.getText().toString().trim();

        if(soldPassword.isEmpty()) {
            oldPassword.setError("Current password is required!");
            oldPassword.requestFocus();
            return;
        }

        if(spassword.isEmpty()) {
            password.setError("New password is required!");
            password.requestFocus();
            return;
        }

        if(sconfirmPassword.isEmpty()) {
            confirmPassword.setError("New password is required!");
            confirmPassword.requestFocus();
            return;
        }

        if(spassword.length() < 6) {
            password.setError("Minimum length should be 6 characters.");
            password.requestFocus();
            return;
        }

        if(!sconfirmPassword.equals(spassword)) {
            confirmPassword.setError("Two passwords didn't match.");
            confirmPassword.requestFocus();
            return;
        }

        if(!(userEmail.isEmpty() && soldPassword.isEmpty())) {
            credential = EmailAuthProvider.getCredential(userEmail, soldPassword);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(spassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Changed password successfully.", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getActivity().getBaseContext(), Home.class);
                                            getActivity().startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to change password.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Failed to change password. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}