package it.polito.mobile.androidassignment2.timetablestandalone;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.TextView;


public class ViewDetail extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.92);

        setContentView(R.layout.activity_view_detail);

        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview


        //Il faut envoyer info.getDay() info.getTimeStart() info.getEndTime() teacher Name ifo.getRoom()

        Bundle extras = getIntent().getExtras();

        if (extras == null) throw new RuntimeException();

        int dayOfTheWeek = extras.getInt("dayOfTheWeek");

        int timeStartHour = extras.getInt("timeStartHour");
        int timeStartMin = extras.getInt("timeStartMin");

        int timeEndHour = extras.getInt("timeEndHour");
        int timeEndMin = extras.getInt("timeEndMin");

        String timeRange = extras.getString("timeRange");

        String teacherName = extras.getString("teacherName");
        String roomName = extras.getString("roomName");

        Resources res = getResources();
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(res.getString(R.string.teacherName) + " : " + teacherName);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(res.getString(R.string.room) + " : " + roomName);
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(timeRange);


        MapRoom mapRoom = (MapRoom) getFragmentManager().findFragmentById(R.id.mapRoom);

        mapRoom.setCenterMarkerTitle(roomName);
        mapRoom.generateMap();

    }


}
