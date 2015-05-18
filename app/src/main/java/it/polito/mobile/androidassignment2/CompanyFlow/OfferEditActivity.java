package it.polito.mobile.androidassignment2.CompanyFlow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;

public class OfferEditActivity extends AppCompatActivity {


    private DownloadReceiver downloadfinished;
    private View pbLogoSpinner;

    class DownloadReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            Uri logoUri = Uri.parse(filePath);
            Session.getInstance().setPhotoUri(logoUri);
            photo.setImageURI(logoUri);
        }
    }



    private AsyncTask<Object, Void, Object> task=null;
    private TextView code;
    private TextView description;
    private TextView contractType;
    private TextView numberOfMonths;
    private TextView location;
    private ImageView photo;
    private CompetencesCompletionTextView competences;
    private TextView companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_edit);

        code = (TextView) findViewById(R.id.job_offer_code);
        description = (TextView) findViewById(R.id.job_offer_description);
        location = (TextView) findViewById(R.id.location);
        numberOfMonths = (TextView) findViewById(R.id.number_of_months);
        contractType = (TextView) findViewById(R.id.kind_of_contract);
        competences = (CompetencesCompletionTextView) findViewById(R.id.competences);

        companyName = (TextView) findViewById(R.id.offer_company_fullname);
        pbLogoSpinner = findViewById(R.id.photo_spinner);
        photo=(ImageView)findViewById(R.id.photo);
        downloadfinished= new DownloadReceiver();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OfferEditActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        competences.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        final int offerId=getIntent().getIntExtra("offerId", -1);
        if(offerId==-1){
            finish();
            return;
        }
        findViewById(R.id.edit_offer_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer o = new Offer();
                try {
                    o.manuallySetId(offerId);
                } catch (DataFormatException e) {
                    //never here
                }
                o.setCode(code.getText().toString());
                o.setDescriptionOfWork(description.getText().toString());
                o.setLocation(location.getText().toString());
                o.setKindOfContract(contractType.getText().toString());
                if(numberOfMonths.getText().toString().matches("[0-9]+")) {
                    o.setDurationMonths(Integer.parseInt(numberOfMonths.getText().toString()));
                }

                String[] comp = new String[competences.getObjects().size()];
                int i=0;
                for(Object obj : competences.getObjects()){
                    comp[i]=obj.toString();
                    i++;
                }
                o.setCompetences(comp);

                Manager.updateOffer(o, new Manager.ResultProcessor<Offer>() {
                    @Override
                    public void process(Offer arg, Exception e) {
                        if(e!=null){
                            //TODO: show some error message..
                            return;
                        }
                        finish();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });


        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));



        task=Manager.getOfferById(offerId, new Manager.ResultProcessor<Offer>() {
            @Override
            public void process(final Offer arg, Exception e) {
                task=null;
                if(e!=null){
                    //TODO: show some error messge
                    finish();
                    return;
                }
                TransferController.download(getApplicationContext(), new String[]{arg.getCompany().getLogoUrl()});

                code.setText(arg.getCode());
                location.setText(arg.getLocation());
                companyName.setText(arg.getCompanyName());
                description.setText(arg.getDescriptionOfWork());


                if(arg.getCompetences()!=null && arg.getCompetences().length>0){
                    for(int i=0;i<arg.getCompetences().length;i++){
                        competences.addObject(arg.getCompetences()[i]);
                    }

                }

                contractType.setText(arg.getKindOfContract());
                if(arg.getDurationMonths() != null)
                    numberOfMonths.setText(arg.getDurationMonths().toString());

            }

            @Override
            public void cancel() {
                task=null;
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadfinished);
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
