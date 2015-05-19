package it.polito.mobile.androidassignment2.StudentFlow;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class CompaniesFavouritesActivity extends ActionBarActivity implements Communicator {


    private ListView listView;
    private AsyncTask<Object, Void, Object> task;
    private void myAddActionBar(){
        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.student_tabbed_menu,null);

        // Set up your ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

    }

    private void addTabMenuButtonCallbacks(){
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
        listView = new ListView(this);
        listView.setDivider(getResources().getDrawable(R.drawable.items_divider));
        ((LinearLayout)findViewById(R.id.favourite_companies_list)).addView(listView);

        try {


            task=Manager.getFavouriteCompanyOfStudent(Session.getInstance().getStudentLogged().getId(), new Manager.ResultProcessor<List<Company>>() {

                @Override
                public void cancel() {
                    task=null;
                }

                @Override
                public void process(final List<Company> arg, Exception e) {
                    task=null;
                    if(e != null){
                        //TODO: show error message
                        return;
                    }

                    if(arg.size()==0){
                        //TODO: display some message...
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
                            ((TextView) convertView.findViewById(R.id.descrption)).setText(((Company) getItem(position)).getDescription());
                            return convertView;
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //TODO: start the intent


                        }
                    });
                }
            });
        }catch(Exception e){
            //TODO: handle exception
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel(true);
            task=null;
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
					getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
					Intent i = new Intent(getApplicationContext(), LoginActivity.class);
					startActivity(i);
					finish();
					break;
				case 1://delete account
					try {
						Manager.deleteStudent(Session.getInstance().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
							@Override
							public void process(Integer arg, Exception e) {
								if (e != null) {
									//TODO: show error message
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
}
