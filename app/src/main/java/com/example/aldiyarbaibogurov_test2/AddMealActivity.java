package com.example.aldiyarbaibogurov_test2;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddMealActivity extends AppCompatActivity implements FoodItemDialogFragment.FoodItemListener {

    Button btnAddFood, btnSaveMeal;
    TextView tvListPlaceholder;
    ListView lvFoodItems;
    Spinner spMealType;
    TextView tvMealCalories;
    DBHelper db;
    ArrayList<FoodItem> foodItems;
    ArrayAdapter<FoodItem> adapter;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        Button btnBack = findViewById(R.id.btnBack);
        tvListPlaceholder = findViewById(R.id.tvListPlaceholder);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnSaveMeal = findViewById(R.id.btnSaveMeal);
        lvFoodItems = findViewById(R.id.lvFoodItems);
        spMealType = findViewById(R.id.spMealType);
        tvMealCalories = findViewById(R.id.tvMealCalories);

        db = new DBHelper(this);
        foodItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodItems);
        lvFoodItems.setAdapter(adapter);

        // Setting the placeholder as the empty view for the ListView
        lvFoodItems.setEmptyView(tvListPlaceholder);

        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null || selectedDate.isEmpty()) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        lvFoodItems.setOnItemLongClickListener((parent, view, position, id) -> {
            new android.app.AlertDialog.Builder(AddMealActivity.this)
                    .setTitle("Remove Item")
                    .setMessage("Do you want to delete this food item?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        foodItems.remove(position);
                        adapter.notifyDataSetChanged();
                        updateMealTotal();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });


        // Populating spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.meal_types, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMealType.setAdapter(spinnerAdapter);

        btnAddFood.setOnClickListener(v -> {
            FoodItemDialogFragment dialog = new FoodItemDialogFragment();
            dialog.show(getSupportFragmentManager(), "addFoodDialog");
        });

        btnBack.setOnClickListener(v -> finish());
        btnSaveMeal.setOnClickListener(v -> saveMeal());
    }

    @Override
    public void onFoodItemCreated(FoodItem item) {
        foodItems.add(item);
        adapter.notifyDataSetChanged();
        updateMealTotal();
    }

    private void updateMealTotal() {
        int total = 0;
        for (FoodItem f : foodItems) total += f.totalCalories;
        tvMealCalories.setText("Calories: " + total);
    }

    private void saveMeal() {
        if (foodItems.isEmpty()) {
            Toast.makeText(this, "Add at least one food item", Toast.LENGTH_SHORT).show();
            return;
        }

        String mealNameFromInput = "Meal"; // Default name
        String mealType = spMealType.getSelectedItem().toString();
        int total = 0;
        for (FoodItem f : foodItems) total += f.totalCalories;

        long mealId = db.insertMeal(mealNameFromInput, selectedDate, mealType, total);
        if (mealId <= 0) {
            Toast.makeText(this, "Failed to save meal", Toast.LENGTH_SHORT).show();
            return;
        }

        for (FoodItem f : foodItems) {
            db.insertFoodItem(mealId, f.foodName, f.quantity, f.caloriesPerUnit, f.totalCalories);
        }

        Toast.makeText(this, "Meal saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
