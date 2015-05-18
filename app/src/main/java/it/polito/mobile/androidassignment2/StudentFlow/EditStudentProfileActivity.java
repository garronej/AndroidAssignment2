package it.polito.mobile.androidassignment2.StudentFlow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.HobbiesCompletionTextView;
import it.polito.mobile.androidassignment2.LinksCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.models.UploadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class EditStudentProfileActivity extends ActionBarActivity  {
    private ImageView ivPhoto;
    private EditText etName;
    private EditText etSurname;
    private LinksCompletionTextView acLinks;
    private Button bCv;
    private EditText etUniversityCareer;
    private ToggleButton tbAvailability;
    private Button bUpdateProfile;
    private Button bCancelUpdateProfile;
    private Uri photoUri;
    private Student loggedStudent;
    private ProgressBar pbPhotoSpinner;
    private ProgressBar pbUpdateSpinner;
    private ProgressBar pbCvSpinner;
    private DownloadFinished downloadfinished = new DownloadFinished();
    private UploadFinished uploadfinished=new UploadFinished();
    private Spinner sSex;
    private EditText etLocation;
    private AsyncTask<Object, Void, Object> task1 = null;
    private AsyncTask<Object, Void, Object> task2 = null;
    private AsyncTask<Object, Void, Object> task3 = null;
    private AsyncTask<Object, Void, Object> task4 = null;
    private CompetencesCompletionTextView acCompetences;
    private HobbiesCompletionTextView acHobbies;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            Uri uri = Uri.parse(filePath);
            Session.getInstance().setPhotoUri(uri);
            ivPhoto.setImageURI(uri);
        }
    }

    public class UploadFinished extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filePath = intent.getStringExtra(UploadModel.EXTRA_FILENAME);
            if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
                try {
                    Session.getInstance().getStudentLogged().setCvUrl(filePath);
                } catch (DataFormatException e) {
                    throw new RuntimeException();
                }
                task1 = Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
                    @Override
                    public void process(Student arg, Exception e) {
                        pbCvSpinner.setVisibility(View.GONE);
                        bCv.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void cancel() {
                    }
                });
            } else { // photo TODO CHECK IT
                try {
                    Session.getInstance().getStudentLogged().setPhotoUrl(filePath);
                } catch (DataFormatException e) {
                    throw new RuntimeException();
                }
                task2 = Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
                    @Override
                    public void process(Student arg, Exception e) {
                        if (e == null) {
                            TransferController.download(getApplicationContext(), new String[]{ filePath });
                        } else {
                            throw new RuntimeException(); //TODO
                        }
                    }
                    @Override
                    public void cancel() {
                    }
                });

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoUri = Session.getInstance().getPhotoUri();
        try {
            loggedStudent = Session.getInstance().getStudentLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException();
        }
        setContentView(R.layout.activity_edit_student_profile);
        findViews();
        setupViewsAndCallbacks();
    }

    private void findViews() {
        ivPhoto = (ImageView) findViewById(R.id.edit_photo_iv);
        etName = (EditText) findViewById(R.id.edit_name_et);
        etSurname = (EditText) findViewById(R.id.edit_surname_et);
        bCv = (Button) findViewById(R.id.edit_cv_b);
        acLinks = (LinksCompletionTextView) findViewById(R.id.edit_links_ac);
        etUniversityCareer = (EditText) findViewById(R.id.edit_university_career_et);
        acCompetences = (CompetencesCompletionTextView) findViewById(R.id.edit_competences_ac);
        tbAvailability = (ToggleButton) findViewById(R.id.edit_availability_tb);
        acHobbies = (HobbiesCompletionTextView) findViewById(R.id.edit_hobbies_ac);
        bUpdateProfile = (Button) findViewById(R.id.edit_update_profile_b);
        bCancelUpdateProfile = (Button) findViewById(R.id.edit_cancel_update_profile_b);
        pbPhotoSpinner = (ProgressBar) findViewById(R.id.edit_photo_pb);
        pbUpdateSpinner = (ProgressBar) findViewById(R.id.edit_update_pb);
        pbCvSpinner = (ProgressBar) findViewById(R.id.edit_cv_pb);
        sSex = (Spinner) findViewById(R.id.edit_sex_s);
        etLocation = (EditText) findViewById(R.id.edit_location_et);
    }

    private void setupViewsAndCallbacks() {
        etName.setText(loggedStudent.getName());
        etSurname.setText(loggedStudent.getSurname());
        etUniversityCareer.setText(loggedStudent.getUniversityCareer());
        ivPhoto.setImageURI(photoUri);
        tbAvailability.setChecked(loggedStudent.isAvailable());
        etLocation.setText(loggedStudent.getLocation());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sSex.setAdapter(adapter);
        String sex = loggedStudent.getSex();
        int position = 0;
        if (sex != null) {
            if (sex.equals("m") || sex.equals("M")) {
                position = 1;
            } else if (sex.equals("f") || sex.equals("F")) {
                position = 2;
            }
        }
        sSex.setSelection(position);

        bUpdateProfile.setFocusableInTouchMode(true);
        bUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pbUpdateSpinner.setVisibility(ProgressBar.VISIBLE);
                    bUpdateProfile.setVisibility(View.INVISIBLE);
                    loggedStudent.setName(etName.getText().toString());
                    loggedStudent.setSurname(etSurname.getText().toString());
                    loggedStudent.setUniversityCareer(etUniversityCareer.getText().toString());

                    if(acCompetences.getObjects().size() > 0){
                        String[] comp = new String[acCompetences.getObjects().size()];
                        int i=0;
                        for(Object o : acCompetences.getObjects()){
                            comp[i]=o.toString();
                            i++;
                        }
                        loggedStudent.setCompetences(comp);
                    } else {
                        loggedStudent.setCompetences(new String[0]);
                    }

                    if(acHobbies.getObjects().size() > 0){
                        String[] hob = new String[acHobbies.getObjects().size()];
                        int i=0;
                        for(Object o : acHobbies.getObjects()){
                            hob[i]=o.toString();
                            i++;
                        }
                        loggedStudent.setHobbies(hob);
                    } else {
                        loggedStudent.setHobbies(new String[0]);
                    }

                    if(acLinks.getObjects().size() > 0){
                        String[] link = new String[acLinks.getObjects().size()];
                        int i=0;
                        for(Object o : acLinks.getObjects()){
                            link[i]=o.toString();
                            i++;
                        }
                        loggedStudent.setLinks(link);
                    } else {
                        loggedStudent.setLinks(new String[0]);
                    }

                    loggedStudent.setAvailable(tbAvailability.isChecked());
                    String s = sSex.getSelectedItem().toString();
                    if (!s.equals("")) {
                        loggedStudent.setSex(s.substring(0, 1).toLowerCase());
                    }
                    loggedStudent.setLocation(etLocation.getText().toString());
                } catch (DataFormatException e) {
                    throw new RuntimeException(e); //TODO
                }
                task3 = Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
                    @Override
                    public void process(Student arg, Exception e) {
                        if (e == null) {
                            Intent i = new Intent(EditStudentProfileActivity.this, StudentProfileActivity.class);
                            startActivity(i);
                        } else {
                            throw new RuntimeException(); //TODO
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
                Intent i = new Intent(EditStudentProfileActivity.this, StudentProfileActivity.class);
                startActivity(i);
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 0);
            }
        });

        bCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 0);
            }
        });

        acCompetences.setPrefix("");
        acCompetences.setText("");
        task4 = Manager.getAllStudentsCompetences(new Manager.ResultProcessor<List<String>>() {
            @Override
            public void process(final List<String> arg, Exception e) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditStudentProfileActivity.this, android.R.layout.simple_list_item_1, arg);
                acCompetences.setAdapter(adapter);
                String[] cs = loggedStudent.getCompetences();
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

        acHobbies.setPrefix("");
        List<String> hobbiesL = new ArrayList<String>();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditStudentProfileActivity.this, android.R.layout.simple_list_item_1, hobbiesL);
        acHobbies.setAdapter(adapter2);
        String[] hs = loggedStudent.getHobbies();
        if (hs != null && hs.length > 0) {
            for (String h : hs) {
                acHobbies.addObject(h);
            }
        }

        acLinks.setPrefix("");
        List<String> linksL = new ArrayList<String>();
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(EditStudentProfileActivity.this, android.R.layout.simple_list_item_1, linksL);
        acLinks.setAdapter(adapter3);
        String[] ls = loggedStudent.getLinks();
        if (ls != null && ls.length > 0) {
            for (String l : ls) {
                acLinks.addObject(l);
            }
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            //TODO: if not pdf or image do something
            if (uri != null) {
                if (uri.toString().indexOf(".pdf") != -1) {
                    bCv.setVisibility(View.INVISIBLE);
                    pbCvSpinner.setVisibility(ProgressBar.VISIBLE);
                } else {
                    pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);
                }
                TransferController.upload(this, uri, "photo/student3");
            }
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
        if(task4 != null){
            task4.cancel(true);
            task4 = null;
        }
        super.onPause();
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
}
