package com.example.aldiyarbaibogurov_test2;

public class Meal {
    long mealID;
    String mealName;
    String date; // format yyyy-MM-dd
    String mealType; // breakfast, lunch, dinner, snack
    int totalCalories;

    public Meal() {}

    public Meal(String mealName, String date, String mealType, int totalCalories) {
        this.mealName = mealName;
        this.date = date;
        this.mealType = mealType;
        this.totalCalories = totalCalories;
    }
}
