package it.polito.mobile.androidassignment2.CompanyFlow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.CompaniesFavouritesActivity;
import it.polito.mobile.androidassignment2.StudentFlow.EditStudentProfileActivity;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;

public class CompanyProfileActivity extends ActionBarActivity implements Communicator {
    private ImageView ivLogo;
    private ProgressBar pbLogoSpinner;
    private TextView tvName;
    private TextView tvClients;
    private TextView tvEmail;
    private DownloadFinished downloadfinished = new DownloadFinished();
    private Uri logoUri;
    private Button bEditProfile;
    private TextView tvLocation;
    private TextView tvNumberOfWorkers;
    private TextView tvMission;
    private TextView tvCompetences;
    private TextView tvDescription;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            logoUri = Uri.parse(filePath);
            Session.getInstance().setPhotoUri(logoUri);
            ivLogo.setImageURI(logoUri);
            tvName.setVisibility(View.VISIBLE);
            bEditProfile.setEnabled(true);
        }
    }
    private void myAddActionBar(){
        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.company_tabbed_menu,null);

        // Set up your ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

    }
    private void addTabMenuButtonCallbacks(){
        findViewById(R.id.tab_menu_company_search).setVisibility(View.INVISIBLE);
	    findViewById(R.id.tab_menu_company_profile).setBackgroundColor(getResources().getColor(R.color.strong_blue));
	    findViewById(R.id.tab_menu_company_offers).setBackgroundColor(getResources().getColor(R.color.blue_sky));
	    findViewById(R.id.tab_menu_company_students).setBackgroundColor(getResources().getColor(R.color.blue_sky));
        findViewById(R.id.tab_menu_company_students).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentsFavouritesActivity.class);
                startActivity(i);
	            finish();
            }
        });
        findViewById(R.id.tab_menu_company_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OffersProposed.class);
                startActivity(i);
                finish();
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.getInstance().getPhotoUri() != null) {
            logoUri = Session.getInstance().getPhotoUri();
        }
        setContentView(R.layout.activity_company_profile);
        findViews();
        setupViewsAndCallbacks();
        myAddActionBar();
        addTabMenuButtonCallbacks();
    }

    private void findViews() {
        tvEmail = (TextView) findViewById(R.id.c_email_tv);
        ivLogo = (ImageView) findViewById(R.id.c_logo_iw);
        pbLogoSpinner = (ProgressBar) findViewById(R.id.c_logo_pb);
        tvName = (TextView) findViewById(R.id.c_name_tv);
        tvClients = (TextView) findViewById(R.id.c_clients_tv);
        tvCompetences = (TextView) findViewById(R.id.c_competences_tv);
        bEditProfile = (Button) findViewById(R.id.c_edit_profile_b);
        tvLocation = (TextView) findViewById(R.id.c_location_tv);
        tvNumberOfWorkers = (TextView) findViewById(R.id.c_number_of_workers_tv);
        tvMission = (TextView) findViewById(R.id.c_mission_tv);
        tvDescription = (TextView) findViewById(R.id.c_description_tv);
    }

    private void setupViewsAndCallbacks() {
        final Company loggedCompany;
        try {
            loggedCompany = Session.getInstance().getCompanyLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException();
        }
        tvEmail.setText(loggedCompany.getEmail());
        String name = loggedCompany.getName();
        if (name == null) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setText(loggedCompany.getName());
        }

        String clients = loggedCompany.getClientsToString(", ");
        if (clients == null) {
            tvClients.setVisibility(View.GONE);
        } else {
            tvClients.setText(clients);
        }

        Integer numberOfWorkers = loggedCompany.getNumberOfWorkers();
        if (numberOfWorkers == null) {
            tvNumberOfWorkers.setVisibility(View.GONE);
        } else {
            tvNumberOfWorkers.setText(numberOfWorkers.toString());
        }

        String competences = loggedCompany.getCompetencesToString(", ");
        if (competences == null) {
            tvCompetences.setVisibility(View.GONE);
        } else {
            tvCompetences.setText(competences);
        }

        String location = loggedCompany.getLocation();
        if (location == null || location.equals("")) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setText(location);
        }

        String mission = loggedCompany.getMission();
        if (mission == null || mission.equals("")) {
            tvMission.setVisibility(View.GONE);
        } else {
            tvMission.setText(mission);
        }

        String description = loggedCompany.getDescription();
        if (description == null || description.equals("")) {
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setText(location);
        }

        String url = loggedCompany.getLogoUrl();
        if (logoUri == null) { //need to get from s3
            TransferController.download(getApplicationContext(), new String[]{url});
            bEditProfile.setEnabled(false);
            tvName.setVisibility(View.INVISIBLE);
            pbLogoSpinner.setVisibility(ProgressBar.VISIBLE);
        } else { // it was already fetched from s3
            ivLogo.setImageURI(logoUri);
            pbLogoSpinner.setVisibility(ProgressBar.GONE);
        }

        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompanyProfileActivity.this, EditCompanyProfileActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(downloadfinished);
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            showConfirmAlerter(0);
            return true;
        }
        if (id == R.id.action_delete) {
            showConfirmAlerter(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showConfirmAlerter(int kind) {
        AlertYesNo alert = new AlertYesNo();
        Bundle info = new Bundle();
        if (kind == 0)
            info.putString("message", getResources().getString(R.string.logout_message));
        else info.putString("message", getResources().getString(R.string.delete_user_message));

        info.putString("title", getResources().getString(R.string.confirm));
        info.putInt("kind", kind);
        alert.setCommunicator(this);
        alert.setArguments(info);
        alert.show(getSupportFragmentManager(), "Confirm");

    }

    @Override
    public void goSearch(int kind) {

    }

    @Override
    public void respond(int itemIndex, int kind) {

    }

    @Override
    public void dialogResponse(int result, int kind) {
        if (result == 1) {
            switch (kind) {
                case 0://logout
                    getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1://delete account
                    try {
                        Manager.deleteCompany(Session.getInstance().getCompanyLogged().getId(), new Manager.ResultProcessor<Integer>() {
	                        @Override
	                        public void process(Integer arg, Exception e) {
		                        if (e != null) {
			                        //TODO: show error message
			                        return;
		                        }
		                        getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
		                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
		                        startActivity(i);
		                        finish();
	                        }

	                        @Override
	                        public void cancel() {

	                        }
                        });
                    } catch (DataFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    return;
            }
        }
    }
}
