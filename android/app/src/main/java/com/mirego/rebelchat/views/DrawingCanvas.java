package com.mirego.rebelchat.views;

import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;

public class DrawingCanvas extends View {
    private static double DISTANCE_TOLERANCE = 5;

    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint pathPaint;
    private Paint bitmapPaint;
    private float[] pathPos;

    public DrawingCanvas(Context context) {
        super(context);
        this.init();
    }

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DrawingCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init() {
        this.path = new Path();
        this.pathPos = new float[2];
        this.pathPaint = new Paint();
        this.bitmapPaint = new Paint(Paint.DITHER_FLAG);

        this.pathPaint.setARGB(1, 255, 0, 0);
        this.pathPaint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(this.bitmap, 0, 0, this.bitmapPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
    }

    protected void onTouchStart(float x, float y) {
        this.path.reset();
        this.path.moveTo(x, y);
        this.pathPos[0] = x;
        this.pathPos[1] = y;

        System.out.println(String.format("Touch start: [%.2f, %.2f]", x, y));
    }

    protected void onTouchMove(float x, float y) {
        double distance = Math.sqrt(Math.pow(x - this.pathPos[0], 2) + Math.pow(y - this.pathPos[1], 2));

        if (distance > DrawingCanvas.DISTANCE_TOLERANCE) {
            this.path.moveTo(x, y);
            this.pathPos[0] = x;
            this.pathPos[1] = y;
        }

        System.out.println(String.format("Touch move: [%.2f, %.2f]", x, y));
    }

    protected void onTouchEnd() {
        this.canvas.drawPath(this.path, this.pathPaint);
        this.path.reset();

        System.out.println("Touch end");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.onTouchStart(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                this.onTouchMove(event.getX(), event.getY());
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
