package com.example.nirma.moes5;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.nirma.moes5.model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private String messageRecevierId;
    private String messageRecevierName;

    private Toolbar ChatToolbar;

    private TextView UserNameTitle;
    private TextView UserLastSeen;
    private CircleImageView UserChatProfileImage;

    private ImageButton SendMessageButtuon;
    private ImageButton SelectImageButton;
    private EditText InputMessageText;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String messageSenderId;

    private RecyclerView userMessageList;

    private final List<Messages>messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private static int Gallery_Pick = 1 ;
    private StorageReference messageImageStorageRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        messageRecevierId = getIntent().getExtras().get("visit_user_id").toString();
        messageRecevierName = getIntent().getExtras().get("user_name").toString();
        messageImageStorageRef = FirebaseStorage.getInstance().getReference().child("Messages_Pictures");

        ChatToolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolbar);
        loadingBar = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        UserNameTitle = findViewById(R.id.custom_profile_name);
        UserLastSeen =  findViewById(R.id.custom_user_lastseen);
        UserChatProfileImage =  findViewById(R.id.custom_profile_image);

        SendMessageButtuon = findViewById(R.id.send_message);
        SelectImageButton = findViewById(R.id.select_image);
        InputMessageText = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);

        userMessageList = findViewById(R.id.message_list_of_user);

        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);

        FetchMessages();

        UserNameTitle.setText(messageRecevierName);

        rootRef.child("Users").child(messageRecevierId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.get()
                        .load(userThumb)
                        .networkPolicy(NetworkPolicy.OFFLINE) //Bisa liat profil saat offline
                        .placeholder(R.drawable.default_profile)
                        .into(UserChatProfileImage, new Callback() {
                            @Override
                            public void onSuccess()
                            {

                            }

                            @Override
                            public void onError(Exception e)
                            {
                                Picasso.get()
                                        .load(userThumb)
                                        .placeholder(R.drawable.default_profile)
                                        .into(UserChatProfileImage);
                            }
                        });
                if (online.equals("true"))
                {
                    UserLastSeen.setText("Online");
                }
                else
                {
                    LastSeenTime getTime = new LastSeenTime();
                    long last_seen = Long.parseLong(online);
                    String LastSeenDisplayTime = LastSeenTime.getTimeAgo(last_seen, getApplicationContext());
                    UserLastSeen.setText(LastSeenDisplayTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        SendMessageButtuon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });

        SelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galeryIntent = new Intent();
                galeryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent, Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null)
        {
            loadingBar.setTitle("Sending Chat Image");
            loadingBar.setMessage("Mohon tunggu, sedang mengirim gambar");
            loadingBar.show();

            Uri ImageUri = data.getData();
            final String message_sender_ref = "Message/" + messageSenderId + "/" + messageRecevierId;
            final String message_recevier_ref = "Message/" + messageRecevierId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Message").child(messageSenderId)
                    .child(messageRecevierId).push();
            final String message_push_id = user_message_key.getKey();

            StorageReference filePath = messageImageStorageRef.child(message_push_id + ".jpg");

            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();
                        Map messageTextBody = new HashMap();

                        messageTextBody.put("message",downloadUrl);
                        messageTextBody.put("seen",false);
                        messageTextBody.put("type","image");
                        messageTextBody.put("time", ServerValue.TIMESTAMP);
                        messageTextBody.put("from", messageSenderId);

                        Map messageBodyDtails = new HashMap();

                        messageBodyDtails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
                        messageBodyDtails.put(message_recevier_ref + "/" + message_push_id, messageTextBody);

                        rootRef.updateChildren(messageBodyDtails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                            {
                                if (databaseError != null)
                                {
                                    Log.d("Chat_Log", databaseError.getMessage().toString());
                                }

                                InputMessageText.setText("");
                                loadingBar.dismiss();
                            }
                        });

                        Toast.makeText(ChatActivity.this, "Berhasil mengirim gambar..", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Gagal mengirim gambar..", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void FetchMessages()
    {
        rootRef.child("Message").child(messageSenderId).child(messageRecevierId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage()
    {
        String messageText = InputMessageText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this, "Please write your message.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String message_sender_ref = "Message/" + messageSenderId + "/" + messageRecevierId;
            String message_recevier_ref = "Message/" + messageRecevierId + "/" + messageSenderId;

            DatabaseReference user_message_key = rootRef.child("Message").child(messageSenderId)
                                                        .child(messageRecevierId).push();
            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("seen",false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", messageSenderId);

            Map messageBodyDtails = new HashMap();

            messageBodyDtails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
            messageBodyDtails.put(message_recevier_ref + "/" + message_push_id, messageTextBody);

            rootRef.updateChildren(messageBodyDtails, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if (databaseError != null)
                    {
                        Log.d("Chat_Log",databaseError.getMessage().toString());
                    }

                    InputMessageText.setText("");
                }
            });
        }
    }
}
