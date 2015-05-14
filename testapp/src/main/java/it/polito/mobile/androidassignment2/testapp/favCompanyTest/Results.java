package it.polito.mobile.androidassignment2.testapp.favCompanyTest;

/**
 * Created by Joseph on 10/05/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.testapp.R;

public class Results extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootLayout = inflater.inflate(R.layout.results_fragment, container, false);

        final TextView textView = (TextView) rootLayout.findViewById(R.id.textView);


        Button button = (Button) rootLayout.findViewById(R.id.button);




        button.setText("Retrieve favourite students of all company");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                textView.setText("");


                Manager.getCompaniesMatchingCriteria(null, new Manager.ResultProcessor<List<Company>>() {
                    @Override
                    public void process(List<Company> arg, Exception e) {
                        if (e != null) {
                            textView.setText(textView.getText() + processException(e) + "\n");
                        } else {
                            for (final Company company : arg) {

                                final StringBuffer buffer = new StringBuffer();

                                buffer.append("For company id= " + company.getId() + "(" + company.getEmail() + ")\n\n");

                                Manager.getFavouriteStudentOfCompany(company.getId(), new Manager.ResultProcessor<List<Student>>() {

                                    @Override
                                    public void process(List<Student> arg, Exception e) {
                                        if (e != null) {
                                            buffer.append(processException(e) + "\n");
                                            return;
                                        }

                                        buffer.append("    " + "Favourite Student :\n");

                                        int i = 1;
                                        for (Student student : arg) {
                                            buffer.append("    " + i + ") id= " + student.getId() + "(" + student.getName() + ")\n");
                                            i++;
                                        }
                                        buffer.append("\n");
                                        textView.setText(textView.getText() + buffer.toString());

                                    }

                                    @Override
                                    public void cancel() {
                                    }

                                });
                            }
                        }
                    }
                    @Override
                    public void cancel() {}
                });
            }
        });
        return rootLayout;
    }


    private static String processException(Exception exception){

        String message;
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

        return message;
    }

}


