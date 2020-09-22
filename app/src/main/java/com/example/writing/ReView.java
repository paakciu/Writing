package com.example.writing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class ReView extends View {
    public List<WordPoint> mPointsB  = new ArrayList<>() ;
    public List<List<WordPoint>> listPointB=new ArrayList<>();
    public boolean canDraw=true;
    public boolean canReDraw=false;
    private final Paint mBitmapPaint;
    public final Canvas cacheCanvas;
    private final Bitmap cachebBitmap;
    public List<Integer>mColor=new ArrayList<>();
    public List<List<Integer>>colorB=new ArrayList<>();
    int Col=0;
    public List<Integer> colors=new ArrayList<>();
    private final Paint paint;
    public ReView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint =  new Paint() ;
        paint.setColor(Color.BLACK) ;//设置画笔颜色为红色
        paint.setAntiAlias(true);
        paint.setStrokeWidth(100);
        paint.setStyle(Paint.Style.STROKE);
        cachebBitmap=Bitmap.createBitmap(596,372, Bitmap.Config.ARGB_8888);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        cacheCanvas = new Canvas(cachebBitmap);
    }
    //画线
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        super.onDraw(canvas);

       for(int j=0;j<listPointB.size();j++) {
           List<WordPoint> lists=listPointB.get(j);
           List<Integer>colors=colorB.get(j);
           for (int i = 1; i < lists.size(); i++) {
               WordPoint first = null;
               WordPoint last = null;
               first = lists.get(i - 1);
               last =lists.get(i);
               paint.setColor(colors.get(i));
               drawLine(canvas, first.x, first.y, first.width, last.x, last.y, last.width, paint);
               //canvas.drawLine(first.x, first.y, last.x, last.y, paint);
           }
          // Log.d("s","ss"+j);
       }
        for (int i = 1; i < mPointsB.size(); i++) {
            WordPoint first = null;
            WordPoint last = null;
                first = mPointsB.get(i - 1);
                last = mPointsB.get(i);
                paint.setColor(mColor.get(i));
                drawLine(canvas, first.x, first.y, first.width, last.x, last.y, last.width, paint);
                //canvas.drawLine(first.x, first.y, last.x, last.y, paint);
        }
    }
    private void drawLine(Canvas canvas, double x0, double y0, double w0, double x1, double y1, double w1, Paint paint) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个园
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int steps = 1;
        if (paint.getStrokeWidth() < 6) {
            steps = 1 + (int) (curDis / 2);
        } else if (paint.getStrokeWidth() > 60) {
            steps = 1 + (int) (curDis / 4);
        } else {
            steps = 1 + (int) (curDis / 3);
        }
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;

        for (int i = 0; i < steps; i++) {
            //都是用于表示坐标系中的一块矩形区域，并可以对其做一些简单操作
            //精度不一样。Rect是使用int类型作为数值，RectF是使用float类型作为数值。
            //            Rect rect = new Rect();
            RectF oval = new RectF();
            oval.set((float) (x - w / 4.0f), (float) (y - w / 2.0f), (float) (x + w / 4.0f), (float) (y + w / 2.0f));
            // oval.set((float)(x+w/4.0f), (float)(y+w/4.0f), (float)(x-w/4.0f), (float)(y-w/4.0f));
            //最基本的实现，通过点控制线，绘制椭圆
            canvas.drawOval(oval, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }
    }
}
