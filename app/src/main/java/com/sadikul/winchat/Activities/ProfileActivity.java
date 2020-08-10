package com.sadikul.winchat.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.Constants;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {

    String user_id;
    @BindView(R.id.user_profileImageview)
    ImageView userProfileImageview;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.tv_numberOfFriends)
    TextView tvNumberOfFriends;

    @BindView(R.id.btn_sendFriendRequest)
    Button btnSendFriendRequest;

    @BindView(R.id.ll_progressbar)
    LinearLayout ll_progressbar;

    @BindView(R.id.profileActivityToolbar)
    Toolbar toolbar;
    // mcurrent status 0=nothing, 1= sent, 2= received, 3=friends
    private int mCurrentFriendStatus=0;
    private static String tag="ProfileActivityTAG";


    @BindView(R.id.btn_rejectFriendRequest)
    Button btn_rejectFriendRequest;

    private DatabaseReference databaseReference;
    private DatabaseReference mFriendReqDatabaseRef;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationsDatabaseRef;
    private FirebaseUser mCurrentUser;
    String name;
    String status;
    String imagelink;
    String requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        user_id = getIntent().getStringExtra("user_id");

        databaseReference= FirebaseDatabase.getInstance().getReference().child(Constants.usersDatabaseRef).child(user_id);
        databaseReference.keepSynced(true);

        mFriendReqDatabaseRef= FirebaseDatabase.getInstance().getReference().child(Constants.friendReqDatabaseRef);
        mFriendReqDatabaseRef.keepSynced(true);

        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child(Constants.friendsDatabaseRef);
        mFriendsDatabase.keepSynced(true);

        mNotificationsDatabaseRef= FirebaseDatabase.getInstance().getReference().child(Constants.notificationsDatabaseRef);
        mNotificationsDatabaseRef.keepSynced(true);

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();

        mCurrentFriendStatus=0;
        ll_progressbar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.child(Constants.nameChild).getValue().toString();
                status=dataSnapshot.child(Constants.statusChild).getValue().toString();
                imagelink=dataSnapshot.child(Constants.imageChild).getValue().toString();

                tvName.setText(name);
                tvStatus.setText(status);
                Glide.with(ProfileActivity.this).load(imagelink).into(userProfileImageview);

                mFriendReqDatabaseRef.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){
                            requestType=dataSnapshot.child(user_id).child(Constants.requestType).getValue().toString();

                            Log.e("mFriendReqfSync","type "+requestType);
                            if(requestType.equals(Constants.received)){
                                mCurrentFriendStatus = 2;
                                btnSendFriendRequest.setText(Constants.acceptFriendRequest);

                                //enable the reject button
                                btn_rejectFriendRequest.setVisibility(View.VISIBLE);
                                btn_rejectFriendRequest.setEnabled(true);
                            }else if(requestType.equals(Constants.sent)){
                                mCurrentFriendStatus = 1;
                                btnSendFriendRequest.setText(Constants.cancelFriendRequest);

                                //disable the reject button
                                btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                btn_rejectFriendRequest.setEnabled(false);
                            }
                        }else {
                            mFriendsDatabase.child(mCurrentUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Log.e("mFriendfSync",user_id+" + type + "+dataSnapshot.toString());
                                    if(dataSnapshot.hasChild(user_id)){
                                        mCurrentFriendStatus = 3;
                                        btnSendFriendRequest.setText(Constants.unfriendRequest);

                                        btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                        btn_rejectFriendRequest.setEnabled(false);
                                    }else{
                                        mCurrentFriendStatus = 0;
                                        btnSendFriendRequest.setText(Constants.sentFriendRequest);


                                        btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                        btn_rejectFriendRequest.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                      /*      mFriendsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(mCurrentUser.getUid())){
                                            mCurrentFriendStatus = 0;
                                            btnSendFriendRequest.setText(Constants.sentFriendRequest);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });*/
                        }


                        Log.e("mCurrentStatus",mCurrentFriendStatus+" by type "+requestType);
                        ll_progressbar.setVisibility(View.GONE);
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

/*
                mFriendsDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                ll_progressbar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Something went wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_sendFriendRequest)
    public void onViewClicked() {
        //mCurrentFriendStatus 0=nothing
        Log.e("mCurrentStatus",mCurrentFriendStatus+" sks");
        if(mCurrentFriendStatus == 0){
            if(mCurrentUser != null){
                btnSendFriendRequest.setEnabled(false);
                mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).child(Constants.requestType).setValue(Constants.sent)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mFriendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid())
                                            .child(Constants.requestType)
                                            .setValue(Constants.received)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    HashMap<String,String> notification=new HashMap<>();
                                                    notification.put("from",mCurrentUser.getUid());
                                                    notification.put("type","request");

                                                    //send notification to user
                                                    mNotificationsDatabaseRef.child(user_id).push().setValue(notification)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mCurrentFriendStatus=1;
                                                                    btnSendFriendRequest.setText(Constants.cancelFriendRequest);
                                                                    Toast.makeText(ProfileActivity.this, Constants.requestSentSuccessfully, Toast.LENGTH_SHORT).show();
                                                                    //disable the reject button
                                                                    btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                                                    btn_rejectFriendRequest.setEnabled(false);
                                                                }
                                                            });

                                                }
                                            });
                                }else{
                                    Toast.makeText(ProfileActivity.this, Constants.requestSentFailed, Toast.LENGTH_SHORT).show();

                                }

                                btnSendFriendRequest.setEnabled(true);
                            }
                        });
            }
            //mCurrentFriendStatus 1 = sent
        }else if(mCurrentFriendStatus == 1){
            mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid())
                                    .removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mCurrentFriendStatus=0;
                                            btnSendFriendRequest.setEnabled(true);
                                            btnSendFriendRequest.setText(Constants.sentFriendRequest);
                                            //Toast.makeText(ProfileActivity.this, Constants.requestCancelSuccessfully, Toast.LENGTH_SHORT).show();

                                            //disable the reject button
                                            btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                            btn_rejectFriendRequest.setEnabled(false);
                                        }
                                    });

                        }
                    });


            //mCurrentFriendStatus 2 = received
        }else if(mCurrentFriendStatus == 2){
            final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
            mFriendsDatabase.child(mCurrentUser.getUid()).child(user_id).setValue(currentDate)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(user_id).child(mCurrentUser.getUid()).setValue(currentDate)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id)
                                                                    .removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            mCurrentFriendStatus=3;
                                                                            btnSendFriendRequest.setEnabled(true);
                                                                            btnSendFriendRequest.setText(Constants.unfriendRequest);
                                                                            //Toast.makeText(ProfileActivity.this, Constants.requestCancelSuccessfully, Toast.LENGTH_SHORT).show();
                                                                            //disable the reject button
                                                                            btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                                                            btn_rejectFriendRequest.setEnabled(false);
                                                                        }
                                                                    });

                                                        }
                                                    });

                                        }
                                    });
                        }
                    });
        }
        //mCurrentFriendStatus 3 = unfriend
        else if(mCurrentFriendStatus == 3){
            mFriendsDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mCurrentFriendStatus=0;
                                            btnSendFriendRequest.setText(Constants.sentFriendRequest);

                                            //disable the reject button
                                            btn_rejectFriendRequest.setVisibility(View.INVISIBLE);
                                            btn_rejectFriendRequest.setEnabled(false);
                                        }
                                    });
                        }
                    });
        }
    }

    // mcurrent status 0=nothing, 1= sent, 2= received, 3=friends
    private void removeFriendRequest(final int currentFriendStatus,final String sendFriendReqButtonText) {
        mFriendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mCurrentFriendStatus=currentFriendStatus;
                                        btnSendFriendRequest.setEnabled(true);
                                        btnSendFriendRequest.setText(sendFriendReqButtonText);
                                        //Toast.makeText(ProfileActivity.this, Constants.requestCancelSuccessfully, Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
    }
}
