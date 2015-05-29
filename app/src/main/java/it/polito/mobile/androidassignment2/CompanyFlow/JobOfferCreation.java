package it.polito.mobile.androidassignment2.CompanyFlow;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.PlacesAutoCompleteAdapter;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.context.AppContext;

public class JobOfferCreation extends AppCompatActivity {

	private EditText description;
	private EditText numberOfMonths;
	private EditText kindOfContract;
	private EditText location;
	private CompetencesCompletionTextView competences;
	private AsyncTask<Object, Void, Object> task;
	private EditText code;
	private AsyncTask<Object, Void, Object> task1;
	private EditText title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_offer_creation);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		title = (EditText) findViewById(R.id.job_title);
		code = (EditText) findViewById(R.id.job_offer_code);
		description = (EditText) findViewById(R.id.job_offer_description);
		numberOfMonths = (EditText) findViewById(R.id.number_of_months);
		kindOfContract = (EditText) findViewById(R.id.kind_of_contract);
		location = (EditText) findViewById(R.id.location);
		competences = (CompetencesCompletionTextView) findViewById(R.id.competences);
		location_autocomplete();
		task1 = Manager.getAllOffersCompetences(new Manager.ResultProcessor<List<String>>() {
			@Override
			public void process(final List<String> arg, Exception e) {
				task1 = null;
				if (e != null) {
					return;
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(JobOfferCreation.this, android.R.layout.simple_list_item_1, arg);

				competences.setAdapter(adapter);


			}

			@Override
			public void cancel() {
				task1 = null;
			}
		});


		findViewById(R.id.add_job_offer).setFocusableInTouchMode(true);
		findViewById(R.id.add_job_offer).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Offer o = new Offer();
				try {
					o.setCompanyId(((AppContext) getApplication()).getSession().getCompanyLogged().getId());
				} catch (Exception e) {
					//should never be here..
					return;
				}
				o.setDescriptionOfWork(description.getText().toString());
				o.setKindOfContract(kindOfContract.getText().toString());
				if (numberOfMonths.getText().toString().matches("[0-9]+")) {
					o.setDurationMonths(Integer.parseInt(numberOfMonths.getText().toString()));
				}
				o.setCode(code.getText().toString());
				o.setLocation(location.getText().toString());
				o.setTitle(title.getText().toString());
				if (competences.getObjects().size() > 0) {
					String[] comp = new String[competences.getObjects().size()];
					int i = 0;
					for (Object obj : competences.getObjects()) {
						comp[i] = obj.toString();
						i++;
					}
					o.setCompetences(comp);
				}
				task = Manager.insertNewOffer(o, new Manager.ResultProcessor<Offer>() {
					@Override
					public void process(Offer arg, Exception e) {
						task = null;
						if (e != null) {
							Toast.makeText(JobOfferCreation.this, R.string.job_offer_failed, Toast.LENGTH_LONG).show();

							return;
						}

						try {
							((AppContext) getApplication()).getSession().getOfferOfTheLoggedCompany().add(arg);
						} catch (DataFormatException e1) {
							//never here
						}
						finish();
					}

					@Override
					public void cancel() {
						task = null;
					}
				});


			}
		});
	}


	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) {
			task.cancel(true);
			task = null;
		}
		if (task1 != null) {
			task1.cancel(true);
			task1 = null;
		}

	}

	private void location_autocomplete() {
		AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.location);
		autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.location_list_item));

		autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get data associated with the specified position
				// in the list (AdapterView)
				String description = (String) parent.getItemAtPosition(position);
				Toast.makeText(JobOfferCreation.this, description, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
