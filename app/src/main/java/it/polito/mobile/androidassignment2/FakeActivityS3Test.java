package it.polito.mobile.androidassignment2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.polito.mobile.androidassignment2.s3client.Util;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.models.UploadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class FakeActivityS3Test extends ActionBarActivity  {


    public class UploadFinished extends BroadcastReceiver{
        public String fname="";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("poliJob", "Intent about finished upload of "+intent.getStringExtra(UploadModel.EXTRA_FILENAME));
            fname=intent.getStringExtra(UploadModel.EXTRA_FILENAME);
        }
    }

    public class DownloadFinished extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("poliJob", "Intent about finished download of " + intent.getStringExtra(DownloadModel.EXTRA_FILE_URI));
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(intent.getStringExtra(DownloadModel.EXTRA_FILE_URI)), "*/*");
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_activity_s3_test);
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 0);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TransferController.download(getApplicationContext(), new String[]{uploadfinished.fname});
            }
        });
    }


    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                TransferController.upload(this, uri, "photo/student3");
            }
        }
    }
    private UploadFinished uploadfinished=new UploadFinished();
    private DownloadFinished downloadfinished=new DownloadFinished();

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(uploadfinished, new IntentFilter(UploadModel.INTENT_UPLOADED));
        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(uploadfinished);
        unregisterReceiver(downloadfinished);
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
