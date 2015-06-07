package org.garrone.googlemapapitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);





        Bundle extras = getIntent().getExtras();
        String coordinate = null;
        String title = null;
        String snippet = null;


        if (extras == null) {
            throw new RuntimeException("Timetable lunched without extras");
        }

        coordinate = extras.getString("coordinate");
        title = extras.getString("title");
        snippet = extras.getString("snippet");

        TextView textView1 = (TextView)findViewById(R.id.textView1);
        TextView textView2 = (TextView)findViewById(R.id.textView2);
        TextView textView3 = (TextView)findViewById(R.id.textView2);

        textView1.setText("coordinate : " + coordinate);
        textView2.setText("title : " + title);
        textView3.setText("snippet : " + snippet);

    }




    }
