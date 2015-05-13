package it.polito.mobile.androidassignment2.testapp.loginTest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class Profile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        TextView textViex = (TextView)findViewById(R.id.textView);

        Session session = Session.getInstance();



        if( session.getWhoIsLogged() == Student.class){

            try {

                textViex.setText(session.getStudentLogged().toString());

            }catch( Exception ee){
                textViex.setText(ee.getMessage());
            }

        }


    }



}
