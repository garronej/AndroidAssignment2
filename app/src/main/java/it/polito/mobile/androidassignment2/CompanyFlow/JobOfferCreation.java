package it.polito.mobile.androidassignment2.CompanyFlow;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class JobOfferCreation extends AppCompatActivity {

    private EditText description;
    private EditText numberOfMonths;
    private EditText kindOfContract;
    private EditText location;
    private CompetencesCompletionTextView competence;
    private AsyncTask<Object, Void, Object> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_offer_creation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        description = (EditText)findViewById(R.id.job_offer_description);
        numberOfMonths = (EditText)findViewById(R.id.number_of_months);
        kindOfContract = (EditText)findViewById(R.id.kind_of_contract);
        location = (EditText)findViewById(R.id.location);
        competence = (CompetencesCompletionTextView)findViewById(R.id.competences);

        //TODO set competences autocompletion....



        findViewById(R.id.add_job_offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer o = new Offer();
                try {
                    o.setCompanyId(Session.getInstance().getCompanyLogged().getId());
                }catch (Exception e){
                    //should never be here..
                    return;
                }
                o.setDescriptionOfWork(description.getText().toString());
                o.setKindOfContract(kindOfContract.getText().toString());
                if(numberOfMonths.getText().toString().matches("[0-9]+")) {
                    o.setDurationMonths(Integer.parseInt(numberOfMonths.getText().toString()));
                }
                //TODO: set location
                //TODO: set competences
                task= Manager.insertNewOffer(o, new Manager.ResultProcessor<Offer>() {
                    @Override
                    public void process(Offer arg, Exception e) {
                        task=null;
                        if(e!=null){
                            Toast.makeText(JobOfferCreation.this, R.string.job_offer_failed, Toast.LENGTH_LONG ).show();

                            return;
                        }
                        finish();
                    }

                    @Override
                    public void cancel() {
                        task=null;
                    }
                });



            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job_offer_creation, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel(true);
            task=null;
        }

    }
}
