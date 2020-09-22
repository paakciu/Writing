package com.example.writing;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button bt_login = null;
    private Button del=null;
    private EditText et_account = null;
    private EditText et_password = null;
    private EditText et_filename= null;
    private String p;
    private String ff;
    private final GetData getData=new GetData();
    private SharedPreferences sp;
    private String account;
    private String password;
    private String filename;
    private long firstTime = 0;
    private final User user=new User();
    private Spinner spinnertext;
    private ArrayAdapter<String> adapter;
    private List<String>list;
    private String st="";
    private List<Han> list_han= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //检查初始状态
        initUI();
        try {
            String s=Environment.getExternalStorageDirectory()
                    .getCanonicalPath().toString()
                    + "/Writing/PointData/";
            list=getFilesAllName(s);
            Log.d("SS","list"+list);
        }catch (Exception ignored) {}
        if(sp.getString("USER_NAME", "")!="") {
            checkInitStatus(sp);
        }
        //登陆
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = et_account.getText().toString();
                password = et_password.getText().toString();
                filename=et_filename.getText().toString();
                if (account.equals("")) {
                    Toast.makeText(MainActivity.this, "学号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.equals("")) {
                    Toast.makeText(MainActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (filename.equals("")) {
                    Toast.makeText(MainActivity.this, "练习名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    user.setNickname(account);
                    user.setPassword(password);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("USER_NAME", account);
                    editor.putString("PASSWORD", password);
                    editor.putString("FILENAME",filename);
                    editor.apply();
                    Intent intent=new Intent(MainActivity.this, NoteActivity.class);
                    startActivity(intent);
                }
            }
        });
            /*ji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.size()>0) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Path", st);
                        editor.apply();
                        Intent intent = new Intent(MainActivity.this, Jixu.class);
                    startActivity(intent);
                    }else Toast.makeText(MainActivity.this, "没有练习，请新建！", Toast.LENGTH_SHORT).show();
                }
            });
*/
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Intent intent=new Intent(MainActivity.this,ReBack.class);
                   // startActivity(intent);
                    if(st!=""){  duihua();}
                    else
                        Toast.makeText(MainActivity.this, "没有练习！", Toast.LENGTH_SHORT).show();
                }
            });
        final String[]strings={"ss","sdf"};
        spinnertext = (Spinner) findViewById(R.id.spinner1);
        //第二步：为下拉列表定义一个适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        //第三步：设置下拉列表下拉时的菜单样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        spinnertext.setAdapter(adapter);
        //第五步：添加监听器，为下拉列表设置事件的响应
        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> argO, View argl, int arg2, long arg3) {
                // TODO Auto-generated method stub
                st=adapter.getItem(arg2);
                String[] strlist = st.split("-");
                String[] str = strlist[2].split("\\.");
                et_account.setText(strlist[0]);
                et_filename.setText(str[0]);
                et_password.setText(strlist[1]);
                Log.d("st","st="+strlist[2]);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Path", st);
                Log.d("st","s="+st);
                editor.apply();
                argO.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> argO) {
                // TODO Auto-generated method stub
                argO.setVisibility(View.VISIBLE);
            }
        });
    }
    private void duihua(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("提示：");
        builder.setMessage("您确定删除"+st+"?");
        //设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                p=sp.getString("Path","");
                if(st!="") try {
                    ff = Environment.getExternalStorageDirectory()
                            .getCanonicalPath().toString()
                            + "/Writing/PointData/" +st;
                }catch (Exception ignored){}
                String ss="";
                String str=getData.readJsonFile(ff,ss);
                Gson gson1=new Gson();
                List<Han> list1 = gson1.fromJson(str, new TypeToken<List<Han>>(){}.getType());
                if(list1!=null) {
                    list_han = list1;
                }
                for(int i=0;i<list_han.size();i++){
                    delete(list_han.get(i).getBitmap());
                }
                delete(ff);
                st="";
                try {
                    String s=Environment.getExternalStorageDirectory()
                            .getCanonicalPath().toString()
                            + "/Writing/PointData/";
                    list=getFilesAllName(s);
                    Log.d("SS","list"+list);
                }catch (Exception ignored) {}
                spinnertext = (Spinner) findViewById(R.id.spinner1);
                //第二步：为下拉列表定义一个适配器
                adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, list);
                //第三步：设置下拉列表下拉时的菜单样式
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //第四步：将适配器添加到下拉列表上
                spinnertext.setAdapter(adapter);
                //第五步：添加监听器，为下拉列表设置事件的响应
                spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> argO, View argl, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        st=adapter.getItem(arg2);
                        String[] strlist = st.split("-");
                        String[] str = strlist[2].split("\\.");
                        et_account.setText(strlist[0]);
                        et_filename.setText(str[0]);
                        et_password.setText(strlist[1]);
                        Log.d("st","st="+strlist[2]);
                        argO.setVisibility(View.VISIBLE);
                    }
                    public void onNothingSelected(AdapterView<?> argO) {
                        // TODO Auto-generated method stub
                        argO.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        //设置取消按钮
        builder.setNegativeButton("取消",null);
        //显示提示框
        builder.show();
    }
    private boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            Toast.makeText(getApplicationContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
        }
        return false;
    }
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {

            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除文件" + st+ "成功！");
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "删除文件" + st + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除文件失败：" + st+ "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private static List<String> getFilesAllName(String path) {
        File file=new File(path);
        File[] files=file.listFiles();

        List<String> s = new ArrayList<>();
        if (files == null){
           // s.add("没有练习！");
            Log.e("error","空目录");return s;}
        for (File value : files) {
            // .substring(0,files[i].getName().length()-4
            s.add(value.getName());
        }
        return s;
    }
            // 初始化UI对象
            private void initUI() {
                bt_login = findViewById(R.id.bt_login); // 登录按钮
                //ji=findViewById(R.id.bt_jixu);
                et_account = findViewById(R.id.et_account); // 输入账号
                et_filename=findViewById(R.id.filename);
                et_password = findViewById(R.id.et_password); // 输入密码
                del=findViewById(R.id.del);
            }
            @Override
            public void onBackPressed() {
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                } else {
                    System.exit(0);
                }
            }
    private void checkInitStatus(SharedPreferences sp) {
        //if (sp.getBoolean("ISCHECK", false)) {
        //设置默认是记录密码状态
        // mCbPwd.setChecked(true);
        et_account.setText(sp.getString("USER_NAME", ""));
        et_password.setText(sp.getString("PASSWORD", ""));
        et_filename.setText(sp.getString("FILENAME",""));
        account = et_account.getText().toString();
        password =et_password.getText().toString();
        filename=et_filename.getText().toString();
    }
}
