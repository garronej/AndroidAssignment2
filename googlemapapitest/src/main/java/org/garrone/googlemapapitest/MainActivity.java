package org.garrone.googlemapapitest;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public class Place{
        public LatLng coordinate = null;
        public String title = null;
        public String snippet = null;
    }


    private List<Place> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        this.initPlaces();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for( Place place : this.places){

            //Pre-positioning.
            if( this.places.indexOf(place) == 0) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(this.places.get(0).coordinate, 13));
            }


            map.addMarker(new MarkerOptions().position(place.coordinate)
            .title(place.title)
            .snippet(place.snippet));

            builder.include(place.coordinate);
        }


        LatLngBounds bounds = builder.build();
        int padding = 30; // offset from edges of the map in pixels
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.animateCamera(cu);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {


                if (round(map.getCameraPosition().target.latitude, 6) == arg0.getPosition().latitude &&
                        round(map.getCameraPosition().target.longitude, 6) == arg0.getPosition().longitude) {
                    MainActivity.this.startActivity(arg0);
                    return true;
                } else {
                    return false;
                }
            }

        });




    }

    private void initPlaces(){

        Place place = new Place();
        place.coordinate = new LatLng(43.614258,3.870448);
        place.title = "Joseph Garrone";
        place.snippet = "Joseph's house";
        this.places.add(place);

        place = new Place();
        place.coordinate = new LatLng(43.613077,3.876057);
        place.title = "Gabrielle Rucheton";
        place.snippet = "Gabrielle's house";
        this.places.add(place);

        place = new Place();
        place.coordinate = new LatLng(43.609192,3.876008);
        place.title = "Jean-Marie Lepen";
        place.snippet = "666's house";
        this.places.add(place);


    }

    private void startActivity(Marker marker){

        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("coordinate","Lat : " + marker.getPosition().latitude + "; Lng : " + marker.getPosition().longitude);
        intent.putExtra("title",marker.getTitle());
        intent.putExtra("snippet",marker.getSnippet());
        startActivity(intent);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}


