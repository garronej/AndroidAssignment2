package it.polito.mobile.androidassignment2;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;


import java.net.MalformedURLException;
import java.net.URL;

import it.polito.mobile.androidassignment2.businessLogic.*;


public class ActivityPolijob extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serivice_polijob);

    }

    //the post treatment.
    Manager.ResultProcessor<Student> resultProcessor = new Manager.ResultProcessor<Student>() {
        @Override
        public void process(Student s, Exception exception) {

            String message = "";


            if( exception != null){

                //There where  problem during the request
                if (exception.getClass() == RestApiException.class) {

                    //It was an error on the web service side.
                    //Nb : err code -1 mean a internal bug, report if you exprerience.
                   Integer errCode =  ((RestApiException)exception).getResponseCode();
                    message = errCode.toString() + " / " + exception.getMessage();


                }else{
                    //It was an error with the internet conextion.
                    message = "Network problem : " + exception.getMessage();
                }

            }else{


                //No error, we can process the result.
                message = s.toString();

            }



            ((TextView)findViewById(R.id.response)).setText(message);
        }
    };


    public void send(View view) {

        //The id we are searching for.

        Integer id = null;

        String idString = ((TextView)findViewById(R.id.student_id)).getText().toString();

        if( !idString.equals("")){

            id = Integer.parseInt(idString);

            ((TextView)findViewById(R.id.response)).setText("Loading...");
            Manager.getStudentById(id , resultProcessor );

        }else{
            Toast.makeText(ActivityPolijob.this,"Fill id first", Toast.LENGTH_SHORT).show();
        }


    }

    public void insertNewStudent(View view){



        //The id we are searching for.

        Student s = null;
        try {

            s = new Student();

            s.setEmail("JoSePh.garrone.GJ@gmail.com");
            s.setName("gaRRone");
            s.setSurname("Joseph");
            s.setUniversityCareer("Computer EngineEring");
            s.setCompetences(new String[]{"java", "android", ".net", "C#", "C-"});
            s.setPassword("MaisOuiCestClaire");
            s.setCvUrl(new URL("http://cv.test.it"));

        }catch(Exception e){

            //Welformed here.
        }

        ((TextView)findViewById(R.id.response)).setText("Loading ...");
        Manager.insertNewStudent(s , resultProcessor );


    }

}