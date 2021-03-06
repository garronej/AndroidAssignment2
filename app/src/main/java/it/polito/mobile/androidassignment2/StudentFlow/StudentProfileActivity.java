package it.polito.mobile.androidassignment2.StudentFlow;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Career;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.customView.CareerLayout;
import it.polito.mobile.androidassignment2.gcm.QuickstartPreferences;
import it.polito.mobile.androidassignment2.gcm.RegistrationIntentService;
import it.polito.mobile.androidassignment2.gcm.UnregistrationManager;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;


public class StudentProfileActivity extends AppCompatActivity implements Communicator {
	private ImageView ivPhoto;
	private ProgressBar pbPhotoSpinner;
	private ProgressBar pbCvSpinner;
	private TextView tvFullname;
	private TextView tvLinks;
	private Button bCv;
	private TextView tvEmail;
	private TextView tvCompetences;
	private Button bAvailability;
	private TextView tvHobbies;
	private DownloadFinished downloadfinished = new DownloadFinished();
	private DownloadFailed downloadfailed = new DownloadFailed();
	private Uri photoUri;
	private Button bEditProfile;
	private Button bSex;
	private TextView tvLocation;
	private AsyncTask<Object, Void, Object> task;
	private LinearLayout univCareersLL;
	private TextView birthDate;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
    private Student loggedStudent;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "MainActivity";
	private BroadcastReceiver mRegistrationBroadcastReceiver;

	private boolean firstRun = false;

	public class DownloadFinished extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
			if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
				/*Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse(filePath), "application/pdf");
				bCv.setVisibility(View.VISIBLE);
				pbCvSpinner.setVisibility(View.INVISIBLE);
				startActivity(i);*/
				Intent target = new Intent(Intent.ACTION_VIEW);
				target.setDataAndType(Uri.parse(filePath), "application/pdf");
				bCv.setVisibility(View.VISIBLE);
				pbCvSpinner.setVisibility(View.INVISIBLE);
				target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				Intent i = Intent.createChooser(target, getResources().getString(R.string.open_file));
				startActivity(i);
			} else { // photo
				pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
				photoUri = Uri.parse(filePath);
				((AppContext) getApplication()).getSession().setPhotoUri(photoUri);
				ivPhoto.setImageURI(photoUri);
				tvFullname.setVisibility(View.VISIBLE);
				bCv.setEnabled(true);
				bEditProfile.setEnabled(true);
			}
		}
	}

	public class DownloadFailed extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
			if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
				bCv.setVisibility(View.VISIBLE);
				pbCvSpinner.setVisibility(View.INVISIBLE);
				Toast t = Toast.makeText(StudentProfileActivity.this, getResources().getString(R.string.error_loading_cv), Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
			} else { // photo
				pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
				photoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
						getResources().getResourcePackageName(R.drawable.photo_placeholder_err) +
						'/' +
						getResources().getResourceTypeName(R.drawable.photo_placeholder_err) +
						'/' +
						getResources().getResourceEntryName(R.drawable.photo_placeholder_err));
				((AppContext) getApplication()).getSession().setPhotoUri(photoUri);
				ivPhoto.setImageURI(photoUri);
				tvFullname.setVisibility(View.VISIBLE);
				bCv.setEnabled(true);
				bEditProfile.setEnabled(true);
				Toast t = Toast.makeText(StudentProfileActivity.this, getResources().getString(R.string.error_loading_photo), Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
			}
		}
	}

	private void myAddActionBar() {
		ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
				R.layout.student_tabbed_menu, null);

		// Set up your ActionBar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

	}

	private void addTabMenuButtonCallbacks() {
		findViewById(R.id.tab_menu_student_search).setVisibility(View.GONE);
		findViewById(R.id.tab_menu_student_profile).setBackgroundColor(getResources().getColor(R.color.strong_blue));
		findViewById(R.id.tab_menu_student_offers).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_companies).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_companies).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CompaniesFavouritesActivity.class);
                startActivity(i);
                finish();

            }
        });
		findViewById(R.id.tab_menu_student_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OffersListsActivity.class);
                startActivity(i);
                finish();
            }
        });

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (((AppContext) getApplication()).getSession().getPhotoUri() != null) {
			photoUri = ((AppContext) getApplication()).getSession().getPhotoUri();
		}
        try {
            loggedStudent = ((AppContext) getApplication()).getSession().getStudentLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException();
        }
		setContentView(R.layout.activity_student_profile);
		findViews();
		myAddActionBar();
		addTabMenuButtonCallbacks();
		setUpNavigationDrawer();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.d(TAG, getString(R.string.gcm_send_message));
                } else {
                    Log.d(TAG, getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra("studentId", loggedStudent.getId());
            startService(intent);
        }

	}
private void setUpNavigationDrawer(){
	mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
	mTitle = getTitle();
	mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position", 3));

	mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
}
	private void findViews() {
		tvEmail = (TextView) findViewById(R.id.email);
		ivPhoto = (ImageView) findViewById(R.id.photo);
		pbPhotoSpinner = (ProgressBar) findViewById(R.id.photo_spinner);
		pbCvSpinner = (ProgressBar) findViewById(R.id.cv_pb);
		tvFullname = (TextView) findViewById(R.id.fullname);
		bCv = (Button) findViewById(R.id.cv_button);
		tvLinks = (TextView) findViewById(R.id.links);
		univCareersLL = (LinearLayout) findViewById(R.id.university_career);
		tvCompetences = (TextView) findViewById(R.id.competences);
		bAvailability = (Button) findViewById(R.id.availability);
		tvHobbies = (TextView) findViewById(R.id.hobbies);
		bEditProfile = (Button) findViewById(R.id.edit_profile_button);
		bSex = (Button) findViewById(R.id.sex_b);
		tvLocation = (TextView) findViewById(R.id.location_tv);
		birthDate = (TextView) findViewById(R.id.birth_date);
	}

	private void setupViewsAndCallbacks() {
		tvEmail.setText(loggedStudent.getEmail());
		pbCvSpinner.setVisibility(View.INVISIBLE);

		String fullname = loggedStudent.getFullname();
		if (fullname == null) {
			tvFullname.setVisibility(View.GONE);
		} else {
			tvFullname.setText(loggedStudent.getFullname());
		}

		String links = loggedStudent.getLinksToString(System.getProperty("line.separator")
				+ System.getProperty("line.separator"));
		if (links == null) {
			tvLinks.setVisibility(View.GONE);
		} else {
			tvLinks.setText(links);
		}

		birthDate.setText(loggedStudent.getBirthDate());
		univCareersLL.removeAllViews();
		Career[] universityCareers = loggedStudent.getUniversityCareers();
		if (universityCareers != null) {
			for (int i = 0; i < universityCareers.length; i++) {

				CareerLayout cView = new CareerLayout(this);
				cView.initializeValues(universityCareers[i]);

				univCareersLL.addView(cView);

				if (i != universityCareers.length - 1) {
					View v = new View(this);
					v.setBackgroundDrawable(getResources().getDrawable(R.drawable.items_divider));
					v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
					univCareersLL.addView(v);
				}
			}
		}


		String competences = loggedStudent.getCompetencesToString(", ");
		if (competences == null) {
			tvCompetences.setVisibility(View.GONE);
		} else {
			tvCompetences.setText(competences);
		}

		String hobbies = loggedStudent.getHobbiesToString(", ");
		if (hobbies == null) {
			tvHobbies.setVisibility(View.GONE);
		} else {
			tvHobbies.setText(hobbies);
		}

		String sex = loggedStudent.getSex();
		if (sex == null) {
			bSex.setVisibility(View.GONE);
		} else {
			bSex.setText(sex);
		}

		String location = loggedStudent.getLocation();
		if (location == null || location.equals("")) {
			tvLocation.setVisibility(View.GONE);
		} else {
			tvLocation.setText(location);
		}

		String url = loggedStudent.getPhotoUrl();
		if (photoUri == null) { //need to get from s3
			TransferController.download(getApplicationContext(), new String[]{url});
			bCv.setEnabled(false);
			bEditProfile.setEnabled(false);
			tvFullname.setVisibility(View.INVISIBLE);
			pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);
		} else { // it was already fetched from s3
			ivPhoto.setImageURI(photoUri);
			pbPhotoSpinner.setVisibility(ProgressBar.GONE);
		}

		final String cvUrl = loggedStudent.getCvUrl();
		if (cvUrl == null) {
			bCv.setVisibility(View.GONE);
			pbCvSpinner.setVisibility(View.GONE);
		} else {
			bCv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					TransferController.download(getApplicationContext(), new String[]{cvUrl});
					pbCvSpinner.setVisibility(View.VISIBLE);
					bCv.setVisibility(View.INVISIBLE);
				}
			});
		}

		boolean isAvailable = loggedStudent.isAvailable();
		if (isAvailable) {
			bAvailability.setText(getResources().getString(R.string.available_for_jobs));
			bAvailability.getBackground().setColorFilter(getResources().getColor(R.color.green_ok), PorterDuff.Mode.MULTIPLY);
		} else {
			bAvailability.setText(getResources().getString(R.string.not_available_for_jobs));
			bAvailability.getBackground().setColorFilter(getResources().getColor(R.color.red_warning), PorterDuff.Mode.MULTIPLY);
		}

		bEditProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(StudentProfileActivity.this, EditStudentProfileActivity.class);
				startActivity(i);
			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();
		setupViewsAndCallbacks();
		registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
		registerReceiver(downloadfailed, new IntentFilter(DownloadModel.INTENT_DOWNLOAD_FAILED));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
	}

	@Override
	protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
		unregisterReceiver(downloadfailed);
		unregisterReceiver(downloadfinished);
		if (task != null) {
			task.cancel(true);
			task = null;
		}
		super.onPause();
	}

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
				Toast.makeText(StudentProfileActivity.this, R.string.device_not_supported, Toast.LENGTH_SHORT);
                finish();
            }
            return false;
        }
        return true;
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
					new UnregistrationManager(StudentProfileActivity.this).unregisterGcm();
					getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
					((AppContext)getApplication()).freeSession();
					Intent i = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(i);
					finish();
					break;
				case 1://delete account
					try {
						task = Manager.deleteStudent(((AppContext) getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
							@Override
							public void process(Integer arg, Exception e) {
								task = null;
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
								task = null;
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


}