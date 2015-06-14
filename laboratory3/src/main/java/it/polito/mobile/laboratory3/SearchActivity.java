package it.polito.mobile.laboratory3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class SearchActivity extends ActionBarActivity {

	public static final int MAX_RADIUS = 300;
	private SeekBar radiusSeekBar;
	private TextView radiusDisplayText;
	private EditText location;
	private AutoCompleteTextView autocompleteView;
	private EditText categoryText;
	private Button searchBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initializeVariables();
		setSeekBar();
		location_autocomplete();
	}

	private void setSeekBar() {
		// Initialize the textview with '0'.
		radiusDisplayText.setText(radiusSeekBar.getProgress() + " Km");
		radiusSeekBar.setMax(MAX_RADIUS);
		radiusSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress = 0;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				progress = progresValue;
				radiusDisplayText.setText(progress + " Km");
				//Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				//Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				radiusDisplayText.setText(progress + " Km");
				//Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
			}
		});
	}


	private void initializeVariables() {
		radiusSeekBar = (SeekBar) findViewById(R.id.radius_seekBar);
		radiusDisplayText = (TextView) findViewById(R.id.radius_value);
		autocompleteView = (AutoCompleteTextView) findViewById(R.id.location_input);
		location = autocompleteView;
		categoryText = (EditText)findViewById(R.id.category_input);
		searchBtn = (Button) findViewById(R.id.search_go_btn);
		searchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
	}
	private void location_autocomplete() {
		autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.location_list_item));

		autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get data associated with the specified position
				// in the list (AdapterView)
				String description = (String) parent.getItemAtPosition(position);
				Toast.makeText(SearchActivity.this, description, Toast.LENGTH_SHORT).show();
			}
		});
	}


}
