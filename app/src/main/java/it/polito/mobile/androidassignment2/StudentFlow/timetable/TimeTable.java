package it.polito.mobile.androidassignment2.StudentFlow.timetable;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.NavigationDrawerFragment;
import it.polito.mobile.androidassignment2.businessLogic.timetable.Course;
import it.polito.mobile.androidassignment2.businessLogic.timetable.Event;
import it.polito.mobile.androidassignment2.businessLogic.timetable.PracticalInformation;
import it.polito.mobile.androidassignment2.context.AppContext;

/**
 * Created by Joseph on 01/04/2015.
 */
public class TimeTable extends AppCompatActivity {

    private List<Event> data = new ArrayList<>();
    private Boolean isCourse = null;

    private int[] backgroundSet = { R.drawable.background1,
                                    R.drawable.background2,
                                    R.drawable.background3,
                                    R.drawable.background4,
                                    R.drawable.background5,
                                    R.drawable.background6 };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_table);
        setUpNavigationDrawer();



        Bundle extras = getIntent().getExtras();
        String selectedCourse = null;
        String selectedTeacher = null;


        if (extras == null) {
            throw new RuntimeException("Timetable lunched without extras");
        }

        if (extras.getString("type").equals("Course")) {

            Log.d(TimeTable.class.getName(), "On est dans cour");

            isCourse = true;
            selectedCourse = extras.getString("selectedCourse");
            selectedTeacher = extras.getString("selectedTeacher");


            this.data.addAll(((AppContext) getApplicationContext()).getTimeTableData().getCourses());

        } else if (extras.getString("type").equals("Consultation")) {

            isCourse = false;
            selectedTeacher = extras.getString("selectedTeacher");
            this.data.addAll(((AppContext) getApplicationContext()).getTimeTableData().getConsultations());


        } else {

            throw new RuntimeException();

        }


        for (int i = 0; i < this.data.size(); i++) {

            Event event = this.data.get(i);

            //We are displaying courses timetable

            if (this.isCourse){
                //Case 1 : No parameter entered, display everything,
                //Case 2 : Current event is user selected course
                //Case 3 : Current event is given by the selected teacher
                if (selectedCourse == null && selectedTeacher == null ||
                        selectedCourse != null && selectedCourse.equals(((Course) event).getTitle()) ||
                        selectedTeacher != null && selectedTeacher.equals(event.getTeacher()))
                    this.drawEvent(event, this.backgroundSet[i % 6]);
            //We are displaying consultation time table.
            }else {
                //In consultation time you can display consultation of one and only one teacher.
                if (selectedTeacher.equals(event.getTeacher()))
                    this.drawEvent(event, this.backgroundSet[i % 6]);
            }
        }
    }



    //Print all the weekly lectures of a specific course
    private void drawEvent(Event event, int background){
        String teacherName = event.getTeacher();

        String courseTitle = null;

        if( this.isCourse)
            courseTitle = ((Course)event).getTitle();

        for( PracticalInformation practicalInformation : event.getPracticalInformation())
            this.drawPracticalInformation(practicalInformation, background, teacherName, courseTitle);

    }


    private void drawPracticalInformation(
            final PracticalInformation info,
            int background,
            final String teacherName,
            final String courseTitle){

        final Resources res = getResources();
        LayoutInflater vi = (LayoutInflater) TimeTable.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View customFrame = vi.inflate(R.layout.custom_frame, null);

        //Set background
        LinearLayout layout = (LinearLayout) customFrame.findViewById(R.id.layout);
        layout.setBackground(ResourcesCompat.getDrawable(res, background, null));

        //Set callback on click
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TimeTable.this,ViewDetail.class);

                intent.putExtra("dayOfTheWeek",info.getDay());

                intent.putExtra("timeStartHour", info.getStartTime().getHour());
                intent.putExtra("timeStartMin", info.getStartTime().getMin());

                intent.putExtra("timeRange", info.getRange());

                intent.putExtra("timeEndHour",info.getEndTime().getHour());
                intent.putExtra("timeEndMin",info.getEndTime().getMin());

                intent.putExtra("teacherName",teacherName);

                if( courseTitle != null )
                    intent.putExtra("eventTitle", courseTitle);
                else
                    intent.putExtra("eventTitle", teacherName);

                intent.putExtra("roomName",info.getRoom());

                startActivity(intent);


            }
        });

        //Set text at the top of the frame
        TextView durationText = (TextView) customFrame.findViewById(R.id.duration);
        durationText.setText(info.getRange());

        //Set text inside the frame.
        TextView content = (TextView) customFrame.findViewById(R.id.content);

        if( courseTitle == null ) content.setText(teacherName);
        else content.setText(courseTitle);



        //Setting size and offset.
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)convertDpToPixel(info.getDuration()));
        layoutParams.setMargins(0,(int)convertDpToPixel(info.getOffset()),0,0);
        customFrame.setLayoutParams(layoutParams);

        //Adding the frame in the right day

        int id;
        switch( info.getDay() ) {
            case Calendar.MONDAY:
                id = R.id.mondayRelativeLayout;
                break;

            case Calendar.TUESDAY:
                id = R.id.tuesdayRelativeLayout;
                break;

            case Calendar.WEDNESDAY:
                id = R.id.wednesdayRelativeLayout;
                break;

            case Calendar.THURSDAY:
                id = R.id.thursdayRelativeLayout;
                break;

            case Calendar.FRIDAY:
                id = R.id.fridayRelativeLayout;
                break;

            default:
                throw new RuntimeException();


        }


        ViewGroup dayLayout = (ViewGroup) findViewById(id);
        dayLayout.addView(customFrame);
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels

     * @return A float value to represent px equivalent to dp depending on device density
     */
    private float convertDpToPixel(float dp){
        Resources resources = TimeTable.this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }


    private void setUpNavigationDrawer(){
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position", 0));

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        //due it's a new instance of NavDraw

        mNavigationDrawerFragment.setTitle(getTitle());

    }

}

