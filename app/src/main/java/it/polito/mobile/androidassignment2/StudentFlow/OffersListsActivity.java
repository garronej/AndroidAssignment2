package it.polito.mobile.androidassignment2.StudentFlow;

import android.content.Intent;

import android.support.v7.app.ActionBar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.CompanyFlow.OfferShowActivity;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.adapter.OfferArrayAdapter;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.context.AppContext;


public class OffersListsActivity extends AppCompatActivity implements Communicator {

	protected Boolean isOnFav = null;
	private OfferArrayAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offers_lists);
		myAddActionBar();
		addTabMenuButtonCallbacks();


		final ListView listview = (ListView) findViewById(R.id.proposed_offers_list);



		try {

			List<Offer> offers = new ArrayList<>();

			if (((AppContext)getApplication()).getSession().getFavoriteOffer().size() != 0) {


				offers.addAll(((AppContext)getApplication()).getSession().getFavoriteOffer());
			} else {

				Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_fav_offers_yet),
						Toast.LENGTH_SHORT).show();
			}

			this.adapter = new OfferArrayAdapter(OffersListsActivity.this, offers);


		} catch (Exception e) {
		}



		listview.setAdapter(this.adapter);


		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									final int position, long id) {


				Intent i = new Intent(OffersListsActivity.this, OfferShowActivity.class);
				i.putExtra("offerId", (int) id);
				i.putExtra("student_flow", (boolean) true);

				startActivity(i);


			}

		});


		(findViewById(R.id.show_favourite)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				OffersListsActivity.this.isOnFav = true;
				findViewById(R.id.show_favourite).setBackgroundColor(getResources().getColor(R.color.strong_blue));
				findViewById(R.id.show_candidature).setBackgroundColor(getResources().getColor(R.color.blue_sky));
				//findViewById(R.id.show_favourite).getBackground().setColorFilter(getResources().getColor(R.color.strong_blue), PorterDuff.Mode.MULTIPLY);
				//findViewById(R.id.show_candidature).getBackground().setColorFilter(getResources().getColor(R.color.blue_sky), PorterDuff.Mode.MULTIPLY);

				List<Offer> offers = adapter.getValue();


				try {

					if (((AppContext)getApplication()).getSession().getFavoriteOffer().size() == 0) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_fav_offers_yet),
								Toast.LENGTH_SHORT).show();

					}

					offers.clear();

					offers.addAll(((AppContext)getApplication()).getSession().getFavoriteOffer());
				} catch (DataFormatException e) {
					e.printStackTrace();
				}


				listview.animate().setDuration(350).translationX(1000).withEndAction(new Runnable() {
					@Override
					public void run() {

						adapter.notifyDataSetChanged();
						listview.animate().setDuration(0).translationX(0);
					}
				});


			}
		});


		(findViewById(R.id.show_candidature)).setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View v) {

				OffersListsActivity.this.isOnFav = false;
				findViewById(R.id.show_favourite).setBackgroundColor(getResources().getColor(R.color.blue_sky));
				findViewById(R.id.show_candidature).setBackgroundColor(getResources().getColor(R.color.strong_blue));
				List<Offer> offers = adapter.getValue();


				try {


					if (((AppContext)getApplication()).getSession().getAppliedOffers().size() == 0) {
						Toast.makeText(getApplicationContext(), "No applied offer yet, use the search icon",
								Toast.LENGTH_SHORT).show();

					}

					offers.clear();

					offers.addAll(((AppContext)getApplication()).getSession().getAppliedOffers());
				} catch (DataFormatException e) {
					e.printStackTrace();
				}
				listview.animate().setDuration(350).translationX(-1000)
						.withEndAction(new Runnable() {
							@Override
							public void run() {

								adapter.notifyDataSetChanged();
								listview.animate().setDuration(0).translationX(0);
							}
						});


			}
		});
		//findViewById(R.id.show_favourite).callOnClick();

	}


	@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first


		if (this.isOnFav == null) {
			this.isOnFav = true;
		} else {

			if (this.isOnFav) {
				(findViewById(R.id.show_favourite)).callOnClick();
			} else {
				(findViewById(R.id.show_candidature)).callOnClick();
			}
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
						Manager.deleteStudent(((AppContext)getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
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

	private void myAddActionBar() {
		ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
				R.layout.student_tabbed_menu, null);

		// Set up your ActionBar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);

	}

	private void addTabMenuButtonCallbacks() {
		//findViewById(R.id.tab_menu_student_companies)
		setContentView(R.layout.activity_offers_lists);
		findViewById(R.id.tab_menu_student_profile).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_offers).setBackgroundColor(getResources().getColor(R.color.strong_blue));
		findViewById(R.id.tab_menu_student_companies).setBackgroundColor(getResources().getColor(R.color.blue_sky));
		findViewById(R.id.tab_menu_student_search).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SearchOffer.class);
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
		findViewById(R.id.tab_menu_student_companies).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), CompaniesFavouritesActivity.class);
				startActivity(i);
				finish();
			}
		});


	}

}