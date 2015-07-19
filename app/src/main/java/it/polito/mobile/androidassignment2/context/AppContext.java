package it.polito.mobile.androidassignment2.context;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.timetable.TimeTableData;


/**
 * Created by Joseph on 21/05/2015.
 */


/*

Need to replace Session.login(email,password,postProcessor); by

    AppContext appState = ((AppContext)getApplicationContext());
    AsyncTask<?,?,?> task = appState.login( email, password, postProcessor);


Need to replace Session session = Session.getInstance();

 by

   Session session = ((AppContext)getApplicationContext()).getSession()



   For initializing timetable data :
    AppContext appState = ((AppContext)getApplicationContext());
    AsyncTask<?,?,?> task = appState.retrieveTimeTableData(postProcessor);

For getting timetable data
   TimeTableData timeTableData = ((AppContext)getApplicationContext()).getTimeTableData();

 */

public class AppContext extends Application {

    public static final String SESSION_FILENAME = "sessionFile";

    public AppContext(){
        super();
    }


    private Session state = null;

    public Session getSession(){


        if( this.state == null ){
            try {
                FileInputStream fileInputStream = openFileInput(SESSION_FILENAME);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                this.state = (Session) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            }catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
                Log.e("LoginPoliJobs", " session file unreadable");
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //throw new ExceptionInInitializerError("Session error : login First !");
            }

        }

        return  this.state;


    }
    public void freeSession() {
        deleteFile(SESSION_FILENAME);
    }


    public void redirectToLogin(Class<?> c){
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("redirect", c.getName());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    //Synchronous version of login
    private synchronized Integer login( String email, String password) throws IOException,RestApiException, DataFormatException {
        this.state = new Session(email,password);
        return 0;
    }


    public AsyncTask<?, ?, ?> login( final String email, final String password, final Manager.ResultProcessor<Integer> postProcessor ){

        AsyncTask<Void, Void, Integer> pool = new AsyncTask<Void, Void, Integer>() {

            private Exception exception = null;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                   AppContext.this.login(email, password);
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

        pool.execute();

        return pool;

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