package it.polito.mobile.androidassignment2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Student;

/**
 * Created by mark9 on 11/05/15.
 */
public class AlertStudentOrCompany extends Activity {


    private Spinner s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_student_company);

        s=(Spinner)findViewById(R.id.spinner_student_company);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                new String[]{ getResources().getString(R.string.company), getResources().getString(R.string.student)});
        s.setAdapter(adapter);

        final String email = getIntent().getStringExtra("EMAIL");
        final String pwd = getIntent().getStringExtra("PWD");

        ((Button)findViewById(R.id.register_as_an_instance_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s.getSelectedItem().toString().equals(getResources().getString(R.string.company))) {

                    Company c = new Company();
                    try {
                        c.setPassword(pwd);
                        c.setEmail(email);
                    } catch (Exception e) {
                        Toast.makeText(AlertStudentOrCompany.this, R.string.error_invalid_password, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    Manager.insertNewCompany(c, new Manager.ResultProcessor<Company>() {
                        @Override
                        public void process(Company arg, Exception e) {
                            if (AlertStudentOrCompany.this.getParent() == null) {
                                AlertStudentOrCompany.this.setResult(Activity.RESULT_OK);
                            } else {
                                AlertStudentOrCompany.this.getParent().setResult(Activity.RESULT_OK);
                            }
                            AlertStudentOrCompany.this.finish();
                        }

                        @Override
                        public void cancel() {

                        }
                    });


                } else {

                    Student s = new Student();
                    try {
                        s.setPassword(pwd);
                        s.setEmail(email);
                    } catch (Exception e) {
                        Toast.makeText(AlertStudentOrCompany.this, R.string.error_invalid_password, Toast.LENGTH_LONG).show();
                        finish();
                    }
                    Manager.insertNewStudent(s, new Manager.ResultProcessor<Student>() {
                        @Override
                        public void process(Student arg, Exception e) {
                            if (AlertStudentOrCompany.this.getParent() == null) {
                                AlertStudentOrCompany.this.setResult(Activity.RESULT_OK);
                            } else {
                                AlertStudentOrCompany.this.getParent().setResult(Activity.RESULT_OK);
                            }
                            AlertStudentOrCompany.this.finish();
                        }

                        @Override
                        public void cancel() {

                        }
                    });


                }
            }
        });


    }




}
