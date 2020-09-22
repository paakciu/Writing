package com.example.writing;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Jixu extends Activity implements View.OnClickListener{
    private String ff;
    private final static float DESIGN_WIDTH = 750; //绘制页面时参照的设计图宽度
    private int select_paint_size_index = 0;
    private int select_paint_color_index = 0;
    private Button btnComplete;
    private Button btnReWrite;
    private Button reback;
    private final GetData getData=new GetData();
    private String path2;
    private NoteView fvFont;
    private static List<Bitmap> listBitMap= new ArrayList<>();
    private final List<Bitmap> listBitMap1= new ArrayList<>();
    private List<WordPoint> pointList;
    private List<List<WordPoint>> itemPointList;
    private List<List<Integer>>cL;
    private List<Integer>cc;
    private final List<List<List<WordPoint>>>  listAllPoint= new ArrayList<>();
    private List<Han> list_han= new ArrayList<>();
    public void onCreate(Bundle s){
        super.onCreate(s);
        resetDensity();//注意不要漏掉
        setContentView(R.layout.note);
        initData();
        initUI();
        initData1();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetDensity();//这个方法重写也是很有必要的
    }
    private void resetDensity(){
        Point size = new Point();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        getResources().getDisplayMetrics().xdpi = size.x/DESIGN_WIDTH*72f;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void initUI(){
        //标题框
        btnComplete=(Button)findViewById(R.id.btnComplete);
        btnReWrite=(Button)findViewById(R.id.btnReWrite);
        reback=(Button)findViewById(R.id.btnReBack);
        ImageView setting = (ImageView) findViewById(R.id.setting);
        btnComplete.setOnClickListener(this);
        btnReWrite.setOnClickListener(this);
        reback.setOnClickListener(this);
        setting.setOnClickListener(this);
        fvFont=(NoteView)findViewById(R.id.fvFont);

    }
    //弹出画笔颜色选项对话框
    private void showPaintColorDialog(View parent){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.custom_dialog);
        alertDialogBuilder.setTitle("选择画笔颜色：");
        alertDialogBuilder.setSingleChoiceItems(R.array.paintcolor, select_paint_color_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_color_index = which;
                fvFont.selectPaintColor(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }
    //弹出画笔设置选项对话框
    private void showSetDialog(){
        final String[] set = new String[] { "字体大小","字体颜色" };
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.custom_dialog);
        alertDialogBuilder.setTitle("选择设置：");
        alertDialogBuilder.setSingleChoiceItems(set, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==1){
                    showPaintColorDialog(fvFont);
                }else if(which==0){
                    showPaintSizeDialog(fvFont);
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }
    //弹出画笔大小选项对话框
    private void showPaintSizeDialog(View parent){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.custom_dialog);
        alertDialogBuilder.setTitle("选择画笔大小：");
        alertDialogBuilder.setSingleChoiceItems(R.array.paintsize, select_paint_size_index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select_paint_size_index = which;
                fvFont.selectPaintSize(which);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }
    //创建文件夹
    private void initData() {
        //如果手机有sd卡
        try {
            String path = Environment.getExternalStorageDirectory()
                    .getCanonicalPath().toString()
                    + "/Writing";
            File files = new File(path);
            if (!files.exists()) {
                //如果有没有文件夹就创建文件夹
                files.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            path2 = Environment.getExternalStorageDirectory()
                    .getCanonicalPath().toString()
                    + "/Writing/Picture";
            File file1 = new File(path2);
            if (!file1.exists()) {
                //如果有没有文件夹就创建文件夹
                file1.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String path3 = Environment.getExternalStorageDirectory()
                    .getCanonicalPath().toString()
                    + "/Writing/PointData";
            File file2 = new File(path3);
            if (!file2.exists()) {
                //如果有没有文件夹就创建文件夹
                file2.mkdir();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private  void initData1(){
        SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String account = sp.getString("USER_NAME", "");
        String password = sp.getString("PASSWORD", "");
        String filename = sp.getString("FILENAME", "");
        String p = sp.getString("Path", "");
        Log.d("st","s1="+ p);
        String f = account + "-" + password + "-" + filename + ".txt";
        if(p !="") try {
            ff = Environment.getExternalStorageDirectory()
                    .getCanonicalPath().toString()
                    + "/Writing/PointData/" + p;
        }catch (Exception ignored){}

       // Log.d("dd","ddd"+ff);
        String ss="";
        String s=getData.readJsonFile(ff,ss);
       // Log.d("MainActivity","list3="+s);
        Gson gson1=new Gson();
        List<Han> statusLs = gson1.fromJson(s, new TypeToken<List<Han>>(){}.getType());
        //Log.d("MainActivity","li="+statusLs);
        if(statusLs!=null){
            list_han=statusLs;

            for (int i=0;i<statusLs.size();i++ ){
                listAllPoint.add(statusLs.get(i).getLists());
            }
        }
        for(int i=0;i!=-1;i++) {
            if(get(i)!=null) {
                listBitMap1.add(get(i));
                if(listBitMap!=null)
                    listBitMap = listBitMap1;
            }
            else break;
        }
    }

    //写到本地文件夹
    private String saveToLocal(String fileName) {
        //文件名
        // String fileName ="config.txt";
        try {
            //文件夹路径
           // File dir = new File(path3);
            //文件夹不存在和传入的value值为1时，才允许进入创建
            Gson gson = new Gson();
            String str = gson.toJson(list_han);
            File file = new File(fileName);
            OutputStream out = new FileOutputStream(file);
            out.write(str.getBytes());
            out.close();
            return file.getPath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private void saveToSystemGallery(Context context, Bitmap bmp, long filename) {
        // 首先保存图片
        File appDir = new File(path2);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = filename+ ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap get(int filename) {
        String path3 = path2 + "/"+filename+".jpg";
        File mFile = new File(path3);
       // Log.d("MainActivity","bit="+mFile);
        //若该文件存在
        if(mFile!=null) {
            // Log.d("MainActivity","bit="+bitmap);
            return BitmapFactory.decodeFile(path3);
        }
        return null;
    }
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnComplete) {
            if(fvFont.canSave) {
                fvFont.canSave=false;
                //btnComplete.setEnabled(false);
                if (fvFont.listPointB.size() > 0) {
                    // Log.d("W", "fv=" + fvFont.listPoint);
                    itemPointList = new ArrayList<>();
                    cL= new ArrayList<>();

                    for (List<WordPoint> listP : fvFont.listPointB) {
                        pointList = new ArrayList<>();
                        for (WordPoint p : listP) {
                            pointList.add(p);
                        }
                        itemPointList.add(pointList);
                    }
                    for(List<Integer>c:fvFont.colorB){
                        cc=new ArrayList<>();
                        for(int n:c){
                            cc.add(n);
                        }
                        cL.add(cc);
                    }
                    listAllPoint.add(itemPointList);
                    Bitmap bitmap = viewToBitmap(fvFont, fvFont.getWidth(), fvFont.getHeight());
                    listBitMap.add(viewToBitmap(fvFont, fvFont.getWidth(), fvFont.getHeight()));
                    Han han = new Han();
                    long id_han = new Date().getTime();
                    han.setId(id_han);
                    Date curDate = new Date(System.currentTimeMillis());
                    han.setTime(curDate);
                    han.setBitmap(path2 + "/" + id_han + ".jpg");
                    han.setLists(itemPointList);
                    han.setColor(cL);
                    list_han.add(han);
                    for(int i=0;i<list_han.size();i++){
                        Log.d("han","hanc="+list_han.get(i).getColor()+"i="+i);
                    }
                    saveToLocal(ff);
                    saveToSystemGallery(Jixu.this, bitmap, id_han);
                    // fvFont.colors.clear();
                }
            }else Toast.makeText(this, "请写字！", Toast.LENGTH_SHORT).show();
        }
        if(v.getId()==R.id.btnReWrite)
        {
            ReWrite();
        }
        else if(v.getId()==R.id.setting){
            showSetDialog();
        }
        else if(v.getId()==R.id.btnReBack){
            //保存图片到本地
            //跳到回放界面
            Intent intent = new Intent(Jixu.this, NoteReBack.class);
            startActivity(intent);
        }
    }

    //重写
    private void ReWrite()
    {
        fvFont.listPointB.clear();
        fvFont.mPointsB.clear();
        fvFont.colorB.clear();
        fvFont.mColor.clear();
        fvFont.mColor=new ArrayList<>();
        fvFont.colorB=new ArrayList<>();
        fvFont.mPointsB=new ArrayList<>();
        fvFont.listPointB= new ArrayList<>();
        fvFont.currentColor=fvFont.colors.get(fvFont.colors.size()-1);
        fvFont.colors.clear();
        fvFont.invalidate();
        if (fvFont.cacheCanvas != null) {
            fvFont.cacheCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
            fvFont.invalidate();
        }
    }
    //生成位图
    private Bitmap viewToBitmap(View view, int bitmapWidth, int bitmapHeight){
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }
}
