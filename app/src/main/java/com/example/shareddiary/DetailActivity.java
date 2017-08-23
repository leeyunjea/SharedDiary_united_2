package com.example.shareddiary;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    TextView title;
    TextView userID;
    TextView date;
    TextView contents;

    String Btitle;
    String BuserID;
    String Bdate;
    String Bcontents;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/DeleteDiary.jsp";

    DeleteDB DDB = new DeleteDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();

        Btitle = intent.getStringExtra("title");
        title = (TextView)findViewById(R.id.boardtitle);
        title.setText(Btitle);

        BuserID = intent.getStringExtra("userID");
        userID = (TextView)findViewById(R.id.boarduserID);
        userID.setText(BuserID);

        Bdate = intent.getStringExtra("date");
        date = (TextView)findViewById(R.id.boarddate);
        date.setText(Bdate);

        Bcontents = intent.getStringExtra("contents");
        contents = (TextView)findViewById(R.id.boardcontents);
        contents.setText(Bcontents);
    }

    @Override
    public void onBackPressed() {
        //Intent intent = new Intent(getWindow().getContext(), B.class);
        //startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final Intent intent;
        //final Intent listintent = new Intent(getWindow().getContext(), ListActivity.class);

        if(id==R.id.modification) {
            intent = new Intent(getWindow().getContext(), ModifyActivity.class);
            intent.putExtra("title", Btitle);
            intent.putExtra("contents", Bcontents);
            intent.putExtra("userID",BuserID);
            startActivity(intent);
            finish();
            return true;
        }

        else if(id==R.id.delete) {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DetailActivity.this);
            alert_confirm.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DDB.execute();
                            Toast.makeText(getWindow().getContext(),"삭제되었습니다.",Toast.LENGTH_LONG);
                            //startActivity(listintent);
                            finish();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 'No'
                    return;
                }
            });
            AlertDialog alert = alert_confirm.create();
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class DeleteDB extends AsyncTask<String, String, String> {
        String data;
        String receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            String param = "&bd_title=" + Btitle + "&bd_user=" + BuserID+"";
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