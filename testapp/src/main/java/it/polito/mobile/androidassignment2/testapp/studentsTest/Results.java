package it.polito.mobile.androidassignment2.testapp.studentsTest;

/**
 * Created by Joseph on 07/05/2015.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class Results extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootLayout = inflater.inflate(R.layout.results_fragment, container, false);


        Button button = (Button) rootLayout.findViewById(R.id.button);

        button.setText("retrieve students");

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((TextView) rootLayout.findViewById(R.id.textView)).setText("Loading...");

                Manager.getStudentsMatchingCriteria(null, new Manager.ResultProcessor<List<Student>>() {

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Student> students, Exception exception) {

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
                            for( Student student : students){

                                message += student.toString() + "\n\n";
                            }

                        }

                        ((TextView) rootLayout.findViewById(R.id.textView)).setText("Done : \n" + message);
                    }
                } );

            }
        });



        return rootLayout;

    }


}