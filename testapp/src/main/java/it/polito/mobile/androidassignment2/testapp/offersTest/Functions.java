package it.polito.mobile.androidassignment2.testapp.offersTest;

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



        button1.setText("Delete all offer");

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView1.setText("");

                Manager.getOffersMatchingCriteria(null, new Manager.ResultProcessor<List<Offer>>() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Offer> offers, Exception exception) {


                        textView1.setText("");

                        if (exception == null) {



                            for (final Offer offer : offers) {




                                Manager.deleteOffer(offer.getId(), new Manager.ResultProcessor<Integer>() {
                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void process(Integer arg, Exception e) {

                                        if (e == null) {

                                            textView1.setText(textView1.getText() + "Deleted offer id : " + offer.getId() + " of company : " + offer.getCompanyName() +"\n");

                                        } else {
                                            textView1.setText(textView1.getText() + processException(e) + "\n");
                                        }

                                    }
                                });
                            }


                        } else {

                            textView1.setText(processException(exception));
                        }
                    }

                });
            }
        });

        button2.setText("Add samples offers");

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView2.setText("");


                final Manager.ResultProcessor<Offer> postProcessor = new Manager.ResultProcessor<Offer>() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(Offer arg, Exception e) {
                        if (e == null) {
                            textView2.setText(textView2.getText() + "Created offer id : " + arg.getId() + " of company : " + arg.getCompanyName() +"\n");
                        } else {
                            textView2.setText(textView2.getText() + processException(e) + '\n');
                        }
                    }
                };


                //Adding two offer from Apple

                Company criteria = null;
                try {

                    criteria = new Company();

                    criteria.setName("Apple");
                }catch(Exception e){}


                Manager.getCompaniesMatchingCriteria(criteria,new Manager.ResultProcessor<List<Company>>(){
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Company> arg, Exception e) {

                        if( arg.size() == 1){

                            Offer o = null;

                            try{

                                o = new Offer();

                                o.setCompanyId(arg.get(0).getId());
                                o.setKindOfContract("Contract 1");
                                o.setDescriptionOfWork("description of work 1");
                                o.setCode("aaz33");
                                o.setLocation("Montpellier, France");

                            }catch( Exception ee){

                            }

                            Manager.insertNewOffer(o,postProcessor);

                        }

                    }
                });

                Manager.getCompaniesMatchingCriteria(criteria,new Manager.ResultProcessor<List<Company>>(){
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Company> arg, Exception e) {

                        if( arg.size() == 1){

                            Offer o = null;

                            try{

                                o = new Offer();

                                o.setCompanyId(arg.get(0).getId());
                                o.setKindOfContract("Contract 2");
                                o.setDescriptionOfWork("description of work 2");
                                o.setDurationMonths(3);
                                o.setCode("oss117");
                                o.setLocation("Paris");

                            }catch( Exception ee){

                            }

                            Manager.insertNewOffer(o,postProcessor);

                        }

                    }
                });


               //Adding one offer from google
                try {

                    criteria = new Company();

                    criteria.setName("Google");
                }catch(Exception e){}


                Manager.getCompaniesMatchingCriteria(criteria,new Manager.ResultProcessor<List<Company>>(){
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Company> arg, Exception e) {

                        if( arg.size() == 1){

                            Offer o = null;

                            try{

                                o = new Offer();

                                o.setCompanyId(arg.get(0).getId());
                                o.setKindOfContract("Contract 1");
                                o.setDescriptionOfWork("description of work 1");
                                o.setDurationMonths(3);
                                o.setCode("OuiOui");
                                o.setLocation("Seattle");

                            }catch( Exception ee){

                            }

                            Manager.insertNewOffer(o,postProcessor);

                        }

                    }
                });


            }
        });

        button3.setText("Modify a offer");

        button3.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                textView3.setText("Loading...");

                Offer criteria = null;
                try {
                    criteria = new Offer();
                    criteria.setCompanyName("Apple");
                    criteria.setDescriptionOfWork("description of work 2");

                } catch (Exception e) {
                    Log.v("anticipation", "cration of criteria for search : " + e.getMessage());
                }


                Manager.getOffersMatchingCriteria(criteria, new Manager.ResultProcessor<List<Offer>>() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Offer> arg, Exception e) {

                        if (e == null) {

                            if (arg.size() == 1) {

                                Offer criteria = null;

                                try {
                                    criteria = new Offer();

                                    criteria.manuallySetId(arg.get(0).getId());

                                    criteria.setDurationMonths(666);

                                } catch (Exception ee) {
                                    textView3.setText("On a fait des coneries ici!");
                                    return;
                                }

                                Manager.updateOffer(criteria, new Manager.ResultProcessor<Offer>() {
                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void process(Offer arg, Exception e) {

                                        if (e == null) {

                                            textView3.setText("The apple second offer duration have been set to 666 month");

                                        } else {
                                            textView3.setText(processException(e) + '\n');
                                            return;
                                        }

                                    }
                                });

                            } else {
                                textView3.setText("Error number of Offer matching the search is  : " + arg.size() + '\n');

                            }


                        } else {



                            textView3.setText(textView3.getText() + processException(e) + '\n');
                        }

                    }
                });


            }
        });


        return rootView;


    }

    private static String processException(Exception exception){

        String message = "";
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