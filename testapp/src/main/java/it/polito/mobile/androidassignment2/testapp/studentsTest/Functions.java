package it.polito.mobile.androidassignment2.testapp.studentsTest;

/**
 * Created by Joseph on 07/05/2015.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
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




        button1.setText("Delete all students");
        button1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                textView1.setText("Loading ...");

                Manager.getStudentsMatchingCriteria(null, null, new Manager.ResultProcessor<List<Student>>(){

                    @Override
                    public void cancel() {}


                    @Override
                    public void process(final List<Student> students, Exception exception) {



                        if( exception != null ) {

                            textView1.setText(processException(exception));
                            return;
                        }

                            //This will be incremented each time a asyncTask is completed.
                            final AtomicInteger asyncTaskCounter = new AtomicInteger(0);

                            //This will store the tring we want to put in the textView.
                            final StringBuffer buffer = new StringBuffer();

                            AsyncTask<Void, Void, Void> pool = new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {

                                    while( asyncTaskCounter.get() != students.size() && asyncTaskCounter.get() != -1 )
                                        SystemClock.sleep(100);

                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void out) {

                                    if( asyncTaskCounter.get() == -1 ){
                                        return;
                                    }else if (asyncTaskCounter.get() == 0){
                                        textView1.setText("There is no student to delete.");
                                        return;
                                    }else{



                                        textView1.setText(buffer.toString());
                                        return;
                                    }
                                }
                            };

                            for( final Student student : students){



                                Manager.deleteStudent(student.getId(), new Manager.ResultProcessor<Integer>() {

                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void process(Integer arg, Exception e) {

                                        if (e == null) {



                                            buffer.append("Deleted Student : " + student.getEmail() + "\n");
                                            asyncTaskCounter.incrementAndGet();

                                        } else {
                                            textView1.setText(textView1.getText() + processException(e) + "\n");
                                            asyncTaskCounter.set(-1);

                                        }

                                    }
                                });



                            }




                        pool.execute();
                    }

                });
            }
        });

        button2.setText("Add some sample students");

        button2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                textView2.setText("");




                Manager.ResultProcessor<Student> postProcessor = new Manager.ResultProcessor<Student>(){

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(Student arg, Exception e) {
                        if( e == null){
                            textView2.setText(textView2.getText() + "Student " + arg.getEmail() + " inserted\n");
                        }else{
                            textView2.setText(textView2.getText() + processException(e) + '\n' );
                        }
                    }
                };

                Student s = null;


                try{
                    s= new Student();
                    s.setEmail("Joseph.garrOne.gj@gmail.com");
                    s.setName("GaRRone");
                    s.setSurname("Joseph");
                    s.setPassword("stupid2");
                    s.setCvUrl("http://cloud.garrone.org/poliJobs/cvs/garronejoseph.pdf");
                    s.setAvailable(false);
                    s.setCompetences(new String[]{"porn knowelage","fast running"});
                    s.setHobbies(new String[]{"do sport","reading"});
                    s.setLinks(new String[]{ "http://seedbox.garrone.org", "http://etophy.fr"});
                    s.setPhotoUrl("http://cloud.garrone.org/mwa.png");
                }catch( Exception e){
                    textView2.setText("Error creating student dupon paulgarrone joseph, exception message : " + e.getMessage());
                    return;
                }

                Manager.insertNewStudent(s, postProcessor);



                try{
                    s= new Student();
                    s.setEmail("paul.dupont@gmail.com");
                    s.setName("dupont");
                    s.setSurname("paul");
                    s.setPassword("passOfPaul");
                    s.setCvUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/esercitazione2.pdf");
                    s.setAvailable(false);
                    s.setCompetences(new String[]{"java", "c#"});
                    s.setHobbies(new String[]{"sport", "literature"});
                    s.setLinks(new String[]{"http://seedbox.garrone.org", "http://etophy.fr"});
                    s.setPhotoUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/jos.png");
                    s.setSex("m");
                    s.setLocation("Torino");
                }catch( Exception e){
                    textView2.setText("Error creating student dupontpaul, exception message : " + e.getMessage());
                    return;
                }

                Manager.insertNewStudent(s, postProcessor);

                try{
                    s= new Student();
                    s.setEmail("louis.lebongars@gmail.com");
                    s.setName("louis");
                    s.setSurname("lebongars");
                    s.setPassword("stupid3");

                }catch( Exception e){
                    textView2.setText("Error creating student louis lebongars, exception message : " + e.getMessage());
                    return;
                }

                Manager.insertNewStudent(s, postProcessor);




            }
        });

        button3.setText("Modify a student");

        button3.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                textView3.setText("Loading...");

                Student criteria = null;
                try {
                    criteria = new Student();
                    criteria.setName("garrone");
                } catch (Exception e) {

                }


                Manager.getStudentsMatchingCriteria(criteria, null, new Manager.ResultProcessor<List<Student>>() {

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void process(List<Student> arg, Exception e) {

                        if (e == null) {


                            if (arg.size() == 1) {

                                Student criteria = null;

                                try {
                                    criteria = new Student();
                                    criteria.setCompetences(new String[]{"sleeping"});
                                    criteria.manuallySetId(arg.get(0).getId());

                                } catch (Exception ee) {
                                    textView3.setText(processException(ee) + '\n');
                                    return;
                                }

                                Manager.updateStudent(criteria, new Manager.ResultProcessor<Student>() {

                                    @Override
                                    public void cancel() {

                                    }

                                    @Override
                                    public void process(Student arg, Exception e) {
                                        if (e == null) {

                                            textView3.setText("Done! Comptetence of the student joseph.garrone.gj@gmail.com have been set to \"sleeping\"");

                                        } else {
                                            textView3.setText(processException(e) + '\n');
                                            return;
                                        }

                                    }
                                });

                            } else {
                                textView3.setText("Error number of student matching the search : " + arg.size() + '\n');

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