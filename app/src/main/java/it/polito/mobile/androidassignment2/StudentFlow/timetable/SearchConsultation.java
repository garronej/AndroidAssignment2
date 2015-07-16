package it.polito.mobile.androidassignment2.StudentFlow.timetable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.NavigationDrawerFragment;
import it.polito.mobile.androidassignment2.businessLogic.timetable.Consultation;
import it.polito.mobile.androidassignment2.businessLogic.timetable.TimeTableData;
import it.polito.mobile.androidassignment2.context.AppContext;


public class SearchConsultation extends AppCompatActivity {



        private AutoCompleteTextView autoCompleteTextView1 = null;

        private Button button = null;

        private TimeTableData timeTableData;

        private List<String> teachers = null;


        @Override
        protected void onCreate(Bundle savedInstanceState) {

            //Open keyboard
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            super.onCreate(savedInstanceState);



            this.timeTableData = ((AppContext)getApplicationContext()).getTimeTableData();




            setContentView(R.layout.activity_search_consultation);
            setUpNavigationDrawer();



            autoCompleteTextView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);

            button = (Button) findViewById(R.id.button);
            button.setVisibility(View.GONE);

            autoCompleteTextView1.addTextChangedListener(this.textWatcher);
            autoCompleteTextView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCompleteTextView1.showDropDown();
                }
            });




            this.teachers = new ArrayList<>();


            for( Consultation consultation : this.timeTableData.getConsultations()){

                boolean addIt = true;
                for( String string : this.teachers)
                    if( string.equals(consultation.getTeacher())) {
                        addIt = false;
                        break;
                    }

                if( addIt )
                teachers.add(consultation.getTeacher());
            }






            String[] pop = teachers.toArray(new String[teachers.size()]);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchConsultation.this, android.R.layout.simple_dropdown_item_1line, pop);
            autoCompleteTextView1.setAdapter(adapter);


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Close keyboard
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(autoCompleteTextView1.getWindowToken(), 0);

                    Intent i = new Intent(SearchConsultation.this, TimeTable.class);
                    i.putExtra("type", "Consultation");


                    String selectedTeacher = autoCompleteTextView1.getText().toString();


                    boolean match = false;
                    for (Consultation consultation : SearchConsultation.this.timeTableData.getConsultations()) {
                        if (consultation.getTeacher().equals(selectedTeacher)) {
                            match = true;
                            break;
                        }

                    }

                    if (!match) {
                        Toast.makeText(SearchConsultation.this, getResources().getString(R.string.noTeacherMatching), Toast.LENGTH_SHORT).show();
                        return;
                    }


                    i.putExtra("selectedTeacher", selectedTeacher);


                    startActivity(i);


                }
            });

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

                if( autoCompleteTextView1.getText().toString().equals("")){
                    button.setText(R.string.selectAteacher);
                    button.setVisibility(View.GONE);
                }else{
                    button.setText(R.string.search);
                    button.setVisibility(View.VISIBLE);
                }
            }
        };


    private void setUpNavigationDrawer(){
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position", 0));

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        //due it's a new instance of NavDraw

        mNavigationDrawerFragment.setTitle(getTitle());
    }

}






