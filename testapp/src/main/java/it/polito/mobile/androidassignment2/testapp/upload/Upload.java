package it.polito.mobile.androidassignment2.testapp.upload;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import it.polito.mobile.androidassignment2.testapp.R;

public class Upload extends AppCompatActivity implements OnClickListener {


    /*********  work only for Dedicated IP ***********/
    static final String FTP_HOST= "garrone.org";

    /*********  FTP USERNAME ***********/
    static final String FTP_USER = "public";

    /*********  FTP PASSWORD ***********/
    static final String FTP_PASS  ="cloud";

    Button btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(this);

    }

    public void onClick(View v) {

        /********** Pick file from sdcard *******/
        //File f = new File("/sdcard/fileTest.txt");


        FileOutputStream fop = null;
        File file = null;
        String content = "This is the text content";

        try {

            file = new File("/sdcard/fileTest.txt");
            fop = new FileOutputStream(file);

            // if file doesn't exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Upload sdcard file
        uploadFile(file);

    }

    public void uploadFile(File fileName){


        FTPClient client = new FTPClient();

        try {

            client.connect(FTP_HOST,21);
            client.login(FTP_USER, FTP_PASS);
            client.setType(FTPClient.TYPE_BINARY);
            client.changeDirectory("/polyJobs/test/");

            client.upload(fileName, new MyTransferListener());

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    /*******  Used to file upload and show progress  **********/

    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            btn.setVisibility(View.GONE);
            // Transfer started
            Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
            //System.out.println(" Upload Started ...");
        }

        public void transferred(int length) {

            // Yet other length bytes has been transferred since the last time this
            // method was called
            Toast.makeText(getBaseContext(), " transferred ..." + length, Toast.LENGTH_SHORT).show();
            //System.out.println(" transferred ..." + length);
        }

        public void completed() {

            btn.setVisibility(View.VISIBLE);
            // Transfer completed

            Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
            //System.out.println(" completed ..." );

            TextView textView = (TextView)findViewById(R.id.textView);
            textView.setText("http://cloud.garrone.org/polyJobs/test/fileTest.txt");

        }

        public void aborted() {

            btn.setVisibility(View.VISIBLE);
            // Transfer aborted
            Toast.makeText(getBaseContext()," transfer aborted ," +
                    "please try again...", Toast.LENGTH_SHORT).show();
            //System.out.println(" aborted ..." );
        }

        public void failed() {

            btn.setVisibility(View.VISIBLE);
            // Transfer failed
            System.out.println(" failed ..." );
        }

    }
}