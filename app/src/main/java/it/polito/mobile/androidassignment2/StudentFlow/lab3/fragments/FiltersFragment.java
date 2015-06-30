package it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.PlacesAutoCompleteAdapter;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.AutocompleteTokensInput;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.NoticeBoard;
import it.polito.mobile.androidassignment2.businessLogic.RESTManager;


/**
 * Created by mark9 on 27/05/15.
 */
public class FiltersFragment extends Fragment {

    public static final int MAX_METERS = 5000;
    private AsyncTask<Integer, Void, List<String>> t;

    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filters, container, false);
        init(root);


        return root;

    }

    private void init(View root) {
        final SeekBar radius = (SeekBar) root.findViewById(R.id.radius);
        final TextView radiusText = (TextView) root.findViewById(R.id.radius_text);
        final AutoCompleteTextView location = (AutoCompleteTextView) root.findViewById(R.id.location);
        location_autocomplete(location);
        final Spinner sort = (Spinner) root.findViewById(R.id.sort);
        final AutocompleteTokensInput tagTokens = (AutocompleteTokensInput) root.findViewById(R.id.tags);
        setTagsAutocomplete(tagTokens);
        final EditText textSize = (EditText) root.findViewById(R.id.size);
        final EditText priceText = (EditText) root.findViewById(R.id.price);

        Button searchBtn = (Button) root.findViewById(R.id.search);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sort_order));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort.setAdapter(adapter);



        radiusText.setText(radius.getProgress() + " m");
        radius.setMax(MAX_METERS);
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int p = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                p = progressValue;
                radiusText.setText(p + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                radiusText.setText("" + p + " m");
            }
        });

        final NoticeBoard activity = (NoticeBoard) getActivity();

        if(activity.getFilters()!=null){

            radius.setProgress(activity.getFilters().getInt("radius", 0));

            if(activity.getFilters().getInt("size", -1)!=-1){
                textSize.setText("" + activity.getFilters().getInt("size", -1));
            }
            if(activity.getFilters().getString("location")!=null
                    && !activity.getFilters().getString("location").equals("")){
                location.setText(activity.getFilters().getString("location"));
            }
            if(activity.getFilters().getString("categories")!=null
                    && !activity.getFilters().getString("categories").equals("")){
                String[] tags=activity.getFilters().getString("categories").split(",");
                for(String s : tags){
                    tagTokens.addObject(s);
                }
            }
            if(activity.getFilters().getInt("price", -1)!=-1){
                priceText.setText("" + activity.getFilters().getInt("price", -1));
            }

            for(int i=0;i<sort.getAdapter().getCount();i++){
                if(sort.getAdapter().getItem(i).toString().equals(activity.getFilters().getString("sort"))){
                    sort.setSelection(i);
                    break;
                }
            }
        }




        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString("location", location.getText().toString());
                bundle.putInt("radius", radius.getProgress());
                if (!textSize.getText().toString().equals(""))
                    bundle.putInt("size", Integer.parseInt(textSize.getText().toString()));
                if (!priceText.getText().toString().equals(""))
                    bundle.putInt("price", Integer.parseInt(priceText.getText().toString()));
                String s = "";
                for (Object o : tagTokens.getObjects()) {
                    s += o + ",";
                }
                if (s.length() > 0) {
                    s = s.substring(0, s.length() - 1);
                }
                bundle.putString("categories", s);
                bundle.putString("sort", sort.getSelectedItem().toString());


                activity.searchWithFilters(bundle);

            }
        });

    }

    private void setTagsAutocomplete(final AutocompleteTokensInput tagTokens){


        t = new AsyncTask<Integer, Void, List<String>>() {
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
                    Toast.makeText(FiltersFragment.this.getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(FiltersFragment.this.getActivity(), android.R.layout.simple_list_item_1, notices);

                tagTokens.setAdapter(adapter);
            }

        };
        t.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(t!=null) t.cancel(true);
    }

    private void location_autocomplete(AutoCompleteTextView location) {
        location.setAdapter(new PlacesAutoCompleteAdapter(this.getActivity(), R.layout.location_list_item, "address"));

    }


}
