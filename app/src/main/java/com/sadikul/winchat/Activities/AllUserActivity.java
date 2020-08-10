package com.sadikul.winchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sadikul.winchat.Network.Model.UserProfile;
import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllUserActivity extends AppCompatActivity {

    @BindView(R.id.alluserToolbar)
    Toolbar alluserToolbar;
    @BindView(R.id.alluserRecyclerView)
    RecyclerView alluserRecyclerView;
    private DatabaseReference databaseReference;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        ButterKnife.bind(this);
        setSupportActionBar(alluserToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference= FirebaseDatabase.getInstance().getReference().child(Constants.usersDatabaseRef);
        alluserRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        };
        databaseReference.addChildEventListener(childEventListener);

        FirebaseRecyclerOptions<UserProfile> options =
                new FirebaseRecyclerOptions.Builder<UserProfile>()
                        .setQuery(databaseReference, UserProfile.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<UserProfile, ProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ProfileViewHolder holder, int position, UserProfile model) {
                holder.name.setText(model.getName());
                holder.status.setText(model.getStatus());
                Glide.with(AllUserActivity.this).load(model.getImage())
                        .apply(RequestOptions.placeholderOf(R.mipmap.ic_launcher_round)
                                .override(100,100)
                                .dontAnimate())
                        .into(holder.imageView);
                final String userKey=getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(AllUserActivity.this,ProfileActivity.class);
                        intent.putExtra("user_id",userKey);
                        startActivity(intent);
                    }
                });
            }


            @Override
            public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_list_item, parent, false);

                return new ProfileViewHolder(view);
            }

        };

        alluserRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView imageView;
        TextView name,status;
        public ProfileViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            name=mView.findViewById(R.id.user_nameView);
            status=mView.findViewById(R.id.user_statusView);
            imageView=mView.findViewById(R.id.userProfileImage);
        }
    }
}
