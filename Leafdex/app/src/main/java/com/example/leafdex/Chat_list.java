package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Chat_list extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUser;
    private List<String> userList;
    private String userID;
    private UserAdapter.UserClickListener listener;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        setOnClickListener();
        recyclerView = findViewById(R.id.recycler_view_chat_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");

        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Chat chat = datasnapshot.getValue(Chat.class);
                    if(chat.getSender().equals(userID)) {
                        userList.add(chat.getReceiver());
                    } else {
                        userList.add(chat.getSender());
                    }
                }
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnClickListener() {
        listener = new UserAdapter.UserClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(Chat_list.this, Chat_users.class);
                intent.putExtra("userID", userID);
                intent.putExtra("posterID", userList.get(position));
                intent.putExtra("posterName", mUser.get(position).fname + " " + mUser.get(position).lname);
                startActivity(intent);
            }
        };
    }

    private void readChat() {
        Collections.reverse(userList);
        Set<String> set = new LinkedHashSet<>();
        set.addAll(userList);
        userList.clear();
        userList.addAll(set);

        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        for(int i = 0; i < userList.size(); i++) {
            int finalI = i;
            reference.child(userList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    mUser.add(user);
                    userAdapter = new UserAdapter(Chat_list.this, mUser, listener, userList);
                    recyclerView.setAdapter(userAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}