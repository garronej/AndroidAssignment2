package it.polito.mobile.androidassignment2.CompanyFlow;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class OfferShowActivity extends AppCompatActivity {

    private AsyncTask<Object, Void, Object> task=null;
    private Button editOfferButton;
    private TextView code;
    private TextView description;
    private TextView contractType;
    private TextView numberOfMonths;
    private TextView location;
    private TextView competences;
    private LinearLayout studentActions;
    private Button applyButton;
    private Button addToFavouriteButton;
    private TextView companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_show);
        editOfferButton = (Button) findViewById(R.id.edit_offer_button);
        code = (TextView) findViewById(R.id.job_offer_code);
        description = (TextView) findViewById(R.id.job_offer_description);
        location = (TextView) findViewById(R.id.location);
        numberOfMonths = (TextView) findViewById(R.id.number_of_months);
        contractType = (TextView) findViewById(R.id.kind_of_contract);
        competences = (TextView) findViewById(R.id.competences);
        studentActions = (LinearLayout) findViewById(R.id.student_actions);
        applyButton = (Button) findViewById(R.id.apply_button);
        addToFavouriteButton = (Button) findViewById(R.id.add_to_favourite);
        companyName = (TextView) findViewById(R.id.offer_company_fullname);

    }


    @Override
    protected void onResume() {
        super.onResume();
        int offerId=getIntent().getIntExtra("offerId", -1);
        if(offerId==-1){
            finish();
            return;
        }




        task=Manager.getOfferById(offerId, new Manager.ResultProcessor<Offer>() {
            @Override
            public void process(Offer arg, Exception e) {
                task=null;
                if(e!=null){
                    //TODO: show some error messge
                    finish();
                    return;
                }
                code.setText(arg.getCode());
                location.setText(arg.getLocation());
                companyName.setText(arg.getCompanyName());
                description.setText(arg.getDescriptionOfWork());

                String c="";
                if(arg.getCompetences()!=null && arg.getCompetences().length>0){
                    c=arg.getCompetences()[0];
                    for(int i=1;i<arg.getCompetences().length;i++){
                        c+=","+arg.getCompetences()[i];
                    }

                }
                competences.setText(c);
                contractType.setText(arg.getKindOfContract());
                if(arg.getDurationMonths() != null)
                    numberOfMonths.setText(arg.getDurationMonths().toString());

                try {
                    if (Session.getInstance().getWhoIsLogged() == Company.class
                            && arg.getCompanyId() == Session.getInstance().getCompanyLogged().getId()) {
                        editOfferButton.setVisibility(View.VISIBLE);
                    }
                }catch (Exception exc){
                    //never here
                }

                //TODO : set photo

            }

            @Override
            public void cancel() {
                task=null;
            }
        });

        if(Session.getInstance().getWhoIsLogged() == Student.class){
            studentActions.setVisibility(View.VISIBLE);
        }




    }


    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel(true);
            task=null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offer_show, menu);
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
}
