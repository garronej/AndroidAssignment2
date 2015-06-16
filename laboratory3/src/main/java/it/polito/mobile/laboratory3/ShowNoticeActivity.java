package it.polito.mobile.laboratory3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowNoticeActivity extends AppCompatActivity {

	private TextView tvTitle;
	private TextView tvDescription;
	private TextView tvTags;
	private TextView tvCreatedOn;
	private TextView tvTelephoneNumber;
	private TextView tvEmail;
	private TextView tvInappropriate;
    private TextView tvLocation;
	private Button bSize;
	private Button bPrice;
	private Button bOpenGallery;

    private List<AsyncTask<?, ?, ?>> pendingTasks = new ArrayList<AsyncTask<?, ?, ?>>();

	private static final String TAG = "ShowNoticeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_notice);
		findViews();

		int noticeId = getIntent().getIntExtra("noticeId", -1);
		//if (noticeId == -1) { throw new RuntimeException("noticeId param is required"); }; TODO

		AsyncTask<Integer, Void, Notice> t1 = new AsyncTask<Integer, Void, Notice>() {
			Exception e=null;

			@Override
			protected Notice doInBackground(Integer... integers) {
				Notice notice = null;
				try {
					String response = RESTManager.send(RESTManager.GET, "notices/" + 14, null);
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
        tvTitle = (TextView) findViewById(R.id.title_tv);
        tvDescription = (TextView) findViewById(R.id.description_tv);
        tvTags = (TextView) findViewById(R.id.tags_tv);
        tvCreatedOn = (TextView) findViewById(R.id.created_on_tv);
        tvTelephoneNumber = (TextView) findViewById(R.id.telephone_number_tv);
        tvLocation = (TextView) findViewById(R.id.location_tv);
        tvEmail = (TextView) findViewById(R.id.email_tv);
        tvInappropriate = (TextView) findViewById(R.id.inappropriate_count_tv);
        bSize = (Button) findViewById(R.id.size_b);
        bPrice = (Button) findViewById(R.id.price_b);
        bOpenGallery = (Button) findViewById(R.id.gallery_b);
    }

    private void setupViews(Notice notice) {
        String title = notice.getTitle();
        if (title != null && !title.equals("")) {
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        String description = notice.getDescription();
        if (description != null && !description.equals("")) {
            tvDescription.setText(description);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        String tags = notice.getTagsToString(", ");
        if (tags == null) {
            tvTags.setVisibility(View.GONE);
        } else {
            tvTags.setText(tags);
        }

        String createdOn = notice.getCreationDate();
        if (createdOn != null && !createdOn.equals("")) {
            tvCreatedOn.setText(createdOn);
        } else {
            tvCreatedOn.setVisibility(View.GONE);
        }

        String telNum = notice.getTelephone();
        if (telNum != null && !telNum.equals("")) {
            tvTelephoneNumber.setText(telNum);
        } else {
            tvTelephoneNumber.setVisibility(View.GONE);
        }

        String location = notice.getAddress();
        if (location != null && !location.equals("")) {
            tvLocation.setText(location);
        } else {
            tvLocation.setVisibility(View.GONE);
        }

        //tvEmail TODO

        tvInappropriate.setText(String.valueOf(notice.getCountInappropriate())); // it's at least 0

        // TODO what if size == null?
        bSize.setText(notice.getSize() + "mq");

        // TODO what if price == null?
        bPrice.setText(String.valueOf(notice.getPrice()) + "€");

        String[] pictures = notice.getPictures();
        if (pictures == null || pictures.length == 0) {
            bOpenGallery.setVisibility(View.GONE);
        }
    }

    private void setupCallbacks(final Notice notice) {
        bOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowNoticeActivity.this, GalleryActivity.class);
                Bundle b = new Bundle();
                //b.putStringArray("picturesUrlsArray", notice.getPictures()); TODO
                String[] urls = {};
                b.putStringArray("picturesUrlsArray", urls);

                startActivity(i);
            }
        });
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
}