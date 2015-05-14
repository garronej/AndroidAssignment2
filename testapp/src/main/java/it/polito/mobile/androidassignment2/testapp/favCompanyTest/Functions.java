package it.polito.mobile.androidassignment2.testapp.favCompanyTest;

/**
 * Created by Joseph on 07/05/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class Functions extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.functions_fragment,container,false);



        final Button button1 = (Button) rootView.findViewById(R.id.button1);
        final Button button2 = (Button) rootView.findViewById(R.id.button2);
        final Button button3 = (Button) rootView.findViewById(R.id.button3);

        final TextView textView1 = (TextView) rootView.findViewById(R.id.textView1);
        final TextView textView2 = (TextView) rootView.findViewById(R.id.textView2);
        final TextView textView3 = (TextView) rootView.findViewById(R.id.textView3);

        textView3.setVisibility(View.INVISIBLE);



        button1.setText("Delete favourite Student of company Apple");

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView1.setText("");

                Company criteria = new Company();

                try{
                    criteria.setName("Apple");
                }catch(Exception e){

                }



                Manager.getCompaniesMatchingCriteria(criteria, new Manager.ResultProcessor<List<Company>>() {


                    @Override
                    public void process(List<Company> arg, Exception e) {
                        if (e != null) {
                            textView1.setText(textView1.getText() + "Error in getCompaniesMatchingCriteria name=Apple" + processException(e) + "\n");
                            return;
                        }


                            if (arg.size() != 1) {
                                textView1.setText(textView1.getText() + "Error : number of student matching the search  name: Dupont, surname: Paul :" + arg.size() + "\n");
                                return;
                            }

                            final Company company = arg.get(0);



                            Manager.getFavouriteStudentOfCompany(company.getId(), new Manager.ResultProcessor<List<Student>>() {


                                @Override
                                public void process(List<Student> arg, Exception e) {

                                    if (e != null) {
                                        textView1.setText(textView1.getText() + "Error in getFavouriteStudentOfCompany" + processException(e) + "\n");
                                        return;
                                    }
                                    for (final Student student : arg) {


                                        Manager.deleteAFavouriteStudentOfACompany(company.getId(), student.getId(), new Manager.ResultProcessor<Integer>() {


                                            @Override
                                            public void process(Integer arg, Exception e) {
                                                if (e != null) {
                                                    textView1.setText(textView1.getText() + "Error in deleteAFavouriteStudentOfACompany " + processException(e) + "\n");
                                                    return;
                                                }

                                                textView1.setText(textView1.getText() + "Student id " + student.getId() +
                                                        " has been removed from the fav of company "
                                                        + company.getName() + "\n");

                                            }

                                            @Override
                                            public void cancel() {

                                            }
                                        });

                                    }

                                }

                                @Override
                                public void cancel() {

                                }

                            });





                    }

                    @Override
                    public void cancel() {

                    }
                });

            }
        });

        button2.setText("Add some fav student to company Apple");

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView2.setText("");

                Company criteria = new Company();

                try{
                    criteria.setName("Apple");


                }catch(Exception e){

                }

                Manager.getCompaniesMatchingCriteria(criteria, new Manager.ResultProcessor<List<Company>>() {

                    @Override
                    public void process(List<Company> arg, Exception e) {
                        if (e != null) {
                            textView2.setText(textView2.getText() + "Error in getCompaniesMatchingCriteria searching Apple " + e.getMessage() + "\n");
                            return;
                        }


                        if (arg.size() != 1) {
                            textView2.setText(textView2.getText() + "Error : number of company matching the search Apple :" + arg.size() + "\n");
                            return;
                        }


                        final Company company = arg.get(0);

                        Student criteria = new Student();

                        try {
                            criteria.setName("Dupont");
                            criteria.setSurname("Paul");
                        } catch (Exception ee) {}

                        Manager.getStudentsMatchingCriteria(criteria, new Manager.ResultProcessor<List<Student>>() {


                            @Override
                            public void process(List<Student> arg, Exception e) {

                                if (e != null) {
                                    textView2.setText(textView2.getText() + " Error in getStudentsMatchingCriteria searching for Paul Dupont" + e.getMessage() + "\n");
                                }

                                    if (arg.size() != 1) {
                                        textView2.setText(textView2.getText() + "Error : number of Student matching the search Paul Dupont is " + arg.size() + "\n");
                                        return;
                                    }


                                    Manager.addFavouriteStudentForCompany(company.getId(), arg.get(0).getId(), new Manager.ResultProcessor<Student>() {


                                        @Override
                                        public void process(Student arg, Exception e) {
                                            if (e != null) {
                                                textView2.setText(textView2.getText()
                                                        + "Error addFavouriteStudentForCompany, probably student already added : \n"
                                                        + e.getMessage() + "\n\n");
                                                return;
                                            }


                                                textView2.setText(textView2.getText() + "Student id : "
                                                        + arg.getId() + "  have been set to favorite for company : " + company.getName() + "\n");

                                        }

                                        @Override
                                        public void cancel() {

                                        }
                                    });

                            }

                            @Override
                            public void cancel() {

                            }
                        });



                    }

                    @Override
                    public void cancel() {

                    }
                });


            }
        });

        button3.setVisibility(View.INVISIBLE);

        return rootView;


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