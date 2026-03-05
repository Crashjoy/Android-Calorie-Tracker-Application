package com.example.aldiyarbaibogurov_test2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    Spinner spinnerDates;
    ListView listHistory;
    Button btnBack;
    DBHelper db;
    ArrayList<String> dateList;
    ArrayList<String> foodList;
    ArrayAdapter<String> dateAdapter;
    ArrayAdapter<String> foodAdapter;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        spinnerDates = findViewById(R.id.spinnerDates);
        listHistory = findViewById(R.id.listHistory);
        btnBack = findViewById(R.id.btnBack);

        db = new DBHelper(this);
        dateList = new ArrayList<>();
        foodList = new ArrayList<>();

        // Getting the selected date from MainActivity
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        loadDates();

        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String date = dateList.get(position);
                loadFoodsForDate(date);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadDates() {
        dateList.clear();
        Cursor cursor = db.getAllDates();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No records found.", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            String date = cursor.getString(0);
            dateList.add(date);
        }

        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dateList);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDates.setAdapter(dateAdapter);

        // Setting the spinner to the selected date from MainActivity
        if (selectedDate != null) {
            int spinnerPosition = dateAdapter.getPosition(selectedDate);
            if (spinnerPosition >= 0) {
                spinnerDates.setSelection(spinnerPosition);
            }
        }
    }

    private void loadFoodsForDate(String date) {
        foodList.clear();
        Cursor cursor = db.getMealsByDate(date);
        int total = 0;

        while (cursor.moveToNext()) {
            String mealType = cursor.getString(3); // Index for mealType
            int calories = cursor.getInt(4);      // Index for totalCalories
            String item = mealType + " - " + calories + " cal";
            foodList.add(item);
            total += calories;
        }

        foodList.add("Total: " + total + " cal");
        foodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodList);
        listHistory.setAdapter(foodAdapter);
    }
}
