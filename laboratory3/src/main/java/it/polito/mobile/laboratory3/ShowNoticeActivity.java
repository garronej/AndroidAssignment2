package it.polito.mobile.laboratory3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.mobile.laboratory3.Picasso.GalleryGridViewActivity;

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
    private Button bFav;
    private Button bInad;
	private Button bOpenGallery;
    int noticeId;

    private List<AsyncTask<?, ?, ?>> pendingTasks = new ArrayList<AsyncTask<?, ?, ?>>();

	private static final String TAG = "ShowNoticeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_notice);
		findViews();


		noticeId = getIntent().getIntExtra("noticeId", -1);

		//if (noticeId == -1) { throw new RuntimeException("noticeId param is required"); }; TODO

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
		t1.execute();
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
        bFav = (Button) findViewById(R.id.bookmark_b);
        bInad = (Button) findViewById(R.id.inadequate_b);
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

        String email = notice.getEmail();
        if (email != null && !email.equals("")) {
            tvEmail.setText(email);
        } else {
            tvEmail.setVisibility(View.GONE);
        }

        tvInappropriate.setText(String.valueOf(notice.getCountInappropriate())); // it's at least 0

        int size = notice.getSize();
        if (size != 0) {
            bSize.setText(notice.getSize() + "mq");
        } else {
            bSize.setVisibility(View.GONE);
        }

        double price = notice.getPrice();
        if (price != 0.0) {
            bPrice.setText(String.valueOf(notice.getPrice()) + "€");
        } else {
            bPrice.setVisibility(View.GONE);
        }

        String[] pictures = notice.getPictures();
        if (pictures == null || pictures.length == 0) {
            bOpenGallery.setVisibility(View.GONE);
        } else {
            bOpenGallery.setVisibility(View.VISIBLE);
        }
    }

    private void setupCallbacks(final Notice notice) {
        bOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowNoticeActivity.this, GalleryGridViewActivity.class);
                i.putExtra("picturesUrls", notice.getPictures());
                startActivity(i);
            }
        });
    }

	@Override
	protected void onResume() {
		super.onResume();
        AsyncTask<Integer, Integer, Boolean> t2 = new AsyncTask<Integer, Integer, Boolean>() {
            Exception e=null;
            @Override
            protected Boolean doInBackground(Integer... integers) {

                try {
                    String response = RESTManager.send(RESTManager.GET, "students/"+LoggedStudent.getId()+"/favs/notices", null);
                    JSONArray obj = (new JSONObject(response)).getJSONArray("fav_notices");
                    for(int i=0;i<obj.length();i++){
                        if(noticeId==obj.getJSONObject(i).getInt("notice_id")){
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean isFav) {
                super.onPostExecute(isFav);
                if(e!=null){
                    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                    return;
                }
                if(isFav){
                    setupUnfav();
                }else{
                    setupFav();
                }
            }
        };
        t2.execute();
	}

    private void setupUnfav(){
        bFav.setText(getResources().getString(R.string.unfav));
        bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {

                        try {
                            RESTManager.send(RESTManager.DELETE, "students/" + LoggedStudent.getId() + "/favs/notices/" + noticeId, null);

                        } catch (Exception e) {
                            e.printStackTrace();
                            this.e = e;
                        }
                        return 0;
                    }

                    @Override
                    protected void onPostExecute(Integer i) {
                        super.onPostExecute(i);
                        if (e != null) {
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                            return;
                        }
                        setupFav();
                    }
                };
                t1.execute();
                pendingTasks.add(t1);
            }
        });
    }


    private void setupFav(){
        bFav.setText(getResources().getString(R.string.add_to_bookmark));
        bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e=null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {
                        try {
                            HashMap<String,String> hm = new HashMap<String, String>();
                            hm.put("fav_notice[notice_id]", ""+noticeId);
                            String response = RESTManager.send(RESTManager.POST, "students/"+LoggedStudent.getId()+ "/favs/notices/", hm);

                        } catch (Exception e) {
                            e.printStackTrace();
                            this.e=e;
                        }
                        return 0;
                    }
                    @Override
                    protected void onPostExecute(Integer i) {
                        super.onPostExecute(i);
                        if(e!=null){
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                            return;
                        }
                        setupUnfav();
                    }
                };
                t1.execute();
                pendingTasks.add(t1);
            }
        });
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