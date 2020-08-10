package com.sadikul.winchat.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.Constants;
import com.sadikul.winchat.Utils.MyDialog.SpotsDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeStatusActivity extends AppCompatActivity {

    @BindView(R.id.changeStatusToolbar)
    Toolbar changeStatusToolbar;
    @BindView(R.id.statusEdittext)
    MaterialEditText statusEdittext;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabaseReference;
    SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);
        ButterKnife.bind(this);
        setSupportActionBar(changeStatusToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change status");
        spotsDialog=new SpotsDialog(this);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String user_id=firebaseUser.getUid();
        statusEdittext.setText(getIntent().getStringExtra("name"));

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child(Constants.usersDatabaseRef).child(user_id);

    }

    @OnClick(R.id.statusSubmitButton)
    public void onViewClicked() {

        spotsDialog.show();
        mDatabaseReference.child("status").setValue(statusEdittext.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                spotsDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                spotsDialog.dismiss();
                Toast.makeText(ChangeStatusActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
