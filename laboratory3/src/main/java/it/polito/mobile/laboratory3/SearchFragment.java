package it.polito.mobile.laboratory3;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

	public static final int MAX_RADIUS = 5000;
	private SeekBar radiusSeekBar;
	private TextView radiusDisplayText;
	private AutoCompleteTextView location;
	private AutoCompletionTextView categoryText;
	private Button searchBtn;
	private Spinner sortSpinner;

	AsyncTask<Integer, Integer, List<String>> t;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View root = inflater.inflate(R.layout.activity_search, container, false);
		initializeVariables(root);

		location_autocomplete();
		return root;
	}



	private void initializeVariables(View root) {
		radiusSeekBar = (SeekBar) root.findViewById(R.id.radius_seekBar);
		radiusDisplayText = (TextView) root.findViewById(R.id.radius_value);
		radiusDisplayText.setText(radiusSeekBar.getProgress() + " m");
		radiusSeekBar.setMax(MAX_RADIUS);
		radiusSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progress = 0;

			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				progress = progresValue;
				radiusDisplayText.setText(progress + " m");
				//Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				//Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				radiusDisplayText.setText(progress + " m");
				//Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
			}
		});

		location = (AutoCompleteTextView) root.findViewById(R.id.location_input);
		sortSpinner = (Spinner) root.findViewById(R.id.sort);
		categoryText = (AutoCompletionTextView) root.findViewById(R.id.category_input);

		searchBtn = (Button) root.findViewById(R.id.search_go_btn);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sort_order));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(adapter);

		final EditText sizeText = (EditText) root.findViewById(R.id.size);
		final EditText priceText = (EditText) root.findViewById(R.id.price);

		final NoticeBoard main = (NoticeBoard) getActivity();

		if(main.searchFilters!=null){
			if(main.searchFilters.getString("location")!=null
					&& !main.searchFilters.getString("location").equals("")){
				location.setText(main.searchFilters.getString("location"));
			}
			radiusSeekBar.setProgress(main.searchFilters.getInt("radius",0));
			if(main.searchFilters.getInt("size",-1)!=-1){
				sizeText.setText("" + main.searchFilters.getInt("size", -1));
			}
			if(main.searchFilters.getInt("price",-1)!=-1){
				priceText.setText("" + main.searchFilters.getInt("price", -1));
			}
			if(main.searchFilters.getString("categories")!=null
					&& !main.searchFilters.getString("categories").equals("")){
				String[] tags=main.searchFilters.getString("categories").split(",");
				for(String s : tags){
					categoryText.addObject(s);
				}
			}
			for(int i=0;i<sortSpinner.getAdapter().getCount();i++){
				if(sortSpinner.getAdapter().getItem(i).toString().equals(main.searchFilters.getString("sort"))){
					sortSpinner.setSelection(i);
					break;
				}
			}
		}




		searchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//sortSpinner.getSelectedItem()


				Bundle b = new Bundle();

				b.putString("location", location.getText().toString());
				b.putInt("radius", radiusSeekBar.getProgress());
				if (!sizeText.getText().toString().equals(""))
					b.putInt("size", Integer.parseInt(sizeText.getText().toString()));
				if (!priceText.getText().toString().equals(""))
					b.putInt("price", Integer.parseInt(priceText.getText().toString()));
				String s = "";
				for (Object o : categoryText.getObjects()) {
					s += o + ",";
				}
				if (s.length() > 0) {
					s = s.substring(0, s.length() - 1);
				}
				b.putString("categories", s);
				b.putString("sort", sortSpinner.getSelectedItem().toString());


				main.applyFiltersFromSearch(b);

			}
		});




		t = new AsyncTask<Integer, Integer, List<String>>() {
			Exception e=null;
			@Override
			protected List<String> doInBackground(Integer... integers) {


				List<String> tags = new ArrayList<>();
				try {
					String response = RESTManager.send(RESTManager.GET, "notices/tags", null);
					JSONArray obj = (new JSONObject(response)).getJSONArray("notice_tags");
					for(int i=0;i<obj.length();i++){
						tags.add(obj.getString(i));
					}
				} catch (Exception e) {
					e.printStackTrace();
					this.e=e;
				}
				return tags;
			}

			@Override
			protected void onPostExecute(List<String> notices) {
				t=null;
				super.onPostExecute(notices);
				if(e!=null){
					Toast.makeText(SearchFragment.this.getActivity(), getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
					return;
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchFragment.this.getActivity(), android.R.layout.simple_list_item_1, notices);

				categoryText.setAdapter(adapter);
			}

		};
		t.execute();


	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(t!=null) t.cancel(true);
	}

	private void location_autocomplete() {
		location.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.location_list_item));

		location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get data associated with the specified position
				// in the list (AdapterView)
				String description = (String) parent.getItemAtPosition(position);
				Toast.makeText(SearchFragment.this.getActivity(), description, Toast.LENGTH_SHORT).show();
			}
		});
	}


}
