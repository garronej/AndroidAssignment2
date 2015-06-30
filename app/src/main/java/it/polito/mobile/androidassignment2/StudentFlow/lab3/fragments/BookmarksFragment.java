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
import it.polito.mobile.androidassignment2.context.AppContext;


/**
 * Created by mark9 on 17/06/15.
 */
public class BookmarksFragment extends Fragment implements NoticeFragment {
    private View root;
    private NoticesListView list;

    private List<AsyncTask<?,?,?>> pendingTasks = new ArrayList<>();
    private MapView mapView;
    private Map<String, Notice> markerIdToNotice;

    public BookmarksFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        NoticeBoard activity =((NoticeBoard)getActivity());
        if(activity.isShowAsMap()){
            root = inflater.inflate(R.layout.bookmarks_map, container, false);
        }else{
            root = inflater.inflate(R.layout.bookmarks_layout, container, false);
        }
        list=((NoticesListView) root.findViewById(R.id.bookmarks_list));
        mapView = (MapView) root.findViewById(R.id.map_of_bookmarks);
        markerIdToNotice=new HashMap<>();
        if(mapView!=null){
            mapView.onCreate(savedInstanceState);
        }

        initWithData();

        return root;

    }

    private void initWithData() {
        //fill with initial data
        AsyncTask<Integer, Integer, List<Notice>> t = new AsyncTask<Integer, Integer, List<Notice>>() {
            Exception e=null;
            @Override
            protected List<Notice> doInBackground(Integer... integers) {

                List<Notice> notices = new ArrayList<>();
                try {
                    String response = RESTManager.send(RESTManager.GET, "students/" + ((AppContext)getActivity().getApplication()).getSession().getStudentLogged().getId() + "/favs/notices", null);
                    JSONArray obj = (new JSONObject(response)).getJSONArray("fav_notices");
                    for(int i=0;i<obj.length();i++){
                        notices.add(new Notice(obj.getJSONObject(i).getJSONObject("notice")));
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
                    Toast.makeText(BookmarksFragment.this.getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }
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
            list.setContent(BookmarksFragment.this.getActivity(), notices);
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
