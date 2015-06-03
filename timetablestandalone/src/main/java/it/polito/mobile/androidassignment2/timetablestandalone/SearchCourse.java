package it.polito.mobile.androidassignment2.timetablestandalone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.businessLogic.timetable.Course;
import it.polito.mobile.androidassignment2.businessLogic.timetable.TimeTableData;
import it.polito.mobile.androidassignment2.timetablestandalone.context.AppContext;


public class SearchCourse extends AppCompatActivity {





        private AutoCompleteTextView autoCompleteTextView1 = null;
        private AutoCompleteTextView autoCompleteTextView2 = null;
        private Button button = null;

        private TimeTableData timeTableData;

        private List<String> teachers = null;
        private List<String> titles = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.timeTableData = ((AppContext)getApplicationContext()).getTimeTableData();




            setContentView(R.layout.activity_search_course);


            autoCompleteTextView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
            autoCompleteTextView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
            button = (Button) findViewById(R.id.button);

            autoCompleteTextView1.addTextChangedListener(this.textWatcher);
            autoCompleteTextView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCompleteTextView1.showDropDown();
                }
            });

            autoCompleteTextView2.addTextChangedListener(this.textWatcher);
            autoCompleteTextView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoCompleteTextView2.showDropDown();
                }
            });



            this.teachers = new ArrayList<>();
            this.titles = new ArrayList<>();

            for( Course course : this.timeTableData.getCourses()){

                titles.add(course.getTitle());

                boolean addIt = true;
                for( String string : this.teachers)
                    if( string.equals(course.getTeacher())) {
                        addIt = false;
                        break;
                    }

                if( addIt )
                teachers.add(course.getTeacher());

            }


            String[] pop1 = titles.toArray(new String[titles.size()]);
            ArrayAdapter<?> adapter1 = new ArrayAdapter<Object>(SearchCourse.this, android.R.layout.simple_dropdown_item_1line, pop1);
            autoCompleteTextView1.setAdapter(adapter1);



            String[] pop2 = teachers.toArray(new String[teachers.size()]);
            ArrayAdapter<?> adapter2 = new ArrayAdapter<Object>(SearchCourse.this, android.R.layout.simple_dropdown_item_1line, pop2);
            autoCompleteTextView2.setAdapter(adapter2);


            button.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent i = new Intent(SearchCourse.this, TimeTable.class);
                    i.putExtra("type","Course");

                    String selectedCourse = autoCompleteTextView1.getText().toString();

                    if( !selectedCourse.equals("")){

                        boolean match = false;
                        for(Course course : SearchCourse.this.timeTableData.getCourses()){
                            if( course.getTitle().equals(selectedCourse)) {
                                match = true;
                                break;
                            }

                        }

                        if( !match ){
                            Toast.makeText(SearchCourse.this, getResources().getString(R.string.noCourseMatching), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        i.putExtra("selectedCourse",selectedCourse);


                    }

                    String selectedTeacher = autoCompleteTextView2.getText().toString();

                    if( !selectedTeacher.equals("")){


                        boolean match = false;
                        for(Course course : SearchCourse.this.timeTableData.getCourses()){
                            if( course.getTeacher().equals(selectedTeacher)) {
                                match = true;
                                break;
                            }

                        }

                        if( !match ){
                            Toast.makeText(SearchCourse.this, getResources().getString(R.string.noTeacherMatching) , Toast.LENGTH_SHORT).show();
                            return;
                        }


                        i.putExtra("selectedTeacher", selectedTeacher);
                    }


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

                if( autoCompleteTextView1.getText().toString().equals("") && autoCompleteTextView2.getText().toString().equals("") ){
                    button.setText(R.string.seeFull);
                }else{
                    button.setText(R.string.search);
                }
            }
        };

    }


