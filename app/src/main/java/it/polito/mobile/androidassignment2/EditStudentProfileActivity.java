package it.polito.mobile.androidassignment2;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

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
    private EditText etLinks;
    private Button bCv;
    private EditText etEmail;
    private EditText etUniversityCareer;
    private EditText etCompetences;
    private ToggleButton tbAvailability;
    private EditText etHobbies;
    private Button bUpdateProfile;
    private Button bCancelUpdateProfile;
    private Uri photoUri;
    private Student loggedStudent;
    private ProgressBar pbPhotoSpinner;
    private UploadFinished uploadfinished=new UploadFinished();
    private Spinner sSex;
    private EditText etLocation;

    public class UploadFinished extends BroadcastReceiver{
        public String fname="";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("poliJob", "Intent about finished upload of "+intent.getStringExtra(UploadModel.EXTRA_FILENAME));
            String filePath = intent.getStringExtra(UploadModel.EXTRA_FILENAME);
            if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
                try {
                    Session.getInstance().getStudentLogged().setCvUrl(filePath);
                } catch (DataFormatException e) {
                    throw new RuntimeException();
                }
                Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
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
                    }
                });
            } else { // photo
                Session.getInstance().setPhotoUri(null); //invalidate photo so that profile activity loads the new one
                try {
                    Session.getInstance().getStudentLogged().setPhotoUrl(filePath);
                } catch (DataFormatException e) {
                    throw new RuntimeException();
                }
                Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
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
        etEmail = (EditText) findViewById(R.id.edit_email_et);
        ivPhoto = (ImageView) findViewById(R.id.edit_photo_iv);
        etName = (EditText) findViewById(R.id.edit_name_et);
        etSurname = (EditText) findViewById(R.id.edit_surname_et);
        bCv = (Button) findViewById(R.id.edit_cv_b);
        etLinks = (EditText) findViewById(R.id.edit_links_et);
        etUniversityCareer = (EditText) findViewById(R.id.edit_university_career_et);
        etCompetences = (EditText) findViewById(R.id.edit_competences_et);
        tbAvailability = (ToggleButton) findViewById(R.id.edit_availability_tb);
        etHobbies = (EditText) findViewById(R.id.edit_hobbies_et);
        bUpdateProfile = (Button) findViewById(R.id.edit_update_profile_b);
        bCancelUpdateProfile = (Button) findViewById(R.id.edit_cancel_update_profile_b);
        pbPhotoSpinner = (ProgressBar) findViewById(R.id.edit_photo_pb);
        sSex = (Spinner) findViewById(R.id.edit_sex_s);
        etLocation = (EditText) findViewById(R.id.edit_location_et);
    }

    private void setupViewsAndCallbacks() {
        etName.setText(loggedStudent.getName());
        etSurname.setText(loggedStudent.getSurname());
        etEmail.setText(loggedStudent.getEmail());
        etLinks.setText(loggedStudent.getLinksToString(", "));
        etUniversityCareer.setText(loggedStudent.getUniversityCareer());
        etCompetences.setText(loggedStudent.getCompetencesToString(", "));
        etHobbies.setText(loggedStudent.getHobbiesToString(", "));
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

        bUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loggedStudent.setName(etName.getText().toString());
                    loggedStudent.setSurname(etSurname.getText().toString());
                    loggedStudent.setEmail(etEmail.getText().toString());
                    String links = etLinks.getText().toString();
                    if (!links.equals("")) {
                        String[] linksA = links.split(",");
                        List<URL> urls = new ArrayList<URL>();
                        for (String s : linksA) {
                            try {
                                URL u = new URL(s.trim());
                                urls.add(u);
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(); //TODO
                            }
                        }
                        URL[] urlsA = new URL[urls.size()];
                        loggedStudent.setLinks(urls.toArray(urlsA));
                    }
                    loggedStudent.setUniversityCareer(etUniversityCareer.getText().toString());
                    String competences = etCompetences.getText().toString();
                    if (!competences.equals("")) {
                        loggedStudent.setCompetences(competences.split(","));
                    }
                    String hobbies = etHobbies.getText().toString();
                    if (!competences.equals("")) {
                        loggedStudent.setHobbies(hobbies.split(","));
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
                Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
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
                intent.setType("*/*");
                startActivityForResult(intent, 0);
            }
        });


    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            //TODO: if not pdf or image do something
            if (uri != null) {
                pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);
                TransferController.upload(this, uri, "photo/student3");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(uploadfinished, new IntentFilter(UploadModel.INTENT_UPLOADED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(uploadfinished);
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
