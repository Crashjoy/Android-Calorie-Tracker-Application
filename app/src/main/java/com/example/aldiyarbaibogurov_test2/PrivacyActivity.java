package com.example.aldiyarbaibogurov_test2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        CheckBox cbAccept = findViewById(R.id.cbAccept);
        Button btnAgree = findViewById(R.id.btnAgree);

        // Button is disabled by default, so enabling it only when the checkbox is checked
        cbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnAgree.setEnabled(isChecked);
        });

        btnAgree.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
            editor.putBoolean("privacy_accepted", true);
            editor.apply();

            // Sending user to the loading screen
            startActivity(new Intent(this, LoadingActivity.class));
            finish(); // Preventing user from coming back to privacy screen
        });
    }
}
