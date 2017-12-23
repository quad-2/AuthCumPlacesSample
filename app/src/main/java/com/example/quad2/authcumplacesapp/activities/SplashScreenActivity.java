package com.example.quad2.authcumplacesapp.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.quad2.authcumplacesapp.R;

public class SplashScreenActivity extends AppCompatActivity {

  private final long SPLASH_DISPLAY_TIME = 1000;//1 seconds

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    setTask();
  }

  private void setTask() {
    new Handler().postDelayed(new Runnable() {
      // Using handler with postDelayed called runnable run method
      @Override
      public void run() {
        navigateNextScreen();
      }
    }, SPLASH_DISPLAY_TIME);
  }

  private void navigateNextScreen() {
    startActivity(new Intent(this, LoginActivity.class));
    finish();
  }
}
