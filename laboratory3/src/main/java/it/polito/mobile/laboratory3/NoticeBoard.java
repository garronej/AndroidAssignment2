package it.polito.mobile.laboratory3;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public int kindOfMenu=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_notice_board);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
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
            startActivity(new Intent(NoticeBoard.this, SearchActivity.class));
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
                    //TODO
                    layout = R.layout.fragment_bookmarks;
                    break;
                case 3:
                    //TODO
                    layout = R.layout.fragment_my_notices;
                    break;
            }

            View rootView = inflater.inflate(layout, container, false);

            initOnCreate(rootView, getArguments().getInt(ARG_SECTION_NUMBER));

            return rootView;
        }


        private void initOnCreate(final View rootView, int sectionNumber) {
            switch (sectionNumber){
                case 1:

                    final NoticesListView list=((NoticesListView) rootView.findViewById(R.id.notice_list));

                    AsyncTask<Integer, Integer, List<Notice>> t = new AsyncTask<Integer, Integer, List<Notice>>() {
                        Exception e=null;
                        @Override
                        protected List<Notice> doInBackground(Integer... integers) {
                            HashMap<String, String> params = new HashMap<>();

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
                        protected void onPostExecute(List<Notice> notices) {
                            super.onPostExecute(notices);
                            if(e!=null){
                                Toast.makeText(PlaceholderFragment.this.getActivity(), getResources().getString(R.string.error_rest), Toast.LENGTH_LONG).show();
                                return;
                            }
                            list.setContent(PlaceholderFragment.this.getActivity(), notices);
                        }
                    };
                    t.execute();
                    pendingTasks.add(t);

                    rootView.findViewById(R.id.btn_load_more).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            AsyncTask<Integer, Integer, List<Notice>> t1 = new AsyncTask<Integer, Integer, List<Notice>>() {
                                Exception e=null;
                                @Override
                                protected List<Notice> doInBackground(Integer... integers) {
                                    HashMap<String, String> params = new HashMap<>();

                                    params.put("notice[page]",""+Math.ceil(list.getNumberOfNotices()/5.0));
                                    params.put("notice[items_per_page]","5");

                                    List<Notice> notices = new ArrayList<>();
                                    try {
                                        String response = RESTManager.send(RESTManager.GET, "notices", params);
                                        JSONArray obj = (new JSONObject(response)).getJSONArray("notices");
                                        for(int i=0;i<obj.length();i++){
                                            notices.add(new Notice(obj.getJSONObject(i)));
                                        }
                                    } catch (Exception e) {
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
                                    list.addNotices(notices);
                                }
                            };
                            t1.execute();
                            pendingTasks.add(t1);
                        }
                    });


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
