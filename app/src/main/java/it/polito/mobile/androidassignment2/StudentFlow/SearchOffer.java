package it.polito.mobile.androidassignment2.StudentFlow;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompanyFlow.OfferShowActivity;
import it.polito.mobile.androidassignment2.CompetencesCompletionTextView;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.adapter.OfferArrayAdapter;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;

public class SearchOffer extends AppCompatActivity {


    private EditText editText1;
    private CompetencesCompletionTextView editText2;
    private EditText editText3;
    private EditText editText4;

    private Button button;
    private ListView listView;

    private AsyncTask<Object, Void, Object> task = null;


    private OfferArrayAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_offer);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (CompetencesCompletionTextView) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);

        button = (Button) findViewById(R.id.button);


        Manager.getAllCompaniesCompetences(new Manager.ResultProcessor<List<String>>() {
            @Override
            public void process(List<String> arg, Exception e) {

                if( e != null ){
                    Log.d(SearchOffer.class.getName(), "error in getAllCompaniesCompetences");
                    return;
                }
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(SearchOffer.this, android.R.layout.simple_list_item_1, arg);

                editText2.setAdapter(adapter);
            }

            @Override
            public void cancel() {

            }
        });





        listView = new ListView(this);
        listView.setDivider(getResources().getDrawable(R.drawable.items_divider));

        ((LinearLayout)findViewById(R.id.linearLayout)).addView(listView);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Offer criteria=new Offer();


                criteria.setLocation(editText1.getText().toString());
                if(editText2.getObjects().size() > 0){
                    String[] comp = new String[editText2.getObjects().size()];
                    int i=0;
                    for(Object o : editText2.getObjects()){
                        comp[i]=o.toString();
                        i++;
                    }
                    criteria.setCompetences(comp);
                }



                try {
                    criteria.setCompanyName(editText3.getText().toString());
                }catch(DataFormatException e){
                    Log.d(SearchOffer.class.getName(),
                            "Error in  criteria.setCompanyName(editText3.getText().toString())");
                    Toast.makeText(SearchOffer.this,"Invalid company name !", Toast.LENGTH_SHORT).show();
                }

                    criteria.setKindOfContract(editText4.getText().toString());





                Log.d(SearchOffer.class.getName(),"Criteria = " + criteria.toString());



                task=Manager.getOffersMatchingCriteria(criteria, new Manager.ResultProcessor<List<Offer>>() {
                    @Override
                    public void process(final List<Offer> arg, Exception e) {
                        task = null;
                        if (e != null) {
                            Log.d(SearchOffer.class.getName(), "Error in getOffersMatchingCriteria");
                            return;
                        }
                        if (arg.size() == 0) {
                            Toast.makeText(SearchOffer.this,"No offer matching search criteria", Toast.LENGTH_SHORT).show();
                        }


                       SearchOffer.this.adapter = new OfferArrayAdapter(SearchOffer.this, arg);


                        listView.setAdapter(SearchOffer.this.adapter);


                    }

                    @Override
                    public void cancel() {
                        SearchOffer.this.task = null;
                    }
                });




                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            final int position, long id) {

                        Intent i=new Intent(SearchOffer.this,OfferShowActivity.class);
                        i.putExtra("offerId", (int)id);
                        i.putExtra("student_flow",(boolean)true);

                        startActivity(i);

                    }

                });


            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if( this.adapter != null ) {
            this.adapter.notifyDataSetChanged();
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
}
