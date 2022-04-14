package com.example.leafdex;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.UUID;

public class Register extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView login;
    private Button register;
    private ImageView pic;
    private Uri picUri;
    private String downloadURL;
    private EditText fname, lname, email, password, confirm, contact, birthdate;
    private Spinner sex;
    private String choice, noPic;
    private DatePickerDialog datePickerDialog;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        login = (TextView) findViewById(R.id.tv_nav_to_signin);
        login.setOnClickListener(this);

        register = (Button) findViewById(R.id.btn_register);
        register.setOnClickListener(this);

        pic = (ImageView) findViewById(R.id.imageView);
        pic.setOnClickListener(this);

        fname = (EditText) findViewById(R.id.input_first_name);
        lname = (EditText) findViewById(R.id.input_last_name);
        email = (EditText) findViewById(R.id.input_emailAddress);
        password = (EditText) findViewById(R.id.input_password);
        confirm = (EditText) findViewById(R.id.input_confirm_password);
        contact = (EditText) findViewById(R.id.input_phone_number);
        sex = (Spinner) findViewById(R.id.input_Sex);
        birthdate = (EditText) findViewById(R.id.input_birthDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(adapter);
        sex.setOnItemSelectedListener(this);

        birthdate.setText("1/1/2000");
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        birthdate.setText((month+1) + "/" + day + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        noPic = "true";
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        choice = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.tv_nav_to_signin:
                startActivity(new Intent (this, Login.class));
                break;
            case R.id.imageView:
                choosePicture();
                break;
            case R.id.btn_register:
                registerUser();
                break;
        }
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
                    picUri = data.getData();
                    pic.setImageURI(picUri);
                    noPic = "false";
                }
            }
        }
    );

    private void registerUser() {
        String sfname = fname.getText().toString().trim();
        String slname = lname.getText().toString().trim();
        String semail = email.getText().toString().trim();
        String spassword = password.getText().toString().trim();
        String sconfirm = confirm.getText().toString().trim();
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

        if(semail.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(spassword.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        if(sconfirm.isEmpty()) {
            confirm.setError("Password is required!");
            confirm.requestFocus();
            return;
        }

        if(scontact.isEmpty()) {
            contact.setError("Contact number is required!");
            contact.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(semail).matches()) {
            email.setError("Please provide valid email.");
            email.requestFocus();
            return;
        }

        if(spassword.length() < 6) {
            password.setError("Minimum length should be 6 characters.");
            password.requestFocus();
            return;
        }

        if(!sconfirm.equals(spassword)) {
            confirm.setError("Two passwords didn't match.");
            confirm.requestFocus();
            return;
        }

        if(scontact.length() < 11) {
            contact.setError("Please provide valid contact number.");
            contact.requestFocus();
            return;
        }

        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Creating user profile...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        //Picture upload
        if(noPic.equals("false")) {
            ref.putFile(picUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadURL = uri.toString();

                                //If picture is uploaded, upload table
                                mAuth.createUserWithEmailAndPassword(semail, spassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            String search = sfname.toLowerCase() + " " + slname.toLowerCase();
                                            User user = new User(sfname, slname, semail, scontact, ssex, sbirthdate, downloadURL, search);

                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    mProgressDialog.dismiss();
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(Register.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent (Register.this, Home.class));
                                                        finish();
                                                    } else {
                                                        Toast.makeText(Register.this, "Failed to register. Please try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(Register.this, "Failed to register.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Register.this, "Failed to upload image. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
        } else {
            downloadURL = "https://firebasestorage.googleapis.com/v0/b/leafdex-8b555.appspot.com/o/images%2Fplaceholder.png?alt=media&token=1ad1b982-e26d-43f3-bf0e-7fd99173f95d";
            
            mAuth.createUserWithEmailAndPassword(semail, spassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            String search = sfname.toLowerCase() + " " + slname.toLowerCase();
                            User user = new User(sfname, slname, semail, scontact, ssex, sbirthdate, downloadURL, search);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mProgressDialog.dismiss();
                                    if(task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registered successfully.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent (Register.this, Home.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "Failed to register. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(Register.this, "Failed to register.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent (Register.this, Login.class));
        finish();
        return;
    }
}