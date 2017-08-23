package com.example.shareddiary;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.shareddiary.HomeActivity.REQ_CODE_PICK_PICTURE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View view;
    TextView MyName;
    ArrayList<FriendsItem> friendsItems;
    String groupName;
    ArrayList<String> nameList = new ArrayList<>();
    int basic_profile = R.drawable.basic_profile;
    //ArrayList<String> group_name = new ArrayList<String>();
    String myName;
    String groupPw;
    Adapter_Home adapter_home;
    String serverURL = "http://192.168.0.33/AndroidProject_SharedDiary/printGroupName.jsp";

    //boolean isFirst = HomeActivity.isFirst;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("eeeee","onDestroy/////");
        getActivity().moveTaskToBack(true);
        getActivity().finish();
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HomeActivity.isFirst = true;
        Log.i("eeeee","homefragment onDestroyView????"+HomeActivity.isFirst);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);

        Log.i("eeeee", "HomeFragment생성");

        Bundle bundle = getArguments();
        myName = bundle.getString("myName");
        groupName = bundle.getString("groupName");
        groupPw = bundle.getString("groupPw");

        MyName = (TextView) view.findViewById(R.id.myName);
        MyName.setText(myName);


        Log.i("yunjae", "myName = " + myName);


        ImageView myImage = (ImageView) view.findViewById(R.id.myimage);
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, REQ_CODE_PICK_PICTURE);
            }
        });

        printGroupName printGroupNameThread = new printGroupName();
        printGroupNameThread.execute(groupName, myName);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PICK_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                ImageView img = (ImageView) view.findViewById(R.id.myimage);
                img.setImageURI(data.getData()); // 사진 선택한 사진URI로 연결하기
            }
        }
    }

    class printGroupName extends AsyncTask<String, Integer, String> {
        String data=null;
        String receiveMsg="";

        @Override
        protected String doInBackground(String... params) {

            try {
                //String data;

                Log.i("yunjae", "HomeFragment스레드 생성");

                JSONObject sendObject = new JSONObject();
                //JSONArray sendArray = new JSONArray();

                JSONObject json_ob = new JSONObject();
                json_ob.put("groupCode", groupName);
                json_ob.put("myName", myName);
                json_ob.put("groupPw", groupPw);

                String sendMsg = json_ob.toString();
                Log.i("yunjae", "sendMsg = " + sendMsg);

                //sendArray.put(json_b);
                //sendObject.put("json", sendArray);

                URL url = null;
                try {
                    url = new URL(serverURL);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDefaultUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.connect();

                Log.i("yunjae", "URL에 접속");

                //안드로이드 -> 서버 파라미터값 전달




                OutputStreamWriter ows = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                ows.write("param="+sendMsg);
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
                    Log.i("yunjae", "서버에서 안드로이드로 전달 됨" + receiveMsg);

                    JSONObject json = new JSONObject(receiveMsg);
                    JSONArray jArr = json.getJSONArray("datasend");

                    int row = jArr.length();

                    Log.i("yunjae", "row = " + row);

                    nameList.clear();
                    for(int i=0; i<row; i++){
                        json = jArr.getJSONObject(i);
                        nameList.add(json.getString("name"+i));
                        Log.i("yunjae", "getJsonata="+nameList.get(i));
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

        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Log.i("aaa", "onPostExecute()");
            friendsItems = new ArrayList<FriendsItem>();
            for(int i=0; i<nameList.size(); i++) {
                friendsItems.add(new FriendsItem(basic_profile, nameList.get(i)));
                Log.i("aaa", "for문");
            }

            adapter_home = new Adapter_Home(view.getContext(), R.layout.friends_item, friendsItems);

            ListView list = (ListView) view.findViewById(R.id.listView);
            list.smoothScrollToPosition(0);
            list.setAdapter(adapter_home);

        }

    }

/*    private void ListRedraw(){
        contents_list.clear();//원래 있던 목록 삭제
        LoadScheduleDB loadScheduleDB = new LoadScheduleDB();
        loadScheduleDB.execute(); //DB에서 일정 목록 가져와서 contents_list에 추가

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("yeji","ListRedraw 호출 후 일정갯수 = "+contents_list.size());

                adapter_calContents = new Adapter_CalContents(getApplicationContext(),R.layout.contents_item,contents_list);//어뎁터생성
                ListView CalContents_ListView = (ListView)findViewById(R.id.CalContents_ListView);
                CalContents_ListView.setDivider(new ColorDrawable(Color.WHITE));
                CalContents_ListView.setDividerHeight(3);
                CalContents_ListView.setAdapter(adapter_calContents);
            }
        },500);
    }*/

}
