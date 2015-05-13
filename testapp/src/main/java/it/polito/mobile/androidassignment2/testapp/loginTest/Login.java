package it.polito.mobile.androidassignment2.testapp.loginTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button button1 = (Button)findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {



                Session.login("paul.dupont@gmail.com","passOfPaul", new Manager.ResultProcessor<Integer>() {
                    @Override
                    public void process(Integer arg, Exception e) {
                        if( e != null ){

                            Toast.makeText(Login.this, "Error while login in " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }else{

                            startActivity(new Intent(Login.this,Profile.class ));

                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });



            }
        });
    }



}
