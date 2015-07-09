package it.polito.mobile.androidassignment2.StudentFlow;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;

import it.polito.mobile.androidassignment2.businessLogic.Utils;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.gcm.UnregistrationManager;


public class CompaniesFavouritesActivity extends AppCompatActivity implements Communicator {
    private ListView listView;
    private AsyncTask<?, ?, ?> task = null;
    private AsyncTask<?, ?, ?> task2 = null;
    private AsyncTask<Object, Void, Object> task1;
	private List<Company> companies = new ArrayList<Company>();
	BaseAdapter adapter = null;
	private CharSequence mTitle;

	private boolean firstRun = false;
	private NavigationDrawerFragment mNavigationDrawerFragment;

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
		//findViewById(R.id.tab_menu_student_companies)
		findViewById(R.id.tab_menu_student_profile).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_offers).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_companies).setBackgroundColor(getResources().getColor(R.color.strong_blue));


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
		setContentView(R.layout.activity_companies_favourites);
		myAddActionBar();
		addTabMenuButtonCallbacks();
		setUpNavigationDrawer();
		listView = new ListView(this);
		listView.setDivider(getResources().getDrawable(R.drawable.items_divider));
		((LinearLayout) findViewById(R.id.favourite_companies_list)).addView(listView);
		final TextView emptyMessage = (TextView) findViewById(R.id.empy_favourite_message);
		emptyMessage.setVisibility(View.GONE);

		try {


			task = Manager.getFavouriteCompanyOfStudent(((AppContext) getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<List<Company>>() {

				@Override
				public void cancel() {
					task = null;
				}

				@Override
				public void process(final List<Company> arg, Exception e) {
					task = null;
					if (e != null) {
						Log.d(CompaniesFavouritesActivity.class.getSimpleName(), "Error in getFavouriteCompanyOfStudent");
						return;
					}

					CompaniesFavouritesActivity.this.companies.addAll(arg);

					if (CompaniesFavouritesActivity.this.companies.size() == 0) {
						emptyMessage.setVisibility(View.VISIBLE);
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.favourite_company_empty), Toast.LENGTH_LONG).show();
					}


					listView.setAdapter(adapter = new BaseAdapter() {


						@Override
						public int getCount() {
							return CompaniesFavouritesActivity.this.companies.size();
						}

						@Override
						public Object getItem(int position) {
							return CompaniesFavouritesActivity.this.companies.get(position);
						}

						@Override
						public long getItemId(int position) {
							return CompaniesFavouritesActivity.this.companies.get(position).getId();
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
		} catch (Exception e) {
			//TODO: handle exception
		}
	}


	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first
		final TextView emptyMessage = (TextView) findViewById(R.id.empy_favourite_message);
		emptyMessage.setVisibility(View.GONE);

		if (adapter == null) return;

		Integer studentId = null;

		try {
			studentId = ((AppContext) getApplication()).getSession().getStudentLogged().getId();

		} catch (DataFormatException e) {
		}

		task2 = Manager.getFavouriteCompanyOfStudent(studentId, new Manager.ResultProcessor<List<Company>>() {

			@Override
			public void process(List<Company> arg, Exception e) {
				task2=null;

				if (e != null) {


					Toast.makeText(CompaniesFavouritesActivity.this
							, Utils.processException(e, "Refresh failed"), Toast.LENGTH_SHORT).show();
					return;
				}

				CompaniesFavouritesActivity.this.companies.clear();
				CompaniesFavouritesActivity.this.companies.addAll(arg);


				CompaniesFavouritesActivity.this.adapter.notifyDataSetChanged();
				if (CompaniesFavouritesActivity.this.companies.size() == 0) {
					emptyMessage.setVisibility(View.VISIBLE);
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.favourite_company_empty), Toast.LENGTH_LONG).show();
				}

			}

			@Override
			public void cancel() {

				task2 = null;

			}
		});


	}

    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel(true);
            task=null;
        }
        if(task1!=null){
            task1.cancel(true);
            task1=null;
        }
        if(task2!=null){
            task2.cancel(true);
            task2=null;
        }
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
					new UnregistrationManager(CompaniesFavouritesActivity.this).unregisterGcm();
					getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
					Intent i = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(i);
					finish();
					break;
				case 1://delete account
					try {
						task1=Manager.deleteStudent(((AppContext)getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
							@Override
							public void process(Integer arg, Exception e) {
                                task1=null;
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
                                task1=null;
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
	private void setUpNavigationDrawer(){
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position", 3));

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		//due it's a new instance of NavDraw
	}

}
