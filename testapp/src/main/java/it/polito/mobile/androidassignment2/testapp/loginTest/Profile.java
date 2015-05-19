package it.polito.mobile.androidassignment2.testapp.loginTest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.testapp.R;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class Profile extends AppCompatActivity {

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

                textView.setText(textView.getText() + "    \n");

                textView.setText(textView.getText() + "    Subscribed offer : " + session.getAppliedOffers().size() + "\n");

                i = 1;
                for( Offer offer : session.getAppliedOffers()){
                    textView.setText(textView.getText() + "    " + Integer.toString(i) + ") id=" + offer.getId() + " ( from company : " + offer.getCompanyName() +  ")\n");
                    i++;
                }



            }catch( Exception ee){
                textView.setText(ee.getMessage());
            }

        }else if( session.getWhoIsLogged() == Company.class){

            try {

                textView.setText("Company logged : " + session.getCompanyLogged().getEmail() + "\n\n");
                textView.setText(textView.getText() + "    Favourite students : " + session.getFavStudents().size() + "\n");

                int i = 1;
                for( Student student : session.getFavStudents()){
                    textView.setText(textView.getText() + "    " + Integer.toString(i) + ") " + student.getEmail()+ "\n");
                    i++;
                }

                textView.setText(textView.getText() + "\n");


                textView.setText(textView.getText() + "    Published offer : " + session.getOfferOfTheLoggedCompany().size() + "\n");

                i = 1;
                for( Offer offer : session.getOfferOfTheLoggedCompany()){
                    textView.setText(textView.getText() + "    " + Integer.toString(i) + ") id=" + offer.getId() + " ( description of work : " + offer.getDescriptionOfWork() +  ")\n");
                    i++;
                }



            }catch( Exception ee){
                textView.setText(ee.getMessage());
            }

        }
    }

}
