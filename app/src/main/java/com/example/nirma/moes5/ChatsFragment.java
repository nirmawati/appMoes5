package com.example.nirma.moes5;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirma.moes5.model.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment
{
    private RecyclerView myChatsList;
    private DatabaseReference friendsReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;

    String onlineUserId;

    private View myMainView;


    public ChatsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        myChatsList = myMainView.findViewById(R.id.chats_list);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUserId);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        myChatsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Query query = friendsReference
                .limitToLast(50);

        FirebaseRecyclerOptions<Chats> options =
                new FirebaseRecyclerOptions.Builder<Chats>()
                        .setQuery(query, Chats.class)
                        .build();

        FirebaseRecyclerAdapter<Chats,ChatsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull final Chats model)
            {

                final String listUserId = getRef(position).getKey();


                usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        final String username = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                        if (dataSnapshot.hasChild("online"))
                        {
                            String onlineStatus = (String) dataSnapshot.child("online").getValue().toString();

                            holder.setUserOnline(onlineStatus);
                        }

                        holder.setUsername(username);
                        holder.setThumbImage(thumbImage);
                        holder.setUserStatus(userStatus);

                        holder.mView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if (dataSnapshot.child("online").exists())
                                {
                                    Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", listUserId);
                                    chatIntent.putExtra("user_name", username);
                                    startActivity(chatIntent);
                                }
                                else
                                {
                                    usersReference.child(listUserId).child("online")
                                            .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", listUserId);
                                            chatIntent.putExtra("user_name", username);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout,parent,false);
                return new ChatsViewHolder(v);
            }
        };
        myChatsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }



    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setThumbImage(final String thumbImage) {
            final CircleImageView thumb_image = mView.findViewById(R.id.all_users_profile_image);

            Picasso.get()
                    .load(thumbImage)
                    .networkPolicy(NetworkPolicy.OFFLINE) //Bisa liat profil saat offline
                    .placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess()
                        {

                        }

                        @Override
                        public void onError(Exception e)
                        {
                            Picasso.get()
                                    .load(thumbImage)
                                    .placeholder(R.drawable.default_profile)
                                    .into(thumb_image);
                        }
                    });
        }

        public void setDate(String date){
            TextView sinceFriendData = mView.findViewById(R.id.all_users_status);
            sinceFriendData.setText(date);
        }

        public void setUsername(String username){
            TextView usernameFriends = mView.findViewById(R.id.all_users_username);
            usernameFriends.setText(username);
        }

        public void setUserOnline(String onlineStatus) {
            ImageView onlineStatusView = mView.findViewById(R.id.online_status);

            if (onlineStatus.equals("true")){
                onlineStatusView.setVisibility(View.VISIBLE);
            }else {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserStatus(String userStatus)
        {
            TextView user_status = mView.findViewById(R.id.all_users_status);
            user_status.setText(userStatus);
        }
    }
}
