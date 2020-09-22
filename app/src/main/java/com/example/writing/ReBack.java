package com.example.writing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ReBack extends Activity {
    private String tt="";
    private ReView fvFont;
    private final List<List<Test>> lists=new ArrayList<>();
    private final List<List<List<WordPoint>>>  listAllPoint= new ArrayList<>();
    private final List<WordPoint>itP=new ArrayList<>();
    private final List<List<WordPoint>>itP1=new ArrayList<>();
    private final List<List<List<Integer>>>listAllColor=new ArrayList<>();
    private final List<Integer>itC=new ArrayList<>();
    private final List<List<Integer>>itC1=new ArrayList<>();
    public void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_test);
        try {
            tt= Environment.getExternalStorageDirectory()
                    .getCanonicalPath().toString()
                    + "/HandWriting/data/" +"点.txt";
        }catch (Exception ignored){}
        GetData getData=new GetData();
        String tts=getData.readJsonFile(tt,"");
        //Log.d("dfs","tts="+tts);
        Gson gson6=new Gson();
        List<List<Test>> wocw = gson6.fromJson(tts, new TypeToken<List<List<Test>>>(){}.getType());
        Log.d("dfs","tw="+wocw.size());
        if(wocw!=null) {
            for (int i = 0; i < wocw.size(); i++) {
                for(int j=0;j<wocw.get(i).size();j++) {
                    WordPoint wordPoint = new WordPoint();
                    wordPoint.setX(wocw.get(i).get(i).getX());
                    wordPoint.setY(wocw.get(i).get(i).getX());
                    wordPoint.setWidth(20);
                    itP.add(wordPoint);
                    itC.add(-16777216);
                    //listAllColor.add(listHan.get(i).getColor());
                    //t_pro.setText("1");
                }
                itP1.add(itP);
                itC1.add(itC);
            }
            listAllPoint.add(itP1);
            listAllColor.add(itC1);

        }
        fvFont=(ReView)findViewById(R.id.tt);
        initView(0);
    }
    private void initView(int item){
        Log.d("dfs","tw="+listAllPoint.get(item));
        if(lists.size()>0) {
            for (int i = 0; i < listAllPoint.get(item).size(); i++) {
                List<WordPoint> teampPoint = listAllPoint.get(item).get(i);
                List<Integer> teampColor = listAllColor.get(item).get(i);
                fvFont.mPointsB = new ArrayList<>();
                fvFont.mColor = new ArrayList<>();
                for (int j = 0; j < teampPoint.size(); j++) {
                    fvFont.mColor.add(teampColor.get(j));
                    fvFont.mPointsB.add(teampPoint.get(j));
                    fvFont.postInvalidate();
                }
                fvFont.colorB.add(fvFont.mColor);
                fvFont.listPointB.add(fvFont.mPointsB);
                fvFont.mPointsB = new ArrayList<>();
                fvFont.mColor = new ArrayList<>();
                fvFont.postInvalidate();
            }
        }
    }
}
