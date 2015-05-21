package it.polito.mobile.androidassignment2.CompanyFlow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.ClientsCompletionTextView;
import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.HobbiesCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.StudentProfileActivity;
import it.polito.mobile.androidassignment2.Utils;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.models.UploadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class EditCompanyProfileActivity extends ActionBarActivity  {
    private ImageView ivLogo;
    private EditText etName;
    private EditText etDescription;
    private EditText etMission;
    private EditText etNumberOfWorkers;
    private ProgressBar pbLogoSpinner;
    private Button bUpdateProfile;
    private Button bCancelUpdateProfile;
    private Uri logoUri;
    private Company loggedCompany;
    private ProgressBar pbUpdateSpinner;
    private DownloadFinished downloadfinished = new DownloadFinished();
    private UploadFinished uploadfinished=new UploadFinished();
    private EditText etLocation;
    private AsyncTask<Object, Void, Object> task1 = null;
    private AsyncTask<Object, Void, Object> task2 = null;
    private AsyncTask<Object, Void, Object> task3 = null;
    private CompetencesCompletionTextView acCompetences;
    private ClientsCompletionTextView acClients;
    private static final int PICK_PICTURE = 0;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            Uri uri = Uri.parse(filePath);
            Session.getInstance().setPhotoUri(uri);
            ivLogo.setImageURI(uri);
        }
    }

    public class UploadFinished extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filePath = intent.getStringExtra(UploadModel.EXTRA_FILENAME);
            //check if it's image -> IT'S IMPLICIT
            try {
                loggedCompany = Session.getInstance().getCompanyLogged();
            } catch (DataFormatException e) {
                throw new RuntimeException();
            }
            loggedCompany.setLogoUrl(filePath);
            task1 = Manager.updateCompany(loggedCompany, new Manager.ResultProcessor<Company>() {
                @Override
                public void process(Company arg, Exception e) {
                    if (e == null) {
                        TransferController.download(getApplicationContext(), new String[]{filePath});
                    } else {
                        throw new RuntimeException();
                    }
                }

                @Override
                public void cancel() {
                }
            });

            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logoUri = Session.getInstance().getPhotoUri();
        try {
            loggedCompany = Session.getInstance().getCompanyLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException();
        }
        setContentView(R.layout.activity_edit_company_profile);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        setupViewsAndCallbacks();
    }

    private void findViews() {
        ivLogo = (ImageView) findViewById(R.id.edit_c_logo_iv);
        etName = (EditText) findViewById(R.id.edit_c_name_et);
        etMission = (EditText) findViewById(R.id.edit_c_mission_et);
        etDescription = (EditText) findViewById(R.id.edit_c_description_et);
        acCompetences = (CompetencesCompletionTextView) findViewById(R.id.edit_c_competences_ac);
        acClients = (ClientsCompletionTextView) findViewById(R.id.edit_c_clients_ac);
        bUpdateProfile = (Button) findViewById(R.id.edit_c_update_profile_b);
        bCancelUpdateProfile = (Button) findViewById(R.id.edit_c_cancel_update_profile_b);
        pbLogoSpinner = (ProgressBar) findViewById(R.id.edit_c_logo_pb);
        pbUpdateSpinner = (ProgressBar) findViewById(R.id.edit_c_update_pb);
        etLocation = (EditText) findViewById(R.id.edit_c_location_et);
        etNumberOfWorkers = (EditText) findViewById(R.id.edit_c_number_of_workers_et);
    }

    private void setupViewsAndCallbacks() {
        etName.setText(loggedCompany.getName());
        etDescription.setText(loggedCompany.getDescription());
        ivLogo.setImageURI(logoUri);
        etLocation.setText(loggedCompany.getLocation());
        etMission.setText(loggedCompany.getMission());
        if (loggedCompany.getNumberOfWorkers() != null) {
            etNumberOfWorkers.setText(loggedCompany.getNumberOfWorkers().toString()); //TODO use something else not edit text + validate number
        }
        bUpdateProfile.setFocusableInTouchMode(true);
        bUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pbUpdateSpinner.setVisibility(ProgressBar.VISIBLE);
                    bUpdateProfile.setVisibility(View.INVISIBLE);
                    loggedCompany.setName(etName.getText().toString());
                    loggedCompany.setDescription(etDescription.getText().toString());
                    loggedCompany.setMission(etMission.getText().toString());
                    loggedCompany.setLocation(etLocation.getText().toString());
                    if (!etNumberOfWorkers.getText().toString().equals("")) {
                        loggedCompany.setNumberOfWorkers(Integer.parseInt(etNumberOfWorkers.getText().toString()));
                    } else {
                        loggedCompany.setNumberOfWorkers(null);
                    }

                    if(acCompetences.getObjects().size() > 0){
                        String[] comp = new String[acCompetences.getObjects().size()];
                        int i=0;
                        for(Object o : acCompetences.getObjects()){
                            comp[i]=o.toString();
                            i++;
                        }
                        loggedCompany.setCompetences(comp);
                    } else {
                        loggedCompany.setCompetences(new String[0]);
                    }

                    if(acClients.getObjects().size() > 0){
                        String[] hob = new String[acClients.getObjects().size()];
                        int i=0;
                        for(Object o : acClients.getObjects()){
                            hob[i]=o.toString();
                            i++;
                        }
                        loggedCompany.setClients(hob);
                    } else {
                        loggedCompany.setClients(new String[0]);
                    }

                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                }
                task2 = Manager.updateCompany(loggedCompany, new Manager.ResultProcessor<Company>() {
                    @Override
                    public void process(Company arg, Exception e) {
                        if (e == null) {
                            Intent i = new Intent(EditCompanyProfileActivity.this, CompanyProfileActivity.class);
                            startActivity(i);
	                        finish();
                        } else {
                            throw new RuntimeException();
                        }
                    }

                    @Override
                    public void cancel() {
                        pbUpdateSpinner.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
            }
        });
        bCancelUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditCompanyProfileActivity.this, CompanyProfileActivity.class);
                startActivity(i);
	            finish();
            }
        });

        ivLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_photo)), PICK_PICTURE);
            }
        });

        task3 = Manager.getAllCompaniesCompetences(new Manager.ResultProcessor<List<String>>() {
            @Override
            public void process(final List<String> arg, Exception e) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditCompanyProfileActivity.this, android.R.layout.simple_list_item_1, arg);
                acCompetences.setAdapter(adapter);
                String[] cs = loggedCompany.getCompetences();
                if (cs != null && cs.length > 0) {
                    for (String c : cs) {
                        acCompetences.addObject(c);
                    }
                }
            }
            @Override
            public void cancel() {
            }
        });

        List<String> clientsL = new ArrayList<String>();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditCompanyProfileActivity.this, android.R.layout.simple_list_item_1, clientsL);
        acClients.setAdapter(adapter2);
        String[] hs = loggedCompany.getClients();
        if (hs != null && hs.length > 0) {
            for (String h : hs) {
                acClients.addObject(h);
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (reqCode == PICK_PICTURE) {
                onActivityResultForPicture(uri);
            } else {
                throw new RuntimeException("missing required param");
            }
        }
    }

    private void onActivityResultForPicture(Uri uri) {
        if (Utils.isPicture(EditCompanyProfileActivity.this, uri)) {
            pbLogoSpinner.setVisibility(ProgressBar.VISIBLE);
            TransferController.upload(this, uri, "photo/student3");
        } else {
            Toast t = Toast.makeText(this, getResources().getString(R.string.select_photo), Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(uploadfinished, new IntentFilter(UploadModel.INTENT_UPLOADED));
        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(downloadfinished);
        unregisterReceiver(uploadfinished);
        if(task1 != null){
            task1.cancel(true);
            task1 = null;
        }
        if(task2 != null){
            task2.cancel(true);
            task2 = null;
        }
        if(task3 != null){
            task3.cancel(true);
            task3 = null;
        }
        super.onPause();
    }

}
