package com.example.shareddiary;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    int selecteYear, selecteMonth, selecteDay;
    String date;//현재날짜를 디폴트로 저장해놓을 변수
    String NowSelected,Today; //해당하는 달의 일정만 검색하기 위한 변수
    boolean isChange = false;
    View view;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/calContentsLoadDB.jsp";

    //TextView todayText = (TextView)findViewById(R.id.today); //여기에 쓰면 안됨
    static Adapter_CalContents adapter_calContents;
    static ArrayList<CalContents> contents_list;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("eeeee","cal destroy");
        /*getActivity().moveTaskToBack(true);
        getActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        // Inflate the layout for this fragment
        SimpleDateFormat todayMonth = new SimpleDateFormat("yyyy/MM", Locale.KOREA);
        NowSelected = todayMonth.format(new Date()); //디폴트 날짜 넣어놓기

        contents_list = new ArrayList<CalContents>();
        LoadScheduleDB loadScheduleDB = new LoadScheduleDB();
        loadScheduleDB.execute(); //DB에서 일정 목록 가져와서 contents_list에 추가

        TextView today_textView = (TextView)view.findViewById(R.id.today_textView);
        SimpleDateFormat today = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        date=today.format(new Date());
        Today = today.format(new Date());
        today_textView.setText(date);

        CalendarView calendarView = (CalendarView)view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if (month+1 < 10){ //1~9월이 경우 '07월'로 표기
                    date = year+"/"+"0"+(month+1)+"/"+dayOfMonth;
                    NowSelected = year+"/"+"0"+(month+1);
                }
                else{
                    date = year+"/"+(month+1)+"/"+dayOfMonth;
                    NowSelected = year+"/"+(month+1);
                }

                if(!Today.contains(NowSelected)){ //선택한 날짜가 이번달이 아닐때(달력이 넘어갈 때)
                    isChange = true;
                    Log.i("yeji","달력 넘어감. 목록일정 재출력");
                    ListRedraw(); //목록일정 다시 뽑기
                }
                if(Today.contains(NowSelected) && isChange){ //다른 달로 넘어갔다가 다시 돌아온경우 그 달의 일정 다시 출력
                    ListRedraw(); //목록일정 다시 뽑기
                    isChange = false;
                }

                TextView today_textView = (TextView)view.findViewById(R.id.today_textView);
                //today_textView.setText(date);
                selecteYear = year;
                selecteMonth = month;
                selecteDay = dayOfMonth;
            }
        });

        Button addBtn = (Button)view.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), AddScheduleActivity.class);
                intent.putExtra("date",date);
                startActivityForResult(intent,333);
            }
        });
        /*adapter_calContents = new Adapter_CalContents(view.getContext(),R.layout.contents_item,contents_list);//어뎁터생성
        ListView CalContents_ListView = (ListView)view.findViewById(R.id.CalContents_ListView);
        CalContents_ListView.setDivider(new ColorDrawable(Color.WHITE));
        CalContents_ListView.setDividerHeight(3);
        CalContents_ListView.setAdapter(adapter_calContents);*/


        return view;
    }

    public void ListRedraw(){
        contents_list.clear();//원래 있던 목록 삭제
        LoadScheduleDB loadScheduleDB = new LoadScheduleDB();
        loadScheduleDB.execute(); //DB에서 일정 목록 가져와서 contents_list에 추가

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("yeji","ListRedraw 호출 후 일정갯수 = "+contents_list.size());

                adapter_calContents = new Adapter_CalContents(view.getContext(),R.layout.contents_item,contents_list);//어뎁터생성
                ListView CalContents_ListView = (ListView)view.findViewById(R.id.CalContents_ListView);
                CalContents_ListView.setDivider(new ColorDrawable(Color.WHITE));
                CalContents_ListView.setDividerHeight(3);
                CalContents_ListView.setAdapter(adapter_calContents);
            }
        },500);
    }

    class LoadScheduleDB extends AsyncTask<String,String,String> {
        String data;
        String receiveMsg;
        CalContents calContents;

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter_calContents = new Adapter_CalContents(view.getContext(),R.layout.contents_item,contents_list);//어뎁터생성
            ListView CalContents_ListView = (ListView)view.findViewById(R.id.CalContents_ListView);
            CalContents_ListView.setDivider(new ColorDrawable(Color.WHITE));
            CalContents_ListView.setDividerHeight(3);
            CalContents_ListView.setAdapter(adapter_calContents);
            Log.i("aaa", "달력 onPost");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //String data;
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                Log.i("yeji", "URL에 접속(Load)");

                //서버 -> 안드로이드 파라미터값 전달
                if(conn.getResponseCode() == conn.HTTP_OK){

                    InputStreamReader in = new InputStreamReader(conn.getInputStream(), "EUC-KR");
                    BufferedReader reader = new BufferedReader(in);
                    StringBuffer buffer = new StringBuffer();

                    while((data = reader.readLine()) != null) {
                        buffer.append(data);
                    }
                    receiveMsg = buffer.toString();
                    Log.i("yeji", "서버에서 안드로이드로 전달 됨" + receiveMsg);

                    JSONObject json = new JSONObject(receiveMsg);
                    JSONArray jArr = json.getJSONArray("datasend");
                    Log.i("yeji", "체크");

                    int dataNum = jArr.length();

                    Log.i("yeji", "dataNum = " + dataNum);


                    for(int i=0; i<dataNum; i++){
                        json = jArr.getJSONObject(i);
                        if(json.getString("dates"+i).contains(NowSelected)) {
                            calContents = new CalContents();
                            calContents.date = json.getString("dates"+i);
                            calContents.content = json.getString("content"+i);
                            calContents.time = json.getString("times"+i);
                            calContents.location = json.getString("location"+i);
                            calContents.myName = json.getString("name"+i);
                            contents_list.add(calContents);
                            Log.i("yeji", "calContents 추가 성공");
                        }
                        else{
                            Log.i("yeji", "DB에만 추가 출력안함");
                        }
                    }

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
