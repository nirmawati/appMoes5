  package com.example.nirma.moes5;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nirma.moes5.model.AllUsers;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import de.hdodenhof.circleimageview.CircleImageView;


  /**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendList;

    private DatabaseReference friendsReference;
    private DatabaseReference usersReference;
    private FirebaseAuth mAuth;

    Query query = FirebaseDatabase.getInstance()
            .getReference()
            .child("Friends")
            .limitToLast(50);

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

        myFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return myMainView;
    }
    FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                      .setQuery(query, Friends.class)
                      .build();
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder>firebaseRecyclerAdapter
                  = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
              @Override
              protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                  holder.setDate(model.getDate());

                  String listUserId = getRef(position).getKey();

                  usersReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          String username = dataSnapshot.child("user_name").getValue().toString();
                          String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                          FriendsViewHolder.setUsername(username);
                          FriendsViewHolder.setThumbImage(thumbImage);
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
      }

    public static class  FriendsViewHolder extends RecyclerView.ViewHolder{

        static View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public static void setThumbImage(final String thumbImage) {
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
        }

        public static void setUsername(String username){
            TextView usernameFriends = mView.findViewById(R.id.all_users_username);
        }
    }
  }
