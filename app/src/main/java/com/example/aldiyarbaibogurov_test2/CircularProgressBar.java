package com.example.aldiyarbaibogurov_test2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {

    private Paint backgroundPaint, progressPaint;
    private RectF rectF = new RectF();
    private float progress = 0;
    private int colorNormal = Color.parseColor("#4CAF50"); // green
    private int colorExceeded = Color.parseColor("#F44336"); // red
    private int goal = 2000;

    public CircularProgressBar(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.parseColor("#DDDDDD"));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(35f);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(35f);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setGoal(int dailyGoal) {
        this.goal = dailyGoal;
        invalidate();
    }

    public void setProgress(int consumed) {
        this.progress = consumed;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight());
        rectF.set(20, 20, size - 20, size - 20);

        // Setting the progress color
        if (progress > goal) {
            progressPaint.setColor(colorExceeded); // red
        } else {
            progressPaint.setColor(colorNormal);   // green
        }

        // Background circle
        canvas.drawArc(rectF, -90, 360, false, backgroundPaint);

        // Progress
        float angle = (progress / goal) * 360f;
        canvas.drawArc(rectF, -90, angle, false, progressPaint);
    }
}

