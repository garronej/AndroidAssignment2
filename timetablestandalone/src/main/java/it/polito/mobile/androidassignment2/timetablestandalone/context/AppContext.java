package it.polito.mobile.androidassignment2.timetablestandalone.context;

import android.app.Application;
import android.os.AsyncTask;

import java.io.IOException;

import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;

import it.polito.mobile.androidassignment2.businessLogic.timetable.TimeTableData;


/**
 * Created by Joseph on 21/05/2015.
 */


/*

    AppContext appState = ((AppContext)getApplicationContext());
    AsyncTask<?,?,?> task = appState.retrieveTimeTableData(postProcessor);


   TimeTableData timeTableData = ((AppContext)getApplicationContext()).getTimeTableData();

 */

public class AppContext extends Application {

    public AppContext(){
        super();
    }


    private TimeTableData timeTableData = null;


    public TimeTableData getTimeTableData(){

        if( this.timeTableData == null ){
            throw new RuntimeException("AppContext not initialised : call retrieveTimeTableData first");
        }else{
            return this.timeTableData;
        }

    }


    //Synchronous version of login
    private synchronized Integer retrieveTimeTableData() throws IOException, RestApiException {
        this.timeTableData = new TimeTableData(this);
        return 0;
    }


    public AsyncTask<?, ?, ?> retrieveTimeTableData(final Manager.ResultProcessor<Integer> postProcessor ){

        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {

            private Exception exception = null;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                   AppContext.this.retrieveTimeTableData();
                } catch ( Exception e){

                    this.exception = e;
                    return -1;
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer out) {
                postProcessor.process(out,this.exception);
                return;

            }

        };

        task.execute();

        return task;

    }


}