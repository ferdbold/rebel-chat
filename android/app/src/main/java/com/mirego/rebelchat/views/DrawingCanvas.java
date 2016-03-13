package com.mirego.rebelchat.views;

import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;

public class DrawingCanvas extends View {

    public DrawingCanvas(Context context) { super(context); }

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void onTouchStart(float x, float y) {
        // TODO: Implement this
        System.out.println(String.format("Touch start: [%.2f, %.2f]", x, y));
    }

    private void onTouchMove(float x, float y) {
        // TODO: Implement this
        System.out.println(String.format("Touch move: [%.2f, %.2f]", x, y));
    }

    private void onTouchEnd() {
        // TODO: Implement this
        System.out.println("Touch end");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.onTouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                this.onTouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                this.onTouchEnd();
                invalidate();
                break;
        }
        return true;
    }
}
