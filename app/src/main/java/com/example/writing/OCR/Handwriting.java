package com.example.writing.OCR;

import android.util.Log;

import com.example.writing.OCR.Base64Util;
import com.example.writing.OCR.FileUtil;
import com.example.writing.OCR.HttpUtil;

import java.net.URLEncoder;

/**
 * 手写文字识别
 */
public class Handwriting {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static String get(String filepath) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/handwriting";
        try {
            // 本地文件路径
            String filePath = filepath;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();

            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            Log.e("手写分析测试", "result: "+result );
            return result;
        } catch (Exception e) {
            Log.e("手写分析测试", "API请求出现问题"+e.toString() );
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        Handwriting.handwriting();
//    }
}