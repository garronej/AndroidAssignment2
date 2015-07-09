package it.polito.mobile.androidassignment2.StudentFlow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.Locale;

import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.gcm.QuickstartPreferences;
import it.polito.mobile.androidassignment2.gcm.UnregistrationManager;


public class ScheduleChangedActivity extends ActionBarActivity {
    private TextView tvTitle;
    private TextView tvMessage;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_changed);
        tvTitle = (TextView) findViewById(R.id.title_tv);
        tvMessage = (TextView )findViewById(R.id.message_tv);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        if (title == null || message == null) {
            throw new RuntimeException("title and message are required");
        }
        tvTitle.setText(title);
        tvMessage.setText(message);
    }


}
