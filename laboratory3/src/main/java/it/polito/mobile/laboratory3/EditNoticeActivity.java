package it.polito.mobile.laboratory3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.laboratory3.Picasso.GalleryGridViewActivity;
import it.polito.mobile.laboratory3.s3client.models.UploadModel;
import it.polito.mobile.laboratory3.s3client.network.TransferController;
import nl.changer.polypicker.ImagePickerActivity;

public class EditNoticeActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etDescription;
    private TagsCompletionTextView acTags;
    private EditText etTelephoneNumber;
    private AutoCompleteTextView acLocation;
    private EditText etSize;
    private EditText etPrice;
    private ActionButton bSubmit;
    private ProgressBar pbSubmit;
    private int noticeId;
    private String role;
    private Notice notice;

    private List<AsyncTask<?, ?, ?>> pendingTasks = new ArrayList<AsyncTask<?, ?, ?>>();
    private List<String> pendingPictures = new ArrayList<String>();

    private static final String TAG = "EditNoticeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notice);
        findViews();
        role = getIntent().getStringExtra("role");
        if (role == null || (!role.equals("create") && !role.equals("update"))) {
            throw new RuntimeException("role param is required and be create or update");
        }

        noticeId = getIntent().getIntExtra("noticeId", -1);
        if (noticeId == -1 && role.equals("update")) {
            throw new RuntimeException("noticeId param is required");
        }

        if (role.equals("update")) {
            AsyncTask<Integer, Void, Notice> t1 = new AsyncTask<Integer, Void, Notice>() {
                Exception e = null;

                @Override
                protected Notice doInBackground(Integer... integers) {
                    Notice notice = null;
                    try {
                        String response = RESTManager.send(RESTManager.GET, "notices/" + noticeId, null);
                        JSONObject obj = (new JSONObject(response));
                        notice = new Notice(obj.getJSONObject("notice"));
                        Log.d(TAG, notice.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.e = e;
                    }

                    return notice;
                }

                @Override
                protected void onPostExecute(Notice notice) {
                    super.onPostExecute(notice);
                    EditNoticeActivity.this.notice = notice;
                    setupViews();
                    setupCallbacks();
                }
            };
            t1.execute(noticeId);
            pendingTasks.add(t1);
        } else { //role == create
            setupViews();
            setupCallbacks();
        }
    }

    private void findViews() {
        etTitle = (EditText) findViewById(R.id.title_et);
        etDescription = (EditText) findViewById(R.id.description_et);
        acTags = (TagsCompletionTextView) findViewById(R.id.tags_ac);
        etTelephoneNumber = (EditText) findViewById(R.id.telephone_et);
        acLocation = (AutoCompleteTextView) findViewById(R.id.location_ac);
        etSize = (EditText) findViewById(R.id.size_et);
        etPrice = (EditText) findViewById(R.id.price_et);
        bSubmit = (ActionButton) findViewById(R.id.submit_b);
        pbSubmit = (ProgressBar) findViewById(R.id.submit_pb);

    }

    private void setupViews() {
        pbSubmit.setVisibility(View.GONE);
        location_autocomplete();

        if (role.equals("update")) {
            //bSubmit.setText(R.string.update);

            String title = notice.getTitle();
            if (title != null && !title.equals("")) {
                etTitle.setText(title);
            }

            String description = notice.getDescription();
            if (description != null && !description.equals("")) {
                etDescription.setText(description);
            }

            List<String> tagsL = new ArrayList<String>();
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(EditNoticeActivity.this, android.R.layout.simple_list_item_1, tagsL);
            acTags.setAdapter(adapter2);
            String[] hs = notice.getTags();
            if (hs != null && hs.length > 0) {
                for (String h : hs) {
                    acTags.addObject(h);
                }
            }

            String telNum = notice.getTelephone();
            if (telNum != null && !telNum.equals("")) {
                etTelephoneNumber.setText(telNum);
            }

            String location = notice.getAddress();
            if (location != null && !location.equals("")) {
                acLocation.setText(location);
            }

            int size = notice.getSize();
            if (size != 0) {
                etSize.setText(String.valueOf(size));
            }

            double price = notice.getPrice();
            if (price != 0.0) {
                etPrice.setText(String.valueOf(price));
            }
        } else { // role == create
            //bSubmit.setText(R.string.create);
        }
    }

    private void setupCallbacks() {
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();

            }
        });
    }

    private void submit() {
        pbSubmit.setVisibility(View.VISIBLE);
        bSubmit.setVisibility(View.INVISIBLE);

        final Map<String, String> params = new HashMap<String, String>();

        String title = etTitle.getText().toString().trim();
        params.put("notice[title]", title);

        String description = etDescription.getText().toString().trim();
        params.put("notice[description]", description);

        if (acTags.getObjects().size() > 0) {
            int i = 0;
            for (Object o : acTags.getObjects()) {
                params.put("notice[tags][" + i + "]", o.toString());
                i++;
            }
        } else {
            params.put("notice[tags][0]", "");
        }

        String telephone = etTelephoneNumber.getText().toString().trim();
        params.put("notice[telephone_number]", telephone);

        String location = acLocation.getText().toString().trim();
        params.put("notice[full_location]", location);

        String size = etSize.getText().toString().trim();
        if (!size.equals("")) {
            params.put("notice[size]", size);
        } else {
            params.put("notice[size]", "0");
        }

        String price = etPrice.getText().toString().trim();
        if (!price.equals("")) {
            params.put("notice[price]", price);
        } else {
            params.put("notice[price]", "0");
        }

        params.put("notice[student_id]", String.valueOf(LoggedStudent.getId()));
        params.put("notice[type_of_notice]", "rent");

        Log.d(TAG, params.toString());

        boolean validationErrors = false;

        if (title.equals("")) {
            validationErrors = true;
            etTitle.setError(getResources().getString(R.string.error_field_required));
        }

        if (location.equals("")) {
            validationErrors = true;
            acLocation.setError(getResources().getString(R.string.error_field_required));
        }

        if (validationErrors) {
            Toast.makeText(EditNoticeActivity.this, R.string.notice_validation_failed, Toast.LENGTH_LONG).show();
            pbSubmit.setVisibility(View.GONE);
            bSubmit.setVisibility(View.VISIBLE);
        } else {
            AsyncTask<Void, Void, String> t = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    String response = null;
                    try {
                        if (role.equals("update")) {
                            response = RESTManager.send(RESTManager.PUT, "notices/" + noticeId, params);
                        } else {
                            response = RESTManager.send(RESTManager.POST, "notices/", params);
                        }
                    } catch (Exception e) {
                        Toast.makeText(EditNoticeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                    return response;
                }

                @Override
                protected void onPostExecute(String response) {
                    super.onPostExecute(response);
                    Log.d(TAG, response);
                    pbSubmit.setVisibility(View.GONE);
                    bSubmit.setVisibility(View.VISIBLE);
                    if (role.equals("create")) {
                        JSONObject obj = null;
                        int noticeId = -1;
                        try {
                            obj = (new JSONObject(response));
                            JSONObject notice = obj.getJSONObject("notice");
                            noticeId = Integer.valueOf(notice.getString("id"));
                        } catch (JSONException e) {
                            throw new RuntimeException();
                        }

                        Intent i = new Intent(EditNoticeActivity.this, ShowNoticeActivity.class);
                        i.putExtra("noticeId", noticeId);
                        startActivity(i);

                    }
                    finish();
                    overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top);

                }
            };
            t.execute();
            pendingTasks.add(t);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_top);

    }

    @Override
    protected void onPause() {
        for(AsyncTask<?,?,?> t : pendingTasks){
            t.cancel(true);
        }
        pendingTasks.clear();
        super.onPause();
    }

    private void location_autocomplete() {
        acLocation.setAdapter(new PlacesAutoCompleteAdapter(EditNoticeActivity.this, R.layout.location_list_item));

        acLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(EditNoticeActivity.this, description, Toast.LENGTH_SHORT).show();
            }
        });
    }

}