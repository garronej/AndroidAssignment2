package it.polito.mobile.androidassignment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.StudentFlow.CompaniesFavouritesActivity;
import it.polito.mobile.androidassignment2.StudentFlow.SearchCompanies;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class StudentProfileActivity extends ActionBarActivity  {
    private ImageView ivPhoto;
    private ProgressBar pbPhotoSpinner;
    private ProgressBar pbCvSpinner;
    private TextView tvFullname;
    private TextView tvLinks;
    private Button bCv;
    private TextView tvEmail;
    private TextView tvUniversityCareer;
    private TextView tvCompetences;
    private Button bAvailability;
    private TextView tvHobbies;
    private DownloadFinished downloadfinished = new DownloadFinished();
    private Uri photoUri;
    private Button bEditProfile;
    private Button bSex;
    private TextView tvLocation;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(filePath), "application/pdf");
                bCv.setVisibility(View.VISIBLE);
                pbCvSpinner.setVisibility(View.INVISIBLE);
                startActivity(i);
            } else { // photo
                pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
                photoUri = Uri.parse(filePath);
                Session.getInstance().setPhotoUri(photoUri);
                ivPhoto.setImageURI(photoUri);
                tvFullname.setVisibility(View.VISIBLE);
                bCv.setEnabled(true);
                bEditProfile.setEnabled(true);
            }
        }
    }

    private void addTabMenuButtonCallbacks(){
        findViewById(R.id.tab_menu_student_search).setVisibility(View.INVISIBLE);
        findViewById(R.id.tab_menu_student_companies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CompaniesFavouritesActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.tab_menu_student_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Intent i = new Intent(getApplicationContext(), );
                //startActivity(i);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.getInstance().getPhotoUri() != null) {
            photoUri = Session.getInstance().getPhotoUri();
        }
        setContentView(R.layout.activity_student_profile);
        findViews();
        setupViewsAndCallbacks();
        addTabMenuButtonCallbacks();
    }

    private void findViews() {
        tvEmail = (TextView) findViewById(R.id.email);
        ivPhoto = (ImageView) findViewById(R.id.photo);
        pbPhotoSpinner = (ProgressBar) findViewById(R.id.photo_spinner);
        pbCvSpinner = (ProgressBar) findViewById(R.id.cv_pb);
        tvFullname = (TextView) findViewById(R.id.fullname);
        bCv = (Button) findViewById(R.id.cv_button);
        tvLinks = (TextView) findViewById(R.id.links);
        tvUniversityCareer = (TextView) findViewById(R.id.university_career);
        tvCompetences = (TextView) findViewById(R.id.competences);
        bAvailability = (Button) findViewById(R.id.availability);
        tvHobbies = (TextView) findViewById(R.id.hobbies);
        bEditProfile = (Button) findViewById(R.id.edit_profile_button);
        bSex = (Button) findViewById(R.id.sex_b);
        tvLocation = (TextView) findViewById(R.id.location_tv);
    }

    private void setupViewsAndCallbacks() {
        final Student loggedStudent;
        try {
            loggedStudent = Session.getInstance().getStudentLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException();
        }
        tvEmail.setText(loggedStudent.getEmail());
        pbCvSpinner.setVisibility(View.INVISIBLE);

        String fullname = loggedStudent.getFullname();
        if (fullname == null) {
            tvFullname.setVisibility(View.GONE);
        } else {
            tvFullname.setText(loggedStudent.getFullname());
        }

        String links = loggedStudent.getLinksToString(System.getProperty("line.separator")
                + System.getProperty("line.separator"));
        if (links == null) {
            tvLinks.setVisibility(View.GONE);
        } else {
            tvLinks.setText(links);
        }

        String universityCareer = loggedStudent.getUniversityCareer();
        if (universityCareer == null) {
            tvUniversityCareer.setVisibility(View.GONE);
        } else {
            tvUniversityCareer.setText(universityCareer);
        }

        String competences = loggedStudent.getCompetencesToString(", ");
        if (competences == null) {
            tvCompetences.setVisibility(View.GONE);
        } else {
            tvCompetences.setText(competences);
        }

        String hobbies = loggedStudent.getHobbiesToString(", ");
        if (hobbies == null) {
            tvHobbies.setVisibility(View.GONE);
        } else {
            tvHobbies.setText(hobbies);
        }

        String sex = loggedStudent.getSex();
        if (sex == null) {
            bSex.setVisibility(View.GONE);
        } else {
            bSex.setText(sex);
        }

        String location = loggedStudent.getLocation();
        if (location == null || location.equals("")) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setText(location);
        }

        String url = loggedStudent.getPhotoUrl();
        if (photoUri == null) { //need to get from s3
            TransferController.download(getApplicationContext(), new String[]{url});
            bCv.setEnabled(false);
            bEditProfile.setEnabled(false);
            tvFullname.setVisibility(View.INVISIBLE);
            pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);
        } else { // it was already fetched from s3
            ivPhoto.setImageURI(photoUri);
            pbPhotoSpinner.setVisibility(ProgressBar.GONE);
        }

        final String cvUrl = loggedStudent.getCvUrl();
        if (cvUrl == null) {
            bCv.setVisibility(View.GONE);
        } else {
            bCv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransferController.download(getApplicationContext(), new String[]{ cvUrl });
                    pbCvSpinner.setVisibility(View.VISIBLE);
                    bCv.setVisibility(View.INVISIBLE);
                }
            });
        }

        boolean isAvailable = loggedStudent.isAvailable();
        if (isAvailable) {
            bAvailability.setText(getResources().getString(R.string.available_for_jobs));
            bAvailability.setBackgroundColor(getResources().getColor(R.color.green_ok));
        } else {
            bAvailability.setText(getResources().getString(R.string.not_available_for_jobs));
            bAvailability.setBackgroundColor(getResources().getColor(R.color.red_warning));
        }

        bEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StudentProfileActivity.this, EditStudentProfileActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fake_activity_s3_test, menu);
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
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(downloadfinished);
        super.onPause();
    }
}
