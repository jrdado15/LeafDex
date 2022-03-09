package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Register extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private TextView login;
    private Button register;
    private EditText fname, lname, email, password, confirm, contact, birthdate;
    private Spinner sex;
    private String choice;
    private DatePickerDialog datePickerDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        login = (TextView) findViewById(R.id.tv_nav_to_signin);
        login.setOnClickListener(this);

        register = (Button) findViewById(R.id.btn_register);
        register.setOnClickListener(this);

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
                }, year, day, month);
                datePickerDialog.show();
            }
        });
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
            case R.id.btn_register:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String sfname = fname.getText().toString().trim();
        String slname = lname.getText().toString().trim();
        String semail = email.getText().toString().trim();
        String spassword = password.getText().toString().trim();
        String sconfirm = confirm.getText().toString().trim();
        String scontact = contact.getText().toString().trim();
        String ssex = choice;
        String sbirthdate = birthdate.getText().toString().trim();

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

        mAuth.createUserWithEmailAndPassword(semail, spassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(sfname, slname, semail, scontact, ssex, sbirthdate);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registered successfully.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent (Register.this, Home.class));
                                    } else {
                                        Toast.makeText(Register.this, "Failed to register.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Register.this, "Failed to register.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}