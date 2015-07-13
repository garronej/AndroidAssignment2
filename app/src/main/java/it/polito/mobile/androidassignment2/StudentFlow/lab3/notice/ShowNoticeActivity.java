package it.polito.mobile.androidassignment2.StudentFlow.lab3.notice;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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


import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.notice.gallery.GalleryActivity;
import it.polito.mobile.androidassignment2.businessLogic.Notice;
import it.polito.mobile.androidassignment2.businessLogic.RESTManager;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.s3client.models.UploadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;
import nl.changer.polypicker.ImagePickerActivity;


/**
 * Created by mark9 on 07/06/15.
 */
public class ShowNoticeActivity extends AppCompatActivity {

    private final static int ACTIVITY_MULTIUPLOAD = 147;
    private final static int MAX_UPLOAD_PICTURES = 5;

    private int noticeId;
    private boolean isCreator;



	private TextView title;
	private TextView description;
	private TextView tags;
    private TextView address;
    private TextView size;
    private TextView price;
	private TextView phoneNumber;
	private TextView email;
    private TextView creationDate;
	private TextView inappropriateCount;
    private ActionButton inappropriateFlag;
    private ActionButton bookmarksButton;


    private ActionButton openGalleryButton;
    private ViewGroup openGalleryParent;

    private ViewGroup uploadImagesParent;
    private ActionButton uploadImagesButton;

    private ProgressBar progressBar;

    private List<AsyncTask<?, ?, ?>> pendingTasks = new ArrayList<AsyncTask<?, ?, ?>>();

    private UploadFinished uploadFinished;
    private int pendingUploads = 0;
    private List<String> pendingPictures = new ArrayList<String>();

    private Notice notice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_notice);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        uploadFinished = new UploadFinished();
        title = (TextView) findViewById(R.id.title_tv);
        description = (TextView) findViewById(R.id.description_tv);
        tags = (TextView) findViewById(R.id.tags_tv);
        creationDate = (TextView) findViewById(R.id.created_on_tv);
        phoneNumber = (TextView) findViewById(R.id.telephone_number_tv);
        address = (TextView) findViewById(R.id.location_tv);
        email = (TextView) findViewById(R.id.email_tv);
        inappropriateCount = (TextView) findViewById(R.id.inappropriate_count_tv);
        size = (TextView) findViewById(R.id.size_b);
        price = (TextView) findViewById(R.id.price_b);
        openGalleryButton = (ActionButton) findViewById(R.id.gallery_b);
        bookmarksButton = (ActionButton) findViewById(R.id.bookmark_b);
        inappropriateFlag = (ActionButton) findViewById(R.id.inadequate_b);
        uploadImagesButton = (ActionButton) findViewById(R.id.upload_b);
        progressBar = (ProgressBar) findViewById(R.id.upload_pb);
        openGalleryParent = (ViewGroup) findViewById(R.id.gallery_l);
        uploadImagesParent = (ViewGroup) findViewById(R.id.upload_l);

	}




    private void initWithNotice() {
        try {
            isCreator = notice.getStudentId() == ((AppContext) getApplication()).getSession().getStudentLogged().getId();
        }catch(Exception e){
            isCreator=false;
        }
        if (isCreator) { uploadImagesParent.setVisibility(View.VISIBLE); }

        if (notice.getTitle() != null && !notice.getTitle().equals("")) {
            title.setText(notice.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        if (notice.getDescription() != null && !notice.getDescription().equals("")) {
            description.setText(notice.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        String[] tags = notice.getTags();
        if (tags == null || tags.length==0) {
            this.tags.setVisibility(View.GONE);
        } else {
            String s=notice.getTags()[0];
            for(int i = 1 ; i < notice.getTags().length; i++){
                s+=","+notice.getTags()[i];
            }
            this.tags.setText(s);
        }

        if (notice.getCreationDate() != null && !notice.getCreationDate().equals("")) {
            creationDate.setText(notice.getCreationDate());
        } else {
            creationDate.setVisibility(View.GONE);
        }

        if (notice.getTelephone() != null && !notice.getTelephone().equals("")) {
            phoneNumber.setText(notice.getTelephone());
        } else {
            phoneNumber.setVisibility(View.GONE);
        }

        if (notice.getAddress() != null && !notice.getAddress().equals("")) {
            address.setText(notice.getAddress());
        } else {
            address.setVisibility(View.GONE);
        }


        if (notice.getEmail() != null && !notice.getEmail().equals("")) {
            email.setText(notice.getEmail());
        } else {
            email.setVisibility(View.GONE);
        }

        inappropriateCount.setText(String.valueOf(notice.getCountInappropriate())); // it's at least 0

        if (notice.getSize() != 0) {
            size.setText(notice.getSize() + "mq");
        }

        if (notice.getPrice() != 0.0) {
            price.setText(String.valueOf(notice.getPrice()) + "€");
        }

        String[] pictures = notice.getPictures();
        if (pictures == null || pictures.length == 0) {
            openGalleryParent.setVisibility(View.GONE);
        } else {
            openGalleryParent.setVisibility(View.VISIBLE);
        }
        uploadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImages();
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowNoticeActivity.this, GalleryActivity.class);
                i.putExtra("picturesUrls", notice.getPictures());
                startActivity(i);
            }
        });

        invalidateOptionsMenu();
    }

    private void uploadImages() {
        uploadImagesButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(ShowNoticeActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, MAX_UPLOAD_PICTURES);
        startActivityForResult(intent, ACTIVITY_MULTIUPLOAD);
    }

	@Override
	protected void onResume() {
		super.onResume();


        uploadImagesParent.setVisibility(View.GONE);
        uploadImagesButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        noticeId = getIntent().getIntExtra("noticeId", -1);
        if (noticeId == -1) { throw new RuntimeException("noticeId param is required"); }

        AsyncTask<Integer, Void, Notice> t = new AsyncTask<Integer, Void, Notice>() {
            Exception e=null;

            @Override
            protected Notice doInBackground(Integer... integers) {
                Notice notice = null;
                try {
                    String response = RESTManager.send(RESTManager.GET, "notices/" + noticeId, null);
                    JSONObject obj = (new JSONObject(response));
                    notice = new Notice(obj.getJSONObject("notice"));
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }

                return notice;
            }
            @Override
            protected void onPostExecute(Notice notice) {
                super.onPostExecute(notice);
                if(e!=null){
                    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }
                ShowNoticeActivity.this.notice = notice;
                initWithNotice();
            }
        };
        t.execute();
        pendingTasks.add(t);

        AsyncTask<Integer, Integer, Boolean> t1 = new AsyncTask<Integer, Integer, Boolean>() {
            Exception e=null;
            @Override
            protected Boolean doInBackground(Integer... integers) {

                try {
                    String response = RESTManager.send(RESTManager.GET, "students/"+ ((AppContext) getApplication()).getSession().getStudentLogged().getId()+"/inappropriate/notices", null);
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
                    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
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
                    String response = RESTManager.send(RESTManager.GET, "students/"+((AppContext) getApplication()).getSession().getStudentLogged().getId()+"/favs/notices", null);
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
                    Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
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
        registerReceiver(uploadFinished, new IntentFilter(UploadModel.INTENT_UPLOADED));
	}

    private void setupUnfav(){
        //bookmarksButton.setText(getResources().getString(R.string.remove_from_bookmarks));
        bookmarksButton.setImageDrawable(getResources().getDrawable(R.drawable.abc_btn_rating_star_on_mtrl_alpha));
        Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.fav_ok), Toast.LENGTH_SHORT).show();

        bookmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {

                        try {
                            RESTManager.send(RESTManager.DELETE, "students/" + ((AppContext) getApplication()).getSession().getStudentLogged().getId() + "/favs/notices/" + noticeId, null);

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
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
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
        //bookmarksButton.setText(getResources().getString(R.string.add_to_bookmarks));
        bookmarksButton.setImageDrawable(getResources().getDrawable(R.drawable.abc_btn_rating_star_off_mtrl_alpha));
        Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.unfav_ok), Toast.LENGTH_SHORT).show();

        bookmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {
                        try {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("fav_notice[notice_id]", "" + noticeId);
                            String response = RESTManager.send(RESTManager.POST, "students/" + ((AppContext) getApplication()).getSession().getStudentLogged().getId() + "/favs/notices", hm);

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
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
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
       // inappropriateFlag.setImageDrawable(getResources().getDrawable(R.drawable.appropriate));
        inappropriateFlag.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_deny));
        Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.add_in_ok), Toast.LENGTH_SHORT).show();
        inappropriateFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {

                        try {
                            RESTManager.send(RESTManager.DELETE, "students/" + ((AppContext) getApplication()).getSession().getStudentLogged().getId() + "/inappropriate/notices/" + noticeId, null);

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
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                            return;
                        }
                        inappropriateCount.setText("" + (Integer.parseInt(inappropriateCount.getText().toString()) - 1));
                        setupInadequate();
                    }
                };
                t1.execute();
                pendingTasks.add(t1);
            }
        });
    }


    private void setupInadequate(){
        //inappropriateFlag.setImageDrawable(getResources().getDrawable(R.drawable.inappropriate));
        inappropriateFlag.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_flag));
        Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.rem_in_ok), Toast.LENGTH_SHORT).show();

        inappropriateFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Integer, Void, Integer> t1 = new AsyncTask<Integer, Void, Integer>() {
                    Exception e = null;

                    @Override
                    protected Integer doInBackground(Integer... integers) {
                        try {
                            HashMap<String, String> s = new HashMap<String, String>();
                            s.put("inappropriate_notice[notice_id]", "" + noticeId);
                            String response = RESTManager.send(RESTManager.POST, "students/" + ((AppContext) getApplication()).getSession().getStudentLogged().getId() + "/inappropriate/notices", s);

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
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                            return;
                        }
                        inappropriateCount.setText("" + (Integer.parseInt(inappropriateCount.getText().toString()) + 1));
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
            if(t.getStatus()== AsyncTask.Status.PENDING
                    || t.getStatus()== AsyncTask.Status.RUNNING)
                t.cancel(true);
        }
        pendingTasks.clear();
        unregisterReceiver(uploadFinished);
		super.onPause();
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_notice, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(!isCreator){
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_delete).setVisible(false);
        }else{
            menu.findItem(R.id.action_edit).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_edit:
                Intent i = new Intent(ShowNoticeActivity.this, EditNoticeActivity.class);
                i.putExtra("noticeId", noticeId);
                i.putExtra("role", "update");
                startActivity(i);
                return true;
            case R.id.action_delete:
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
                            Toast.makeText(ShowNoticeActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                            return;
                        }
                        finish();
                    }
                };
                t1.execute();
                pendingTasks.add(t1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTIVITY_MULTIUPLOAD) {
                uploadImagesButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                pendingUploads += uris.length;
                for (Uri uri : uris) {
                    if (!uri.toString().contains("content://")) { // probably a relative uri
                        uri = getImageContentUri(ShowNoticeActivity.this, new File(uri.toString()));
                    }
                    if (uri != null) {
                        uploadImagesButton.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        TransferController.upload(ShowNoticeActivity.this, uri, "photo/student3");
                    }
                }

            }
        }
    }



    public class UploadFinished extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filePath = intent.getStringExtra(UploadModel.EXTRA_FILENAME);
            pendingPictures.add(filePath);
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
                notice.setPictures(pendingPictures.toArray(new String[pendingPictures.size()]));
                uploadImagesButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                openGalleryParent.setVisibility(View.VISIBLE);
            }
        };
        t.execute();
        pendingTasks.add(t);
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);
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
        }finally {
            if(cursor!=null && ! cursor.isClosed())
                cursor.close();
        }
    }
}