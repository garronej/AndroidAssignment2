package it.polito.mobile.androidassignment2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Array;

import it.polito.mobile.androidassignment2.businessLogic.*;
import it.polito.mobile.androidassignment2.s3client.Util;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class ActivityPolijob extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serivice_polijob);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serivice_polijob, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void send(View view) {

        //The id we are searching for.

        Integer id = 1;

        if( !((TextView)findViewById(R.id.student_id)).getText().equals("") ) {

            id = Integer.parseInt(((TextView) findViewById(R.id.student_id)).getText().toString());

        }

        TransferController.download(this, new String[] { Util.getCredProvider(this).getIdentityId() + "/image%3A30.png" });
        //the post treatment.
        Manager.ResultProcessor<Student> resultProcessor = new Manager.ResultProcessor<Student>() {
            @Override
            public void process(Student s, Exception exception) {

                String message = null;

                if( exception != null){



                    if (exception.getClass() == RestApiException.class) {
                        int errCode =  ((RestApiException)exception).getRespCode();

                        if( errCode == -1){

                            message = exception.getMessage();

                        }else{

                            message = "Err code : " + Integer.toString(errCode);

                        }
                    }else{

                        message = "Network problem" + exception.getMessage();

                    }


                }else{

                    message = s.toString();

                }

                ((TextView)findViewById(R.id.response)).setText(message);
            }
        };

        Manager.getStudentById(id , resultProcessor );


        /*
        StudentTasks.StudentInfoGetter sig = new StudentTasks.StudentInfoGetter(new ResultProcessor<Student>() {
            @Override
            public void process(Student s) {
                ((TextView)findViewById(R.id.response)).setText(s.toString());
            }
        });
        sig.execute(Integer.parseInt( ((TextView)findViewById(R.id.student_id)).getText().toString()  ) );
        */

        //finish();
    }

    public void insertNewStudent(View view){




        //The id we are searching for.
        Student s = new Student("prova@test.com","Prova","Test","www.mywonderfulphoto.it","cv.test.it", new String[]{"link1","link2"},
                "Computer Engineering", new String[]{"java","android",".net"}, null,true,"pw");

        //the post treatment.
        Manager.ResultProcessor<Student> resultProcessor = new Manager.ResultProcessor<Student>() {
            @Override
            public void process(Student s, Exception exception) {

                String message = null;

                if( exception != null){
                    message = exception.getMessage();
                }else{

                    message = s.toString();

                }

                ((TextView)findViewById(R.id.response)).setText(message);
            }
        };

        Manager.insertNewStudent(s , resultProcessor );





        /*

        StudentTasks.StudentInserter sig = new StudentTasks.StudentInserter(new ResultProcessor<Student>() {
            @Override
            public void process(Student s) {
                ((TextView)findViewById(R.id.response)).setText(s.toString());
            }
        });
        sig.execute(new Student("prova@test.com","Prova","Test","www.mywonderfulphoto.it","cv.test.it", new String[]{"link1","link2"},
                "Computer Engineering", new String[]{"java","android",".net"}, null,true,"pw"));

        */

    }

}
