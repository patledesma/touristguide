package com.sulkud.touristguide.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import com.sulkud.touristguide.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressFg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressFg = (ProgressBar) findViewById(R.id.circular_progress_fg);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressFg, "progress", 0, 1000);
        progressAnimator.setDuration(100000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
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
}
