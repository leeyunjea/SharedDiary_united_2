package com.example.shareddiary;

/**
 * Created by 이예지 on 2017-08-10.
 */
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BoardActivity extends AppCompatActivity {

    TextView tv;
    EditText content;
    EditText title;
    String bd_dates;
    String title_str;
    String content_str;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/board_saveDB.jsp";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        Intent intent = getIntent();
        String haneul = intent.getStringExtra("haneul");
        Log.i("yeji", haneul);

        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        TextView cancel = (TextView)findViewById(R.id.cancel);
        TextView save = (TextView)findViewById(R.id.save);

        cancel.setOnClickListener(new TextView.OnClickListener(){
            public void onClick(View v) {
                //Intent intent = new Intent(BoardActivity.this, BoardListActivity.class);
                //startActivity(intent);
                finish();
            }
        });

        save.setOnClickListener(new TextView.OnClickListener(){
            public void onClick(View v) {
                // 오늘날짜 관련 변수
                SimpleDateFormat today = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                bd_dates = today.format(new Date());
                title_str = title.getText().toString();
                content_str = content.getText().toString();
                BoardDB BDB = new BoardDB();
                BDB.execute();
            }
        });

        tv = (TextView)findViewById(R.id.tv);
        String count= String.valueOf(content.getText().toString().length());
        tv.setText("글자수 : " + count);

        content.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                String count= String.valueOf(content.getText().toString().length());
                tv.setText("글자수 : " + count);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }

    public Context getMainContext() {
        return this;
    }

    class BoardDB extends AsyncTask<String, String, String> {
        String data;
        String receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            String param = "bd_dates=" + bd_dates + "&bd_contents=" + content_str +  "&bd_title=" + title_str +"&bd_user="+MainActivity.myId;
            Log.i("yeji", param);

            try {
                //String data;
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Charset", "UTF-8");

                conn.setDoInput(true);
                conn.connect();

                Log.i("yeji", "boardSave URL에 접속");

                //안드로이드 -> 서버 파라미터값 전달
                OutputStreamWriter ows = new OutputStreamWriter(conn.getOutputStream());
                ows.write(param);
                ows.flush();
                Log.i("yeji", "!!게시판 성공!!");
                //Intent intent = new Intent(getMainContext(), BoardListActivity.class);
                //startActivity(intent);
                finish();

                //서버 -> 안드로이드 파라미터값 전달
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(in);
                    StringBuffer buffer = new StringBuffer();

                    while ((data = reader.readLine()) != null) {
                        buffer.append(data);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("yeji", "서버에서 안드로이드로 전달 됨");
                }
            }catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
    }
}