  package com.example.nirma.moes5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;

    private DatabaseReference friendsReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;

    String onlineUserId;

    private View myMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        myFriendList = myMainView.findViewById(R.id.friend_list);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(onlineUserId);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        usersReference.keepSynced(true);

        myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Query query = friendsReference
                .limitToLast(50);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter
                  = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull final Friends model) {
                  holder.setDate(model.getDate());

                  final String listUserId = getRef(position).getKey();

                  usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(final DataSnapshot dataSnapshot)
                      {
                          final String username = dataSnapshot.child("user_name").getValue().toString();
                          String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();



                          if (dataSnapshot.hasChild("online"))
                          {
                              String onlineStatus = (String) dataSnapshot.child("online").getValue().toString();

                              holder.setUserOnline(onlineStatus);
                          }

                          holder.setUsername(username);
                          holder.setThumbImage(thumbImage);

                          holder.mView.setOnClickListener(new View.OnClickListener()
                          {
                              @Override
                              public void onClick(View v)
                              {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    username +"'s Profile",
                                                    "Send Message"
                                            };

                                  AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                  builder.setTitle("Select Option");

                                  builder.setItems(options, new DialogInterface.OnClickListener()
                                  {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which)
                                      {
                                          if(position == 0)
                                          {
                                              Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                              profileIntent.putExtra("visit_user_id", listUserId);
                                              startActivity(profileIntent);
                                          }

                                          if(position == 1)
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
                                      }
                                  });
                                  builder.show();
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
              public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                  View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout,parent,false);
                  return new FriendsViewHolder(v);
              }
          };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

      }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView) {
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
    }
  }
