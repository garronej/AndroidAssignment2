package it.polito.mobile.androidassignment2.StudentFlow.timetable;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.CompaniesFavouritesActivity;
import it.polito.mobile.androidassignment2.StudentFlow.NavigationDrawerFragment;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.gcm.UnregistrationManager;


public class MenuTimetable extends AppCompatActivity implements Communicator {

    private AsyncTask<?,?,?> task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_timetable);
        setUpNavigationDrawer();

        final ProgressBar progressBar1 =(ProgressBar)findViewById(R.id.pb1);
        progressBar1.setVisibility(View.GONE);

        final ActionButton button1 = (ActionButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setVisibility(View.INVISIBLE);
                progressBar1.setVisibility(View.VISIBLE);


                task = ((AppContext) getApplicationContext()).retrieveTimeTableData(new Manager.ResultProcessor<Integer>() {
                    @Override
                    public void process(Integer arg, Exception e) {


                        task = null;

                        progressBar1.setVisibility(View.GONE);
                        button1.setVisibility(View.VISIBLE);

                        if (e != null) {
                            Log.d(MenuTimetable.class.getName(), Log.getStackTraceString(e));
                            Toast.makeText(MenuTimetable.this, "Error retrieving timetable data", Toast.LENGTH_LONG).show();
                            return;
                        }


                        startActivity(new Intent(MenuTimetable.this, SearchCourse.class));


                    }

                    @Override
                    public void cancel() {

                        task = null;
                        progressBar1.setVisibility(View.GONE);
                        button1.setVisibility(View.VISIBLE);

                    }

                });

            }
        });

        final ActionButton button2 = (ActionButton) findViewById(R.id.button2);
        final ProgressBar progressBar2 =(ProgressBar)findViewById(R.id.pb2);
        progressBar2.setVisibility(View.GONE);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setVisibility(View.INVISIBLE);
                progressBar2.setVisibility(View.VISIBLE);

                task = ((AppContext) getApplicationContext()).retrieveTimeTableData(new Manager.ResultProcessor<Integer>() {
                    @Override
                    public void process(Integer arg, Exception e) {

                        task = null;

                        progressBar2.setVisibility(View.GONE);
                        button2.setVisibility(View.VISIBLE);

                        if (e != null) {
                            Log.d(MenuTimetable.class.getName(), Log.getStackTraceString(e));
                            Toast.makeText(MenuTimetable.this, "Error retrieving timetable data", Toast.LENGTH_LONG).show();
                            return;
                        }


                        startActivity(new Intent(MenuTimetable.this, SearchConsultation.class));


                    }

                    @Override
                    public void cancel() {

                        task = null;
                        progressBar2.setVisibility(View.GONE);
                        button2.setVisibility(View.VISIBLE);

                    }

                });

            }
        });

        View button3 = findViewById(R.id.button3);
        final ProgressBar progressBar3 =(ProgressBar)findViewById(R.id.pb3);
        progressBar3.setVisibility(View.GONE);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        startActivity(new Intent(MenuTimetable.this, SearchRoom.class));
            }
        });

    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position", 0));

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        //due it's a new instance of NavDraw

        mNavigationDrawerFragment.setTitle(getTitle());
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
                    new UnregistrationManager(this).unregisterGcm();
                    getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                    ((AppContext)getApplication()).freeSession();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
}
