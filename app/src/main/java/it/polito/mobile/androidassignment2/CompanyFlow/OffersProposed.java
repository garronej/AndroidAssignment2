package it.polito.mobile.androidassignment2.CompanyFlow;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class OffersProposed extends AppCompatActivity {


    private ListView offerList;
    private AsyncTask<Object, Void, Object> task;


    private void addTabMenuButtonCallbacks(){

        findViewById(R.id.tab_menu_company_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Intent i = new Intent(getApplicationContext(), .class);
                //startActivity(i);
            }
        });
        findViewById(R.id.tab_menu_company_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Intent i = new Intent(getApplicationContext(), .class);
                //startActivity(i);
            }
        });
        findViewById(R.id.tab_menu_company_students).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentsFavouritesActivity.class);
                startActivity(i);
            }
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_proposed);
        offerList=(ListView)findViewById(R.id.proposed_offers_list);
        addTabMenuButtonCallbacks();



        findViewById(R.id.add_job_offer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), JobOfferCreation.class);
            startActivity(i);
            }
        });

        offerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), OfferShowActivity.class);
                i.putExtra("offerId", (int)id);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Offer o = new Offer();
        try {
            o.setCompanyId(Session.getInstance().getCompanyLogged().getId());
            task = Manager.getOffersMatchingCriteria(o, new Manager.ResultProcessor<List<Offer>>() {
                @Override
                public void process(final List<Offer> arg, Exception e) {
                    task=null;

                    if(e!=null){
                        //TODO: show error messagge
                    }
                    if(arg.size()==0){
                        //TODO: show some messagge...
                    }

                    offerList.setAdapter(new BaseAdapter() {
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
                            ((TextView) convertView.findViewById(R.id.mainName)).setText(((Offer) getItem(position)).getDescriptionOfWork());
                            ((TextView) convertView.findViewById(R.id.descrption)).setText(((Offer) getItem(position)).getCompanyName());
                            return convertView;
                        }
                    });



                }

                @Override
                public void cancel() {
                    task=null;
                }
            });
        }catch (Exception e){
            // we should never be here....
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null){
            task.cancel(true);
            task=null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_offers_proposed, menu);
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
