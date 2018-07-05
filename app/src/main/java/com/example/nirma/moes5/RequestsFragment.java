package com.example.nirma.moes5;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nirma.moes5.model.Requests;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment
{
    private RecyclerView myRequestsList;
    private View myMainView;
    private DatabaseReference FriendRequestsReference;
    private FirebaseAuth mAuth;

    String online_user_id;
    private DatabaseReference UsersReference;
    private DatabaseReference FriendsDatabaseRef;
    private DatabaseReference FriendsReqDatabaseRef;

    public RequestsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        myMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        myRequestsList = myMainView.findViewById(R.id.requests_list);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendRequestsReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends_Requests");

        myRequestsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Query query = FriendRequestsReference
                .limitToLast(50);


        FirebaseRecyclerOptions<Requests> options
                = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(query, Requests.class)
                .build();
        FirebaseRecyclerAdapter<Requests,RequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Requests model)
            {
                final  String list_user_id = getRef(position).getKey();
                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                get_type_ref.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received"))
                            {
                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        final String username = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        holder.setUsername(username);
                                        holder.setThumbImage(thumbImage);
                                        holder.setUserStatus(userStatus);

                                        holder.mView.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Accept Friend Request",
                                                                "Cancel Friend Request"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Req Option");

                                                builder.setItems(options, new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        if(which == 0)
                                                        {
                                                            Calendar calForDate = Calendar.getInstance();
                                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                            FriendsDatabaseRef.child(online_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid)
                                                                        {
                                                                            FriendsDatabaseRef.child(list_user_id).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>()
                                                                                    {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid)
                                                                                        {
                                                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                    {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                                        {
                                                                                                            if (task.isSuccessful())
                                                                                                            {
                                                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                                        {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                                                            {
                                                                                                                                if (task.isSuccessful())
                                                                                                                                {
                                                                                                                                    Toast.makeText(getContext(), "Friend Request accepted succsessfully..", Toast.LENGTH_SHORT).show();
                                                                                                                                }

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

                                                        if(which == 1)
                                                        {
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                        {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Friend Request Cancelled succsessfully..", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                    }
                                });
                            }
                            else if (request_type.equals("sent"))
                            {
                                Button req_sent_btn = holder.mView.findViewById(R.id.request_accept_btn);
                                req_sent_btn.setText("Request Sent");

                                holder.mView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        final String username = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();

                                        holder.setUsername(username);
                                        holder.setThumbImage(thumbImage);
                                        holder.setUserStatus(userStatus);

                                        holder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Cancel Friend Request"
                                                        };

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Friend Request Sent");

                                                builder.setItems(options, new DialogInterface.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        if(which == 0)
                                                        {
                                                            FriendsReqDatabaseRef.child(online_user_id).child(list_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                FriendsReqDatabaseRef.child(list_user_id).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                        {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Friend Request Cancelled succsessfully..", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });

            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_all_user_layout,parent,false);
                return new RequestViewHolder(v);
            }
        };
        myRequestsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public RequestViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String username)
        {
            TextView userNameDisplay = mView.findViewById(R.id.request_profile_name);
            userNameDisplay.setText(username);
        }

        public void setThumbImage(final String thumbImage)
        {
            final CircleImageView thumb_image = mView.findViewById(R.id.request_profile_image);

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

        public void setUserStatus(String userStatus)
        {
            TextView status = mView.findViewById(R.id.request_profile_status);
            status.setText(userStatus);
        }
    }

}
