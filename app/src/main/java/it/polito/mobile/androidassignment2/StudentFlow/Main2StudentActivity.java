package it.polito.mobile.androidassignment2.StudentFlow;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.internal.widget.*;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.context.AppContext;

public class Main2StudentActivity extends AppCompatActivity
		implements NavigationDrawerFragment.NavigationDrawerCallbacks, Communicator {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2_student);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
				.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				break;
			case 3:
				mTitle = getString(R.string.title_section3);
				break;
			case 4:
				mTitle = getString(R.string.title_section4);
				break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		//actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mTitle);
		//actionBar.setCustomView(getLayoutInflater().inflate(R.layout.generic_bar, null));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.global, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_logout) {
			showConfirmAlerter(0);
			return true;
		}
		if (id == R.id.action_delete) {
			showConfirmAlerter(1);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void showConfirmAlerter(int kind) {
		AlertYesNo alert = new AlertYesNo();
		Bundle info = new Bundle();
		if (kind == 0)
			info.putString("message", getResources().getString(R.string.logout_message));
		else info.putString("message", getResources().getString(R.string.delete_user_message));

		info.putString("title", getResources().getString(R.string.confirm));
		info.putInt("kind", kind);
		alert.setCommunicator(this);
		alert.setArguments(info);
		alert.show(getSupportFragmentManager(), "Confirm");

	}


	@Override
	public void goSearch(int kind) {

	}

	@Override
	public void respond(int itemIndex, int kind) {

	}

	@Override
	public void dialogResponse(int result, int kind) {
		if (result == 1) {
			switch (kind) {
				case 0://logout
					getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
					Intent i = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(i);
					finish();
					break;
				case 1://delete account
					try {
						Manager.deleteStudent(((AppContext) getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
							@Override
							public void process(Integer arg, Exception e) {

								if (e != null) {
									Log.d(CompaniesFavouritesActivity.class.getSimpleName(), "Error deleteing user");
									return;
								}
								getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
								Intent i = new Intent(getApplicationContext(), LoginActivity.class);
								startActivity(i);
								finish();
							}

							@Override
							public void cancel() {

							}
						});
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
					break;
				default:
					return;
			}
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
		                         Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main2_student, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((Main2StudentActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
}


