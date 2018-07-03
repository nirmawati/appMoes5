package com.example.nirma.moes5;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {

    private Button SendFriendRequestButton;
    private Button DeclineFriendRequestButton;
    private TextView ProfileName;
    private TextView ProfileStatus;
    private ImageView ProfileImage;

    private DatabaseReference UserReference;

    private String CURRENT_STATE;
    private DatabaseReference FriendRequestReference;
    private FirebaseAuth mAuth;
    String sender_user_id;
    String receiver_user_id;

    private DatabaseReference FriendsReference;
    private DatabaseReference NotificationReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReference.keepSynced(true);

        NotificationReference = FirebaseDatabase.getInstance().getReference().child("Notification");
        NotificationReference.keepSynced(true);

        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");

        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();
        mAuth = FirebaseAuth.getInstance();
        sender_user_id = mAuth.getCurrentUser().getUid();

        SendFriendRequestButton = findViewById(R.id.send_request);
        DeclineFriendRequestButton = findViewById(R.id.decline_request);
        ProfileImage = findViewById(R.id.profile_visit_user_image);
        ProfileName = findViewById(R.id.user_name_visit);
        ProfileStatus = findViewById(R.id.user_status_visit);

        CURRENT_STATE = "not_friends";

        UserReference.child(receiver_user_id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();

                ProfileName.setText(name);
                ProfileStatus.setText(status);
                Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.default_profile)
                        .into(ProfileImage);
                FriendRequestReference.child(sender_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.hasChild(receiver_user_id))
                                {
                                    String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();

                                    if (req_type.equals("sent"))
                                    {
                                        CURRENT_STATE = "request_sent";
                                        SendFriendRequestButton.setText("Cancel Friend Request");

                                        DisableDeclineRequest();
                                    }
                                    else if (req_type.equals("received"))
                                    {
                                        CURRENT_STATE = "request_received";
                                        SendFriendRequestButton.setText("Accept Friend Request");

                                        DeclineFriendRequestButton.setVisibility(View.VISIBLE);
                                        DeclineFriendRequestButton.setEnabled(true);

                                        DeclineFriendRequestButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                            DeclineFriendRequest();
                                            }
                                            });
                                    }
                                }
                                else
                                {
                                    FriendsReference.child(sender_user_id)
                                            .addListenerForSingleValueEvent(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {
                                                    if (dataSnapshot.hasChild(receiver_user_id))
                                                    {
                                                        CURRENT_STATE = "friends";
                                                        SendFriendRequestButton.setText("Unfriend this Person");

                                                        DisableDeclineRequest();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError)
                                                {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        DisableDeclineRequest();

        if (!sender_user_id.equals(receiver_user_id))
        {
            SendFriendRequestButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SendFriendRequestButton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_friends"));
                    {
                        SendFriendRequestToAPerson();
                    }

                    if (CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }

                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }

                    if (CURRENT_STATE.equals("friends"))
                    {
                        UnFriendaFriend();
                    }
                }
            });
        }
        else
        {
            DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
            SendFriendRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void DeclineFriendRequest() {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            SendFriendRequestButton.setText("Send Friend Request");

                                            DisableDeclineRequest();
                                        }
                                    });
                        }
                    }
                });
    }

    private void UnFriendaFriend()
    {
        FriendsReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendsReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            SendFriendRequestButton.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            SendFriendRequestButton.setText("Send Friend Request");

                                            DisableDeclineRequest();
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        FriendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            SendFriendRequestButton.setEnabled(true);
                                                                            CURRENT_STATE = "friends";
                                                                            SendFriendRequestButton.setText("Unfriend this Person");

                                                                            DisableDeclineRequest();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

//    Bug on cannclerequest
//    Not update from click cancle to sent req
//    harus back dlu klo mau cancle
    private void CancelFriendRequest()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                SendFriendRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                SendFriendRequestButton.setText("Send Friend Request");

                                                DisableDeclineRequest();

                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void SendFriendRequestToAPerson()
    {
        FriendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            FriendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                HashMap<String,String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from",sender_user_id);
                                                notificationData.put("type","request");

                                                NotificationReference.child(receiver_user_id).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    SendFriendRequestButton.setEnabled(true);
                                                                    CURRENT_STATE = "request_sent";
                                                                    SendFriendRequestButton.setText("Cancel Friend Request");

                                                                    DisableDeclineRequest();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void DisableDeclineRequest(){
        DeclineFriendRequestButton.setVisibility(View.INVISIBLE);
        DeclineFriendRequestButton.setEnabled(false);
    }
}
