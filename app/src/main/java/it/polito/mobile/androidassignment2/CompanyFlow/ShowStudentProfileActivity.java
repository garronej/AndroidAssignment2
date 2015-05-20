package it.polito.mobile.androidassignment2.CompanyFlow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;

public class ShowStudentProfileActivity extends ActionBarActivity {
	private ImageView ivPhoto;
	private ProgressBar pbPhotoSpinner;
	private ProgressBar pbCvSpinner;
	private ProgressBar pbFav;
	private ProgressBar pbDiscard;
	private TextView tvFullname;
	private TextView tvLinks;
	private Button bCv;
	private TextView tvEmail;
	private TextView tvUniversityCareer;
	private TextView tvCompetences;
	private Button bAvailability;
	private Button bDiscard;
	private Button bFav;
	private TextView tvHobbies;
	private DownloadFinished downloadfinished = new DownloadFinished();
	private Button bSex;
	private TextView tvLocation;
	private Student student;
	private int offerId;
	private AsyncTask<Object, Void, Object> task1 = null;
	private AsyncTask<Object, Void, Object> task2 = null;
	private AsyncTask<Object, Void, Object> task3 = null;
	private Company companyLogged;
	private RelativeLayout rlDiscard;

	public class DownloadFinished extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
			if (filePath.indexOf(".pdf") != -1) { //pdf -> cv
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.parse(filePath), "application/pdf");
				bCv.setVisibility(View.VISIBLE);
				pbCvSpinner.setVisibility(View.INVISIBLE);
				startActivity(i);
			} else { // photo
				pbPhotoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
				Uri photoUri = Uri.parse(filePath);
				ivPhoto.setImageURI(photoUri);
				tvFullname.setVisibility(View.VISIBLE);
				bCv.setEnabled(true);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			companyLogged = Session.getInstance().getCompanyLogged();
		} catch (DataFormatException e) {
			throw new RuntimeException(e);
		}
		Intent i = getIntent();
		int studentId = i.getIntExtra("studentId", -1);
		if (studentId == -1) {
			throw new RuntimeException("studentId is required"); //TODO: toast?
		}

		offerId = i.getIntExtra("offerId", -1);
		setContentView(R.layout.activity_show_student_profile);

		rlDiscard = (RelativeLayout) findViewById(R.id.discard_rl);
		if (offerId == -1) {
			rlDiscard.setVisibility(View.GONE);
		} else {
			rlDiscard.setVisibility(View.VISIBLE);
		}
		findViews();
		//fetch and setup
		task1 = Manager.getStudentById(studentId, new Manager.ResultProcessor<Student>() {

			@Override
			public void cancel() {
			}

			@Override
			public void process(final Student arg, Exception e) {
				student = arg;
				setupViewsAndCallbacks();
			}
		});
	}

	private void findViews() {
		tvEmail = (TextView) findViewById(R.id.email);
		ivPhoto = (ImageView) findViewById(R.id.photo);
		pbPhotoSpinner = (ProgressBar) findViewById(R.id.photo_spinner);
		pbCvSpinner = (ProgressBar) findViewById(R.id.cv_pb);
		pbFav = (ProgressBar) findViewById(R.id.fav_pb);
		pbDiscard = (ProgressBar) findViewById(R.id.discard_pb);
		tvFullname = (TextView) findViewById(R.id.fullname);
		bCv = (Button) findViewById(R.id.cv_button);
		tvLinks = (TextView) findViewById(R.id.links);
		tvUniversityCareer = (TextView) findViewById(R.id.university_career);
		tvCompetences = (TextView) findViewById(R.id.competences);
		bAvailability = (Button) findViewById(R.id.availability);
		tvHobbies = (TextView) findViewById(R.id.hobbies);
		bSex = (Button) findViewById(R.id.sex_b);
		tvLocation = (TextView) findViewById(R.id.location_tv);
		bDiscard = (Button) findViewById(R.id.discard_b);
		bFav = (Button) findViewById(R.id.fav_b);
	}

	private void setupViewsAndCallbacks() {
		tvEmail.setText(student.getEmail());
		pbCvSpinner.setVisibility(View.INVISIBLE);

		if (offerId != -1) { //student is being seen by a company to which the student applied for offerId
			bDiscard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("tag", "discard " + offerId + " " + student.getId());
					bDiscard.setVisibility(View.INVISIBLE);
					pbDiscard.setVisibility(View.VISIBLE);

				}
			});
		}

		List<Student> favStudents;
		try {
			favStudents = Session.getInstance().getFavStudents();
		} catch (DataFormatException e) {
			throw new RuntimeException(); //TODO
		}
		boolean isFaved = false;
		if (favStudents != null) {
			for (Student s : favStudents) {
				if (s.getId() == student.getId()) {
					isFaved = true;
				}
			}
		}
		if (isFaved) {
			setButtonForUnfav();

		} else {
			setButtonForFav();

		}

		String fullname = student.getFullname();
		if (fullname == null) {
			tvFullname.setVisibility(View.GONE);
		} else {
			tvFullname.setText(student.getFullname());
		}

		String links = student.getLinksToString(System.getProperty("line.separator")
				+ System.getProperty("line.separator"));
		if (links == null) {
			tvLinks.setVisibility(View.GONE);
		} else {
			tvLinks.setText(links);
		}

		String universityCareer = student.getUniversityCareer();
		if (universityCareer == null) {
			tvUniversityCareer.setVisibility(View.GONE);
		} else {
			tvUniversityCareer.setText(universityCareer);
		}

		String competences = student.getCompetencesToString(", ");
		if (competences == null) {
			tvCompetences.setVisibility(View.GONE);
		} else {
			tvCompetences.setText(competences);
		}

		String hobbies = student.getHobbiesToString(", ");
		if (hobbies == null) {
			tvHobbies.setVisibility(View.GONE);
		} else {
			tvHobbies.setText(hobbies);
		}

		String sex = student.getSex();
		if (sex == null) {
			bSex.setVisibility(View.GONE);
		} else {
			bSex.setText(sex);
		}

		String location = student.getLocation();
		if (location == null || location.equals("")) {
			tvLocation.setVisibility(View.GONE);
		} else {
			tvLocation.setText(location);
		}

		String url = student.getPhotoUrl();
		TransferController.download(getApplicationContext(), new String[]{url});
		bCv.setEnabled(false);
		tvFullname.setVisibility(View.INVISIBLE);
		pbPhotoSpinner.setVisibility(ProgressBar.VISIBLE);

		final String cvUrl = student.getCvUrl();
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

		boolean isAvailable = student.isAvailable();
		if (isAvailable) {
			bAvailability.setText(getResources().getString(R.string.available_for_jobs));
			bAvailability.getBackground().setColorFilter(getResources().getColor(R.color.green_ok), PorterDuff.Mode.MULTIPLY);
		} else {
			bAvailability.setText(getResources().getString(R.string.not_available_for_jobs));
			bAvailability.getBackground().setColorFilter(getResources().getColor(R.color.red_warning), PorterDuff.Mode.MULTIPLY);
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
	}

	@Override
	protected void onPause() {
		unregisterReceiver(downloadfinished);
		if (task1 != null) {
			task1.cancel(true);
			task1 = null;
		}
		if (task2 != null) {
			task2.cancel(true);
			task2 = null;
		}
		if (task3 != null) {
			task3.cancel(true);
			task3 = null;
		}
		super.onPause();
	}

	private void setButtonForUnfav() {
		bFav.setOnClickListener(null);
		bFav.setText(getResources().getString(R.string.unfav));
		bFav.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pbFav.setVisibility(View.VISIBLE);
				bFav.setVisibility(View.INVISIBLE);
				task3 = Manager.deleteAFavouriteStudentOfACompany(companyLogged.getId(), student.getId(), new Manager.ResultProcessor<Integer>() {

					@Override
					public void cancel() {
					}

					@Override
					public void process(final Integer i, Exception e) {
						pbFav.setVisibility(View.INVISIBLE);
						bFav.setVisibility(View.VISIBLE);
						try {
							Session.getInstance().getFavStudents().remove(student);
						} catch (DataFormatException ee) {
							throw new RuntimeException(ee);
						}
						setButtonForFav();
					}
				});
			}
		});
	}

	private void setButtonForFav() {
		bFav.setOnClickListener(null);
		bFav.setText(getResources().getString(R.string.fav));
		bFav.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pbFav.setVisibility(View.VISIBLE);
				bFav.setVisibility(View.INVISIBLE);
				task3 = Manager.addFavouriteStudentForCompany(companyLogged.getId(), student.getId(), new Manager.ResultProcessor<Student>() {

					@Override
					public void cancel() {
					}

					@Override
					public void process(final Student s, Exception e) {
						pbFav.setVisibility(View.INVISIBLE);
						bFav.setVisibility(View.VISIBLE);
						try {
							Session.getInstance().getFavStudents().add(student);
						} catch (DataFormatException ee) {
							throw new RuntimeException(ee);
						}
						setButtonForUnfav();
					}
				});
			}
		});
	}
}
