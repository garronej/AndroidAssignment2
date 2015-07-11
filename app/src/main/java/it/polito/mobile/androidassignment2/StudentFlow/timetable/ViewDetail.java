package it.polito.mobile.androidassignment2.StudentFlow.timetable;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import it.polito.mobile.androidassignment2.R;


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



        //For exporting to google calendar
        int dayOfTheWeek = extras.getInt("dayOfTheWeek");
        int timeStartHour = extras.getInt("timeStartHour");
        int timeStartMin = extras.getInt("timeStartMin");
        int timeEndHour = extras.getInt("timeEndHour");
        int timeEndMin = extras.getInt("timeEndMin");

        String timeRange = extras.getString("timeRange");

        final String teacherName = extras.getString("teacherName");
        final String eventTitle = extras.getString("eventTitle");
        final String roomName = extras.getString("roomName");

        final Calendar beginTime = Calendar.getInstance();
        beginTime.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
        beginTime.set(Calendar.HOUR_OF_DAY, timeStartHour);
        beginTime.set(Calendar.MINUTE, timeStartMin);
        beginTime.clear(Calendar.SECOND);
        beginTime.clear(Calendar.MILLISECOND);

        final Calendar endingTime = Calendar.getInstance();
        endingTime.set(Calendar.DAY_OF_WEEK, dayOfTheWeek);
        endingTime.set(Calendar.HOUR_OF_DAY, timeEndHour);
        endingTime.set(Calendar.MINUTE, timeEndMin);
        endingTime.clear(Calendar.SECOND);
        endingTime.clear(Calendar.MILLISECOND);

        if(beginTime.before(Calendar.getInstance())){
            beginTime.add(Calendar.DAY_OF_MONTH, 7);
            endingTime.add(Calendar.DAY_OF_MONTH, 7);
        }


        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endingTime.getTimeInMillis());
                intent.putExtra(CalendarContract.Events.TITLE, eventTitle);
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, roomName);
                intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    Toast.makeText(ViewDetail.this, getResources().getString(R.string.noCalendar), Toast.LENGTH_LONG).show();
                }
            }
        });
        


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
