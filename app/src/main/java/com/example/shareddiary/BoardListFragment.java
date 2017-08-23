package com.example.shareddiary;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class BoardListFragment extends Fragment {

    static MyAdapter adapter;
    Button addboard;
    ArrayList<BoardItem> getJsonData;
    String title;
    static String userID;
    String date;
    String contents;
    SimpleDateFormat today = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
    View view;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/board_loadDB.jsp";

    final static String TAG = "SQLITEDBTEST";

    public BoardListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_board_list, container, false);

        getJsonData = new ArrayList<BoardItem>();
        ListDB BDB = new ListDB();
        BDB.execute();

        //어댑터 생성
        adapter = new MyAdapter(getContext(), R.layout.board_item, getJsonData);
        //어댑터 연결
        ListView listView = (ListView)view.findViewById(R.id.listView);
        adapter.addItem(new BoardItem("글제목", "작성자", "등록날짜", "♡♡ 수", R.drawable.profile, "내용"));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                userID = getJsonData.get(position).getUserID().toString();
                title  = getJsonData.get(position).getTitle().toString();
                date  = getJsonData.get(position).getDate().toString();
                contents = getJsonData.get(position).getContents().toString();

                Intent intent = new Intent(view.getContext(), DetailActivity.class);

                intent.putExtra("userID", userID);
                intent.putExtra("title", title);
                intent.putExtra("date", date);
                intent.putExtra("contents", contents);
                startActivity(intent);
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        addboard = (Button) view.findViewById(R.id.boardBtn);

        addboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("haneul", "버튼클릭");
                Intent intent = new Intent(view.getContext(), BoardActivity.class);
                intent.putExtra("haneul", "인텐트");
                startActivity(intent);
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return view;
    }

    public void ListRedraw(){
        getJsonData.clear();//원래 있던 목록 삭제
        ListDB BDB = new ListDB();
        BDB.execute();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //어댑터 생성
                adapter = new MyAdapter(getContext(), R.layout.board_item, getJsonData);
                //어댑터 연결
                ListView listView = (ListView)view.findViewById(R.id.listView);
                adapter.addItem(new BoardItem("글제목", "Haneul", today.format(new Date()), "♡♡ 수", R.drawable.profile, "내용"));
                listView.setAdapter(adapter);
            }
        },500);
    }


    class ListDB extends AsyncTask<String, String, String> {
        String data;
        String receiveMsg;

        BoardItem items;
        ArrayList<BoardItem> ArrItems;

        @Override
        protected String doInBackground(String... params) {
            try {
                //String data;
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept-Charset", "UTF-8");

                conn.setDoInput(true);
                conn.connect();

                if (conn.getResponseCode() == conn.HTTP_OK) {

                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(in);
                    StringBuffer buffer = new StringBuffer();

                    while ((data = reader.readLine()) != null) {
                        buffer.append(data);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("yunjae", "서버에서 안드로이드로 전달 됨~~~");
                    Log.i("haneulhaneul", receiveMsg);

                    JSONObject json = new JSONObject(receiveMsg);
                    JSONArray jArr = json.getJSONArray("datasend");
                    Log.i("yunjae","~"+jArr.length());

                    for (int i = 0; i < jArr.length(); i++) {
                        json = jArr.getJSONObject(i);
                        items =  new BoardItem();
                        items.setTitle(json.getString("bd_title"+i));
                        items.setUserID(json.getString("bd_user"+i));
                        items.setContents(json.getString("bd_contents"+i));
                        items.setDate(json.getString("bd_dates"+i));
                        getJsonData.add(items);
                        //if(items.getUserID() =="user1")

                    }
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
