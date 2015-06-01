package it.polito.mobile.androidassignment2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.CompanyFlow.CompanyProfileActivity;
import it.polito.mobile.androidassignment2.StudentFlow.Main2StudentActivity;
import it.polito.mobile.androidassignment2.StudentFlow.StudentProfileActivity;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;

import it.polito.mobile.androidassignment2.businessLogic.Utils;
import it.polito.mobile.androidassignment2.context.AppContext;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {


	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private AsyncTask<?,?,?> mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		populateAutoComplete();

		mPasswordView = (EditText) findViewById(R.id.password);
		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);

		if(getSharedPreferences("login_pref",MODE_PRIVATE).contains("EMAIL")
				&& getSharedPreferences("login_pref",MODE_PRIVATE).contains("PWD")){


			attemptLogin(getSharedPreferences("login_pref", MODE_PRIVATE).getString("EMAIL",""),
					getSharedPreferences("login_pref", MODE_PRIVATE).getString("PWD",""));



		}


		/*


		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
					return true;
				}
				return false;
			}
		});

		*/

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {


				attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
			}
		});

		((Button) findViewById(R.id.register)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i =new Intent(LoginActivity.this, AlertStudentOrCompany.class);
				i.putExtra("EMAIL", mEmailView.getText().toString());
				i.putExtra("PWD", mPasswordView.getText().toString());
				startActivityForResult(i, 1);
			}
		});



	}

	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1 && resultCode == Activity.RESULT_OK){
			attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
		}

	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(final String email, final String password) {
		Log.d("poliJobs", "Attempt login with "+email+" and "+password);
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);



		boolean cancel = false;
		View focusView = null;

		// Check for a valid password, if the user entered one.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);







			AppContext appState = ((AppContext)getApplicationContext());
			mAuthTask = appState.login( email, password, new Manager.ResultProcessor<Integer>(){


				@Override
				public void process(Integer arg, Exception e) {
					Log.d("poliJobs", "here in the process");
					if(e!=null){


						String message;

						//There where  problem during the request
						if (e.getClass() == RestApiException.class) {
							message = getResources().getString(R.string.error_server_side);
							//It was an error on the web service side.
							//Nb : err code -1 mean a internal bug, report if you exprerience.
							Integer errCode =  ((RestApiException)e).getResponseCode();
							//message = errCode.toString() + " / " + e.getMessage();
							if(errCode == 404){
								message = getResources().getString(R.string.error_login_failed);
							}

						}else{
							//It was an error with the internet conextion.
							message = getResources().getString(R.string.error_network);
						}









						Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
						showProgress(false);
						mAuthTask = null;
						return;

					}



					SharedPreferences.Editor editor = getSharedPreferences("login_pref",MODE_PRIVATE).edit();


					editor.putString("EMAIL", email);
					editor.putString("PWD", password);

					editor.commit();

					if( ((AppContext)getApplication()).getSession().getWhoIsLogged() == Company.class){
						Log.d("poliJobs", "Company");
						Intent i = new Intent(getApplicationContext(), CompanyProfileActivity.class);
						startActivity(i);
						finish();


					}else{
						Log.d("poliJobs", "Student");
						Intent i = new Intent(getApplicationContext(), Main2StudentActivity.class);
						startActivity(i);
						finish();
					}



				}

				@Override
				public void cancel() {
					mAuthTask = null;
					showProgress(false);
				}

			});

		}
	}

	private boolean isEmailValid(String email) {

		if(email == null){
			return false;
		}
		try{
			Utils.formatEmail(email);
			return true;
		}catch (Exception e){
			return false;
		}
	}

	private boolean isPasswordValid(String password) {
		if(password == null){
			return false;
		}
		try {
			Utils.checkPassword(password);
			return true;
		}catch (Exception e){
			return false;
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});


			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAuthTask != null) {
			mAuthTask.cancel(true);
			mAuthTask = null;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
				.CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
		int IS_PRIMARY = 1;
	}


	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

		mEmailView.setAdapter(adapter);
	}

}

