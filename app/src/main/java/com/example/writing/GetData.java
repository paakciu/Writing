package com.example.writing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

class GetData {
    //读取数据
    public  String readJsonFile(String p,String pp) {

        StringBuilder sb = new StringBuilder();

            File file;
            if(pp!="") {
                 file = new File(pp);

            }else {
                file = new File(p);
            }
            try {
            InputStream in = null;
            if(file.exists()) {
                in = new FileInputStream(file);

                int tempbyte;
                while ((tempbyte = in.read()) != -1) {
                    sb.append((char) tempbyte);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public Bitmap getBitmap(String path) {
        File mFile = new File(path);
        //若该文件存在
        if(mFile!=null) {
            return BitmapFactory.decodeFile(path);
        }else
        return null;
    }
}
