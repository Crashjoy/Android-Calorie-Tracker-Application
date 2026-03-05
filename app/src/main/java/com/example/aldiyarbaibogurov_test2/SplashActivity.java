package com.example.aldiyarbaibogurov_test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean privacyAccepted = prefs.getBoolean("privacy_accepted", false);

            if (privacyAccepted) {
                // If privacy has been accepted, goes to the loading activity
                Intent intent = new Intent(SplashActivity.this, LoadingActivity.class);
                startActivity(intent);
            } else {
                // On first launch (or after clearing data), goes to the privacy screen
                Intent intent = new Intent(SplashActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
            finish();
        }, 3000);
    }
}
