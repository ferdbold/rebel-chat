package com.mirego.rebelchat.views;

import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;

public class DrawingCanvas extends View {
    private static final double DISTANCE_TOLERANCE = 20;

    public static final int SMALL_BRUSH = 5;
    public static final int MEDIUM_BRUSH = 25;
    public static final int LARGE_BRUSH = 50;

    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint pathPaint;
    private Paint bitmapPaint;
    private PointF pathPos;

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
        this.pathPos = new PointF();
        this.pathPaint = new Paint();
        this.bitmapPaint = new Paint(Paint.DITHER_FLAG);

        this.pathPaint.setDither(true);
        this.pathPaint.setColor(Color.WHITE);
        this.pathPaint.setStyle(Paint.Style.STROKE);
        this.pathPaint.setStrokeJoin(Paint.Join.ROUND);
        this.pathPaint.setStrokeCap(Paint.Cap.ROUND);
        this.pathPaint.setStrokeWidth(DrawingCanvas.SMALL_BRUSH);
    }

    public void SetBrushSize(int size) {
        this.pathPaint.setStrokeWidth(size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(this.bitmap, 0, 0, this.bitmapPaint);
        canvas.drawPath(this.path, this.pathPaint);
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
        this.pathPos.x = x;
        this.pathPos.y = y;
    }

    protected void onTouchMove(float x, float y) {
        double distance = Math.sqrt(Math.pow(x - this.pathPos.x, 2) + Math.pow(y - this.pathPos.y, 2));

        if (distance > DrawingCanvas.DISTANCE_TOLERANCE) {
            this.path.lineTo(x, y);
            this.pathPos.x = x;
            this.pathPos.y = y;
        }
    }

    protected void onTouchEnd() {
        this.canvas.drawPath(this.path, this.pathPaint);
        this.path.reset();
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
