package it.polito.mobile.androidassignment2.StudentFlow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.HobbiesCompletionTextView;
import it.polito.mobile.androidassignment2.LinksCompletionTextView;
import it.polito.mobile.androidassignment2.PlacesAutoCompleteAdapter;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.Utils;
import it.polito.mobile.androidassignment2.businessLogic.Career;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.customView.CareerEditableLayout;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.models.UploadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class EditStudentProfileActivity extends AppCompatActivity {
    private ImageView ivPhoto;
    private EditText etName;
    private EditText etSurname;
    private LinksCompletionTextView acLinks;
    private Button bCv;
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
    private static final int PICK_PICTURE = 0;
    private static final int PICK_PDF = 1;
    private EditText birthDate;
    private LinearLayout llUniversityCareer;
    private ArrayAdapter<String> careersAdapter;
    private AsyncTask<Object, Void, Object> task5;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            Uri uri = Uri.parse(filePath);
            ((AppContext)getApplication()).getSession().setPhotoUri(uri);
            ivPhoto.setImageURI(uri);
        }
    }

    public class UploadFinished extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filePath = intent.getStringExtra(UploadModel.EXTRA_FILENAME);
            if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
                try {
                    ((AppContext)getApplication()).getSession().getStudentLogged().setCvUrl(filePath);
                } catch (DataFormatException e) {
                    throw new RuntimeException();
                }
                task1 = Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
                    @Override
                    public void process(Student arg, Exception e) {
                        task1=null;
                        if (e == null) {
                            pbCvSpinner.setVisibility(View.GONE);
                            bCv.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(EditStudentProfileActivity.this, it.polito.mobile.androidassignment2.businessLogic.Utils.processException(e, "Error message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void cancel() {
                        pbCvSpinner.setVisibility(View.GONE);
                        bCv.setVisibility(View.VISIBLE);
                        task1 = null;
                    }
                });
            } else { // photo check is implicit
                try {
                    ((AppContext)getApplication()).getSession().getStudentLogged().setPhotoUrl(filePath);
                } catch (DataFormatException e) {
                    throw new RuntimeException();
                }
                task2 = Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
                    @Override
                    public void process(Student arg, Exception e) {
                        task2=null;
                        if (e == null) {
                            TransferController.download(getApplicationContext(), new String[]{filePath});
                        } else {
                            Toast.makeText(EditStudentProfileActivity.this, it.polito.mobile.androidassignment2.businessLogic.Utils.processException(e, "Error message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void cancel() {
                        task2 = null;
                    }
                });

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoUri = ((AppContext)getApplication()).getSession().getPhotoUri();
        try {
            loggedStudent = ((AppContext)getApplication()).getSession().getStudentLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException();
        }
        setContentView(R.layout.activity_edit_student_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();

    }

    private void findViews() {
        ivPhoto = (ImageView) findViewById(R.id.edit_photo_iv);
        etName = (EditText) findViewById(R.id.edit_name_et);
        etSurname = (EditText) findViewById(R.id.edit_surname_et);
        bCv = (Button) findViewById(R.id.edit_cv_b);
        acLinks = (LinksCompletionTextView) findViewById(R.id.edit_links_ac);
        llUniversityCareer = (LinearLayout) findViewById(R.id.university_career);
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

        birthDate = (EditText) findViewById(R.id.birth_date);

    }

    private void setupViewsAndCallbacks() {
        careersAdapter = new ArrayAdapter<String>(EditStudentProfileActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        etName.setText(loggedStudent.getName());
        etSurname.setText(loggedStudent.getSurname());
        Career[] universityCareers = loggedStudent.getUniversityCareers();
        llUniversityCareer.removeAllViews();
        if (universityCareers != null) {
            for(int i=0;i<universityCareers.length;i++){

                CareerEditableLayout cView = new CareerEditableLayout(this);
                cView.initializeValues(universityCareers[i]);
                cView.setCareerTitleAdapter(careersAdapter);
                llUniversityCareer.addView(cView);
                if(i!=universityCareers.length-1){
                    View v = new View(this);
                    v.setBackgroundDrawable(getResources().getDrawable(R.drawable.items_divider));
                    v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    llUniversityCareer.addView(v);
                }
            }
        }

        findViewById(R.id.add_career).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llUniversityCareer.getChildCount()>0){
                    View div = new View(EditStudentProfileActivity.this);
                    div.setBackgroundDrawable(getResources().getDrawable(R.drawable.items_divider));
                    div.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    llUniversityCareer.addView(div);
                }

                CareerEditableLayout cView = new CareerEditableLayout(EditStudentProfileActivity.this);
                cView.setCareerTitleAdapter(careersAdapter);
                llUniversityCareer.addView(cView);

            }
        });
        ivPhoto.setImageURI(photoUri);
        tbAvailability.setChecked(loggedStudent.isAvailable());
        etLocation.setText(loggedStudent.getLocation());
        birthDate.setText(loggedStudent.getBirthDate());
        birthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                    String[] ss = birthDate.getText().toString().split("-");
                    Calendar cal = Calendar.getInstance();
                    int aaaa=cal.get(Calendar.YEAR);
                    int mm = cal.get(Calendar.MONTH);
                    int gg = cal.get(Calendar.DAY_OF_MONTH);

                    if(ss.length==3){
                        aaaa=Integer.parseInt(ss[0]);
                        mm=Integer.parseInt(ss[1])-1;
                        gg=Integer.parseInt(ss[2]);
                    }

                    DatePickerDialog dp = new DatePickerDialog(EditStudentProfileActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    birthDate.setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                                }
                            },aaaa,mm,gg);

                    dp.show();
                }
            }
        });

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

                    loggedStudent.setBirthDate(birthDate.getText().toString());
                    List<Career> careers = new ArrayList<Career>();
                    for (int i = 0; i < llUniversityCareer.getChildCount(); i++) {
                        View child = llUniversityCareer.getChildAt(i);

                        if (child instanceof CareerEditableLayout) {
                            CareerEditableLayout careerLayout = (CareerEditableLayout) child;
                            Career c = new Career();
                            c.setCareer(careerLayout.getCareerTitle());
                            String mString = careerLayout.getGraduationMark();
                            if (!mString.isEmpty()) {
                                c.setMark(Integer.parseInt(mString));
                                if(c.getMark()==110 && careerLayout.isSetLaude()){
                                    c.setMark(111);
                                }
                            }
                            c.setDate(careerLayout.getDate());
                            careers.add(c);
                        }

                    }

                    loggedStudent.setUniversityCareer(careers.toArray(new Career[0]));

                    if (acCompetences.getObjects().size() > 0) {
                        String[] comp = new String[acCompetences.getObjects().size()];
                        int i = 0;
                        for (Object o : acCompetences.getObjects()) {
                            comp[i] = o.toString();
                            i++;
                        }
                        loggedStudent.setCompetences(comp);
                    } else {
                        loggedStudent.setCompetences(new String[0]);
                    }

                    if (acHobbies.getObjects().size() > 0) {
                        String[] hob = new String[acHobbies.getObjects().size()];
                        int i = 0;
                        for (Object o : acHobbies.getObjects()) {
                            hob[i] = o.toString();
                            i++;
                        }
                        loggedStudent.setHobbies(hob);
                    } else {
                        loggedStudent.setHobbies(new String[0]);
                    }

                    if (acLinks.getObjects().size() > 0) {
                        String[] link = new String[acLinks.getObjects().size()];
                        int i = 0;
                        for (Object o : acLinks.getObjects()) {
                            link[i] = o.toString();
                            i++;
                        }
                        loggedStudent.setLinks(link);
                    } else {
                        //When you have no data you should put null unless you have a reason not to do so
                        loggedStudent.setLinks(new String[0]);
                    }

                    loggedStudent.setAvailable(tbAvailability.isChecked());
                    String s = sSex.getSelectedItem().toString();
                    if (!s.equals("")) {
                        loggedStudent.setSex(s.substring(0, 1).toLowerCase());
                    }
                    loggedStudent.setLocation(etLocation.getText().toString());
                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                }
                task3 = Manager.updateStudent(loggedStudent, new Manager.ResultProcessor<Student>() {
                    @Override
                    public void process(Student arg, Exception e) {
                        task3 = null;
                        if (e == null) {
                            Intent i = new Intent(EditStudentProfileActivity.this, StudentProfileActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(EditStudentProfileActivity.this, it.polito.mobile.androidassignment2.businessLogic.Utils.processException(e, "Error message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void cancel() {
                        pbUpdateSpinner.setVisibility(ProgressBar.INVISIBLE);
                        task3 = null;
                    }
                });
            }
        });
        bCancelUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditStudentProfileActivity.this, StudentProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_photo)), PICK_PICTURE);
            }
        });

        bCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_pdf)), PICK_PDF);

            }
        });

        task4 = Manager.getAllStudentsCompetences(new Manager.ResultProcessor<List<String>>() {
            @Override
            public void process(final List<String> arg, Exception e) {
                task4=null;
                if (e == null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditStudentProfileActivity.this, android.R.layout.simple_list_item_1, arg);
                    acCompetences.setAdapter(adapter);
                    String[] cs = loggedStudent.getCompetences();
                    if (cs != null && cs.length > 0) {
                        for (String c : cs) {
                            acCompetences.addObject(c);
                        }
                    }
                } else {
                    Toast.makeText(EditStudentProfileActivity.this, it.polito.mobile.androidassignment2.businessLogic.Utils.processException(e, "Error message"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {
                task4 = null;
            }
        });

        task5=Manager.getAllCareers(new Manager.ResultProcessor<List<String>>() {
            @Override
            public void process(List<String> arg, Exception e) {
                task5=null;
                if(e!=null) return;
                careersAdapter.addAll(arg);
                careersAdapter.notifyDataSetChanged();
            }

            @Override
            public void cancel() {
                task5=null;
            }
        });

        List<String> hobbiesL = new ArrayList<String>();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditStudentProfileActivity.this, android.R.layout.simple_list_item_1, hobbiesL);
        acHobbies.setAdapter(adapter2);
        String[] hs = loggedStudent.getHobbies();
        if (hs != null && hs.length > 0) {
            for (String h : hs) {
                acHobbies.addObject(h);
            }
        }

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
    private void location_autocomplete() {
        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.edit_location_et);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.location_list_item));

        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(EditStudentProfileActivity.this, description, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (reqCode == PICK_PICTURE) {
                onActivityResultForPicture(uri);
            } else if (reqCode == PICK_PDF) {
                onActivityResultForPdf(uri);
            } else {
                throw new RuntimeException("missing required param");
            }
        }
    }

    private void onActivityResultForPicture(Uri uri) {
        if (Utils.isPicture(EditStudentProfileActivity.this, uri)) {
            pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);
            TransferController.upload(this, uri, "photo/student3");
        } else {
            Toast t = Toast.makeText(this, getResources().getString(R.string.select_photo), Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    private void onActivityResultForPdf(Uri uri) {
        if (Utils.isPdf(EditStudentProfileActivity.this, uri)) {
            bCv.setVisibility(View.INVISIBLE);
            pbCvSpinner.setVisibility(ProgressBar.VISIBLE);
            TransferController.upload(this, uri, "photo/student3");
        } else {
            Toast t = Toast.makeText(this, getResources().getString(R.string.select_pdf), Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(uploadfinished, new IntentFilter(UploadModel.INTENT_UPLOADED));
        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
        setupViewsAndCallbacks();
        location_autocomplete();
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
        if(task5 != null){
            task5.cancel(true);
            task5 = null;
        }
        super.onPause();
    }


}
