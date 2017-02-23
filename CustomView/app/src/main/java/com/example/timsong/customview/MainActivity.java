package com.example.timsong.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static class LinedEditText extends EditText {
        private Rect mRect;
        private Paint mPaint;
        private int mLineOffset;

        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);

            mRect = new Rect();
            mPaint = new Paint();

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinedEditText);
            int lineColor = typedArray.getColor(R.styleable.LinedEditText_lineColor, 0x800000FF);
            mLineOffset = typedArray.getDimensionPixelOffset(R.styleable.LinedEditText_lineOffset, 3);
            typedArray.recycle();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(lineColor);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int count = getLineCount();
            Rect rect = mRect;
            Paint paint = mPaint;
            for (int i = 0; i < count - 1; i++) {
                int baseline = getLineBounds(i, rect);
                canvas.drawLine(rect.left, baseline + mLineOffset, rect.right, baseline + mLineOffset, paint);
            }
            super.onDraw(canvas);
        }
    }
}
