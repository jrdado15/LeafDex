package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chat_users extends AppCompatActivity {

    private TextView posterName_TV;
    private EditText textbox_ET;
    private ImageButton send;
    private String message;

    private ChatAdapter chatAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        String userID = bundle.getString("userID");
        String posterID = bundle.getString("posterID");
        String posterName = bundle.getString("posterName");

        reference = FirebaseDatabase.getInstance().getReference();
        posterName_TV = findViewById(R.id.posterName);
        textbox_ET = findViewById(R.id.textbox);
        send = findViewById(R.id.send_btn);

        posterName_TV.setText(posterName);
        readChats(userID, posterID); // display chat

        // if send button is clicked
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = textbox_ET.getText().toString().trim();
                if(!message.isEmpty()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", userID);
                    hashMap.put("receiver", posterID);
                    hashMap.put("message", message);
                    reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d("TAG", "MESSAGE SENT");
                            } else {
                                Log.d("TAG", "MESSAGE NOT SENT");
                            }
                        }
                    });
                }
                textbox_ET.setText("");
            }
        });
    }

    private void readChats(final String userID, final String posterID) {
        mChat = new ArrayList<>();
        reference.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Chat chat = datasnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(userID) && chat.getSender().equals(posterID) ||
                            chat.getReceiver().equals(posterID) && chat.getSender().equals(userID)) {
                        mChat.add(chat);
                    }
                    chatAdapter = new ChatAdapter(Chat_users.this, mChat);
                    recyclerView.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}