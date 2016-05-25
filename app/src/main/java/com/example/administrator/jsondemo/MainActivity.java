package com.example.administrator.jsondemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG     = "MainActivity";
    private static final String HttpUrl = "http://192.168.137.1:8080/index2.jsp";
    private Bean       bean;
    private List<Bean> list;
    private TextView   tv_person1;
    private TextView   tv_person2;
    private TextView   tv_class;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    tv_person1.setText("name:" + list.get(0).name + "age:" + list.get(0).age);
                    tv_person2.setText("name:" + list.get(1).name + "age:" + list.get(1).age);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_class = (TextView) findViewById(R.id.tv_class);
        tv_person1 = (TextView) findViewById(R.id.tv_person1);
        tv_person2 = (TextView) findViewById(R.id.tv_person2);
        list = new ArrayList<>();

        new Thread() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;//用于缓存
                String info = null; //用于返回得到的信息
                try {
                    URL url = new URL(HttpUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5 * 1000);


                    InputStream is = connection.getInputStream();//打开输入流
                    StringBuffer buffer = new StringBuffer();
                    if (is == null) {
                        return;//返回空值
                    }
                    reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return;//流是空值
                    }
                    info = buffer.toString();//得到JSON信息
                    Log.d(TAG, "info----------" + info);


//                    解析JSON代码
                    JSONObject jsonObject = new JSONObject(info);
                    String classInfo = jsonObject.getString("class");
                    tv_class.setText(classInfo);
                    Log.d(TAG, "classInfo--------" + classInfo);
                    JSONArray person = jsonObject.getJSONArray("students");
                    for (int i = 0; i < person.length(); i++) {
//                       将students信息进行封装
                        JSONObject json = person.getJSONObject(i);
                        bean = new Bean();
                        bean.age = json.getInt("age");
                        bean.name = json.getString("name");
                        list.add(bean);
                    }
                    handler.sendEmptyMessage(0x123);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }

}
