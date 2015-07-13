package it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.NoticeBoard;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.notice.ShowNoticeActivity;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.noticesList.NoticesListView;
import it.polito.mobile.androidassignment2.businessLogic.Notice;
import it.polito.mobile.androidassignment2.businessLogic.RESTManager;


/**
 * Created by mark9 on 17/06/15.
 */
public class AllNoticesFragment extends Fragment implements NoticeFragment {

    private static int NUMBER_OF_ITEMS_PER_PAGE = 10;
    private View root;
    private NoticesListView list;
    private boolean currentAsMap = false;
    private int countOfNotices = 0;

    private List<AsyncTask<?,?,?>> pendingTasks = new ArrayList<>();
    private MapView mapView;
    private Map<String, Notice> markerIdToNotice;

    public AllNoticesFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NoticeBoard activity =((NoticeBoard)getActivity());
        if(activity.isShowAsMap()){
            root = inflater.inflate(R.layout.fragment_notice_map, container, false);
        }else{
            root = inflater.inflate(R.layout.fragment_notice_list, container, false);
        }
        list=((NoticesListView) root.findViewById(R.id.notice_list));
        mapView = (MapView) root.findViewById(R.id.map_of_notices);
        markerIdToNotice=new HashMap<>();
        if(mapView!=null){
            mapView.onCreate(savedInstanceState);
        }

        initWithData();

        View btnMore = root.findViewById(R.id.btn_load_more);

        if(btnMore != null){
            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                addPageOfData();
                }
            });

        }


        return root;

    }

    private void initWithData() {
        //fill with initial data
        AsyncTask<Integer, Integer, List<Notice>> t = new AsyncTask<Integer, Integer, List<Notice>>() {
            Exception e=null;
            @Override
            protected List<Notice> doInBackground(Integer... integers) {
                Map<String, String> params = getSearchFilters();

                params.put("notice[page]","0");
                params.put("notice[items_per_page]",""+NUMBER_OF_ITEMS_PER_PAGE);

                List<Notice> notices = new ArrayList<>();
                try {
                    String response = RESTManager.send(RESTManager.GET, "notices", params);
                    JSONArray jsonResp = (new JSONObject(response)).getJSONArray("notices");
                    for(int i=0;i<jsonResp.length();i++){
                        notices.add(new Notice(jsonResp.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return notices;
            }

            @Override
            protected void onPostExecute(List<Notice> notices) {
                super.onPostExecute(notices);
                if(e!=null){
                    Toast.makeText(AllNoticesFragment.this.getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }

                countOfNotices=notices.size();
                initList(notices);
                initMap(notices);


            }
        };

        t.execute();
        pendingTasks.add(t);

    }

    private void initMap(final List<Notice> notices) {
        if(mapView!=null && notices.size()>0) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    googleMap.clear();
                    for (Notice n : notices) {
                        Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(n.getLatitude(), n.getLongitude()))
                                        .title(n.getTitle())
                                        .snippet(n.getDescription())
                        );
                        markerIdToNotice.put(m.getId(), n);

                        builder.include(new LatLng(n.getLatitude(), n.getLongitude()));
                    }


                    LatLngBounds bounds = builder.build();
                    int padding = 50;
                    final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            googleMap.animateCamera(cu);
                        }
                    });

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker arg0) {
                            Intent i = new Intent(getActivity(),ShowNoticeActivity.class);
                            i.putExtra("noticeId",markerIdToNotice.get(arg0.getId()).getId());
                            startActivity(i);
                        }

                    });
                }


            });
        }
    }

    private void initList(List<Notice> notices){
        if(list!=null){
            list.setContent(AllNoticesFragment.this.getActivity(), notices);
            //add the listener
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), ShowNoticeActivity.class);

                    intent.putExtra("noticeId", (int)l);
                    startActivity(intent);

                }
            });
        }

    }


    private void addPageOfData(){
        AsyncTask<Integer, Integer, List<Notice>> t = new AsyncTask<Integer, Integer, List<Notice>>() {
            Exception e = null;

            @Override
            protected List<Notice> doInBackground(Integer... integers) {

                Map<String, String> params = getSearchFilters();
                params.put("notice[page]", "" + Math.ceil(countOfNotices / (double)NUMBER_OF_ITEMS_PER_PAGE));
                params.put("notice[items_per_page]", ""+NUMBER_OF_ITEMS_PER_PAGE);

                List<Notice> notices = new ArrayList<>();
                try {
                    String response = RESTManager.send(RESTManager.GET, "notices", params);
                    JSONArray obj = (new JSONObject(response)).getJSONArray("notices");
                    for (int i = 0; i < obj.length(); i++) {
                        notices.add(new Notice(obj.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    this.e = e;
                }
                return notices;
            }

            @Override
            protected void onPostExecute(final List<Notice> notices) {
                super.onPostExecute(notices);
                if (e != null) {
                    Toast.makeText(AllNoticesFragment.this.getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }
                countOfNotices+=notices.size();
                if(list!=null)
                    list.addNotices(notices);

                if(mapView!=null){
                    if(notices.size()>0) {
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(final GoogleMap googleMap) {
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                for (Notice n : notices) {
                                    Marker m=googleMap.addMarker(new MarkerOptions().position(new LatLng(n.getLatitude(), n.getLongitude()))
                                            .title(n.getTitle())
                                            .snippet(n.getDescription()));
                                    builder.include(new LatLng(n.getLatitude(), n.getLongitude()));
                                    markerIdToNotice.put(m.getId(), n);
                                }


                                LatLngBounds bounds = builder.build();
                                int padding = 30; // offset from edges of the map in pixels
                                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                    @Override
                                    public void onMapLoaded() {
                                        googleMap.animateCamera(cu);
                                    }
                                });

                                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                    @Override
                                    public void onInfoWindowClick(Marker arg0) {
                                        Intent i = new Intent(getActivity(), ShowNoticeActivity.class);
                                        i.putExtra("noticeId", markerIdToNotice.get(arg0.getId()).getId());
                                        startActivity(i);
                                    }

                                });
                            }
                        });
                    }
                }
            }
        };
        t.execute();
        pendingTasks.add(t);
    }



    private Map<String,String> getSearchFilters(){
        final HashMap<String, String> params = new HashMap<>();
        if(!(getActivity() != null && getActivity() instanceof NoticeBoard)) return params;
        NoticeBoard activity =((NoticeBoard)getActivity());

        if(activity.getFilters() != null){

            if(activity.getFilters().getString("location")!=null
                    && !activity.getFilters().getString("location").equals("")){
                params.put("notice[full_location]", activity.getFilters().getString("location"));
            }
            if(activity.getFilters().getInt("radius", -1)!=-1){
                params.put("notice[radius]", ""+activity.getFilters().getInt("radius", -1));
            }
            if(activity.getFilters().getInt("size", -1)!=-1){
                params.put("notice[size]", ""+activity.getFilters().getInt("size", -1));
            }
            if(activity.getFilters().getInt("price", -1)!=-1){
                params.put("notice[price]", ""+activity.getFilters().getInt("price", -1));
            }
            if(activity.getFilters().getString("categories") !=null
                    && !activity.getFilters().getString("categories").equals("")){
                params.put("notice[tags]", activity.getFilters().getString("categories"));
            }
            String[] sortOptions = getResources().getStringArray(R.array.sort_order);
            int index = -1;
            for(int i = 0; i<sortOptions.length;i++){
                if(sortOptions[i].equals(activity.getFilters().getString("sort"))) {
                    index=i;
                    break;
                }
            }

            switch(index){
                case 0:
                    params.put("notice[date_order]", "desc");
                    break;
                case 1:
                    params.put("notice[date_order]", "asc");
                    break;
                case 2:
                    params.put("notice[price_order]", "asc");
                    break;
                case 3:
                    params.put("notice[price_order]", "desc");
                    break;
                case 4:
                    params.put("notice[size_order]", "asc");
                    break;
                case 5:
                    params.put("notice[size_order]", "desc");
                    break;
            }

        }

        return params;
    }

    @Override
    public void onResume() {
        if(mapView!=null)
            mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView!=null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapView!=null)
            mapView.onLowMemory();
    }


    @Override
    public void refresh() {
        initWithData();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        for(AsyncTask<?,?,?> t : pendingTasks){
            if(t.getStatus()== AsyncTask.Status.PENDING
                    || t.getStatus()== AsyncTask.Status.RUNNING)
                t.cancel(true);
        }
        pendingTasks.clear();
    }
}
