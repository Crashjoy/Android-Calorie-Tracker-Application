package com.example.aldiyarbaibogurov_test2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class WeeklyStatsActivity extends AppCompatActivity {

    private BarChartView barChart;
    private TextView tvTotalCalories, tvAvgCalories, tvDaysTracked, tvDaysGoalMet, tvDaysGoalNotMet;
    private DBHelper db;
    private int dailyGoal;

    private static final String PREFS_NAME = "app_prefs";
    private static final String DAILY_GOAL_KEY = "DAILY_GOAL_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_stats);

        // Initialize views
        barChart = findViewById(R.id.barChart);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);
        tvAvgCalories = findViewById(R.id.tvAvgCalories);
        tvDaysTracked = findViewById(R.id.tvDaysTracked);
        tvDaysGoalMet = findViewById(R.id.tvDaysGoalMet);
        tvDaysGoalNotMet = findViewById(R.id.tvDaysGoalNotMet);
        Button btnBack = findViewById(R.id.btnBack);

        db = new DBHelper(this);

        // Loading daily goal from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        dailyGoal = prefs.getInt(DAILY_GOAL_KEY, 2000); // Default is 2000 if not set

        btnBack.setOnClickListener(v -> finish());

        loadWeeklyStats();
    }

    private void loadWeeklyStats() {
        List<DayData> weeklyData = db.getCaloriesForLast7Days();

        if (weeklyData.isEmpty()) {
            // Handling case with no data
            barChart.setVisibility(BarChartView.GONE);
            tvTotalCalories.setText("No data available for the last 7 days.");
            return;
        }

        int[] calories = new int[weeklyData.size()];
        String[] dates = new String[weeklyData.size()];
        int totalCalories = 0;
        int daysGoalMet = 0;

        for (int i = 0; i < weeklyData.size(); i++) {
            DayData day = weeklyData.get(i);
            calories[i] = day.calories;
            dates[i] = day.date.substring(5); // Format MM-dd
            totalCalories += day.calories;

            if (day.calories >= dailyGoal) {
                daysGoalMet++;
            }
        }

        // Updating Bar Chart
        barChart.setData(calories, dates);

        // Updating statistics
        int daysTracked = weeklyData.size();
        int avgCalories = daysTracked > 0 ? totalCalories / daysTracked : 0;

        tvTotalCalories.setText("Total Calories Consumed: " + totalCalories);
        tvAvgCalories.setText("Average Daily Calories: " + avgCalories);
        tvDaysTracked.setText("Days Tracked: " + daysTracked + "/7");
        tvDaysGoalMet.setText("Days Goal Met: " + daysGoalMet);
        tvDaysGoalNotMet.setText("Days Goal Not Met: " + (daysTracked - daysGoalMet));
    }
}
