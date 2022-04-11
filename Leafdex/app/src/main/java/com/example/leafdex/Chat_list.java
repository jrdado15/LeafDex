package com.example.leafdex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    List<User> search_mUser;
    List<String> search_userList;
    private String userID;
    private UserAdapter.UserClickListener listener;
    private UserAdapter.UserClickListener search_listener;
    private EditText search_users;

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

        search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        search_listener = new UserAdapter.UserClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(Chat_list.this, Chat_users.class);
                intent.putExtra("userID", userID);
                intent.putExtra("posterID", search_userList.get(position));
                intent.putExtra("posterName", search_mUser.get(position).fname + " " + search_mUser.get(position).lname);
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

    private void searchUsers(String s) {
        search_mUser = new ArrayList<>();
        search_userList = new ArrayList<>();
        for(int i = 0; i < userList.size(); i++) {
            if(mUser.get(i).search.matches(s.toLowerCase() + "(.*)")) {
                search_mUser.add(mUser.get(i));
                search_userList.add(userList.get(i));
            }
        }
        userAdapter = new UserAdapter(Chat_list.this, search_mUser, search_listener, search_userList);
        recyclerView.setAdapter(userAdapter);
        /*
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("search").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                userList.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()) {
                    User user = datasnapshot.getValue(User.class);
                    if(!user.email.equals(userEmail)) {
                        mUser.add(user);
                        userList.add(datasnapshot.getKey());
                    }
                }
                userAdapter = new UserAdapter(Chat_list.this, mUser, listener, userList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */
    }
}