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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.laboratory3.Picasso.GalleryGridViewActivity;
import it.polito.mobile.laboratory3.s3client.models.UploadModel;
import it.polito.mobile.laboratory3.s3client.network.TransferController;
import nl.changer.polypicker.ImagePickerActivity;

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
    private ActionButton bFav;
    private ActionButton bInad;
    int noticeId;
    private Button bOpenGallery;
    private ActionButton bEdit;
    private RelativeLayout lOpenGallery;
    private boolean owner;
    private RelativeLayout lUpload;
    private Button bUpload;
    private ProgressBar pbUpload;
    private UploadFinished uploadfinished = new UploadFinished();
    private int pendingUploads = 0;
    private List<String> pendingPictures = new ArrayList<String>();
    private Notice notice;

    private List<AsyncTask<?, ?, ?>> pendingTasks = new ArrayList<AsyncTask<?, ?, ?>>();

	private static final String TAG = "ShowNoticeActivity";
    private final static int ACTIVITY_MULTIUPLOAD = 147;
    private final static int MAX_UPLOAD_PICTURES = 5;

    public class UploadFinished extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filePath = intent.getStringExtra(UploadModel.EXTRA_FILENAME);
            pendingPictures.add(filePath);
            Log.d(TAG, "upped " + filePath);
            pendingUploads--;
            if (pendingUploads == 0) {
                putPictures();
            }
        }
    }

    private void putPictures() {
        final Map<String, String> params = new HashMap<String, String>();
        int i = 0;
        for (String url : pendingPictures) {
            params.put("notice[pictures][" + i + "]", url);
            i++;
        }
        Log.d(TAG, params.toString());

        AsyncTask<Void, Void, String> t = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    RESTManager.send(RESTManager.PUT, "notices/" + noticeId, params);
                } catch (Exception e) {
                    Toast.makeText(ShowNoticeActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                bUpload.setVisibility(View.VISIBLE);
                pbUpload.setVisibility(View.GONE);
                lOpenGallery.setVisibility(View.VISIBLE);
                notice.setPictures(pendingPictures.toArray(new String[pendingPictures.size()]));
            }
        };
        t.execute();
        pendingTasks.add(t);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_notice);
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
        bFav = (ActionButton) findViewById(R.id.bookmark_b);
        bInad = (ActionButton) findViewById(R.id.inadequate_b);
        bUpload = (Button) findViewById(R.id.upload_b);
        bEdit = (ActionButton) findViewById(R.id.edit_b);
        pbUpload = (ProgressBar) findViewById(R.id.upload_pb);
        lOpenGallery = (RelativeLayout) findViewById(R.id.gallery_l);
        lUpload = (RelativeLayout) findViewById(R.id.upload_l);
    }

    private void setupViews(Notice notice) {
        if (notice.getStudentId() == LoggedStudent.getId()) {
            owner = true;
        } else {
            owner = false;
        }

        if (owner) { lUpload.setVisibility(View.VISIBLE); }
        if (owner) { bEdit.setVisibility(View.VISIBLE); }

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
            bSize.setText(size + "mq");
        } else {
            bSize.setText(getResources().getString(R.string.size_undef));
        }

        double price = notice.getPrice();
        if (price != 0.0) {
            bPrice.setText(String.valueOf(price) + "€");
        } else {
            bPrice.setText(getResources().getString(R.string.price_undef));
        }

        String[] pictures = notice.getPictures();
        if (pictures == null || pictures.length == 0) {
            lOpenGallery.setVisibility(View.GONE);
        } else {
            lOpenGallery.setVisibility(View.VISIBLE);
        }
    }

    private void setupCallbacks(final Notice notice) {
        bUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImages();
            }
        });

        bOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowNoticeActivity.this, GalleryGridViewActivity.class);
                i.putExtra("picturesUrls", notice.getPictures());
                startActivity(i);
            }
        });

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowNoticeActivity.this, EditNoticeActivity.class);
                i.putExtra("noticeId", notice.getId());
                i.putExtra("role", "update");
                startActivity(i);
            }
        });
    }

    private void getImages() {
        bUpload.setVisibility(View.INVISIBLE);
        pbUpload.setVisibility(View.VISIBLE);
        Intent intent = new Intent(ShowNoticeActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, MAX_UPLOAD_PICTURES);
        startActivityForResult(intent, ACTIVITY_MULTIUPLOAD);
    }

	@Override
	protected void onResume() {
		super.onResume();
        findViews();

        noticeId = getIntent().getIntExtra("noticeId", -1);
        if (noticeId == -1) { throw new RuntimeException("noticeId param is required"); }

        AsyncTask<Integer, Void, Notice> t0 = new AsyncTask<Integer, Void, Notice>() {
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
                ShowNoticeActivity.this.notice = notice;
                super.onPostExecute(notice);
                setupViews(notice);
                setupCallbacks(notice);
            }
        };
        t0.execute();
        pendingTasks.add(t0);

        AsyncTask<Integer, Integer, Boolean> t1 = new AsyncTask<Integer, Integer, Boolean>() {
            Exception e=null;
            @Override
            protected Boolean doInBackground(Integer... integers) {

                try {
                    String response = RESTManager.send(RESTManager.GET, "students/"+LoggedStudent.getId()+"/inappropriate/notices", null);
                    JSONArray obj = (new JSONObject(response)).getJSONArray("inappropriate_notices");
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
                    setupRemoveInadequate();
                }else{
                    setupInadequate();
                }
            }
        };
        t1.execute();
        pendingTasks.add(t1);
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
        pendingTasks.add(t2);
        registerReceiver(uploadfinished, new IntentFilter(UploadModel.INTENT_UPLOADED));
	}

    private void setupUnfav(){
        //bFav.setText(getResources().getString(R.string.unfav));
        bFav.setImageDrawable(getResources().getDrawable(R.drawable.abc_btn_rating_star_off_mtrl_alpha));
	    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.unfav_ok), Toast.LENGTH_SHORT).show();

	    bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {

                        try {
                            RESTManager.send(RESTManager.DELETE, "students/" + LoggedStudent.getId() + "/favs/notices/" + noticeId, null);
                            //Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.unfav_ok), Toast.LENGTH_SHORT).show();

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
        //bFav.setText(getResources().getString(R.string.add_to_bookmark));
        bFav.setImageDrawable(getResources().getDrawable(R.drawable.abc_btn_rating_star_on_mtrl_alpha));
	    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.fav_ok), Toast.LENGTH_SHORT).show();

	    bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {
                        try {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("fav_notice[notice_id]", "" + noticeId);
                            String response = RESTManager.send(RESTManager.POST, "students/" + LoggedStudent.getId() + "/favs/notices", hm);

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
                        setupUnfav();
                    }
                };
                t1.execute();
                pendingTasks.add(t1);
            }
        });
    }

    private void setupRemoveInadequate(){
        //bInad.setText(getResources().getString(R.string.unflag_as_inadequate));
	    bInad.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_flag));
	    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.rem_in_ok), Toast.LENGTH_SHORT).show();
        bInad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {

                        try {
                            RESTManager.send(RESTManager.DELETE, "students/" + LoggedStudent.getId() + "/inappropriate/notices/" + noticeId, null);

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
                        tvInappropriate.setText("" + (Integer.parseInt(tvInappropriate.getText().toString()) - 1));
                        setupInadequate();
                    }
                };
                t1.execute();
                pendingTasks.add(t1);
            }
        });
    }


    private void setupInadequate(){
        //bInad.setText(getResources().getString(R.string.flag_as_inadequate));
	    bInad.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_deny));
	    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.add_in_ok), Toast.LENGTH_SHORT).show();
        bInad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {
                        try {
                            HashMap<String, String> s = new HashMap<String, String>();
                            s.put("inappropriate_notice[notice_id]", "" + noticeId);
                            String response = RESTManager.send(RESTManager.POST, "students/" + LoggedStudent.getId() + "/inappropriate/notices", s);

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
                        tvInappropriate.setText("" + (Integer.parseInt(tvInappropriate.getText().toString()) + 1));
                        setupRemoveInadequate();
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
        unregisterReceiver(uploadfinished);
		super.onPause();
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.delete_notice);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.delete_notice))) {
            AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                Exception e = null;

                @Override
                protected Integer doInBackground(Integer... integers) {

                    try {
                        RESTManager.send(RESTManager.DELETE, "notices/" + noticeId, null);

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
                    finish();
                }
            };
            t1.execute();
            pendingTasks.add(t1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTIVITY_MULTIUPLOAD) {
                bUpload.setVisibility(View.GONE);
                pbUpload.setVisibility(View.VISIBLE);
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    pendingUploads += uris.length;
                    for (Uri uri : uris) {
                        if (!uri.toString().contains("content://")) { // probably a relative uri
                            uri = getImageContentUri(ShowNoticeActivity.this, new File(uri.toString()));
                        }
                        if (uri != null) {
                            Log.i(TAG, "uploading: " + uri);
                            bUpload.setVisibility(View.INVISIBLE);
                            pbUpload.setVisibility(View.VISIBLE);
                            TransferController.upload(ShowNoticeActivity.this, uri, "photo/student3");
                        }
                    }
                }
            }
        }
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}