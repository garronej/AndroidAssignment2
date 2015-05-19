package it.polito.mobile.androidassignment2.StudentFlow;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompanyFlow.OfferShowActivity;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.adapter.OfferArrayAdapter;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class OffersListsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//TODO: ( if time ) Make replace by fragment.
		addTabMenuButtonCallbacks();


		final ListView listview = (ListView) findViewById(R.id.proposed_offers_list);



		OfferArrayAdapter adapterTmp = null;
		try {

			List<Offer> offers = new ArrayList<>();

			if( Session.getInstance().getFavoriteOffer().size() != 0 ) {


				offers.addAll(Session.getInstance().getFavoriteOffer());
			}else{

				Toast.makeText(getApplicationContext(), "No favourite offer yet, use the search icon",
						Toast.LENGTH_SHORT).show();
			}

			adapterTmp = new OfferArrayAdapter(OffersListsActivity.this, offers);


		}catch(Exception e) {}

		final OfferArrayAdapter adapter = adapterTmp;

		listview.setAdapter(adapter);


		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									final int position, long id) {

				view.animate().setDuration(300).translationX(1000)
						.withEndAction(new Runnable() {
							@Override
							public void run() {




								Intent i=new Intent(OffersListsActivity.this,OfferShowActivity.class);
								i.putExtra("offerId", adapter.getValue().get(position).getId());


								startActivity(i);



								view.animate().setStartDelay(300).translationX(0);
							}
						});
			}

		});


		((Button)findViewById(R.id.show_favourite)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Offer> offers = adapter.getValue();





				try {

					if( Session.getInstance().getFavoriteOffer().size() == 0 ){
						Toast.makeText(getApplicationContext(), "No favourite offer yet, use the search icon",
								Toast.LENGTH_SHORT).show();
						return;
					}

					offers.clear();

					offers.addAll(Session.getInstance().getFavoriteOffer());
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


		((Button)findViewById(R.id.show_candidature)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<Offer> offers = adapter.getValue();



				try {


					if( Session.getInstance().getAppliedOffers().size() == 0 ){
						Toast.makeText(getApplicationContext(), "No applied offer yet, use the search icon",
								Toast.LENGTH_SHORT).show();
						return;
					}

					offers.clear();

					offers.addAll(Session.getInstance().getAppliedOffers());
				} catch (DataFormatException e) {
					e.printStackTrace();
				}

				listview.animate().setDuration(350).translationX(-1000).withEndAction(new Runnable() {
					@Override
					public void run() {

						adapter.notifyDataSetChanged();

						listview.animate().setDuration(0).translationX(0);
					}
				});


			}
		});


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
			getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
			Intent i = new Intent(this, LoginActivity.class);
			startActivity(i);
			return true;
		}
		if (id == R.id.action_delete) {
			try {
				Manager.deleteStudent(Session.getInstance().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
					@Override
					public void process(Integer arg, Exception e) {
						if (e != null) {
							//TODO: show error message
							return;
						}
						getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
						Intent i = new Intent(OffersListsActivity.this, LoginActivity.class);
						startActivity(i);
					}

					@Override
					public void cancel() {

					}
				});
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
			return true;
		}

		return super.onOptionsItemSelected(item);
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
				Intent i = new Intent(getApplicationContext(), SearchCompanies.class);
				startActivity(i);
			}
		});
		findViewById(R.id.tab_menu_student_profile).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), StudentProfileActivity.class);
				startActivity(i);
			}
		});
		findViewById(R.id.tab_menu_student_offers).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), OffersListsActivity.class);
				startActivity(i);
			}
		});


	}
}