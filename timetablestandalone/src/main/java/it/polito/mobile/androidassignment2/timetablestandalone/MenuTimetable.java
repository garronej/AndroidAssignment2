package it.polito.mobile.androidassignment2.timetablestandalone;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.timetablestandalone.context.AppContext;


public class MenuTimetable extends AppCompatActivity {

    private AsyncTask<?,?,?> task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_timetable);

        final ProgressBar progressBar =(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                task = ((AppContext) getApplicationContext()).retrieveTimeTableData(new Manager.ResultProcessor<Integer>() {
                    @Override
                    public void process(Integer arg, Exception e) {


                        task = null;

                        progressBar.setVisibility(View.GONE);

                        if (e != null) {
                            Log.d(MenuTimetable.class.getName(), Log.getStackTraceString(e));
                            Toast.makeText(MenuTimetable.this, "Error retrieving timetable data", Toast.LENGTH_LONG).show();
                            return;
                        }


                        startActivity(new Intent(MenuTimetable.this, SearchCourse.class));


                    }

                    @Override
                    public void cancel() {

                        task = null;
                        progressBar.setVisibility(View.GONE);

                    }

                });

            }
        });

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                task = ((AppContext) getApplicationContext()).retrieveTimeTableData(new Manager.ResultProcessor<Integer>() {
                    @Override
                    public void process(Integer arg, Exception e) {

                        task = null;

                        progressBar.setVisibility(View.GONE);

                        if (e != null) {
                            Log.d(MenuTimetable.class.getName(), Log.getStackTraceString(e));
                            Toast.makeText(MenuTimetable.this, "Error retrieving timetable data", Toast.LENGTH_LONG).show();
                            return;
                        }


                        startActivity(new Intent(MenuTimetable.this, SearchConsultation.class));


                    }

                    @Override
                    public void cancel() {

                        task = null;
                        progressBar.setVisibility(View.GONE);

                    }

                });

            }
        });





    }





}
