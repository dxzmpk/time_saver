package com.dxzmpk.time_saver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Item> itemList;

    private TextView textShow;

    private Button buttonGet;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textShow = (TextView) findViewById(R.id.text_show);
        buttonGet = (Button) findViewById(R.id.button_get);
    }

    public void sendHttpRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: started");
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://42.192.206.123/");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    // 超时时间设定为18秒
                    connection.setReadTimeout(18000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!=null) {
                        response.append(line);
                    }
                    Log.e(TAG, "http success" + response.toString() );

                    JSONArray jsonarray = new JSONArray(response.toString());
                    StringBuilder parsedString = new StringBuilder();

                    List<Item> itemList = new ArrayList<>();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        Item currentItem = new Item(jsonobject.getString("title"), jsonobject.getString("link"));
                        itemList.add(currentItem);
                        parsedString.append(currentItem.toString());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textShow.setText(parsedString.toString());
                            buttonGet.setEnabled(true);
                            buttonGet.setText(R.string.get);
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textShow.setText("获取失败");
                        }
                    });
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }


    public void getNewest(View view) {
        view.setEnabled(false);
        ((Button)view).setText(R.string.getting_message);
        sendHttpRequest();
    }
}