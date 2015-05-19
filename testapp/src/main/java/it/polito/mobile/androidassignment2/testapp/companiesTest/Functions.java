package it.polito.mobile.androidassignment2.testapp.companiesTest;

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

import java.net.URL;
import java.util.List;

import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
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



        button1.setText("Delete all company");

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView1.setText("");

                Manager.getCompaniesMatchingCriteria(null, new Manager.ResultProcessor<List<Company>>() {


                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Company> companies, Exception exception) {


                        textView1.setText("");

                        if (exception == null) {

                            Log.v("azert", "companies.size() = " + companies.size());

                            for (final Company company : companies) {

                                Log.v("azert", "company.getId() = " + company.getId());


                                Manager.deleteCompany(company.getId(), new Manager.ResultProcessor<Integer>() {

                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void process(Integer arg, Exception e) {

                                        if (e == null) {

                                            textView1.setText(textView1.getText() + "Deleted company : " + company.getEmail() + "\n");
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

        button2.setText("Add samples companies");

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                textView2.setText("");


                Manager.ResultProcessor<Company> postProcessor = new Manager.ResultProcessor<Company>() {

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(Company arg, Exception e) {
                        if (e == null) {
                            textView2.setText(textView2.getText() + "Company " + arg.getEmail() + " inserted\n");
                        } else {
                            textView2.setText(textView2.getText() + processException(e) + '\n');
                        }
                    }
                };

                Company c = null;


                try {
                    c = new Company();
                    c.setEmail("google@gmail.com");
                    c.setName("GooGle");
                    c.setLogoUrl("http://mySpace.org/google.ico");
                    c.setPassword("stupid3");
                    c.setMission("control the all world");
                    c.setNumberOfWorkers(666666);
                    c.setClients(new String[]{"Oracle", "IBM", "LENOVO", "everyone"});
                    c.setLocation("New York");
                    c.setDescription("Sell softwar for free");

                } catch (Exception e) {
                    textView2.setText(" Error creating google" + e.getMessage());
                    return;
                }

                Log.v("azert", c.toString());

                Manager.insertNewCompany(c, postProcessor);


                try {
                    c = new Company();
                    c.setEmail("apple@gmail.com");
                    c.setName("Apple");
                    c.setLogoUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/jos.png");
                    c.setPassword("stupid4#");
                    c.setMission("control the all world");
                    c.setNumberOfWorkers(300);
                    c.setClients(new String[]{"US Government", "Hipsters"});
                    c.setLocation("Seattle");
                    c.setDescription("Sell decorative objects that can be used as electronic device");
                } catch (Exception e) {
                    textView2.setText("Error creating company Apple, exception message : " + e.getMessage());
                    return;
                }


                Log.v("qsdfg", "Print apple after creation : " + c.toString());

                Manager.insertNewCompany(c, postProcessor);

                try {
                    c = new Company();
                    c.setEmail("TheVoid@gmail.com");
                    c.setPassword("IamStupid");


                } catch (Exception e) {
                    textView2.setText("Error creating company TheVoid : " + e.getMessage());
                    return;
                }

                Manager.insertNewCompany(c, postProcessor);

            }
        });

        button3.setText("Modify a company");

        button3.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                textView3.setText("Loading...");

                Company criteria = null;
                try {
                    criteria = new Company();
                    criteria.setName("Apple");
                } catch (Exception e) {

                }


                Manager.getCompaniesMatchingCriteria(criteria, new Manager.ResultProcessor<List<Company>>() {

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Company> arg, Exception e) {

                        if (e == null) {


                            if (arg.size() == 1) {

                                Company criteria = null;

                                try {
                                    criteria = new Company();


                                    String[] newClients = new String[arg.get(0).getClients().length + 1];

                                    for (int i = 0; i < arg.get(0).getClients().length; i++) {
                                        newClients[i] = new String(arg.get(0).getClients()[i]);
                                    }

                                    newClients[arg.get(0).getClients().length] = "new Client";


                                    criteria.setClients(newClients);

                                    criteria.manuallySetId(arg.get(0).getId());

                                } catch (Exception ee) {
                                    textView3.setText("On a fait des coneries ici!");
                                    return;
                                }

                                Manager.updateCompany(criteria, new Manager.ResultProcessor<Company>() {

                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void process(Company arg, Exception e) {

                                        if (e == null) {

                                            textView3.setText("Done! To " + arg.getName() + " company's clients have been added \"new client\"");

                                        } else {
                                            textView3.setText(processException(e) + '\n');
                                            return;
                                        }

                                    }
                                });

                            } else {
                                textView3.setText("Error number of company matching the search \"Apple\" is  : " + arg.size() + '\n');

                            }


                        } else {

                            Log.v("FFFF", "On est la, getCompanyMatchingCriteria a crash");

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