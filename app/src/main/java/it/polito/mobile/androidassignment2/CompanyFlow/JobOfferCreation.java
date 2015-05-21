package it.polito.mobile.androidassignment2.CompanyFlow;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.context.AppContext;

public class JobOfferCreation extends AppCompatActivity {

    private EditText description;
    private EditText numberOfMonths;
    private EditText kindOfContract;
    private EditText location;
    private CompetencesCompletionTextView competences;
    private AsyncTask<Object, Void, Object> task;
    private EditText code;
    private AsyncTask<Object, Void, Object> task1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_offer_creation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        code = (EditText)findViewById(R.id.job_offer_code);
        description = (EditText)findViewById(R.id.job_offer_description);
        numberOfMonths = (EditText)findViewById(R.id.number_of_months);
        kindOfContract = (EditText)findViewById(R.id.kind_of_contract);
        location = (EditText)findViewById(R.id.location);
        competences = (CompetencesCompletionTextView)findViewById(R.id.competences);

        task1=Manager.getAllOffersCompetences(new Manager.ResultProcessor<List<String>>() {
            @Override
            public void process(final List<String> arg, Exception e) {
                task1 = null;
                if (e != null) {
                    return;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(JobOfferCreation.this, android.R.layout.simple_list_item_1, arg);

                competences.setAdapter(adapter);


            }

            @Override
            public void cancel() {
                task1 = null;
            }
        });


        findViewById(R.id.add_job_offer).setFocusableInTouchMode(true);
        findViewById(R.id.add_job_offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer o = new Offer();
                try {
                    o.setCompanyId(((AppContext)getApplication()).getSession().getCompanyLogged().getId());
                }catch (Exception e){
                    //should never be here..
                    return;
                }
                o.setDescriptionOfWork(description.getText().toString());
                o.setKindOfContract(kindOfContract.getText().toString());
                if(numberOfMonths.getText().toString().matches("[0-9]+")) {
                    o.setDurationMonths(Integer.parseInt(numberOfMonths.getText().toString()));
                }
                o.setCode(code.getText().toString());
                o.setLocation(location.getText().toString());
                if(competences.getObjects().size() > 0){
                    String[] comp = new String[competences.getObjects().size()];
                    int i=0;
                    for(Object obj : competences.getObjects()){
                        comp[i]=obj.toString();
                        i++;
                    }
                    o.setCompetences(comp);
                }
                task= Manager.insertNewOffer(o, new Manager.ResultProcessor<Offer>() {
                    @Override
                    public void process(Offer arg, Exception e) {
                        task=null;
                        if(e!=null){
                            Toast.makeText(JobOfferCreation.this, R.string.job_offer_failed, Toast.LENGTH_LONG ).show();

                            return;
                        }

                        try {
                            ((AppContext)getApplication()).getSession().getOfferOfTheLoggedCompany().add(arg);
                        } catch (DataFormatException e1) {
                            //never here
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
    protected void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel(true);
            task=null;
        }
        if(task1!=null){
            task1.cancel(true);
            task1=null;
        }

    }
}
