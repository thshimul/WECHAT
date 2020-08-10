package com.sadikul.winchat.Activities;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.sadikul.winchat.R;
import com.sadikul.winchat.Utils.PreferenceManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {

    @BindView(R.id.logo)
    ImageView logo;

    PreferenceManager mPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mPreference=new PreferenceManager(this);
        //scaleView(logo,0f,0.6f);

      /*  PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
        ObjectAnimator scaleAnimation =
                ObjectAnimator.ofPropertyValuesHolder(logo, pvhX, pvhY);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(ValueAnimator.REVERSE);
*/
        //scaleAnimation.start();
        //setupAnimation(logo,scaleAnimation,R.animator.scale);

        Animator anim = AnimatorInflater.loadAnimator(Splash.this, R.animator.scale);
        anim.setTarget(logo);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(mPreference.getLoginStatus()){
                    startActivity(new Intent(Splash.this,HomeActivity.class));
                }else{
                    startActivity(new Intent(Splash.this,SignIn.class));
                }
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(3000);
        v.startAnimation(anim);
    }
}
