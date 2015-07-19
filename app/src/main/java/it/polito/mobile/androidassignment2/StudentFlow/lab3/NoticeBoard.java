package it.polito.mobile.androidassignment2.StudentFlow.lab3;

import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.Locale;
import java.util.zip.DataFormatException;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.CompaniesFavouritesActivity;
import it.polito.mobile.androidassignment2.StudentFlow.NavigationDrawerFragment;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments.AllNoticesFragment;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments.BookmarksFragment;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments.FiltersFragment;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments.NoticeFragment;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments.YourNoticesFragment;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.gcm.UnregistrationManager;


/**
 * Created by mark9 on 02/06/2015.
 */
public class NoticeBoard extends AppCompatActivity implements Communicator,MaterialTabListener{

    private static String FILTERS = "filters";
    private static String SHOW_AS_MAP = "map_show";

    private MaterialTabHost mTabHost;
    //private ViewPagerAdapter pagerAdapter;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;
    private Button bList;
    private Button bMap;
    private Menu mMenu;

    public NavigationDrawerFragment getmNavigationDrawerFragment() {
		return mNavigationDrawerFragment;
	}
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

    private int currentTabSelected =0;

    private Bundle filters;

    private boolean showAsMap = false;
    private AsyncTask<Object, Void, Object> task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        if(savedInstanceState!=null){
            filters = savedInstanceState.getBundle(FILTERS);
            showAsMap = savedInstanceState.getBoolean(SHOW_AS_MAP);
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        setUpTabs();
        // Set up the ViewPager with the sections adapter.


        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
      /*  mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
/*
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
        }*/

       mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
       mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position",1));

       mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	    onSectionAttached(2);
	 
	    setTitle(mTitle);
	    mNavigationDrawerFragment.setTitle(mTitle);
    }
/*
    private void setUpMapChooser() {
        bList = (Button) findViewById(R.id.btn_list);
        bMap = (Button) findViewById(R.id.btn_map);


        bList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAsMap=false;

                bMap.setBackgroundColor(getResources().getColor(R.color.blue_sky));
                if(mSectionsPagerAdapter.getFragmentAtPosition(0)!=null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .detach(mSectionsPagerAdapter.getFragmentAtPosition(0))
                            .attach(mSectionsPagerAdapter.getFragmentAtPosition(0))
                            .commit();
                }
                bList.setBackgroundColor(getResources().getColor(R.color.strong_blue));
            }
        });
        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAsMap=true;
                bList.setBackgroundColor(getResources().getColor(R.color.blue_sky));
                if(mSectionsPagerAdapter.getFragmentAtPosition(1)!=null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .detach(mSectionsPagerAdapter.getFragmentAtPosition(1))
                            .attach(mSectionsPagerAdapter.getFragmentAtPosition(1))
                            .commit();
                }
                bMap.setBackgroundColor(getResources().getColor(R.color.strong_blue));
            }
        });
        bList.callOnClick();
    }
*/
    public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				break;
			case 3:
				mTitle = getString(R.string.title_section3);
				break;
			case 4:
				mTitle = getString(R.string.title_section4);
				break;
		}
	}
	private void setUpTabs() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mTabHost.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.VISIBLE);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mTabHost.setSelectedNavigationItem(position);
			}
		});
		// insert all tabs from pagerAdapter data
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			mTabHost.addTab(
					mTabHost.createTabText(mSectionsPagerAdapter.getPageTitle(i).toString())
							.setTabListener(this)
			);
		}
	}
    public Bundle getFilters(){
        return filters;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NoticeFragment f = (NoticeFragment)mSectionsPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem());
        if(f!=null) f.refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null && (task.getStatus()== AsyncTask.Status.PENDING || task.getStatus()== AsyncTask.Status.RUNNING)){
            task.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notice_board, menu);
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_as_map) {
	        //onPrepareOptionsMenu(mMenu);
            showAsMap=!showAsMap;

            if(mSectionsPagerAdapter.getFragmentAtPosition(0)!=null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .detach(mSectionsPagerAdapter.getFragmentAtPosition(0))
                        .attach(mSectionsPagerAdapter.getFragmentAtPosition(0))
                        .commit();
            }
            if(mSectionsPagerAdapter.getFragmentAtPosition(1)!=null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .detach(mSectionsPagerAdapter.getFragmentAtPosition(1))
                        .attach(mSectionsPagerAdapter.getFragmentAtPosition(1))
                        .commit();
            }
            onPrepareOptionsMenu(mMenu);
            return true;
        }
        if (id == R.id.action_search) {
            Log.d("searchFragment", "search fragment clicked...");
            Fragment searchFragment = getSupportFragmentManager().findFragmentByTag("searchFragment");
            if(searchFragment!=null
                    && searchFragment.isVisible()){

                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(searchFragment)
                        .commit();
            }else {
                mViewPager.setCurrentItem(0);

                searchFragment= new FiltersFragment();
                Log.d("searchFragment", "search fragment opened...");
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.search_container, searchFragment, "searchFragment")
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        }
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem showAsMap = menu.findItem(R.id.action_show_as_map);
        MenuItem search = menu.findItem(R.id.action_search);
	    mMenu = menu;
        if(this.showAsMap){
            //showAsMap.setTitle(getResources().getString(R.string.action_show_as_list));
            showAsMap.setIcon(getResources().getDrawable(android.R.drawable.ic_menu_sort_by_size));
        } else {
            showAsMap.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_map));
            //showAsMap.setTitle(getResources().getString(R.string.action_show_as_map));
        }
        if(currentTabSelected == 2){
            showAsMap.setVisible(false);
        }else{
            showAsMap.setVisible(true);
        }
        if(currentTabSelected == 0){
            search.setVisible(true);
        }else{
            search.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        currentTabSelected =tab.getPosition();
        if(mSectionsPagerAdapter.getFragmentAtPosition(currentTabSelected)!=null)
            ((NoticeFragment)mSectionsPagerAdapter.getFragmentAtPosition(currentTabSelected)).refresh();
        invalidateOptionsMenu();
    }

	@Override
	public void onTabReselected(MaterialTab materialTab) {

	}

	@Override
	public void onTabUnselected(MaterialTab materialTab) {

	}


	private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(FILTERS, filters);
        outState.putBoolean(SHOW_AS_MAP, showAsMap);
        super.onSaveInstanceState(outState);
    }

    public void searchWithFilters(Bundle bundle) {
        filters=bundle;
        Fragment searchFragment = getSupportFragmentManager().findFragmentByTag("searchFragment");

        if(searchFragment!=null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit();
        }
        hideKeyboard();
        ((NoticeFragment)mSectionsPagerAdapter.getFragmentAtPosition(0)).refresh();
    }

    public boolean isShowAsMap() {
        return showAsMap;
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
                    new UnregistrationManager(NoticeBoard.this).unregisterGcm();
                    getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1://delete account
                    try {
                        task = Manager.deleteStudent(((AppContext) getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
                            @Override
                            public void process(Integer arg, Exception e) {
                                task = null;
                                if (e != null) {
                                    Log.d(CompaniesFavouritesActivity.class.getSimpleName(), "Error deleteing user");
                                    return;
                                }
                                getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void cancel() {
                                task = null;
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
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.all).toUpperCase(l);
                case 1:
                    return getString(R.string.favs).toUpperCase(l);
                case 2:
                    return getString(R.string.yours).toUpperCase(l);
            }
            return null;
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

        public Fragment getFragmentAtPosition(int position) {
            return registeredFragments.get(position);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment {

        public static Fragment newInstance(int sectionNumber) {
            Fragment f = null;
            switch (sectionNumber) {
                case 1:
                    f = new AllNoticesFragment();
                    break;
                case 2:
                    f = new BookmarksFragment();
                    break;
                case 3:
                    f = new YourNoticesFragment();
                    break;
            }


            return f;
        }
    }

}
