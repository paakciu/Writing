package com.example.writing;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteReBack extends Activity {
    private SharedPreferences sp;
    private String account;
    private String password;
    private String filename;
    private int jia=50;
    private ListView listFont;
    private ReView fvFont;
    private final GetData getData=new GetData();
    private ProgressBarAsyncTask progressBarAsyncTask;
    private String path="";
    private String filepath="";
    public void onCreate(Bundle s){
        super.onCreate(s);

        setContentView(R.layout.note_reback);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            //这里是一个把uri转成String类型的路径的工具类
            path = JxdUtils.getPath(NoteReBack.this, uri);
            //Log.d("ss","p"+path);
        }
        //标题框
        listFont=(ListView)findViewById(R.id.listFont1);
        fvFont=(ReView)findViewById(R.id.fvFont);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        account = sp.getString("USER_NAME", "");
        password =sp.getString("PASSWORD", "");
        filename=sp.getString("FILENAME","");
        filepath=sp.getString("Path","");
        if(filepath!="") {
            try {
                path = Environment.getExternalStorageDirectory()
                        .getCanonicalPath().toString()
                        + "/Writing/PointData/" + filepath;
            } catch (Exception ignored) {
            }
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Path", "");
        editor.apply();
        String ff="";
        if(account!="") {
            try {
                ff = Environment.getExternalStorageDirectory()
                        .getCanonicalPath().toString()
                        + "/Writing/PointData/" + account + "-" + password + "-" + filename + ".txt";
            } catch (Exception ignored) {
            }
        }
        String str=getData.readJsonFile(ff,path);
        Gson gson1=new Gson();
        List<Han> listHan = gson1.fromJson(str, new TypeToken<List<Han>>(){}.getType());
        myAdpater=new MyAdpater(this,listHan);
        listFont.setAdapter(myAdpater);
        fvFont.canDraw=false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private MyAdpater myAdpater;
    private int currentPositon=-1;
    private Button currentButton;
    public class MyAdpater extends BaseAdapter
    {
        private final List<Han> listAdapter;
        private final Context contextMyShelf;
        MyAdpater(Context context, List<Han> list)
        {
            this.listAdapter = list;
            this.contextMyShelf = context;
        }
        @Override
        public int getCount() {
            if(listAdapter==null)return 0;
            return listAdapter.size();
        }
        @Override
        public Object getItem(int position) {
            return listAdapter.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        void ReWrite()
        {
            fvFont.listPointB.clear();
            fvFont.colorB.clear();
            fvFont.listPointB= new ArrayList<>();
            fvFont.colorB= new ArrayList<>();
            fvFont.invalidate();
            if (fvFont.cacheCanvas != null) {
                fvFont.cacheCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
                fvFont.invalidate();
            }
        }
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            MyAdpater.RecentViewHolder holder=null;
            if (convertView == null)
            {
                convertView= LayoutInflater.from(contextMyShelf).inflate(R.layout.list_font, null);
                holder= new RecentViewHolder();
                holder.btnPlay=(Button)convertView.findViewById(R.id.btnPlay);
                holder.ivFont=(ImageView)convertView.findViewById(R.id.ivFont);
                holder.textView=(TextView)convertView.findViewById(R.id.w_time);
                holder.btnShare=(Button)convertView.findViewById(R.id.btnshare);
                holder.btJ=(Button)convertView.findViewById(R.id.jia);
                convertView.setTag(holder);
            }
            else
            {
                holder= (RecentViewHolder)convertView.getTag();
            }
            Bitmap hashmap=getData.getBitmap(listAdapter.get(position).getBitmap());
            SimpleDateFormat formatter   =   new   SimpleDateFormat   ("yyyy年MM月dd日   HH:mm:ss");
            Date h=listAdapter.get(position).getTime();
            String   str   =   formatter.format(h);
            holder.ivFont.setImageBitmap(hashmap);
            holder.textView.setText(str);
            final Button btnPlay=holder.btnPlay;
            final Button btnShare=holder.btnShare;
            final Button btJ=holder.btJ;
            btnPlay.setOnClickListener(new View.OnClickListener(){

                public void onClick(View v)
                {
                   // Log.d("ss","cur1="+currentPositon+"pos="+position);
                    if(currentPositon!=position)
                    {
                      //  Log.d("ss","cur="+currentPositon+"pos="+position);
                        if(currentButton==null||!currentButton.equals(btnPlay))
                        {
                            currentPositon=position;
                        }
                        if(currentPositon==-2)
                        {
                            if(btnPlay.getText().equals("回放"))
                            {
                                btnPlay.setText("暂停");
                                fvFont.canDraw=false;
                                isPause=false;
                            }else{
                                btnPlay.setText("回放");
                                isPause=true;
                                return;
                            }
                        }
                        else
                        {
                            if(currentButton!=null&&currentPositon!=-2)
                            {
                               // flag=0;
                                //itemFlag=0;
                               // fvFont.listPointB.clear();
                               // Log.d("aa","fv=");
                                currentButton.setText("回放");
                                //progressBarAsyncTask.cancel(true);
                                //ReWrite();
                                //fvFont.colors.clear();
                            }
                       //     Log.d("aa","fv=");
                            btnPlay.setText("暂停");
                            fvFont.canDraw=false;
                            isPause=false;
                            currentButton=btnPlay;
                            ReWrite();
                        }
                        //fvFont.colors.clear();
                        if(progressBarAsyncTask!=null)
                        {
                            progressBarAsyncTask.cancel(true);
                            progressBarAsyncTask=null;
                        }

                    //    Log.d("ss","i="+1);
                        //fvFont.colors=listAdapter.get(position).getColor();
                       // Log.d("ss","get="+listAdapter.get(position).getColor());
                        progressBarAsyncTask=new ProgressBarAsyncTask(listAdapter.get(position).getLists(),listAdapter.get(position).getColor());
                        progressBarAsyncTask.execute();

                    }
                    else
                    {
                        currentPositon=-2;
                        btnPlay.setText("回放");
                        isPause=true;
                    }

                }

            });
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    account = sp.getString("USER_NAME", "");
                    password =sp.getString("PASSWORD", "");
                    filename=sp.getString("FILENAME","");
                    String ff=account+"-"+password+"-"+filename+".txt";
                        try {
                            File file = new File(Environment.getExternalStorageDirectory()
                                    .getCanonicalPath().toString()
                                    + "/Writing/PointData"
                                    + "/" + ff);
                           // Log.d("FF", "FF" + file);

                            shareFile(file, "*/*");
                        } catch (Exception ignored) {
                        }
                }
            });
            btJ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btJ.getText().equals("加速")){
                        btJ.setText("恢复");
                        jia=25;
                        //Toast.makeText(NoteReBack.this, "加速不稳定，卡死我不管的哦！", Toast.LENGTH_SHORT).show();
                    }else{
                        btJ.setText("加速");
                        jia=50;
                    }
                }
            });
            return convertView;
        }
        class RecentViewHolder
        {
            Button btnShare;
            Button btnPlay;
            ImageView ivFont;
            TextView textView;
            Button btJ;
        }
    }
    /** 分享指定（路径、类型）的文件 */
    private void shareFile(File file, String fileType)
    {

        if (file.exists())
        {
            Uri uri= FileProvider.getUriForFile(this,"com.example.writing.fileProvider", file);
           // Log.d("MM","uri="+uri);
            shareFile(this, uri, fileType);
        }
    }
    private final static int SELECT = 1;	// 标记选取
    private final static int SHARE = 2;		// 标记分享
    /** Activity执行结果 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == SELECT)
            {
                Uri uri = data.getData();
                String fileType = getType(uri);
                shareFile(this, uri, fileType);
                //Toast.makeText(this, "分享文件：" + uri.getPath().toString(), Toast.LENGTH_SHORT).show();
            }
            else if (requestCode == SHARE)
            {
                ShareSuccess();
            }
        }
    }
    /** 获取uri对应的文件类型 */
    private String getType(Uri uri)
    {
        String uriPath = uri.getPath().toString();	// "/document/image:57329"
        int start = uriPath.lastIndexOf("/");
        int end = uriPath.lastIndexOf(":");
        String type = "*/*";
        if (end > start)
        {
            type = uriPath.substring(start + 1, end) + "/*";
        }
        return type;
    }
    /** 分享完成逻辑 */
    private void ShareSuccess()
    {
        Toast.makeText(this, "分享完成！", Toast.LENGTH_SHORT).show();

    }
    /** 调用系統方法, 分享文件 */
    private static void shareFile(Activity context, Uri fileUri, String type)
    {
        if (fileUri != null)
        {
           // Log.d("MM","uri="+fileUri);
            Intent shareInt = new Intent(Intent.ACTION_SEND);
            shareInt.putExtra(Intent.EXTRA_STREAM, fileUri);
            //Toast.makeText(context, "分享文件类型：" + type, Toast.LENGTH_SHORT).show();
            shareInt.setType(type);			// 文件类型
            shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareInt.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareInt, "分享给"));
        }
        else
        {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
        }
    }
    private int flag=0;
    private int itemFlag=0;
    private boolean isPause;
    class ProgressBarAsyncTask extends AsyncTask<Void, Integer, Void> {
        final List<List<WordPoint>> points;
        final List<List<Integer>> colors;

        ProgressBarAsyncTask(List<List<WordPoint>> points, List<List<Integer>> colors)
        {
            this.points=points;
            this.colors=colors;
        }
        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数
         * 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected Void doInBackground(Void... params) {
            for(int i=itemFlag;i<points.size();i++)
            {
                List<Integer>teampColor=colors.get(i);
                List<WordPoint> teampPoint=points.get(i);
                fvFont.mPointsB= new ArrayList<>();
                fvFont.mColor=new ArrayList<>();
                for(int j=flag;j<teampPoint.size();j++)
                {
                   // Log.d("ss","j="+j);
                    if(isPause)
                    {
                        fvFont.colorB.add(fvFont.mColor);
                        fvFont.listPointB.add(fvFont.mPointsB);
                        return null;
                    }
                    fvFont.mColor.add(teampColor.get(j));
                    fvFont.mPointsB.add(teampPoint.get(j));
                    fvFont.postInvalidate();
                    try {
                        Thread.sleep(jia);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                       // Log.d("ss","获取access_token异常");
                    }
                    flag=j;
                }
                flag=0;
                fvFont.colorB.add(fvFont.mColor);
                fvFont.listPointB.add(fvFont.mPointsB);

                fvFont.mPointsB= new ArrayList<>();
                fvFont.mColor=new ArrayList<>();
                fvFont.postInvalidate();
                itemFlag=i;
            }
            currentPositon=-1;
            return null;
        }
        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(Void result) {
            fvFont.canReDraw=true;
            if(currentPositon!=-2)
            {
                if(currentButton!=null)
                {
                    currentButton.setText("回放");
                }
                currentButton=null;
                flag=0;
                itemFlag=0;
                isPause=false;
            }
        }
        //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            fvFont.canReDraw=false;
            fvFont.mPointsB= new ArrayList<>();
            fvFont.mColor=new ArrayList<>();
        }
        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            fvFont.invalidate();
        }
    }
}

