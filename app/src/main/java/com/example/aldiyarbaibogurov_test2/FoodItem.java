package com.example.aldiyarbaibogurov_test2;

public class FoodItem {
    long foodID;
    long mealID;
    String foodName;
    int quantity;
    int caloriesPerUnit;
    int totalCalories;

    public FoodItem() {}

    public FoodItem(String foodName, int quantity, int caloriesPerUnit) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.caloriesPerUnit = caloriesPerUnit;
        this.totalCalories = quantity * caloriesPerUnit;
    }

    @Override
    public String toString() {
        return foodName + " x" + quantity + " (" + totalCalories + " cal)";
    }
}
