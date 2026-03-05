package com.example.aldiyarbaibogurov_test2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvCaloriesConsumed, tvRemaining, tvDate;
    Button btnAddMeal;
    DBHelper db;
    int dailyGoal;
    String selectedDate;
    Calendar selectedCalendar;
    CircularProgressBar circle;

    private static final String PREFS_NAME = "app_prefs";
    private static final String DAILY_GOAL_KEY = "DAILY_GOAL_KEY";
    private static final String SELECTED_DATE_KEY = "SELECTED_DATE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing views
        tvCaloriesConsumed = findViewById(R.id.tvCaloriesConsumed);
        tvRemaining = findViewById(R.id.tvRemaining);
        tvDate = findViewById(R.id.tvDate);
        btnAddMeal = findViewById(R.id.btnAddMeal);
        circle = findViewById(R.id.circleProgress);
        LinearLayout datePickerLayout = findViewById(R.id.datePickerLayout);
        Button btnSetGoal = findViewById(R.id.btnSetGoal);

        db = new DBHelper(this);
        selectedCalendar = Calendar.getInstance();

        // Loading daily goal
        loadDailyGoal();

        // Restoring selected date or initializing it to today
        if (savedInstanceState != null) {
            selectedDate = savedInstanceState.getString(SELECTED_DATE_KEY);
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate);
                selectedCalendar.setTime(date);
            } catch (Exception e) { /* Default to today (Prevent crashes) */ }
        } else {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        updateDateLabel();

        // Setting listeners
        datePickerLayout.setOnClickListener(v -> showDatePickerDialog());
        btnSetGoal.setOnClickListener(v -> showSetGoalDialog());

        findViewById(R.id.btnWeeklyStats).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, WeeklyStatsActivity.class));
        });

        btnAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMealActivity.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });

        Button btnCalculator = findViewById(R.id.btnCalculator);
        btnCalculator.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivity(i);
        });

        updateDashboard(selectedDate);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_DATE_KEY, selectedDate);
    }

    private void loadDailyGoal() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dailyGoal = prefs.getInt(DAILY_GOAL_KEY, 2000); // Default to 2000
        circle.setGoal(dailyGoal);
    }

    private void showSetGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Daily Calorie Goal");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(dailyGoal));
        builder.setView(input);

        builder.setPositiveButton("Set", (dialog, which) -> {
            try {
                int newGoal = Integer.parseInt(input.getText().toString());
                if (newGoal > 0) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putInt(DAILY_GOAL_KEY, newGoal);
                    editor.apply();

                    dailyGoal = newGoal;
                    circle.setGoal(dailyGoal);
                    updateDashboard(selectedDate);
                    Toast.makeText(this, "Daily goal updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a positive number.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            selectedCalendar.set(Calendar.YEAR, year);
            selectedCalendar.set(Calendar.MONTH, month);
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedCalendar.getTime());
            updateDateLabel();
            updateDashboard(selectedDate);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, dateSetListener,
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateLabel() {
        Calendar todayCalendar = Calendar.getInstance();
        if (selectedCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
            selectedCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
            tvDate.setText("Today");
        } else {
            tvDate.setText(selectedDate);
        }
    }

    private void updateDashboard(String date) {
        int total = db.getTotalCaloriesByDate(date);
        tvCaloriesConsumed.setText(total + "\ncalories\nconsumed");
        int remaining = Math.max(0, dailyGoal - total);
        tvRemaining.setText(remaining + " remaining");
        circle.setProgress(total);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailyGoal(); // Reloading goal in case it was changed elsewhere
        updateDashboard(selectedDate);
    }
}
