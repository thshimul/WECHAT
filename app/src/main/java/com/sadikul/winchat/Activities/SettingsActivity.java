package com.sadikul.winchat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.Constants;
import com.sadikul.winchat.Utils.MyDialog.SpotsDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settingsToolbar)
    Toolbar settingsToolbar;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.name)
    TextView nameEditText;
    @BindView(R.id.status)
    TextView statusEdittext;
    @BindView(R.id.change_image)
    Button changeImage;
    @BindView(R.id.change_status)
    Button changeStatus;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mUserDatabase;
    FirebaseUser firebaseUser;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    SpotsDialog spotsDialog;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.ll_progressbar)
    LinearLayout llProgressbar;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    String name,status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spotsDialog = new SpotsDialog(this,"Uploading image..");
        //spotsDialog.setMessage("Image uploading");
       // spotsDialog.show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("profile_photo");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String firebaseUserUid = null;
        if (firebaseUser == null) {
            Intent intent = new Intent(this, SignIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            firebaseUserUid = firebaseUser.getUid();
        }
        mUserDatabase = firebaseDatabase.getReference().child(Constants.usersDatabaseRef).child(firebaseUserUid);
        //mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name = dataSnapshot.child("name").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String thumbnailImage = dataSnapshot.child("thumbnailImage").getValue().toString();

                changeAllvalue(name, status, image, thumbnailImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //spotsDialog.dismiss();
                //hideAll();
                Toast.makeText(SettingsActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeAllvalue(String name, String status, String image, String thumbnailImage) {
        //spotsDialog.dismiss();
        showAll();
        nameEditText.setText(name);
        statusEdittext.setText(status);
        Glide.with(this).load(image).apply(RequestOptions.placeholderOf(R.mipmap.ic_launcher_round).dontAnimate().override(100, 100)).into(profileImage);
    }


    @OnClick({R.id.profile_image, R.id.change_image, R.id.change_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_image:
                break;
            case R.id.change_image:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
                break;
            case R.id.change_status:
                Intent intent =new Intent(this,ChangeStatusActivity.class);
                intent.putExtra("name",status);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //spotsDialog.setMessage("Image uploading");
                spotsDialog.show();
                Uri resultUri = result.getUri();
                StorageReference filePath=storageReference.child(getSaltString()+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            String imageLink=task.getResult().getDownloadUrl().toString();
                            mUserDatabase.child("image").setValue(imageLink).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    spotsDialog.dismiss();

                                    Toast.makeText(SettingsActivity.this, "Image updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    spotsDialog.dismiss();

                                    Toast.makeText(SettingsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            
                        }else{

                            Toast.makeText(SettingsActivity.this, "Please try again..", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SettingsActivity.this, "failed on image updated", Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    void showAll(){
        llOne.setVisibility(View.VISIBLE);
        llTwo.setVisibility(View.VISIBLE);
        llThree.setVisibility(View.VISIBLE);
        llProgressbar.setVisibility(View.GONE);
    }
    void hideAll(){
        llOne.setVisibility(View.GONE);
        llTwo.setVisibility(View.GONE);
        llThree.setVisibility(View.GONE);
        llProgressbar.setVisibility(View.VISIBLE);
    }
}
