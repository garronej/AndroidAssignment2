package it.polito.mobile.androidassignment2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.businessLogic.Task;
import it.polito.mobile.androidassignment2.businessLogic.Utils;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {


	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private Task.General mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);


		if(getPreferences(MODE_PRIVATE).contains("EMAIL")
				&& getPreferences(MODE_PRIVATE).contains("PWD")){


			attemptLogin(getPreferences(MODE_PRIVATE).getString("EMAIL",""),
					getPreferences(MODE_PRIVATE).getString("PWD",""));



		}




		// Set up the login form.
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		populateAutoComplete();

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin(mEmailView.getText().toString(),mPasswordView.getText().toString());
					return true;
				}
				return false;
			}
		});

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin(final String email, final String password) {
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

			mAuthTask = Session.login(email, password, new Manager.ResultProcessor<Integer>(){


				@Override
				public void process(Integer arg, Exception e) {
					Log.d("poliJobs", "here in the process");
					if(e!=null){
						//TODO: show some error...

						Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
						showProgress(false);
						mAuthTask = null;
						return;

					}


					if(arg==0){
						SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();


						editor.putString("EMAIL", email);
						editor.putString("PWD", password);

						if(Session.getInstance().getWhoIsLogged() == Company.class){
							//TODO: launch the company profile
							Log.d("poliJobs", "Company");

						}else{
							//TODO: launch the student profile
							Log.d("poliJobs", "Student");
						}
						editor.commit();

					}else{
						mPasswordView.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();

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

