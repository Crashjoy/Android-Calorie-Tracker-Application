package com.example.aldiyarbaibogurov_test2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    EditText etHeight, etWeight, etAge;
    Spinner spActivity, spGoal, spGender;
    TextView tvResult;
    Button btnSetAsGoal;
    int calculatedGoal = 0;

    private static final String PREFS_NAME = "app_prefs";
    private static final String DAILY_GOAL_KEY = "DAILY_GOAL_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etAge = findViewById(R.id.etAge);
        spGender = findViewById(R.id.spGender);
        spActivity = findViewById(R.id.spActivity);
        spGoal = findViewById(R.id.spGoal);
        tvResult = findViewById(R.id.tvResult);
        btnSetAsGoal = findViewById(R.id.btnSetAsGoal);

        // Back button action
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Gender array
        String[] genders = {"Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        // Activity levels array
        String[] activityLevels = {
                "Sedentary (little exercise)",
                "Light (1–3 days/week)",
                "Moderate (3–5 days/week)",
                "Active (6–7 days/week)",
                "Very Active (hard exercise daily)"
        };

        ArrayAdapter<String> actAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityLevels);
        actAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivity.setAdapter(actAdapter);

        // Goals array
        String[] goals = {
                "Lose weight",
                "Maintain weight",
                "Gain weight",
                "Gain muscle"
        };

        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGoal.setAdapter(goalAdapter);

        // Calculating button action
        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculateCalories());

        // Goal button logic
        btnSetAsGoal.setOnClickListener(v -> {
            if (calculatedGoal > 0) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt(DAILY_GOAL_KEY, calculatedGoal);
                editor.apply();
                Toast.makeText(this, "New daily goal set!", Toast.LENGTH_SHORT).show();
                finish(); // Goes back to main activity
            } else {
                Toast.makeText(this, "Please calculate your goal first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateCalories() {
        if (etHeight.getText().toString().isEmpty() || etWeight.getText().toString().isEmpty()
                || etAge.getText().toString().isEmpty()) {
            tvResult.setText("Please enter height, weight, and age.");
            return;
        }

        double height = Double.parseDouble(etHeight.getText().toString());
        double weight = Double.parseDouble(etWeight.getText().toString());
        int age = Integer.parseInt(etAge.getText().toString());
        int genderIndex = spGender.getSelectedItemPosition();

        double bmr;

        // Mifflin-St Jeor equation
        if (genderIndex == 0) {
            // Male
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            // Female
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        // Sport Activity multiplier
        double multiplier;
        switch (spActivity.getSelectedItemPosition()) {
            case 0: multiplier = 1.2; break;
            case 1: multiplier = 1.375; break;
            case 2: multiplier = 1.55; break;
            case 3: multiplier = 1.725; break;
            default: multiplier = 1.9; break;
        }

        double maintenance = bmr * multiplier;
        double result = maintenance;

        // Adjusting calories based on goal
        switch (spGoal.getSelectedItemPosition()) {
            case 0: result -= 400; break;   // lose weight
            case 2: result += 300; break;   // gain
            case 3: result += 500; break;   // gain muscle
        }

        calculatedGoal = (int) result;
        tvResult.setText("Your daily calorie target: " + calculatedGoal + " kcal");

        // Showing the button
        btnSetAsGoal.setVisibility(View.VISIBLE);
    }
}
