package com.example.shareddiary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    String reg_idText;
    String reg_passwordText;
    String reg_nameText;
    String reg_ageText;
    String reg_groupCode;
    String reg_groupPw;

    EditText idText;
    EditText passwordText;
    EditText nameText;
    EditText ageText;
    EditText groupCode;
    EditText groupPw;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/signup.jsp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        nameText = (EditText) findViewById(R.id.nameText);
        ageText = (EditText) findViewById(R.id.ageText);
        groupCode = (EditText) findViewById(R.id.groupCode);
        groupPw = (EditText) findViewById(R.id.groupPw);

        Button registerButton = (Button) findViewById(R.id.registerButton);

        //버튼 클릭시 입력한 정보들 저장, DB 로 전송
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg_idText = idText.getText().toString();
                reg_passwordText = passwordText.getText().toString();
                reg_nameText = nameText.getText().toString();
                reg_ageText = ageText.getText().toString();
                reg_groupCode = groupCode.getText().toString();
                reg_groupPw = groupPw.getText().toString();


                loginDB IDB = new loginDB();
                IDB.execute(reg_idText , reg_passwordText, reg_nameText, reg_ageText, reg_groupCode, reg_groupPw);
            }
        });
    }

    class loginDB extends AsyncTask<String, Integer, String>{

        String receiveMsg;

        //메인쓰레드 -> 신규쓰레드 작업
        protected String doInBackground(String...params){

            // param 에 문자열 저장
            String param = "userId=" + reg_idText + "&userPw=" + reg_passwordText + "&userName="
                    + reg_nameText + "&userAge=" + reg_ageText + "&groupCode=" + reg_groupCode + "&groupPw=" + reg_groupPw + "";
            Log.i("yunjae", param);
            try{
                //URL 연결 conn은 웹서버랑 연결하는 객체
                URL url = new URL("http://10.57.177.97/AndroidProject_SharedDiary/signup.jsp");
                Log.i("yunjae", "http://10.57.177.97/AndroidProject_SharedDiary/signup.jsp");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.connect();
                //URL 연결

                Log.i("yunjae", "URL연결");

                //osw 은 출력할 문자열을 담을 객체 바구니 //osw 객체에 conn 을 연결
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //osw 객체 바구니에 param 을 집어넣음
                Log.i("yunjae", "1");
                osw.write(param);
                //flush 쏟아내다 토해내다 . 버퍼에 담긴 내용을 토해냄
                Log.i("yunjae", param);
                osw.flush();
                Log.i("yunjae", "3");

                //만악 통신할 준비가 되었을 경우
                if (conn.getResponseCode() == conn.HTTP_OK){

                    //서버에서 안드로이드로 거꾸로 파라미터 값 전달
                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(in);
                    StringBuffer buffer = new StringBuffer();

                    //줄을 넘기기 전까지 계속 반복
                    while(reader.readLine() != null){
                        buffer.append(reader.readLine());
                    }
                    receiveMsg = buffer.toString();
                }else{
                    Log.i("MyDiary", conn.getResponseCode()+"통신할 준비가 되지 않은 에러");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return receiveMsg;
        }

        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if(receiveMsg.contains("0")){
                Log.i("yunjae", "로그인 실패");
                Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_LONG).show();
            }
            else if(receiveMsg != null){
                Log.i("yunjae", "로그인에 성공"+receiveMsg);
                //로그인 성공시 intent 사용해서 다른 액티비티로 넘어감

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //receiveMsg = receiveMsg.replaceAll("\\p{Z}","");
                //intent.putExtra("myName", receiveMsg);
                Toast.makeText(getApplicationContext(), "회원가입을 환영합니다",Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();

            }
            else {
                Log.i("yunjae", "로그인에 실패하였습니다else"+receiveMsg);
                Toast.makeText(getApplicationContext(),"로그인 실패",Toast.LENGTH_LONG).show();
            }
        }
    }
}
