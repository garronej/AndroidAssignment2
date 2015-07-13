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


public class SearchRoom extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.92);

        setContentView(R.layout.activity_search_room);

        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview

        MapRoom mapRoom = (MapRoom) getFragmentManager().findFragmentById(R.id.mapRoom);
        mapRoom.generateMap();

    }


}