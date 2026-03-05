package com.example.aldiyarbaibogurov_test2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BarChartView extends View {

    private int[] data = new int[0];
    private String[] labels = new String[0];
    private Paint barPaint = new Paint();
    private Paint textPaint = new Paint();

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        barPaint.setColor(Color.parseColor("#4CAF50"));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(int[] values, String[] labels) {
        this.data = values;
        this.labels = labels;
        invalidate(); // Redrawing the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data.length == 0) return;

        int width = getWidth();
        int height = getHeight();
        int max = 1;

        for (int v : data) {
            if (v > max) max = v;
        }

        int barWidth = width / data.length;

        for (int i = 0; i < data.length; i++) {
            float barHeight = (data[i] / (float) max) * (height - 40); // Creating space for labels
            canvas.drawRect(
                    i * barWidth + 10,
                    height - barHeight - 40,
                    (i + 1) * barWidth - 10,
                    height - 40,
                    barPaint
            );

            // Drawing label
            canvas.drawText(
                    labels[i],
                    i * barWidth + (barWidth / 2),
                    height - 10,
                    textPaint
            );
        }
    }
}
