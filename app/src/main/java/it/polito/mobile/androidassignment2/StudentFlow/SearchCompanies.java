package it.polito.mobile.androidassignment2.StudentFlow;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;

public class SearchCompanies extends FragmentActivity {


    private EditText companyLocationText;
    private EditText fieldOfInterestText;
    private Button button;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_companies);
        companyLocationText = (EditText) findViewById(R.id.searchCompanyLocation);
        fieldOfInterestText = (EditText) findViewById(R.id.seachCompanyFieldOfInterest);
        button = (Button) findViewById(R.id.companySearchButton);
        listView = new ListView(this);
        listView.setDivider(getResources().getDrawable(R.drawable.items_divider));

        ((LinearLayout)findViewById(R.id.search_companies_result_details)).addView(listView);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Company c=new Company();
                c.setLocation(companyLocationText.getText().toString());
                c.setDescription(fieldOfInterestText.getText().toString());


                Manager.getCompaniesMatchingCriteria(c, new Manager.ResultProcessor<List<Company>>() {
                    @Override
                    public void process(final List<Company> arg, Exception e) {
                        if (e != null) {
                            //TODO: show error message
                            return;
                        }
                        if (arg.size() == 0) {
                            //TODO: display some message...
                        }
                        listView.setAdapter(new BaseAdapter() {
                            @Override
                            public int getCount() {
                                return arg.size();
                            }

                            @Override
                            public Object getItem(int position) {
                                return arg.get(position);
                            }

                            @Override
                            public long getItemId(int position) {
                                return arg.get(position).getId();
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                if (convertView == null) {
                                    convertView = getLayoutInflater().inflate(R.layout.list_adapter_item, parent, false);
                                }
                                ((TextView) convertView.findViewById(R.id.mainName)).setText(((Company) getItem(position)).getName());
                                ((TextView) convertView.findViewById(R.id.descrption)).setText(((Company) getItem(position)).getDescription());
                                return convertView;
                            }
                        });


                    }

                    @Override
                    public void cancel() {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //TODO: start the intent


                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_companies, menu);
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
