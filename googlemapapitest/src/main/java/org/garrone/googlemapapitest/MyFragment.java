package org.garrone.googlemapapitest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 13/06/2015.
 */
public class MyFragment extends Fragment {

    public static class  Place{
        public LatLng coordinate = null;
        public String title = null;
        public String snippet = null;
    }

    private List<Place> places = new ArrayList<>();
    private MapView mapView;

    public static List<Place> getSamplePlaces(){

        List<Place> out =new ArrayList<>();

        Place place = new Place();
        place.coordinate = new LatLng(43.614258,3.870448);
        place.title = "Joseph Garrone";
        place.snippet = "Joseph's house";
        out.add(place);

        place = new Place();
        place.coordinate = new LatLng(43.613077,3.876057);
        place.title = "Gabrielle Rucheton";
        place.snippet = "Gabrielle's house";
        out.add(place);

        place = new Place();
        place.coordinate = new LatLng(43.609192,3.876008);
        place.title = "Jean-Marie Lepen";
        place.snippet = "666's house";
        out.add(place);

        return out;

    }


    public void setPlaces(List<Place> places){
        this.places = places;
    }

    public List<Place> getPlaces(){
        return this.places;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View v = inflater.inflate(R.layout.my_fragment, container, false);


        this.mapView = (MapView) v.findViewById(R.id.map);
        this.mapView.onCreate(savedInstanceState);


        //By uncommenting that there will be nothing to do in the main activity.

        //this.setPlaces(getSamplePlaces());
        //this.generateMap();


        return v;
    }


    public void generateMap(){

        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap map) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (Place place : MyFragment.this.places) {

                    //Pre-positioning.
                    if (MyFragment.this.places.indexOf(place) == 0) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyFragment.this.places.get(0).coordinate, 13));
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
                            MyFragment.this.startActivity(arg0);
                            return true;
                        } else {
                            return false;
                        }
                    }

                });

            }
        });

    }






    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




    //Start activity after ckick on a marker.
    private void startActivity(Marker marker){

        Intent intent = new Intent(MyFragment.this.getActivity(),DetailActivity.class);
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
