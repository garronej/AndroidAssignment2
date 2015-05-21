package it.polito.mobile.androidassignment2.CompanyFlow;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;

public class OffersProposed extends AppCompatActivity implements Communicator {


    private ListView offerList;
    private void myAddActionBar(){
        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.company_tabbed_menu,null);

        // Set up your ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

    }
    private void addTabMenuButtonCallbacks(){
        findViewById(R.id.tab_menu_company_search).setVisibility(View.INVISIBLE);
        findViewById(R.id.tab_menu_company_profile).setBackgroundColor(getResources().getColor(R.color.blue_sky));
        findViewById(R.id.tab_menu_company_offers).setBackgroundColor(getResources().getColor(R.color.strong_blue));
        findViewById(R.id.tab_menu_company_students).setBackgroundColor(getResources().getColor(R.color.blue_sky));
        /*findViewById(R.id.tab_menu_company_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), .class);
                startActivity(i);
            }
        });*/
        findViewById(R.id.tab_menu_company_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CompanyProfileActivity.class);
                startActivity(i);
                finish();
            }
        });
        findViewById(R.id.tab_menu_company_students).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StudentsFavouritesActivity.class);
                startActivity(i);
                finish();
            }
        });

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_proposed);
        offerList=(ListView)findViewById(R.id.proposed_offers_list);
        myAddActionBar();
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
        List<Offer> offers = new ArrayList<Offer>();
        try {
            offers=Session.getInstance().getOfferOfTheLoggedCompany();
        }catch(Exception e){
        //never here
        }
        Offer o = new Offer();
        try {
            o.setCompanyId(Session.getInstance().getCompanyLogged().getId());
        } catch (DataFormatException e) {
            //never here
        }
        Manager.getOffersMatchingCriteria(o, new Manager.ResultProcessor<List<Offer>>() {
            @Override
            public void process(final List<Offer> arg, Exception e) {
                if (e != null) {
                    Log.e(OffersProposed.class.getSimpleName(),"Error retrieving offers list");
                }
                if (arg == null || arg.size() == 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_empty_offer_proposed), Toast.LENGTH_LONG).show();

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
                        ((TextView) convertView.findViewById(R.id.descrption)).setText(((Offer) getItem(position)).getLocation());
                        return convertView;
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            showConfirmAlerter(0);
            return true;
        }
        if (id == R.id.action_delete) {
            showConfirmAlerter(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showConfirmAlerter(int kind) {
        AlertYesNo alert = new AlertYesNo();
        Bundle info = new Bundle();
        if (kind == 0)
            info.putString("message", getResources().getString(R.string.logout_message));
        else info.putString("message", getResources().getString(R.string.delete_user_message));

        info.putString("title", getResources().getString(R.string.confirm));
        info.putInt("kind", kind);
        alert.setCommunicator(this);
        alert.setArguments(info);
        alert.show(getSupportFragmentManager(), "Confirm");

    }

    @Override
    public void goSearch(int kind) {

    }

    @Override
    public void respond(int itemIndex, int kind) {

    }

    @Override
    public void dialogResponse(int result, int kind) {
        if (result == 1) {
            switch (kind) {
                case 0://logout
                    getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1://delete account
                    try {
                        Manager.deleteCompany(Session.getInstance().getCompanyLogged().getId(), new Manager.ResultProcessor<Integer>() {
                            @Override
                            public void process(Integer arg, Exception e) {
                                if (e != null) {
                                    Log.d(OffersProposed.class.getSimpleName(), "Error deleteing account");
                                    return;
                                }
                                getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                    } catch (DataFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    return;
            }
        }
    }
}
