package it.polito.mobile.androidassignment2.StudentFlow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class OffersListsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offers_lists);





		Session.login("paul.dupont@gmail.com", "passOfPaul", new Manager.ResultProcessor<Integer>() {
			@Override
			public void process(Integer arg, Exception e) {
				if (e != null) {

					Toast.makeText(OffersListsActivity.this, "Error while login in " + e.getMessage(), Toast.LENGTH_SHORT).show();
					return;

				}


				Toast.makeText(OffersListsActivity.this, "Logged in" + e.getMessage(), Toast.LENGTH_SHORT).show();


				final ListView listview = (ListView) findViewById(R.id.listView);
				String[] values = new String[]{"Android", "iPhone", "WindowsMobile",
						"Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
						"Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
						"OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
						"Android", "iPhone", "WindowsMobile"};

				final ArrayList<String> list = new ArrayList<>();
				for (int i = 0; i < values.length; ++i) {
					list.add(values[i]);
				}
				final ArrayAdapter<String> adapter = new ArrayAdapter<>(OffersListsActivity.this, android.R.layout.simple_list_item_1, list);
				listview.setAdapter(adapter);


				listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, final View view,
											int position, long id) {
						final String item = (String) parent.getItemAtPosition(position);
						view.animate().setDuration(2000).alpha(0)
								.withEndAction(new Runnable() {
									@Override
									public void run() {
										list.remove(item);
										adapter.notifyDataSetChanged();
										view.setAlpha(1);
									}
								});
					}

				});


			}

			@Override
			public void cancel() {
			}

		});







	}












/*

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<>();

		public StableArrayAdapter(Context context, int textViewResourceId,
								  List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	*/




















	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_offers_lists, menu);
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
}
