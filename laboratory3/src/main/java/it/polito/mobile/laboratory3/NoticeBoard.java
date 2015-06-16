package it.polito.mobile.laboratory3;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.software.shell.fab.ActionButton;


public class NoticeBoard extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    Fragment searchFragment = null;
    Bundle searchFilters = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public int kindOfMenu=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_notice_board);

        if(savedInstanceState!=null){
            searchFilters = savedInstanceState.getBundle("last_search");
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
	    mViewPager.setAdapter(mSectionsPagerAdapter);


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle("last_search", searchFilters);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_search) {
            //startActivity(new Intent(NoticeBoard.this, SearchActivity.class));
            //return true;
            searchFragment = getSupportFragmentManager().findFragmentByTag("searchFragment");
            Log.d("marco", "search fragment is "+searchFragment);
            if(searchFragment!=null
                    && searchFragment.isVisible()){

                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(searchFragment)
                            .commit();

            }else {
                searchFragment= new SearchActivity();

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.search_container, searchFragment, "searchFragment")
                        .addToBackStack(null)
                        .commit();
                mViewPager.setCurrentItem(0);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void applyFiltersFromSearch(Bundle b){
        searchFilters=b;
        searchFragment = getSupportFragmentManager().findFragmentByTag("searchFragment");

        if(searchFragment!=null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit();
        }
        hideKeyboard();
        ((PlaceholderFragment)mSectionsPagerAdapter.getRegisteredFragment(0)).updateViewContent();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        List<AsyncTask<?,?,?>> pendingTasks = new ArrayList<>();

        private View root=null;
        private int sectionNumber = 0;
        private MapView mapView = null;
        private int numberOfNotices=0;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int layout = 0;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    layout = R.layout.fragment_notice_list;
                    break;
                case 2:
                    layout = R.layout.fragment_bookmarks;
                    break;
                case 3:
                    layout = R.layout.fragment_my_notices;
                    break;
            }


            View rootView = inflater.inflate(layout, container, false);
            root=rootView;
            sectionNumber=getArguments().getInt(ARG_SECTION_NUMBER);
            mapView = (MapView) rootView.findViewById(R.id.mapview);
            if(mapView!=null){
                mapView.onCreate(savedInstanceState);
            }
            initOnCreate(rootView, getArguments().getInt(ARG_SECTION_NUMBER));

            return rootView;
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


        public void updateViewContent(){
            initOnCreate(root, sectionNumber);
        }


        private void initOnCreate(final View rootView, int sectionNumber) {
            switch (sectionNumber){
                case 1:

                    final NoticesListView list=((NoticesListView) rootView.findViewById(R.id.notice_list));
                    if(list!=null) {
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                //TODO start the activity. Log.d("marco", "l'id è questo "+l);
                            }
                        });
                    }

                    final HashMap<String, String> params = new HashMap<>();
                    NoticeBoard main =((NoticeBoard)getActivity());
                    if(main.searchFilters != null){

                        if(main.searchFilters.getString("location")!=null
                                && !main.searchFilters.getString("location").equals("")){
                            params.put("notice[full_location]", main.searchFilters.getString("location"));
                        }
                        if(main.searchFilters.getInt("radius", -1)!=-1){
                            params.put("notice[radius]", ""+main.searchFilters.getInt("radius",-1));
                        }
                        if(main.searchFilters.getInt("size",-1)!=-1){
                            params.put("notice[size]", ""+main.searchFilters.getInt("size",-1));
                        }
                        if(main.searchFilters.getInt("price",-1)!=-1){
                            params.put("notice[price]", ""+main.searchFilters.getInt("price",-1));
                        }
                        if(main.searchFilters.getString("categories")!=null
                                && !main.searchFilters.getString("categories").equals("")){
                            params.put("notice[tags]", main.searchFilters.getString("categories"));
                        }
                        String[] sortOptions = getResources().getStringArray(R.array.sort_order);
                        int index = -1;
                        for(int i = 0; i<sortOptions.length;i++){
                            if(sortOptions[i].equals(main.searchFilters.getString("sort"))) {
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

                    AsyncTask<Integer, Integer, List<Notice>> t = new AsyncTask<Integer, Integer, List<Notice>>() {
                        Exception e=null;
                        @Override
                        protected List<Notice> doInBackground(Integer... integers) {


                            params.put("notice[page]","0");
                            params.put("notice[items_per_page]","5");

                            List<Notice> notices = new ArrayList<>();
                            try {
                                String response = RESTManager.send(RESTManager.GET, "notices", params);
                                JSONArray obj = (new JSONObject(response)).getJSONArray("notices");
                                for(int i=0;i<obj.length();i++){
                                    notices.add(new Notice(obj.getJSONObject(i)));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                this.e=e;
                            }
                            return notices;
                        }

                        @Override
                        protected void onPostExecute(final List<Notice> notices) {
                            super.onPostExecute(notices);
                            if(e!=null){
                                Toast.makeText(PlaceholderFragment.this.getActivity(), getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                                return;
                            }
                            numberOfNotices=notices.size();
                            if(list!=null) {
                                list.setContent(PlaceholderFragment.this.getActivity(), notices);
                            }
                            if(mapView!=null){
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(final GoogleMap googleMap) {
                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        googleMap.clear();
                                        for( Notice n : notices){
                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(n.getLatitude(), n.getLongitude()))
                                                    .title(n.getTitle())
                                                    .snippet(n.getDescription())
                                                    );

                                            builder.include(new LatLng(n.getLatitude(), n.getLongitude()));
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

                                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                            @Override
                                            public boolean onMarkerClick(Marker arg0) {
                                                //TODO
                                                return false;
                                            }

                                        });
                                    }
                                });
                            }
                        }
                    };
                    t.execute();
                    pendingTasks.add(t);
                    if(rootView.findViewById(R.id.btn_load_more)!=null) {
                        rootView.findViewById(R.id.btn_load_more).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                AsyncTask<Integer, Integer, List<Notice>> t1 = new AsyncTask<Integer, Integer, List<Notice>>() {
                                    Exception e = null;

                                    @Override
                                    protected List<Notice> doInBackground(Integer... integers) {


                                        params.put("notice[page]", "" + Math.ceil(numberOfNotices / 5.0));
                                        params.put("notice[items_per_page]", "5");

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
                                            Toast.makeText(PlaceholderFragment.this.getActivity(), getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        numberOfNotices+=notices.size();
                                        if(list!=null)
                                            list.addNotices(notices);

                                        if(mapView!=null){
                                            if(notices.size()>0) {
                                                mapView.getMapAsync(new OnMapReadyCallback() {
                                                    @Override
                                                    public void onMapReady(final GoogleMap googleMap) {
                                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                                        for (Notice n : notices) {
                                                            googleMap.addMarker(new MarkerOptions().position(new LatLng(n.getLatitude(), n.getLongitude()))
                                                                    .title(n.getTitle())
                                                                    .snippet(n.getDescription()));

                                                            builder.include(new LatLng(n.getLatitude(), n.getLongitude()));
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

                                                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                                            @Override
                                                            public boolean onMarkerClick(Marker arg0) {
                                                                //TODO
                                                                return false;
                                                            }

                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    }
                                };
                                t1.execute();
                                pendingTasks.add(t1);
                            }
                        });
                    }

                    break;
                case 2:
                    final NoticesListView list1=((NoticesListView) rootView.findViewById(R.id.notice_list));
                    AsyncTask<Integer, Integer, List<Notice>> t2 = new AsyncTask<Integer, Integer, List<Notice>>() {
                        Exception e=null;
                        @Override
                        protected List<Notice> doInBackground(Integer... integers) {

                            List<Notice> notices = new ArrayList<>();
                            try {
                                String response = RESTManager.send(RESTManager.GET, "students/"+LoggedStudent.getId()+"/favs/notices", null);
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
                                Toast.makeText(PlaceholderFragment.this.getActivity(), getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                                return;
                            }
                            list1.setContent(PlaceholderFragment.this.getActivity(), notices);
                        }
                    };
                    t2.execute();
                    pendingTasks.add(t2);

                    break;
                case 3:
                    final NoticesListView list2=((NoticesListView) rootView.findViewById(R.id.notice_list));
	                final ActionButton fab = (ActionButton) rootView.findViewById(R.id.btn_new_rent);
                    AsyncTask<Integer, Integer, List<Notice>> t3 = new AsyncTask<Integer, Integer, List<Notice>>() {
                        Exception e=null;
                        @Override
                        protected List<Notice> doInBackground(Integer... integers) {
                            Map<String, String> params = new HashMap<>();
                            params.put("notice[student_id]", ""+LoggedStudent.getId());
                            List<Notice> notices = new ArrayList<>();
                            try {
                                String response = RESTManager.send(RESTManager.GET, "notices", params);
                                JSONArray obj = (new JSONObject(response)).getJSONArray("notices");
                                for(int i=0;i<obj.length();i++){
                                    notices.add(new Notice(obj.getJSONObject(i)));
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
                                Toast.makeText(PlaceholderFragment.this.getActivity(), getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                                return;
                            }
                            list2.setContent(PlaceholderFragment.this.getActivity(), notices);
                        }
                    };
                    t3.execute();
                    pendingTasks.add(t3);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(PlaceholderFragment.this.getActivity(), "NEW ONE", Toast.LENGTH_LONG).show();
						}
					});

	                break;
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            for(AsyncTask<?,?,?> t : pendingTasks){
                t.cancel(true);
            }
            pendingTasks.clear();
        }
    }

}
