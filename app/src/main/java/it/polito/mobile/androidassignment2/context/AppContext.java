package it.polito.mobile.androidassignment2.context;

import android.app.Application;
import android.os.AsyncTask;

import java.io.IOException;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;
import it.polito.mobile.androidassignment2.businessLogic.Session;


/**
 * Created by Joseph on 21/05/2015.
 */


/*

Need to replace Session.login(email,password,postProcessor); by

    AppContext appState = ((AppContext)getApplication());
    AsyncTask<?,?,?> task = appState.login( email, password, postProcessor);


Need to replace Session session = Session.getInstance();

 by

   Session session = ((AppContext)getApplication()).getSession()

 */

public class AppContext extends Application {

    public AppContext(){
        super();
    }


    private Session state = null;

    public Session getSession() throws ExceptionInInitializerError{

        if( this.state == null ){
            throw new ExceptionInInitializerError("Session error : login First !");
        }else{
            return  this.state;
        }

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


}
