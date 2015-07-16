package it.polito.mobile.androidassignment2.StudentFlow.timetable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import it.polito.mobile.androidassignment2.R;



public class SearchRoom extends Activity {

    private AutoCompleteTextView autoCompleteTextView1 = null;
    private String[] rooms = null;
    private MapRoom mapRoom = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.92);

        setContentView(R.layout.activity_search_room);

        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview

        mapRoom = (MapRoom) getFragmentManager().findFragmentById(R.id.mapRoom);
        mapRoom.generateMap();



        autoCompleteTextView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

        autoCompleteTextView1.addTextChangedListener(this.textWatcher);
        autoCompleteTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView1.showDropDown();
            }
        });

        List<MapRoom.Place> places = MapRoom.getSamplePlaces(this);
        rooms = new String[places.size()];

        for(int i=0; i<rooms.length; i++){
            rooms[i]=places.get(i).title;
        }


        autoCompleteTextView1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, rooms));

    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = autoCompleteTextView1.getText().toString();
                for( int i=0; i< rooms.length; i++){
                    if( input.toUpperCase().equals(rooms[i])){
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(autoCompleteTextView1.getWindowToken(), 0);
                        mapRoom.centerOn(rooms[i]);
                        return;
                    }
                }
        }
    };


}