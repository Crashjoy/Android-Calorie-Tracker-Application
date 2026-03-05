package com.example.aldiyarbaibogurov_test2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "CalorieTracker.db";
    private static final int DB_VERSION = 2;

    // Tables
    public static final String T_MEAL = "Meal";
    public static final String T_FOOD = "FoodItem";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMeal = "CREATE TABLE " + T_MEAL + "(" +
                "mealID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mealName TEXT," +
                "date TEXT," +
                "mealType TEXT," +
                "totalCalories INTEGER" +
                ")";
        db.execSQL(createMeal);

        String createFood = "CREATE TABLE " + T_FOOD + "(" +
                "foodID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mealID INTEGER," +
                "foodName TEXT," +
                "quantity INTEGER," +
                "caloriesPerUnit INTEGER," +
                "totalCalories INTEGER," +
                "FOREIGN KEY(mealID) REFERENCES " + T_MEAL + "(mealID) ON DELETE CASCADE" +
                ")";
        db.execSQL(createFood);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + T_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + T_MEAL);
        onCreate(db);
    }

    public long insertMeal(String mealName, String date, String mealType, int totalCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mealName", mealName);
        cv.put("date", date);
        cv.put("mealType", mealType);
        cv.put("totalCalories", totalCalories);
        return db.insert(T_MEAL, null, cv);
    }

    public long insertFoodItem(long mealID, String foodName, int quantity, int caloriesPerUnit, int totalCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mealID", mealID);
        cv.put("foodName", foodName);
        cv.put("quantity", quantity);
        cv.put("caloriesPerUnit", caloriesPerUnit);
        cv.put("totalCalories", totalCalories);
        return db.insert(T_FOOD, null, cv);
    }

    public Cursor getMealsByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + T_MEAL + " WHERE date = ? ORDER BY mealID DESC", new String[]{date});
    }

    public Cursor getAllDates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DISTINCT date FROM " + T_MEAL + " ORDER BY date DESC", null);
    }

    public int getTotalCaloriesByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(totalCalories) FROM " + T_MEAL + " WHERE date = ?", new String[]{date});
        int total = 0;
        if (c.moveToFirst()) {
            total = c.isNull(0) ? 0 : c.getInt(0);
        }
        c.close();
        return total;
    }

    public List<DayData> getCaloriesForLast7Days() {
        List<DayData> weeklyData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String date = sdf.format(cal.getTime());
            int totalCalories = getTotalCaloriesByDate(date);
            if (totalCalories > 0) {
                weeklyData.add(new DayData(date, totalCalories));
            }
        }
        Collections.reverse(weeklyData);
        return weeklyData;
    }
}

class DayData {
    String date;
    int calories;

    DayData(String date, int calories) {
        this.date = date;
        this.calories = calories;
    }
}
