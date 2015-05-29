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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;

public class OfferEditActivity extends AppCompatActivity {


    private DownloadReceiver downloadfinished;
    private View pbLogoSpinner;
    private DownloadErrorReceiver downloadError;
    private AsyncTask<Object, Void, Object> task1;
    private EditText title;


    private class DownloadErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            pbLogoSpinner.setVisibility(ProgressBar.GONE);
            Toast.makeText(getApplicationContext(), R.string.download_error, Toast.LENGTH_LONG).show();
            //exTODO: set placeholder image
            photo.setImageDrawable(getResources().getDrawable(R.drawable.photo_placeholder_err));
        }
    }

    class DownloadReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            Uri logoUri = Uri.parse(filePath);

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
        title = (EditText) findViewById(R.id.job_title);
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
        downloadError = new DownloadErrorReceiver();
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
        findViewById(R.id.edit_offer_save).setFocusableInTouchMode(true);
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
                o.setTitle(title.getText().toString());
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

                task1=Manager.updateOffer(o, new Manager.ResultProcessor<Offer>() {
                    @Override
                    public void process(Offer arg, Exception e) {
                        task1=null;
                        if(e!=null){
                            Log.d(OfferEditActivity.class.getSimpleName(), "Error updating offer");
                            return;
                        }
                        finish();
                    }

                    @Override
                    public void cancel() {
                        task1=null;
                    }
                });
            }
        });


        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));

        registerReceiver(downloadError, new IntentFilter(DownloadModel.INTENT_DOWNLOAD_FAILED));

        task=Manager.getOfferById(offerId, new Manager.ResultProcessor<Offer>() {
            @Override
            public void process(final Offer arg, Exception e) {
                task=null;
                if(e!=null){
                    Log.d(OfferEditActivity.class.getSimpleName(),"Error retrieving offer");
                    finish();
                    return;
                }
                TransferController.download(getApplicationContext(), new String[]{arg.getCompany().getLogoUrl()});
                title.setText(arg.getTitle());
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
        unregisterReceiver(downloadError);
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
