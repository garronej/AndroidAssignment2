package it.polito.mobile.androidassignment2.CompanyFlow;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.ShowStudentProfileActivity;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;

public class StudentsFavouritesActivity extends Activity {


    private ListView listView;


    private void addTabMenuButtonCallbacks(){
        //findViewById(R.id.tab_menu_student_companies)
        findViewById(R.id.tab_menu_company_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchStudents.class);
                startActivity(i);
            }
        });
        findViewById(R.id.tab_menu_company_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CompanyProfileActivity.class);
                startActivity(i);
            }
        });
        findViewById(R.id.tab_menu_company_offers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OffersProposed.class);
                startActivity(i);
            }
        });

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_favourites);
        addTabMenuButtonCallbacks();
        listView = new ListView(this);
        listView.setDivider(getResources().getDrawable(R.drawable.items_divider));
        ((LinearLayout)findViewById(R.id.favourite_students_list)).addView(listView);

        List<Student> students = null;
        try {
            students = Session.getInstance().getFavStudents();
        } catch (DataFormatException e) {
            //never here
        }
        final List<Student> finalStudents = students;
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return finalStudents.size();
            }

            @Override
            public Object getItem(int position) {
                return finalStudents.get(position);
            }

            @Override
            public long getItemId(int position) {
                return finalStudents.get(position).getId();
            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_adapter_item, parent, false);
                }
                ((TextView) convertView.findViewById(R.id.mainName)).setText(((Student) getItem(position)).getFullname());
                ((TextView) convertView.findViewById(R.id.descrption)).setText(((Student) getItem(position)).getUniversityCareer());
                return convertView;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(), ShowStudentProfileActivity.class);
                i.putExtra("studentId", (int)id);
                startActivity(i);


            }
        });


    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_companies_favourites, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
