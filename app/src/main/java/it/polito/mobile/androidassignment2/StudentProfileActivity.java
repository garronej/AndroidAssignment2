package it.polito.mobile.androidassignment2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class StudentProfileActivity extends ActionBarActivity  {
    private ImageView ivPhoto;
    private ProgressBar pbPhotoSpinner;
    private TextView tvFullname;
    private TextView tvLinks;
    private TextView bCv;
    private TextView tvEmail;
    private TextView tvUniversityCareer;
    private TextView tvCompetences;
    private Button bAvailability;
    private TextView tvHobbies;
    private DownloadFinished downloadfinished = new DownloadFinished();
    private String photoUrl;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(filePath), "application/pdf");
                Log.d("onReceive", "pdf intent");
                startActivity(i);
            } else { // photo
                photoUrl = filePath;
                ivPhoto.setImageURI(Uri.parse(filePath));
                tvFullname.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            //to avoid fetching again on rotation
            photoUrl = savedInstanceState.getString("photoUrl");
        }
        setContentView(R.layout.activity_student_profile);
        findViews();
        setupViewsAndCallbacks();
    }

    private void findViews() {
        tvEmail = (TextView) findViewById(R.id.email);
        ivPhoto = (ImageView) findViewById(R.id.photo);
        pbPhotoSpinner = (ProgressBar) findViewById(R.id.photo_spinner);
        tvFullname = (TextView) findViewById(R.id.fullname);
        bCv = (TextView) findViewById(R.id.cv_button);
        tvLinks = (TextView) findViewById(R.id.links);
        tvUniversityCareer = (TextView) findViewById(R.id.university_career);
        tvCompetences = (TextView) findViewById(R.id.competences);
        bAvailability = (Button) findViewById(R.id.availability);
        tvHobbies = (TextView) findViewById(R.id.hobbies);
    }

    private void setupViewsAndCallbacks() {
        final Student loggedStudent = this.getStubbedStudentLogged();//TODO REMOVE

        tvFullname.setText(loggedStudent.getFullname());
        tvEmail.setText(loggedStudent.getEmail());
        tvLinks.setText(loggedStudent.getLinksToString());
        tvUniversityCareer.setText(loggedStudent.getUniversityCareer());
        tvCompetences.setText(loggedStudent.getCompetencesToString());
        tvHobbies.setText(loggedStudent.getHobbiesToString());

        if (photoUrl == null) { //need to get from s3
            String url = loggedStudent.getPhotoUrl();
            TransferController.download(getApplicationContext(), new String[]{ url });
            tvFullname.setVisibility(View.INVISIBLE);
            pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);
        } else { // it was already fetched from s3
            ivPhoto.setImageURI(Uri.parse(photoUrl));
            pbPhotoSpinner.setVisibility(ProgressBar.GONE);
        }

        bCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cvUrl = loggedStudent.getCvUrl();
                TransferController.download(getApplicationContext(), new String[]{ cvUrl });
            }
        });

        boolean isAvailable = loggedStudent.isAvailable();
        if (isAvailable) {
            bAvailability.setText(getResources().getString(R.string.available_for_jobs));
            bAvailability.setBackgroundColor(getResources().getColor(R.color.green_ok));
        } else {
            bAvailability.setText(getResources().getString(R.string.not_available_for_jobs));
            bAvailability.setBackgroundColor(getResources().getColor(R.color.red_warning));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (photoUrl != null) {
            outState.putString("photoUrl", photoUrl);
        }
        super.onSaveInstanceState(outState);
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

    public static Student getStubbedStudentLogged() {
        Student s = null;
        try {
            s = new Student();
            s.setEmail("Joseph.garrOne.gj@gmail.com");
            s.setName("GaRRone");
            s.setSurname("Joseph");
            s.setPassword("stupid2");
            s.setCvUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/esercitazione2.pdf");
            s.setUniversityCareer("computer engineering");
            s.setAvailable(false);
            s.setCompetences(new String[]{"porn knowelage", "fast masturbation"});
            s.setHobbies(new String[]{"porn", "masturbation"});
            s.setLinks(new URL[]{new URL("http://seedbox.garrone.org"), new URL("http://etophy.fr")});
            s.setPhotoUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/jos.png");
            return s;
        } catch(Exception e) {}
        return s;
    }
}
