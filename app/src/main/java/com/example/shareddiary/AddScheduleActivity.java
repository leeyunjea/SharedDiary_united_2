package com.example.shareddiary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class AddScheduleActivity extends AppCompatActivity {

    EditText content_editText, time_editText, location_editText;
    Button saveBtn;
    TextView showToday_TextView;
    String date;
    CalContents contents;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/calendar_DBcontrol.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        showToday_TextView = (TextView)findViewById(R.id.showToday_textView);
        showToday_TextView.setText(date);

        saveBtn = (Button)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            //세이브 버튼이 눌렸을때
            @Override
            public void onClick(View v) {
                getSchedule(); //작성한 내용 가져오기
                SaveScheduleDB saveScheduleDB = new SaveScheduleDB();
                saveScheduleDB.execute();
                Toast.makeText(AddScheduleActivity.this,"저장되었습니다.",Toast.LENGTH_LONG).show();

                /*Intent intent1 = new Intent(getApplicationContext(),CalActivity.class);
                startActivity(intent1);*/

                finish();
           }
        });
    }


    public void getSchedule(){
        contents = new CalContents();

        //작성한 내용 저장
        content_editText = (EditText)findViewById(R.id.content_editText);
        contents.content = content_editText.getText().toString();

        time_editText = (EditText)findViewById(R.id.time_editText);
        contents.time = time_editText.getText().toString();

        location_editText = (EditText)findViewById(R.id.location_editText);
        contents.location = location_editText.getText().toString();

        contents.date = date;
    }

    class SaveScheduleDB extends AsyncTask<String,String,String>{
        String data;
        String receiveMsg;
        String getJsonData;

        @Override
        protected String doInBackground(String... params) {
            String param = "content=" + contents.content + "&time=" + contents.time + "&location=" + contents.location + "&dates=" + contents.date + "&name="+MainActivity.name;
            Log.i("yeji", param);

            try {
                //String data;
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", "UTF-8"); //한글 인코딩
                conn.setDoInput(true);
                conn.connect();

                Log.i("yeji", "URL에 접속");

                //안드로이드 -> 서버 파라미터값 전달
                OutputStreamWriter ows = new OutputStreamWriter(conn.getOutputStream());
                ows.write(param);
                ows.flush();
                //ows.close();

                /*Intent intent = new Intent(getMainContext(), CalActivity.class);
                startActivity(intent);
                finish();*/

                Log.i("yeji", "일정추가 완료");

                //서버 -> 안드로이드 파라미터값 전달
                if(conn.getResponseCode() == conn.HTTP_OK){

                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(in);
                    StringBuffer buffer = new StringBuffer();

                    while((data = reader.readLine()) != null) {
                        buffer.append(data);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("yeji", "서버에서 안드로이드로 전달 됨");

                    JSONObject json = new JSONObject(receiveMsg);
                    JSONArray jArr = json.getJSONArray("datasend");

                    /*for(int i=0; i<jArr.length(); i++){
                        json = jArr.getJSONObject(i);
                        getJsonData = json.getString("myName");
                    }*/

                }else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
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
