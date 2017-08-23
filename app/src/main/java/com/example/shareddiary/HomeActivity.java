package com.example.shareddiary;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    TextView home,chat,board,set,cal;
    static int REQ_CODE_PICK_PICTURE = 100;
    ArrayList<String> name = new ArrayList<String>();
    HomeFragment homeFragment=MainActivity.homeFragment;
    static boolean isFirst = false;
    boolean ban=false;
    //String stack = "stack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        name.add("이윤재");
        name.add("이예지");
        name.add("김하늘");
        name.add("윤성원");

        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawerlayout);
        ListView listView = (ListView)findViewById(R.id.homelistView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        listView.setAdapter(adapter);

        /*Intent intent = getIntent();
        String myName = intent.getStringExtra("myName");
        String groupName = intent.getStringExtra("groupName");
        String groupPw = intent.getStringExtra("groupPw");*/

        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        String fragmentTag_firstHome = "home1";
        getFragmentManager().popBackStack(fragmentTag_firstHome, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        //HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.container, homeFragment);
       /* Bundle bundle = new Bundle();
        bundle.putString("myName", myName);
        bundle.putString("groupName", groupName);
        bundle.putString("groupPw", groupPw);
        homeFragment.setArguments(bundle);*/
        fragmentTransaction.addToBackStack(fragmentTag_firstHome);
        fragmentTransaction.commit();

        /*Log.i("yunjae", myName);
        Log.i("yunjae", groupName);
        Log.i("yunjae", groupPw);*/


        home = (TextView)findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOfActivity(1);
            }
        });

        board = (TextView)findViewById(R.id.board);
        board.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startOfActivity(3);
            }
        });

        cal = (TextView)findViewById(R.id.cal);
        cal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startOfActivity(4);
            }
        });

        set = (TextView)findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startOfActivity(5);
            }
        });
    }

    public void startOfActivity(int menu){
        Intent intent;
        Bundle bundle;
        switch (menu){
            case 1 :
                Log.i("eeeee","home 클릭 시 isFrist - "+isFirst);
                if(isFirst){
                    String fragmentTag_home = "home";
                    getFragmentManager().popBackStack(fragmentTag_home, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container, homeFragment);
                    fragmentTransaction.addToBackStack(fragmentTag_home);
                    fragmentTransaction.commit();
                }
                break;
            case 2 :  break;
            case 3 :
                if(!ban){
                    BoardListFragment boardListFragment = new BoardListFragment();
                    String fragmentTag_board = "borad";
                    getFragmentManager().popBackStack(fragmentTag_board, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    final FragmentTransaction fragmentTransaction_b = getFragmentManager().beginTransaction();
                    fragmentTransaction_b.replace(R.id.container, boardListFragment);
                    bundle = new Bundle();
                    boardListFragment.setArguments(bundle);
                    fragmentTransaction_b.addToBackStack(fragmentTag_board);
                    fragmentTransaction_b.commit();
                }
                break;
            case 4 :
                CalendarFragment calendarFragment = new CalendarFragment();
                String fragmentTag_cal = "cal";
                getFragmentManager().popBackStack(fragmentTag_cal, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                final FragmentTransaction fragmentTransaction_a = getFragmentManager().beginTransaction();
                fragmentTransaction_a.replace(R.id.container, calendarFragment);
                bundle = new Bundle();
                calendarFragment.setArguments(bundle);
                fragmentTransaction_a.addToBackStack(fragmentTag_cal);
                fragmentTransaction_a.commit();
                break;
            case 5 :
                intent = new Intent(this, SetActivity.class);
                startActivity(intent);
                break;
            default: break;
        }
    }



}
