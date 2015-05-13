package it.polito.mobile.androidassignment2;

import it.polito.mobile.androidassignment2.fragment.MyPagerAdapter;



import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class FragmentsHolder extends FragmentActivity {

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.fragment);

        List<Fragment> fragments = new ArrayList<Fragment>();


        String what = getIntent().getExtras().getString("what");

        Log.v("what", "what = " + what);



        if( what.equals("student") ){

            //Studentd

            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.studentsTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.studentsTest.Results.class.getName()));

        }else if( what.equals("company") ){

            //Offer

            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.companiesTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.companiesTest.Results.class.getName()));


        }else if( what.equals("offer")) {


            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.offersTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.offersTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.companiesTest.Results.class.getName()));

        }





        this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);
    }
}