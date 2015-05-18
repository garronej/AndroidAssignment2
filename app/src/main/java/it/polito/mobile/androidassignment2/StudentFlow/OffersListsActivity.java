package it.polito.mobile.androidassignment2.StudentFlow;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class OffersListsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addTabMenuButtonCallbacks();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_logout) {
			getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			return true;
		}
		if (id == R.id.action_delete) {
			try {
				Manager.deleteStudent(Session.getInstance().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
					@Override
					public void process(Integer arg, Exception e) {
						if (e != null) {
							//TODO: show error message
							return;
						}
						getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
						Intent i = new Intent(OffersListsActivity.this, LoginActivity.class);
						startActivity(i);
					}

					@Override
					public void cancel() {

					}
				});
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void addTabMenuButtonCallbacks() {
		//findViewById(R.id.tab_menu_student_companies)
		setContentView(R.layout.activity_offers_lists);
		findViewById(R.id.tab_menu_student_profile).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_offers).setBackgroundColor(getResources().getColor(R.color.strong_blue));
		findViewById(R.id.tab_menu_student_companies).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SearchCompanies.class);
				startActivity(i);
			}
		});
		findViewById(R.id.tab_menu_student_profile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), StudentProfileActivity.class);
				startActivity(i);
			}
		});
		findViewById(R.id.tab_menu_student_offers).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), OffersListsActivity.class);
				startActivity(i);
			}
		});


	}
}