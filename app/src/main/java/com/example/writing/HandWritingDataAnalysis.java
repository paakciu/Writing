package com.example.writing;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;

/**
 * 笔迹数据分析
 * 这个类的任务有
 * 1.维护一张表，所有笔画的所有参数，也就是说，笔画->每次的map->分析保留数值 所有书写情况，分析一系列属性
 * 可能会有点乱，就是一个笔画，每次书写产生一个map，是对这个笔迹的一些描述，描述有：
 *      *       书写时间
 *      *=>difftime
 *      *      起始坐标点
 *      * =>firstX
 *      * =>firstY
 *      * =>lastX
 *      * =>lastX
 *      *      点的个数
 *      * =>pointNum
 *      *      笔画最大角度
 *      * =>maxAngle
 *      * =>DX_X
 *      * =>DX_Y
 *      * =>DY_Y
 *      * =>DY_X
 *      * =>DX
 *      * =>DY
 *      * =>AngleMaxY
 *      * =>AngleMaxX
 *      * =>Angle
 *
 * 根据这些描述，每个描述属性存起来，维护几个属性
 * 有：
 * MAX 最大值
 * MIN 最小值
 * AVG 平均值
 * NUM 个数
 * VAR 方差
 *
 * 即 笔画展开有描述，描述展开有属性
 */
public class HandWritingDataAnalysis {
    //属性表
    //根据CheckBH类，一共有32个基本笔画，其中6号笔画分了6，61，62，63，故一共有35个笔画号，识别不出的笔画不做分析
    //<笔画号，笔画>
    private HashMap<Integer,Strokes> map=new HashMap<Integer,Strokes>();
    //累计的置信度
    public double belief=0;
    private double beliefsum=0;
    private int beliefnum=0;
    private double blf=0;
    //方法表
    public double put(int strokenum,HashMap<String,Double> Dmap)
    {

        if(Dmap==null)return 0;
        if(checkNum(strokenum)) return 0;
        if (map.containsKey(strokenum))
        {
            blf=map.get(strokenum).add(Dmap);
            beliefsum+=blf;
            beliefnum++;
            belief=beliefsum/beliefnum;

        }
        else
        {
            map.put(strokenum,new Strokes(strokenum,Dmap));
        }
        return blf;
    }
    public Strokes get(int strokenum)
    {
        if(checkNum(strokenum))return null;
        return map.get(strokenum);
    }

    private boolean checkNum(int strokenum){
        //如果笔画在1到32之间为正常
        if(strokenum<=0||strokenum>32)
        {
            //如果笔画在不正常的范围且在61-63的范围，是正常的
            if(strokenum>=61&&strokenum<=63)
            {

            }
            //这里是不正常的
            else
                return true;
        }
        return false;
    }

    public void setBelief()
    {
        beliefsum=0;
        beliefnum=0;
    }
    public double getBelief()
    {
        belief=beliefsum/beliefnum;
        Log.e("手写分析测试", "put:累计的置信度: "+belief);
        return belief;
    }
}

//笔画类
class Strokes
{
    //笔画号和笔画名
    public int StrokesNum;
    public String StrokesName;

    //拥有一系列描述，可用map维护这个描述
    // <描述名，描述类>
    private HashMap<String,Describe> map=new HashMap<String, Describe>();


    public Strokes(int strokenum,HashMap<String,Double> Dmap)
    {
        StrokesNum=strokenum;
        StrokesName=CheckBH.BiHua(StrokesNum);
        add(Dmap);
    }
    //传入的是一个该笔画的描述map
    public double add(HashMap<String,Double> Dmap)
    {
        //Log.e("手写分析测试", "add笔画号: "+StrokesNum+"笔画名"+StrokesName);
        int flagnum=0;
        int flagsum=0;
        for (String key:Dmap.keySet())
        {
            flagnum++;
            flagsum+=put(key,Dmap.get(key));
        }
        double belief=(((double)flagnum-(double)flagsum)/(double)flagnum);
        Log.e("手写分析测试", "add 该笔画置信度为:"+belief+" num="+flagnum+" sum="+flagsum);

        return belief;
    }

    //这个put get用于维护笔画的描述表
    public int put(String str,double x)
    {
        int ret=0;
        if(map.containsKey(str))
        {
            ret=map.get(str).add(x);
        }
        else
        {
            map.put(str, new Describe(str,x));
        }
        return ret;
    }

    public Describe get(String str)
    {
        return map.get(str);
    }

}

//笔画里面有描述
class Describe
{
    //描述名
    public String DescribeName;

    //保留的属性分析
    public double MAX=Double.MIN_NORMAL;
    public double MIN=Double.MAX_VALUE;
    public double AVG=0;
    public double NUM=0;
    public double VAR=0;

    private double new_MAX;
    private double new_MIN;
    private double new_AVG;
    private double new_NUM;
    private double new_VAR;

    //构造函数
    public Describe(String name,double x)
    {
        MAX=Double.MIN_NORMAL;
        MIN=Double.MAX_VALUE;
        AVG=0;
        NUM=0;
        VAR=0;
        DescribeName=name;

        add(x);
    }
    public int add(double x)
    {
        //给x做一些检测，判断是否有效
        //if(x)
        int ret=0;
        ret=check(x);

//        //更新最大最小值
//        if(x>MAX)MAX=x;
//        if (x<MIN)MIN=x;
//        //更新求平均数
//        AVG=(NUM*AVG+x)/(NUM+1);
//        //更新求方差
//        VAR=(VAR*NUM+(x-AVG)*(x-AVG))/(NUM+1);
//        //数量加一
//        NUM=NUM+1;
        //更新数值
        MAX=new_MAX;
        MIN=new_MIN;
        AVG=new_AVG;
        VAR=new_VAR;
        NUM=new_NUM;

        //Log.e("笔画描述", "check描述： "+DescribeName+"最大/小值"+MAX+"/"+MIN+" AVG="+AVG+" VAR="+VAR);

        return ret;
    }

    //这个方法用来检测这个新增的值是否落在一个比较可信的范围内，并且通过add的时候返回
    private int check(double x)
    {
        int ret=0;

        //更新最大最小值
        if(x>MAX)new_MAX=x;
        if (x<MIN)new_MIN=x;
        //更新求平均数
        new_AVG=(NUM*AVG+x)/(NUM+1);
        //更新求方差
        new_VAR=(VAR*NUM+(x-new_AVG)*(x-new_AVG))/(NUM+1);
        //数量加一
        new_NUM=NUM+1;

        //开始进行数值比较
        //如果超出的最大最小值的范围，这次笔画不太可信

        //如果偏离平均值太多，不太可信

        //如果方差变大，证明不太稳定
        if(new_VAR>VAR) {
            ret=1;
            Log.e("笔画描述", "check 描述： " + DescribeName + "方差增大 VAR=" + new_VAR);
        }
        //选取比较好的描述点进行测试

        return ret;
    }
}


//描述里有保留的属性分析
