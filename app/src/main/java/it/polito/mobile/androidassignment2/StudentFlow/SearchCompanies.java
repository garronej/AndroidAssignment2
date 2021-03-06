package it.polito.mobile.androidassignment2.StudentFlow;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.PlacesAutoCompleteAdapter;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;

public class SearchCompanies extends AppCompatActivity {


	private EditText companyLocationText;
	private CompetencesCompletionTextView fieldOfInterestText;
	private Button button;
	private ListView listView;

	private AsyncTask<Object, Void, Object> task = null;
	private AsyncTask<Object, Void, Object> task1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_companies);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		companyLocationText = (EditText) findViewById(R.id.searchCompanyLocation);
		fieldOfInterestText = (CompetencesCompletionTextView) findViewById(R.id.seachCompanyFieldOfInterest);
		location_autocomplete();
		button = (Button) findViewById(R.id.companySearchButton);


		task1=Manager.getAllCompaniesCompetences(new Manager.ResultProcessor<List<String>>() {
			@Override
			public void process(List<String> arg, Exception e) {

				task1=null;
				if (e != null) {

					Toast.makeText( SearchCompanies.this
							, it.polito.mobile.androidassignment2.businessLogic.Utils.processException(e, "Error message"), Toast.LENGTH_SHORT).show();
					return;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchCompanies.this, android.R.layout.simple_list_item_1, arg);

				fieldOfInterestText.setAdapter(adapter);
			}

			@Override
			public void cancel() {
				task1=null;
			}
		});


		listView = new ListView(this);
		listView.setDivider(getResources().getDrawable(R.drawable.items_divider));

		((LinearLayout) findViewById(R.id.search_companies_result_details)).addView(listView);

		button.setFocusableInTouchMode(true);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Company c = new Company();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, 0);

				c.setLocation(companyLocationText.getText().toString());
				if (fieldOfInterestText.getObjects().size() > 0) {
					String[] comp = new String[fieldOfInterestText.getObjects().size()];
					int i = 0;
					for (Object o : fieldOfInterestText.getObjects()) {
						comp[i] = o.toString();
						i++;
					}
					c.setCompetences(comp);
				}


				task = Manager.getCompaniesMatchingCriteria(c, new Manager.ResultProcessor<List<Company>>() {
					@Override
					public void process(final List<Company> arg, Exception e) {
						task = null;
						if (e != null) {
							Log.d(SearchCompanies.class.getSimpleName(), "Error retrieving search results");
							return;
						}
						if (arg.size() == 0) {
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_empty_search), Toast.LENGTH_LONG).show();
						}
						listView.setAdapter(new BaseAdapter() {
							@Override
							public int getCount() {
								return arg.size();
							}

							@Override
							public Object getItem(int position) {
								return arg.get(position);
							}

							@Override
							public long getItemId(int position) {
								return arg.get(position).getId();
							}

							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								if (convertView == null) {
									convertView = getLayoutInflater().inflate(R.layout.list_adapter_item, parent, false);
								}
								((TextView) convertView.findViewById(R.id.mainName)).setText(((Company) getItem(position)).getName());
								((TextView) convertView.findViewById(R.id.descrption)).setText(((Company) getItem(position)).getLocation());
								return convertView;
							}
						});


					}

					@Override
					public void cancel() {
						task = null;
					}
				});

				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						Intent i = new Intent(getApplicationContext(), ShowCompanyProfileActivity.class);
						i.putExtra("companyId", (int) id);
						startActivity(i);


					}
				});
			}
		});
		button.callOnClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_search_companies, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
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
		AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.searchCompanyLocation);
		autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.location_list_item));

		autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get data associated with the specified position
				// in the list (AdapterView)
				String description = (String) parent.getItemAtPosition(position);
				Toast.makeText(SearchCompanies.this, description, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
