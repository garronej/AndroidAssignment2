package it.polito.mobile.androidassignment2.CompanyFlow;


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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.PlacesAutoCompleteAdapter;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Career;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class SearchStudents extends AppCompatActivity {


	private Button button;
	private ListView listView;
	private AsyncTask<Object, Void, Object> task = null;
	private EditText locationText;
	private CheckBox availability;
	private Spinner sex;
	private AutoCompleteTextView career;
	private CompetencesCompletionTextView competences;
	private AsyncTask<Object, Void, Object> task1 = null;

	private int offerId = -1;
	private EditText minimumMark;
	private Spinner ageSpinner;
	private AsyncTask<Object, Void, Object> task2;

	private void location_autocomplete() {
		AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.student_search_location);
		autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.location_list_item));

		autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get data associated with the specified position
				// in the list (AdapterView)
				String description = (String) parent.getItemAtPosition(position);
				Toast.makeText(SearchStudents.this, description, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_students);

		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		offerId = getIntent().getIntExtra("offerId", -1);
		location_autocomplete();
		locationText = (EditText) findViewById(R.id.student_search_location);
		availability = (CheckBox) findViewById(R.id.student_search_availability);
		sex = (Spinner) findViewById(R.id.student_search_sex);
		ageSpinner = (Spinner) findViewById(R.id.student_search_age);
		career = (AutoCompleteTextView) findViewById(R.id.student_search_career);
		minimumMark = (EditText) findViewById(R.id.student_min_mark);

		competences = (CompetencesCompletionTextView) findViewById(R.id.student_search_keyword);


		task1 = Manager.getAllStudentsCompetences(new Manager.ResultProcessor<List<String>>() {
			@Override
			public void process(final List<String> arg, Exception e) {
				task1 = null;
				if (e != null) {
					return;
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchStudents.this, android.R.layout.simple_list_item_1, arg);

				competences.setAdapter(adapter);


			}

			@Override
			public void cancel() {
				task1 = null;
			}
		});

		task2 = Manager.getAllCareers(new Manager.ResultProcessor<List<String>>() {
			@Override
			public void process(final List<String> arg, Exception e) {
				task2 = null;
				if (e != null) {
					return;
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchStudents.this, android.R.layout.simple_list_item_1, arg);

				career.setAdapter(adapter);


			}

			@Override
			public void cancel() {
				task2 = null;
			}
		});


		button = (Button) findViewById(R.id.studentSearchButton);

		listView = new ListView(this);
		listView.setDivider(getResources().getDrawable(R.drawable.items_divider));

		((LinearLayout) findViewById(R.id.search_students_result_details)).addView(listView);

		button.setFocusableInTouchMode(true);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Student s = new Student();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, 0);

				if (availability.isChecked()) {
					s.setAvailable(true);
				}
				if (sex.getSelectedItemPosition() != 0) {
					s.setSex(sex.getSelectedItem().toString());
				}
				Map<String, String> filters=new HashMap<>();
				if (career.getText().toString() != null && career.getText().length() > 0) {
					filters.put("student[career]", career.getText().toString());

				}
				if (minimumMark.getText().toString() != null && minimumMark.getText().length() > 0) {
					filters.put("student[min_final_grade]", minimumMark.getText().toString());
				}
				switch (ageSpinner.getSelectedItemPosition()){
					case 1:
						filters.put("student[max_age]", "19");
						break;
					case 2:
						filters.put("student[min_age]", "20");
						filters.put("student[max_age]", "25");
						break;
					case 3:
						filters.put("student[min_age]", "26");
						filters.put("student[max_age]", "35");
						break;
					case 4:
						filters.put("student[min_age]", "36");
						filters.put("student[max_age]", "50");
						break;
					case 5:
						filters.put("student[min_age]", "51");
						break;
				}


				if (locationText.getText().toString() != null && locationText.getText().length() > 0) {
					s.setLocation(locationText.getText().toString());
				}

				if (competences.getObjects().size() > 0) {
					String[] comp = new String[competences.getObjects().size()];
					int i = 0;
					for (Object o : competences.getObjects()) {
						comp[i] = o.toString();
						i++;
					}
					s.setCompetences(comp);
				}

				if (offerId == -1) {
					task = Manager.getStudentsMatchingCriteria(s, filters,new Manager.ResultProcessor<List<Student>>() {
						@Override
						public void process(final List<Student> arg, Exception e) {
							task = null;
							if (e != null) {
								Log.d(SearchStudents.class.getSimpleName(), "Error retrieving search result");
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
									((TextView) convertView.findViewById(R.id.mainName)).setText(((Student) getItem(position)).getFullname());
									Career[] cs = ((Student) getItem(position)).getUniversityCareers();
									String s="";
									if(cs.length>0){
										s+=cs[0].getCareer();
										for(int i=1;i<cs.length;i++)
											s+=", "+cs[i].getCareer();
									}
									((TextView) convertView.findViewById(R.id.descrption)).setText(s);
									return convertView;
								}
							});


						}

						@Override
						public void cancel() {
							task = null;
						}
					});
				} else {
					task = Manager.getStudentsOfJobOffer(offerId, s, new Manager.ResultProcessor<List<Student>>() {
						@Override
						public void process(final List<Student> arg, Exception e) {
							task = null;
							if (e != null) {
								Log.d(SearchStudents.class.getSimpleName(), "Error retrieving search result");
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
									((TextView) convertView.findViewById(R.id.mainName)).setText(((Student) getItem(position)).getFullname());
									Career[] cs = ((Student) getItem(position)).getUniversityCareers();
									String s="";
									if(cs.length>0){
										s+=cs[0].getCareer();
										for(int i=1;i<cs.length;i++)
											s+=", "+cs[i].getCareer();
									}
									((TextView) convertView.findViewById(R.id.descrption)).setText(s);
									return convertView;
								}
							});


						}

						@Override
						public void cancel() {
							task = null;
						}
					});


				}

				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Intent i = new Intent(getApplicationContext(), ShowStudentProfileActivity.class);
						i.putExtra("studentId", (int) id);
						i.putExtra("offerId", offerId);
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
		if (task2 != null) {
			task2.cancel(true);
			task2 = null;
		}
	}
}
