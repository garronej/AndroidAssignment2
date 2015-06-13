package org.garrone.googlemapapitest;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        MyFragment myFragment = (MyFragment) getFragmentManager().findFragmentById(R.id.myFragment);


        //Generating sample marker
        myFragment.setPlaces(MyFragment.getSamplePlaces());

        //Adding one more manualy
        MyFragment.Place place = new MyFragment.Place();
        place.coordinate = new LatLng(43.608401,3.879314);
        place.title = "Louis Vuiton";
        place.snippet = "Here is the expensive stuffs";
        myFragment.getPlaces().add(place);

        //Generate the map.
        myFragment.generateMap();

    }


}


