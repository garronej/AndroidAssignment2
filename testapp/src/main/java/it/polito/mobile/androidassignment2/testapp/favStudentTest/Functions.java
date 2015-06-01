package it.polito.mobile.androidassignment2.testapp.favStudentTest;

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



        button1.setText("Delete favourite Company/offer and applyed offer of Paul Dupont");

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView1.setText("");

                Student criteria = new Student();

                try{
                    criteria.setName("Dupont");
                    criteria.setSurname("Paul");
                }catch(Exception e){

                }

                //textView1.setText(textView1.getText() + "Searching for for student matching criteria name=Dupon, surname=Paul\n");

                Manager.getStudentsMatchingCriteria(criteria, null,new Manager.ResultProcessor<List<Student>>() {


                    @Override
                    public void process(List<Student> arg, Exception e) {
                        if (e != null) {
                            textView1.setText(textView1.getText() + processException(e) + "\n");
                        } else {

                            if (arg.size() != 1) {
                                textView1.setText(textView1.getText() + "Error : number of student matching the search  name: Dupont, surname: Paul :" + arg.size() + "\n");
                                return;
                            }

                            final Student student = arg.get(0);


                            //textView1.setText(textView1.getText() + "Searching for favourite offer of student id=" + student.getId() + "(" + student.getEmail() + ")\n");
                            Manager.getFavouriteOfferOfStudent(student.getId(), new Manager.ResultProcessor<List<Offer>>() {


                                @Override
                                public void process(List<Offer> arg, Exception e) {

                                    if (e != null) {
                                        textView1.setText(textView1.getText() + processException(e) + "\n");
                                    } else {
                                        for (final Offer offer : arg) {

                                            //textView1.setText(textView1.getText() + "Deleting from favourite offer id=" + offer.getId() + " of the student id=" + student.getId() + "\n" );

                                            Manager.deleteAFavouriteOfferOfAStudent(student.getId(), offer.getId(), new Manager.ResultProcessor<Integer>() {


                                                @Override
                                                public void process(Integer arg, Exception e) {
                                                    if (e != null) {
                                                        textView1.setText(textView1.getText() + processException(e) + "\n");
                                                    } else {
                                                        textView1.setText(textView1.getText() + "Offer id " + offer.getId() +
                                                                " has been removed from the fav of student "
                                                                + student.getSurname() + " " + student.getName() + "\n");
                                                    }
                                                }

                                                @Override
                                                public void cancel() {

                                                }
                                            });

                                        }
                                    }

                                }

                                @Override
                                public void cancel() {

                                }

                            });







                            Manager.getAppliedOfferOfStudent(student.getId(), null, new Manager.ResultProcessor<List<Offer>>() {


                                @Override
                                public void process(List<Offer> arg, Exception e) {

                                    if (e != null) {

                                        Log.w("favStudentTest/Function","Error in getAppliedOfferOfStudent",e);

                                        textView1.setText(textView1.getText() + "error in getAppliedOfferOfStudent()  :" + processException(e) + "\n");
                                    } else {
                                        for (final Offer offer : arg) {

                                            //textView1.setText(textView1.getText() + "Deleting from favourite offer id=" + offer.getId() + " of the student id=" + student.getId() + "\n" );

                                            Manager.unsubscribeStudentOfJobOffer(offer.getId(),  student.getId(), new Manager.ResultProcessor<Integer>() {


                                                @Override
                                                public void process(Integer arg, Exception e) {
                                                    if (e != null) {
                                                        textView1.setText(textView1.getText() + "error in unsubscribeStudentOfJobOffer()  :" + processException(e) + "\n");
                                                    } else {
                                                        textView1.setText(textView1.getText() + "Offer id " + offer.getId() +
                                                                " has been removed from the applied list of student "
                                                                + student.getSurname() + " " + student.getName() + "\n");
                                                    }
                                                }

                                                @Override
                                                public void cancel() {

                                                }
                                            });

                                        }
                                    }

                                }

                                @Override
                                public void cancel() {

                                }

                            });












                            Manager.getFavouriteCompanyOfStudent(student.getId(), new Manager.ResultProcessor<List<Company>>() {


                                @Override
                                public void process(List<Company> arg, Exception e) {

                                    if (e != null) {
                                        textView1.setText(textView1.getText() + e.getMessage() + "\n");
                                    } else {

                                        for (final Company company : arg) {

                                            Manager.deleteAFavouriteCompanyOfAStudent(student.getId(), company.getId(), new Manager.ResultProcessor<Integer>() {


                                                @Override
                                                public void process(Integer arg, Exception e) {
                                                    if (e != null) {
                                                        textView1.setText(textView1.getText() + e.getMessage() + "\n");

                                                    } else {
                                                        textView1.setText(textView1.getText() + "Company" + company.getName() +
                                                                " has been removed from the fav of student "
                                                                + student.getSurname() + " " + student.getName() + "\n");
                                                    }
                                                }

                                                @Override
                                                public void cancel() {

                                                }
                                            });
                                        }

                                    }

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
        });

        button2.setText("Add some fav company and offer to paul and subscribe to an offer ");

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView2.setText("");

                Student criteria = new Student();

                try{
                    criteria.setName("Dupont");
                    criteria.setSurname("Paul");

                }catch(Exception e){

                }

                Manager.getStudentsMatchingCriteria(criteria, null,new Manager.ResultProcessor<List<Student>>(){

                    @Override
                    public void process(List<Student> arg, Exception e) {
                        if( e != null){
                            textView2.setText(textView2.getText() + e.getMessage() + "\n");

                        }else{

                            if( arg.size() != 1) {
                                textView2.setText(textView2.getText() + "Error : number of student matching the search  name: Dupont, surname: Paul :" + arg.size() + "\n");
                                return;
                            }


                            final Student student = arg.get(0);

                            Offer criteria = new Offer();

                            try{
                                criteria.setCompanyName("Apple");
                                criteria.setKindOfContract("contract 2");

                            }catch( Exception ee){



                            }

                            Manager.getOffersMatchingCriteria(criteria, new Manager.ResultProcessor<List<Offer>>(){


                                @Override
                                public void process(List<Offer> arg, Exception e) {

                                    if( e != null ){
                                        textView2.setText(textView2.getText() +  e.getMessage() + "\n");
                                    }else{

                                        if( arg.size() != 1) {
                                            textView2.setText(textView2.getText() + "Error : number of offer matching the search name: \"Apple\", " +
                                                    "kind of contract : \"contract 2\"  is " + arg.size() + "\n");
                                            return;
                                        }


                                        Manager.addFavouriteOfferForStudent(student.getId(),arg.get(0).getId(), new Manager.ResultProcessor<Offer>(){


                                            @Override
                                            public void process(Offer arg, Exception e) {
                                                if( e != null){
                                                    textView2.setText(textView2.getText()
                                                            + "Error addFavouriteOfferForStudent, probably offer already added : \n"
                                                            + e.getMessage() + "\n\n");
                                                }else{
                                                    textView2.setText(textView2.getText() + "Offer id : "
                                                            + arg.getId() + "  have been set to favorite for student : " + student.getSurname() + "\n");
                                                }
                                            }

                                            @Override
                                            public void cancel() {

                                            }
                                        });


                                        Manager.subscribeStudentOfJobOffer(arg.get(0).getId(),student.getId(), new Manager.ResultProcessor<Integer>(){


                                            @Override
                                            public void process(Integer arg, Exception e) {
                                                if( e != null){
                                                    textView2.setText(textView2.getText()
                                                            + "Error subscribeStudentOfJobOffer, probably offer already suscribed : \n"
                                                            + e.getMessage() + "\n\n");
                                                }else{
                                                    textView2.setText(textView2.getText() + "Offer have been subscribed by student : " + student.getSurname() + "\n");
                                                }
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

                            Company criteria2 = new Company();

                            try{
                                criteria2.setName("Apple");
                            }catch(Exception ee){

                            }

                            Manager.getCompaniesMatchingCriteria(criteria2, new Manager.ResultProcessor<List<Company>>(){


                                @Override
                                public void process(List<Company> arg, Exception e) {
                                    if( e != null){
                                        textView2.setText(textView2.getText() + processException(e) + "\n");
                                    }else{

                                        if( arg.size() != 1) {
                                            textView2.setText(textView2.getText() + "Error : number of company matching the search  name: Apple :" + arg.size() + "\n");
                                            return;
                                        }


                                        Manager.addFavouriteCompanyForStudent(student.getId(),arg.get(0).getId(), new Manager.ResultProcessor<Company>(){

                                            @Override
                                            public void process(Company arg, Exception e) {

                                                if( e != null ){
                                                    textView2.setText(textView2.getText()
                                                            + "Error addFavouriteCompanyForStudent, probably company already added : \n"
                                                            + e.getMessage() + "\n\n");
                                                }else{
                                                    textView2.setText(textView2.getText() + "Company " + arg.getName() +
                                                            " have been added to the favourite of student "+ student.getSurname() + "\n");
                                                }

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
                            } );
                        }
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