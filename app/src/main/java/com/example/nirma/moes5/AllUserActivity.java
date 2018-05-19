package com.example.nirma.moes5;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allUserDatabaseReference;

    Query query = FirebaseDatabase.getInstance()
            .getReference()
            .child("Users")
            .limitToLast(50);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);

        mToolbar = findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUsersList = (RecyclerView) findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));
    }

    FirebaseRecyclerOptions<AllUsers> options =
            new FirebaseRecyclerOptions.Builder<AllUsers>()
                    .setQuery(query, AllUsers.class)
                    .build();

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(options)
        {

            @NonNull
            @Override
            public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout,parent,false);
                return new AllUsersViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder holder, int position, @NonNull AllUsers model) {
                holder.setUser_name(model.getUser_name());
                holder.setUser_status(model.getUser_status());
                holder.setUser_image(model.getUser_image());
            }
        };
        allUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static  class  AllUsersViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public AllUsersViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }

        public void  setUser_name(String user_name){
            TextView name = mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }

        public void  setUser_status(String user_status){
            TextView status = mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }

        public void  setUser_image(String user_image){
            CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
            Picasso.get()
                    .load(user_image)
                    .placeholder(R.drawable.default_profile)
                    .into(image);
        }
    }
}