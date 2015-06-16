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

import org.json.JSONArray;
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
	private Button bSubmit;
    private ProgressBar pbSubmit;

    private List<AsyncTask<?, ?, ?>> pendingTasks = new ArrayList<AsyncTask<?, ?, ?>>();
	private static final String TAG = "EditNoticeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_notice);
		findViews();

		final int noticeId = getIntent().getIntExtra("noticeId", -1);
		if (noticeId == -1) { throw new RuntimeException("noticeId param is required"); }

		AsyncTask<Integer, Void, Notice> t1 = new AsyncTask<Integer, Void, Notice>() {
			Exception e=null;

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
					this.e=e;
				}

				return notice;
			}
			@Override
			protected void onPostExecute(Notice notice) {
				super.onPostExecute(notice);
                setupViews(notice);
                setupCallbacks(notice);
			}
		};
		t1.execute(noticeId);
        pendingTasks.add(t1);
	}

    private void findViews() {
        etTitle = (EditText) findViewById(R.id.title_et);
        etDescription = (EditText) findViewById(R.id.description_et);
        acTags = (TagsCompletionTextView) findViewById(R.id.tags_ac);
        etTelephoneNumber = (EditText) findViewById(R.id.telephone_et);
        acLocation = (AutoCompleteTextView) findViewById(R.id.location_ac);
        etSize = (EditText) findViewById(R.id.size_et);
        etPrice = (EditText) findViewById(R.id.price_et);
        bSubmit = (Button) findViewById(R.id.submit_b);
        pbSubmit = (ProgressBar) findViewById(R.id.submit_pb);
    }

    private void setupViews(Notice notice) {
        pbSubmit.setVisibility(View.GONE);
        bSubmit.setText(R.string.update);

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

        location_autocomplete();

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
    }

    private void setupCallbacks(final Notice notice) {
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
        params.put("notice[location]", location);

        String size = etSize.getText().toString().trim();
        params.put("notice[size]", size);

        String price = etPrice.getText().toString().trim();
        params.put("notice[price]", price);

        Log.d(TAG, params.toString());

        AsyncTask<Void, Void, String> t = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    RESTManager.send(RESTManager.PUT, "notices/" + 25, params);
                } catch (Exception e) {
                    Toast.makeText(EditNoticeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pbSubmit.setVisibility(View.GONE);
                bSubmit.setVisibility(View.VISIBLE);
            }
        };
        t.execute();
        pendingTasks.add(t);
    }


	@Override
	protected void onResume() {
		super.onResume();
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