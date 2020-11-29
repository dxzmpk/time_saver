package com.dxzmpk.time_saver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

    private List<Item> itemList;

    private TextView plainTextView;

    private Button buttonGet;

    private ListView textListView;

    private int menuItemId = R.id.plain_text;

    private View scrollView;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plainTextView = (TextView) findViewById(R.id.text_show);
        buttonGet = (Button) findViewById(R.id.button_get);
        textListView = (ListView) findViewById(R.id.text_list);
        scrollView = findViewById(R.id.scroll_view);
    }

    /**
     * create menu bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        menuItemId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.list_view:
                textListView.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                break;
            case R.id.plain_text:
                textListView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                break;
            default:
        }
        return true;
    }

    public void sendHttpRequest(ProgressDialog dialog) {
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
                            dialog.dismiss();
                            if (menuItemId == R.id.plain_text) {
                                plainTextView.setText(parsedString.toString());
                            } else {
                                ItemAdapter adapter = new ItemAdapter(MainActivity.this, R.layout.item,
                                        itemList);
                                textListView.setAdapter(adapter);
                            }

                            buttonGet.setEnabled(true);
                            buttonGet.setText(R.string.get);
                        }
                    });

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            plainTextView.setText("获取失败");
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

        // progress dialog
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("加载中");
        dialog.setMessage("此过程大约需要10秒，请耐心等待");
        dialog.setCancelable(true);
        dialog.show();
        sendHttpRequest(dialog);
    }
}