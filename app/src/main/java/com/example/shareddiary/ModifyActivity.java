package com.example.shareddiary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ModifyActivity extends AppCompatActivity {

    EditText title;
    EditText contents;
    TextView tv;

    String Btitle;
    String BuserID;
    String Bdate;
    String Bcontents;
    ModifyDB MDB = new ModifyDB();

    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/ModifyDiary.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        Intent intent = getIntent();

        Btitle = intent.getStringExtra("title");
        title = (EditText)findViewById(R.id.mtitle);
        title.setText(Btitle);

        Bcontents = intent.getStringExtra("contents");
        contents = (EditText) findViewById(R.id.mcontent);
        contents.setText(Bcontents);

        BuserID = intent.getStringExtra("userID");

        TextView cancel = (TextView)findViewById(R.id.mcancel);
        TextView save = (TextView)findViewById(R.id.msave);

        cancel.setOnClickListener(new TextView.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new TextView.OnClickListener(){
            public void onClick(View v) {
                // 오늘날짜 관련 변수
                SimpleDateFormat today = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                Bdate = today.format(new Date());
                Btitle = title.getText().toString();
                Bcontents = contents.getText().toString();

                MDB.execute();
            }
        });

        tv = (TextView)findViewById(R.id.mtv);
        String count= String.valueOf(contents.getText().toString().length());
        tv.setText("글자수 : " + count);

        contents.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                String count= String.valueOf(contents.getText().toString().length());
                tv.setText("글자수 : " + count);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }

    public Context getMainContext() {
        return this;
    }

    class ModifyDB extends AsyncTask<String, String, String> {
        String data;
        String receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            String param = "bd_dates=" + Bdate + "&bd_contents=" + Bcontents +  "&bd_title=" + Btitle + "&bd_user=" + BuserID+"";
            Log.i("yunjae", param);

            try {
                //String data;
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setDoInput(true);
                conn.connect();

                Log.i("yunjae", "URL에 접속");

                //안드로이드 -> 서버 파라미터값 전달
                OutputStreamWriter ows = new OutputStreamWriter(conn.getOutputStream());
                ows.write(param);
                ows.flush();
                Log.i("yunjae", "!!게시판 성공!!");
                Intent intent = new Intent(getMainContext(), DetailActivity.class);
                intent.putExtra("userID", BuserID);
                intent.putExtra("title", Btitle);
                intent.putExtra("date", Bdate);
                intent.putExtra("contents", Bcontents);
                startActivity(intent);
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
                    Log.i("yunjae", "서버에서 안드로이드로 전달 됨");
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