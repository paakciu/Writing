# Writing

安卓项目，包括手写文字，手写识别，笔迹识别，笔迹匹配分析等功能

# 第一次使用需要配置：

新建一个包和java文件，/src/main/java/com/example/writing/config/ocrConfig.java

内容是关于数据的配置，用户名，密码，ip地址，端口

```
package com.example.writing.config;

public class ocrConfig {
    // 官网获取的 API Key 更新为你注册的
    public static String clientId = "xxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    // 官网获取的 Secret Key 更新为你注册的
    public static String clientSecret = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
}

```

