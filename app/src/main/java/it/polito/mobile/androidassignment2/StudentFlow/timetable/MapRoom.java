package it.polito.mobile.androidassignment2.StudentFlow.timetable;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import it.polito.mobile.androidassignment2.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapRoom extends Fragment {


    public static class  Place{
        public LatLng coordinate = null;
        public String title = null;
        public String snippet = null;
    }

    private List<Place> places = new ArrayList<>();
    private MapView mapView;

    public List<Place> getSamplePlaces(){

        List<Place> out =new ArrayList<>();

        String resp = null;
        try {
            InputStream is = (this.getActivity()).getAssets().open("room_coordinates.json");
            Scanner scan = new Scanner(is);
            resp = new String();
            while (scan.hasNext())
                resp += scan.nextLine();
            scan.close();
        }catch(IOException exception){
            throw new RuntimeException(exception.getMessage());
        }


        try {

            JSONArray roomsJson = new JSONArray(resp);

            Place place;
            for (int i = 0; i < roomsJson.length(); i++) {
                JSONObject roomJson = roomsJson.getJSONObject(i);


                place = new Place();
                place.coordinate = new LatLng(roomJson.getDouble("lat"),roomJson.getDouble("lng"));
                place.title = roomJson.getString("name");
                place.snippet = roomJson.getString("info");
                out.add(place);

            }


        } catch (JSONException e) {
            throw new RuntimeException();
        }

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

        View v = inflater.inflate(R.layout.map_room, container, false);


        this.mapView = (MapView) v.findViewById(R.id.map);
        this.mapView.onCreate(savedInstanceState);


        //By uncommenting that there will be nothing to do in the main activity.

        this.setPlaces(getSamplePlaces());
        //this.generateMap();


        return v;
    }


    private String centerMarkerTitle = null;

    public void setCenterMarkerTitle(String centerMarkerTitle){
        this.centerMarkerTitle = centerMarkerTitle;
    }




    public void generateMap(){

        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap map) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (Place place : MapRoom.this.places) {

                    MarkerOptions markerOptions = new MarkerOptions().position(place.coordinate)
                            .title(place.title)
                            .snippet(place.snippet)
                            .alpha(0.3f)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));



                    if( MapRoom.this.centerMarkerTitle != null && place.title.equals(MapRoom.this.centerMarkerTitle)){
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.coordinate, 18));
                            builder=null;
                            markerOptions.alpha(1.0f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    map.addMarker(markerOptions);

                    try {
                        builder.include(place.coordinate);
                    }catch(NullPointerException exception){}

                }


                //If not explicitly centered on a marker, show them all.
                if( builder != null ) {

                    //Pre-positioning.

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(MapRoom.this.places.get(0).coordinate, 15));

                    LatLngBounds bounds = builder.build();
                    int padding = 30; // offset from edges of the map in pixels
                    final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            map.animateCamera(cu);
                        }
                    });

                }

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


    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }



}
