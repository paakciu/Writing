package com.example.writing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
//import android.util.Log;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import static com.example.writing.PenConfig.STEPFACTOR;

public class NoteView extends View {
    public ArrayList<WordPoint> mPointsB  = new ArrayList<>() ;
    private final ArrayList<WordPoint> mHWPointList=new ArrayList<>();
    private final ArrayList<WordPoint>   mPointList = new ArrayList<>();
    public ArrayList<Integer>mColor=new ArrayList<>();
    List<List<WordPoint>> listPointB=new ArrayList<>();
    List<List<Integer>>colorB=new ArrayList<>();
    private WordPoint   mLastPoint = new WordPoint(0, 0);
    private double mBaseWidth;
    private double mLastVel;
    private double mLastWidth;
    private final Bezier mBezier = new Bezier();
    private WordPoint mCurPoint;
    private final boolean canDraw=true;
    private boolean canReDraw=false;
    private boolean canPlay=false;
    public boolean canSave=false;
    private final Paint mBitmapPaint;
    private Paint mPaint;
    public final List<Integer> colors=new ArrayList<>();
    public final Canvas cacheCanvas;
    private final Bitmap cachebBitmap;
    private final Path path;
    public int currentColor = Color.BLACK;
    private int currentSize = 30;




    //时间差
    private Date beginDate ;
    private Date endDate ;
    public long difftime;

    //标记时间差的2个函数
    public void beginSetTime()
    {
        beginDate=new Date(System.currentTimeMillis());
    }
    public void endSetTime()
    {
        endDate=new Date(System.currentTimeMillis());
        difftime=endDate.getTime()-beginDate.getTime();

    }
    //笔迹分析
    public HandWritingDataAnalysis analysis=new HandWritingDataAnalysis();




    //画笔颜色
    private final int[] paintColor = {
            Color.BLACK,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW,
            Color.CYAN,
            Color.LTGRAY
    };
    private void setPaintStyle(){
        mBaseWidth = currentSize;
    }
    public NoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnTouchListener(new OnTouchListenerImpl());
        mBaseWidth = currentSize;
        cachebBitmap=Bitmap.createBitmap(596,372, Config.ARGB_8888);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        cacheCanvas = new Canvas(cachebBitmap);
        path = new Path();
        initPaint();
    }
    public void selectPaintSize(int which){
        currentSize = Integer.parseInt(this.getResources().getStringArray(R.array.paintsize)[which]);
        setPaintStyle();
    }
    public void selectPaintColor(int which){
        currentColor = paintColor[which];
        setPaintStyle();
    }
    private class OnTouchListenerImpl implements OnTouchListener{
        public boolean onTouch(View v, MotionEvent event) {
            if(!canDraw)
            {
                return true;
            }
            int action = event.getAction() ;
            //获取点横纵坐标
            WordPoint WordPoint = new WordPoint((int)event.getX() ,(int)event.getY() ) ;
            //手按下
            if(action == MotionEvent.ACTION_DOWN){
                canReDraw=false;
                canPlay=false;
                onDown(event);

                //开始计时
                beginSetTime();
            }
            //手移动
            else if(action == MotionEvent.ACTION_MOVE){
                onMove(event);
            }
            //手抬起
            else if(action == MotionEvent.ACTION_UP){
                onUp(event);
                canReDraw=true;
                canPlay=true;

                //结束计时
                endSetTime();
                //手抬起来的时候，赶紧分析笔画的属性并且获取这次的map
                CheckBH checkBH =new CheckBH();
                int bh=checkBH.cb(mPointsB);
                HashMap<String, Double> map= checkBH.getMap();
                map.put("difftime", ( double ) difftime/10);
                double belief =analysis.put(bh,map);
                double beliefAVG=analysis.getBelief();
                double Intbelief=(int)(beliefAVG*100)/10;
                Log.e("手写分析测试","这笔画的时间差是"+difftime);
                Log.e("手写分析测试", "笔画号："+bh +"笔画："+checkBH.BiHua(bh));
                Toast.makeText(getContext(),"ID:"+bh +" 笔画：" +checkBH.BiHua(bh)+" 置信:"+belief+" 累计:"+Intbelief,Toast.LENGTH_SHORT).show();



            }
            invalidate() ;
            return true;
        }
    }
    private void onDown(MotionEvent event){
        initPaint();
        colors.add(currentColor);
        //记录down的控制点的信息
        WordPoint curPoint = new WordPoint(event.getX(), event.getY());
        mPointsB = new ArrayList<>() ;
        mColor=new ArrayList<>();
        //如果是手指画的，我们取他的0.8
        mLastWidth = 0.8 * mBaseWidth;
        //down下的点的宽度
        curPoint.width = (float) mLastWidth;
        mLastVel = 0;
        mPointList.add(curPoint);
        //记录当前的点
        mLastPoint = curPoint;
        canSave=true;
    }
    /**
     * 手指移动的事件
     */
    private void onMove(MotionEvent event){
        WordPoint curPoint = new WordPoint(event.getX(),event.getY());
        double deltaX = curPoint.x - mLastPoint.x;
        double deltaY = curPoint.y - mLastPoint.y;
        //deltaX和deltay平方和的二次方根 想象一个例子 1+1的平方根为1.4 （x²+y²）开根号
        //同理，当滑动的越快的话，deltaX+deltaY的值越大，这个越大的话，curDis也越大
        double curDis = Math.hypot(deltaX, deltaY);
        //我们求出的这个值越小，画的点或者是绘制椭圆形越多，这个值越大的话，绘制的越少，笔就越细，宽度越小
        double curVel = curDis * PenConfig.DIS_VEL_CAL_FACTOR;
        double curWidth;
        //点的集合少，我们得必须改变宽度,每次点击的down的时候，这个事件
        if (mPointList.size() < 2) {
            curWidth = calcNewWidth(curVel, mLastVel, curDis, 1.5, mLastWidth);
            curPoint.width = (float) curWidth;
            mBezier.init(mLastPoint, curPoint);
        } else {
            mLastVel = curVel;
            //阐明一点，当滑动的速度很快的时候，这个值就越小，越慢就越大，依靠着mlastWidth不断的变换
            curWidth = calcNewWidth(curVel, mLastVel, curDis, 1.5,
                    mLastWidth);
            curPoint.width = (float) curWidth;
            mBezier.addNode(curPoint);
        }
        //每次移动的话，这里赋值新的值
        mLastWidth = curWidth;
        mPointList.add(curPoint);
        moveNeetToDo(curDis);
        mLastPoint = curPoint;
    }
    /**
     * 手指抬起来的事件
     */
    private void onUp(MotionEvent event){
        mCurPoint = new WordPoint(event.getX(), event.getY());
        double deltaX = mCurPoint.x - mLastPoint.x;
        double deltaY = mCurPoint.y - mLastPoint.y;
        double curDis = Math.hypot(deltaX, deltaY);
        mCurPoint.width = 0;
        mPointList.add(mCurPoint);
        mBezier.addNode(mCurPoint);
        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            //Log.d("d","step="+t);
            WordPoint point = mBezier.getPoint(t);
            mHWPointList.add(point);
            mPointsB.add(point) ;
            mColor.add(currentColor);
        }

        mBezier.end();
        listPointB.add(mPointsB);
        colorB.add(mColor);
        mPointList.clear();



        //
    }


    private double calcNewWidth(double curVel, double lastVel, double curDis,
                                double factor, double lastWidth) {
        double calVel = curVel * 0.6 + lastVel * (1 - 0.6);
        //返回指定数字的自然对数
        //手指滑动的越快，这个值越小，为负数
        double vfac = Math.log(factor * 2.0f) * (-calVel);
        //此方法返回值e，其中e是自然对数的基数。
        //Math.exp(vfac) 变化范围为0 到1 当手指没有滑动的时候 这个值为1 当滑动很快的时候无线趋近于0
        //在次说明下，当手指抬起来，这个值会变大，这也就说明，抬起手太慢的话，笔锋效果不太明显
        //这就说明为什么笔锋的效果不太明显
        double calWidth = mBaseWidth * Math.exp(vfac);
        //滑动的速度越快的话，mMoveThres也越大
        double mMoveThres = curDis * 0.01f;
        //对之值最大的地方进行控制
        if (mMoveThres > PenConfig.WIDTH_THRES_MAX) {
            mMoveThres = PenConfig.WIDTH_THRES_MAX;
        }
        return calWidth;
    }


    private void moveNeetToDo(double curDis) {
        int steps = 1 + (int) curDis / STEPFACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
          //  Log.d("d","step="+t);
            WordPoint point = mBezier.getPoint(t);
            mHWPointList.add(point);
            mPointsB.add(point);
            mColor.add(currentColor);
        }
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(currentColor);
        mPaint.setTextSize(1000);
        mPaint.setStyle(Paint.Style.STROKE);
       // colors.add(currentColor);
    }

    private void draw1(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        //点的集合少 不去绘制
        if (mHWPointList == null || mHWPointList.size() < 1)
            return;
        //当控制点的集合很少的时候，需要画个小圆，但是需要算法
        if (mHWPointList.size() < 2) {
            WordPoint WordPoint = mHWPointList.get(0);
            //由于此问题在算法上还没有实现，所以暂时不给他画圆圈
            //canvas.drawCircle(WordPoint.x, WordPoint.y, WordPoint.width, mPaint);
        } else {
            drawNeetToDo(canvas);
        }
    }

    private void drawNeetToDo(Canvas canvas) {
        int i=0;
        for(List<WordPoint> itemPoints : listPointB)
        {
            if(itemPoints.size() > 1)
            {
                Iterator<WordPoint> pIterator = itemPoints.iterator() ;
                WordPoint first  = null ;
                WordPoint last = null ;
                while(pIterator.hasNext())
                {
                    if(first == null)
                    {
                        first = pIterator.next() ;
                    }
                    else
                    {
                        if(last != null )
                        {
                            first =last;
                        }
                        last = pIterator.next() ;
                       // mPaint.setColor(colorB.get(i).get(0));
                        mPaint.setColor(colors.get(i));
                       // Log.d("fist","point="+first);
                        drawLine(canvas,first.x, first.y,first.width, last.x, last.y,last.width, mPaint) ;
                       // canvas.drawLine(first.x, first.y,last.x, last.y,mPaint);
                    }
                }
                i++;
            }
           // Log.d("d","dd="+(i+1));
        }
//过程
        if(mPointsB.size() > 1)
        {
            //colors.add(Color.BLACK);
            Iterator<WordPoint> pIterator = mPointsB.iterator() ;
            WordPoint first  = null ;
            WordPoint last = null ;
            while(pIterator.hasNext())
            {
                if(first == null)
                {
                    first = pIterator.next();
                }
                else
                {
                    if(last != null )
                    {
                        first =last;
                    }
                    last = pIterator.next() ;
                    //mPaint.setColor(mColor.get(i));
                    mPaint.setColor(currentColor);
                    //Log.d("fist","point1="+first);
                    drawLine(canvas,first.x, first.y,first.width, last.x, last.y,last.width, mPaint);
                    //canvas.drawLine(first.x, first.y,last.x, last.y,mPaint);

                }
            }
           // colors.remove(colors.size()-1);
        }
    }

    //画线
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(cachebBitmap,0,0,null);
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        draw1(canvas);
        //画布背景
    }
    private void drawLine(Canvas canvas, double x0, double y0, double w0, double x1, double y1, double w1, Paint paint) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个园
        double curDis = Math.hypot(x0 - x1, y0 - y1);
       // Log.d("dd","dd="+curDis);
        int steps = 1;
        if (paint.getStrokeWidth() < 6) {
            steps = 1 + (int) (curDis /2);
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