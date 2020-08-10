package com.sadikul.winchat.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sadikul.winchat.Network.Model.UserProfile;
import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.Constants;
import com.sadikul.winchat.Utils.MyDialog.SpotsDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Signup extends AppCompatActivity {

    private static final String TAG = "Signup";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseREference;
    @BindView(R.id.name)
    TextInputEditText nameView;
    @BindView(R.id.email)
    TextInputEditText emailView;
    @BindView(R.id.password)
    TextInputEditText passwordView;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.signup_main_container)
    RelativeLayout signupMainContainer;
    SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        dialog=new SpotsDialog(this);

    }


    private void createUser(final String name, String email, String password) {
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            dialog.setTitle("Signing up...");
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String user_id = user.getUid();
                            String token= FirebaseInstanceId.getInstance().getToken();
                            mDatabaseREference = mDatabase.getReference().child("Users").child(user_id);
                            //DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

                            UserProfile profile=new UserProfile();
                            profile.setName(name);
                            profile.setToken(token);
                            profile.setStatus("");
                            profile.setImage("");
                            profile.setThumbnailImage("");
                           /* HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", name);
                            hashMap.put("status", "i am not ok");
                            hashMap.put("image", "");
                            hashMap.put("thumb_image", "");*/
                            mDatabaseREference.setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "success:save");

                                    dialog.dismiss();
                                    Intent intent=new Intent(Signup.this,SignIn.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dialog.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:save", e.getCause());
                                    Toast.makeText(Signup.this, "Please try again....",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            dialog.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Signup.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @OnClick({R.id.btn_signup, R.id.tv_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:{
                String userName = nameView.getText().toString();
                String userEmail = emailView.getText().toString();
                String userPass = passwordView.getText().toString();
                boolean isAllValid = true;

                if (TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPass) && TextUtils.isEmpty(userName)) {
                    nameView.setError(getResources().getString(R.string.error_field_required));
                    passwordView.setError(getResources().getString(R.string.error_invalid_password));
                    passwordView.setError(getResources().getString(R.string.error_invalid_password));
                    isAllValid = false;
                }

                if (!isEmailValid(userEmail)) {
                    emailView.setError(getResources().getString(R.string.error_invalid_email));
                    isAllValid = false;
                }

                if (userPass.length() < 6) {
                    passwordView.setError(getResources().getString(R.string.error_invalid_password));
                    isAllValid = false;
                }

                if (isAllValid) {
                    createUser(userName, userEmail, userPass);
                    break;
                }
                break;
            }
            case R.id.tv_login:
                startActivity(new Intent(this,SignIn.class));
                finish();
                break;
        }
    }


    private boolean isEmailValid(String userEmail) {
        if ((TextUtils.isEmpty(userEmail))) {
            ShowSnackbar(getResources().getString(R.string.error_invalid_email));
            return false;
        } else if (!userEmail.matches(Constants.emailPattern)) {
            emailView.setError(getResources().getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    private void ShowSnackbar(String string) {
        //show snackbar if emailView null
        Snackbar snackbar = Snackbar.make(signupMainContainer, string, Snackbar.LENGTH_SHORT);//.show();
        snackbar.setActionTextColor(Color.YELLOW);
//        View sbView = snackbar.getView();
//        TextView textView=sbView.getTooltipText()
        snackbar.show();
    }
}
