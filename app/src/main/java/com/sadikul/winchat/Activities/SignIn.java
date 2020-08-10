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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.Constants;
import com.sadikul.winchat.Utils.MyDialog.SpotsDialog;
import com.sadikul.winchat.Utils.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignIn extends AppCompatActivity {
    private static final String TAG = "SigninActivity";
    @BindView(R.id.email)
    EditText emailView;
    @BindView(R.id.password)
    TextInputEditText passwordView;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_signup)
    TextView btnSignup;
    @BindView(R.id.snackbar_container)
    RelativeLayout snackbarContainer;

    PreferenceManager mPreferenceManager;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    SpotsDialog spotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference=mDatabase.getReference().child(Constants.usersDatabaseRef);
        mPreferenceManager=new PreferenceManager(this);
        spotsDialog=new SpotsDialog(this,"Signing in..");
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    private void signin_User(String email, String password) {
        spotsDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            spotsDialog.dismiss();

                            String currentUser_id=mAuth.getCurrentUser().getUid();
                            String firebase_token= FirebaseInstanceId.getInstance().getToken();

                            mDatabaseReference.child(currentUser_id).child(Constants.device_token)
                                    .setValue(firebase_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignIn.this, "Success",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignIn.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    mPreferenceManager.setLoginStatus(true);

                                    finish();
                                }

                            });

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            String excepTion=null;
                            try{

                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                excepTion=e.toString();
                            } catch (FirebaseAuthInvalidUserException e) {

                                excepTion=e.toString();
                            } catch (FirebaseAuthEmailException e) {
                                excepTion=e.toString();
                            } catch (FirebaseAuthUserCollisionException e) {
                                excepTion=e.toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            spotsDialog.dismiss();
                            Log.w(TAG, "Signin:failure", task.getException());
                            Toast.makeText(SignIn.this, excepTion,
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                spotsDialog.dismiss();
                Toast.makeText(SignIn.this, "Please try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick({R.id.btn_login, R.id.btn_signup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:{

                String userEmail = emailView.getText().toString();
                String userPass = passwordView.getText().toString();
                boolean isAllValid = true;

                if (TextUtils.isEmpty(userEmail) && TextUtils.isEmpty(userPass)) {
                    emailView.setError(getResources().getString(R.string.error_invalid_email));
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
                    signin_User(emailView.getText().toString(), passwordView.getText().toString());
                }
                break;
            }

            case R.id.btn_signup:{
                startActivity(new Intent(this, Signup.class));
                break;
            }

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
        Snackbar snackbar = Snackbar.make(snackbarContainer, string, Snackbar.LENGTH_SHORT);//.show();
        snackbar.setActionTextColor(Color.YELLOW);
//        View sbView = snackbar.getView();
//        TextView textView=sbView.getTooltipText()
        snackbar.show();
    }


}
