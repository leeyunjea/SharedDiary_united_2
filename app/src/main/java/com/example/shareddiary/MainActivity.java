package com.example.shareddiary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shareddiary.HomeActivity;
import com.example.shareddiary.LoginFailDialog;
import com.example.shareddiary.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText id;
    EditText pw;
    EditText groupcode;
    EditText grouppw;
    String id_str;
    String pw_str;
    String groupcode_str;
    String grouppw_str;
    LoginFailDialog loginFailDialog;
    Button loginButton;
    public static HomeFragment homeFragment;
    public static String name;
    public static String myId;

    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/login.jsp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = (EditText) findViewById(R.id.useridEntry);
        pw = (EditText) findViewById(R.id.passwordEntry);
        groupcode = (EditText) findViewById(R.id.groupCodeEntry);
        grouppw = (EditText) findViewById(R.id.groupPwEntry);

        loginButton = (Button) findViewById(R.id.loginBtn);

        TextView join = (TextView) findViewById(R.id.join);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerintent = new Intent(MainActivity.this , RegisterActivity.class);
                MainActivity.this.startActivity(registerintent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_str = id.getText().toString();
                pw_str = pw.getText().toString();
                groupcode_str = groupcode.getText().toString();
                grouppw_str = grouppw.getText().toString();
                loginDB IDB = new loginDB();
                IDB.execute(id_str, pw_str, groupcode_str, grouppw_str);
            }
        });

    }

    public Context getMainContext() {
        return this;
    }

    class loginDB extends AsyncTask<String, Integer, String> {
        String data=null;
        String receiveMsg="";
        String groupName;
        String groupPw;

        @Override
        protected String doInBackground(String... params) {

            String param = "userId=" + id_str + "&userPw=" + pw_str + "&groupCode=" + groupcode_str + "&groupPw=" + grouppw_str + "";
            Log.i("yunjae", param);


            try {
                //String data;
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                Log.i("yunjae", "URL에 접속");

                //안드로이드 -> 서버 파라미터값 전달
                OutputStreamWriter ows = new OutputStreamWriter(conn.getOutputStream(),"UTF-8");
                ows.write(param);
                ows.flush();
                //ows.close();

                //서버 -> 안드로이드 파라미터값 전달
                if(conn.getResponseCode() == conn.HTTP_OK){

                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(in);
                    StringBuffer buffer = new StringBuffer();

                    while((data = reader.readLine()) != null) {
                        buffer.append(data);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("yunjae", "서버에서 안드로이드로 전달 됨");

                    JSONObject json = new JSONObject(receiveMsg);
                    JSONArray jArr = json.getJSONArray("datasend");

                    Log.i("yunjae", "MainActivity = " + jArr.length());

                    for(int i=0; i<jArr.length(); i++){
                        json = jArr.getJSONObject(i);
                        name = json.getString("myName");
                        myId = json.getString("myId");
                        groupName = json.getString("groupName");
                        groupPw = json.getString("groupPw");
                    }

                }else {
                    Log.i("통신 결과", conn.getResponseCode()+"에러");
                }


                if(receiveMsg.contains("0")){
                    Log.i("yunjae", "로그인에 실패." + receiveMsg);
                }
                else{
                    Log.i("yunjae", "로그인에 성공" + receiveMsg);
                }

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }

        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if(receiveMsg.contains("0")){
                Log.i("yunjae", "로그인 실패");
                loginFailDialog = new LoginFailDialog(getMainContext(), "로그인에 실패하였습니다.", leftListener);
                loginFailDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                loginFailDialog.show();
            }
            else if(receiveMsg != null){
                Log.i("yunjae", "로그인에 성공"+name);
                Log.i("yunjae", "로그인에 성공"+groupName);
                Log.i("yunjae", "로그인에 성공"+groupPw);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                //receiveMsg = receiveMsg.replaceAll("^\\p{Z}+|\\p{Z}+$","");
                /*intent.putExtra("myName", getJsonData);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupPw", groupPw);*/
                homeFragment = new HomeFragment();
                //fragmentTransaction.replace(R.id.container, homeFragment);
                Bundle bundle = new Bundle();
                bundle.putString("myName", name);
                bundle.putString("groupName", groupName);
                bundle.putString("groupPw", groupPw);
                homeFragment.setArguments(bundle);
                startActivity(intent);
                finish();
            }
            else {
                Log.i("yunjae", "로그인에 실패하였습니다else"+receiveMsg);
            }
        }

    }

    private void receiveObject(String data) {
        try{
            Log.i("yunjae", data);

        }catch(Exception e) {e.printStackTrace();}

    }

    public View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.i("yunjae", "왼쪽클릭");
            loginFailDialog.dismiss();
        }
    };

}

