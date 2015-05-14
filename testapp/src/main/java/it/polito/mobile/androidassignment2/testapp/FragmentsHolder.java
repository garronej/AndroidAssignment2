package it.polito.mobile.androidassignment2.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.testapp.fragment.MyPagerAdapter;

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

            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.studentsTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.studentsTest.Results.class.getName()));

        }else if( what.equals("company") ){

            //Offer

            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.companiesTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.companiesTest.Results.class.getName()));


        }else if( what.equals("offer")) {


            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.offersTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.offersTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.companiesTest.Results.class.getName()));

        }else if( what.equals("fav_students")){

            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.favStudentTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.favStudentTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.studentsTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.companiesTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.offersTest.Results.class.getName()));

        }else if( what.equals("fav_companies")){

            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.favCompanyTest.Functions.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.favCompanyTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.studentsTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.companiesTest.Results.class.getName()));
            fragments.add(Fragment.instantiate(this,it.polito.mobile.androidassignment2.testapp.offersTest.Results.class.getName()));

        }





        this.mPagerAdapter = new MyPagerAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);
    }
}