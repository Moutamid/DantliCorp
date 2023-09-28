package com.moutamid.dantlicorp.Activities.Home;

import static com.moutamid.dantlicorp.helper.Constants.ADMIN_UID;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.moutamid.dantlicorp.Adapter.ChatAdapter;
import com.moutamid.dantlicorp.Model.ChatModel;
import com.moutamid.dantlicorp.Model.UserModel;
import com.moutamid.dantlicorp.R;
import com.moutamid.dantlicorp.helper.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ArrayList<ChatModel> list;
    TextView chatName;
    RecyclerView recyler;
    ImageView back;
    ImageButton send;
    EditText message;
    String ID = ADMIN_UID;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        chatName = findViewById(R.id.chatName);
        recyler = findViewById(R.id.recyler);
        back = findViewById(R.id.back);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);
        String name = "Chat with Admin";
        chatName.setText(name);
        userModel = (UserModel) Stash.getObject("UserDetails", UserModel.class);

        list = new ArrayList<>();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyler.setHasFixedSize(false);

//        Constants.databaseReference().child(Constants.USER).child(  userModel.id)
//                .get().addOnSuccessListener(dataSnapshot -> {
//                    if (dataSnapshot.exists()){
//                        loginUser = dataSnapshot.getValue(UserModel.class);
//                    }
//                });

        getChat(ID);

        send.setOnClickListener(v -> {
            if (!message.getText().toString().isEmpty()) {
                String message_str = message.getText().toString();
                message.setText("");
                long date = new Date().getTime();
                ChatModel conversation = new ChatModel(
                        message_str,
                        userModel.id,
                        ID,
                        date,
                        userModel.name
                );
                Constants.ChatReference.child(userModel.id)
                        .child(ID)
                        .push()
                        .setValue(conversation)
                        .addOnSuccessListener(unused -> {
                            reciver(ID, date, message_str);
                        }).addOnFailureListener(e -> {

                        });
            }
        });
    }

    private void reciver(String ID, long date, String message_str) {
        ChatModel conversation = new ChatModel(
                message_str,
                userModel.id,
                ID,
                date,
                "Admin"
        );
        Constants.ChatReference.child(ID)
                .child(userModel.id)
                .push()
                .setValue(conversation)
                .addOnSuccessListener(unused -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", userModel.name);
                    map.put("chat_id", userModel.id);
                    map.put("message", message_str);
                    map.put("timeStamp", date);
                    map.put("image_url", userModel.image_url);
                    map.put("token", Stash.getString("token"));
                    Constants.ChatListReference.child(ID).child(userModel.id).updateChildren(map)
                            .addOnSuccessListener(unused1 -> {
                                Constants.ChatListReference.child(userModel.id).child(ID).updateChildren(map)
                                        .addOnSuccessListener(unused4 -> {
//                                            new FcmNotificationsSender(
//                                                    "/topics/" + ID, userModel.name,
//                                                    map.get("message").toString(), getApplicationContext(),
//                                                    getActivity()).SendNotifications();
                                        });
                            });
                }).addOnFailureListener(e -> {

                });
    }

    private void getChat(String id) {
        if (userModel.id != null) {
            Constants.ChatReference.child(userModel.id)
                    .child(id)
                    .addChildEventListener(new ChildEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.exists()) {
                                ChatModel conversation = snapshot.getValue(ChatModel.class);
                                list.add(conversation);
                                list.sort(Comparator.comparing(ChatModel::getTimestamps));
                                ChatAdapter adapter = new ChatAdapter(ChatActivity.this, list);
                                recyler.setAdapter(adapter);
                                recyler.scrollToPosition(list.size() - 1);
                                adapter.notifyItemInserted(list.size() - 1);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.exists()) {

                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.exists()) {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}