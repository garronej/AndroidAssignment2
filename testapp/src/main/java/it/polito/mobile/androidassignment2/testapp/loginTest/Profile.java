package it.polito.mobile.androidassignment2.testapp.loginTest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class Profile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        TextView textView = (TextView)findViewById(R.id.textView);

        Session session = Session.getInstance();



        if( session.getWhoIsLogged() == Student.class){

            try {

                textView.setText("Student logged : " + session.getStudentLogged().getEmail() + "\n\n");
                textView.setText(textView.getText() + "    Favourite company : " + session.getFavCompanies().size() + "\n");

                int i = 1;
                for( Company company : session.getFavCompanies()){
                    textView.setText(textView.getText() + "    " + Integer.toString(i) + ") " + company.getName()+ "\n");
                    i++;
                }

                textView.setText(textView.getText() + "    \n");


                textView.setText(textView.getText() + "    Favourite offer : " + session.getFavoriteOffer().size() + "\n");

                i = 1;
                for( Offer offer : session.getFavoriteOffer()){
                    textView.setText(textView.getText() + "    " + Integer.toString(i) + ") id=" + offer.getId() + " ( from company : " + offer.getCompanyName() +  ")\n");
                    i++;
                }



            }catch( Exception ee){
                textView.setText(ee.getMessage());
            }

        }


    }



}
