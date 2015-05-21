package it.polito.mobile.androidassignment2.StudentFlow;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.CompanyFlow.EditCompanyProfileActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.adapter.OfferArrayAdapter;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;

public class ShowCompanyProfileActivity extends ActionBarActivity  {
    private ImageView ivLogo;
    private ProgressBar pbLogoSpinner;
    private TextView tvName;
    private TextView tvClients;
    private TextView tvEmail;
    private DownloadFinished downloadfinished = new DownloadFinished();
    private DownloadFailed downloadfailed = new DownloadFailed();
    private Uri logoUri;
    private TextView tvLocation;
    private TextView tvNumberOfWorkers;
    private TextView tvMission;
    private TextView tvCompetences;
    private TextView tvDescription;
    private ProgressBar pbFav;
    private Button bOffers;
    private Button bFav;
    private Company company;
    private List<Offer> offers;
    private AsyncTask<Object, Void, Object> task1 = null;
    private AsyncTask<Object, Void, Object> task2 = null;
    private AsyncTask<Object, Void, Object> task3 = null;
    private AsyncTask<Object, Void, Object> task4 = null;
    private Student studentLogged;

    public class DownloadFinished extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            //TODO CHECK IS A PHOTO?
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            Uri logoUri = Uri.parse(filePath);
            ivLogo.setImageURI(logoUri);
            tvName.setVisibility(View.VISIBLE);
            if(bOffers.getVisibility() == View.VISIBLE) {
                bOffers.setEnabled(true);
            }
        }
    }

    public class DownloadFailed extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            Uri logoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(R.drawable.photo_placeholder_err) +
                    '/' +
                    getResources().getResourceTypeName(R.drawable.photo_placeholder_err) +
                    '/' +
                    getResources().getResourceEntryName(R.drawable.photo_placeholder_err));
            ivLogo.setImageURI(logoUri);
            tvName.setVisibility(View.VISIBLE);
            if(bOffers.getVisibility() == View.VISIBLE) {
                bOffers.setEnabled(true);
            }
            Toast t = Toast.makeText(ShowCompanyProfileActivity.this, getResources().getString(R.string.error_loading_photo), Toast.LENGTH_LONG);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            studentLogged = Session.getInstance().getStudentLogged();
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
        Intent i = getIntent();
        int companyId = i.getIntExtra("companyId", -1);
        if (companyId == -1) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_invalid_company), Toast.LENGTH_LONG).show();

            throw new RuntimeException("companyId is required");

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_show_company_profile);

        findViews();

        task1 = Manager.getCompanyById(companyId, new Manager.ResultProcessor<Company>() {

            @Override
            public void cancel() {
            }

            @Override
            public void process(final Company arg, Exception e) {
                company = arg;
                Offer criteria = new Offer();
                try {
                    criteria.setCompanyId(company.getId());
                } catch (DataFormatException e1) {
                    throw new RuntimeException();
                }
                task4 = Manager.getOffersMatchingCriteria(criteria, new Manager.ResultProcessor<List<Offer>>() {
                    @Override
                    public void process(final List<Offer> arg, Exception e) {
                        task4 = null;
                        offers = arg;
                        setupViewsAndCallbacks();
                    }

                    @Override
                    public void cancel() {
                        ShowCompanyProfileActivity.this.task4 = null;
                    }
                });

            }
        });
    }

    private void findViews() {
        tvEmail = (TextView) findViewById(R.id.c_email_tv);
        ivLogo = (ImageView) findViewById(R.id.c_logo_iw);
        pbLogoSpinner = (ProgressBar) findViewById(R.id.c_logo_pb);
        tvName = (TextView) findViewById(R.id.c_name_tv);
        tvClients = (TextView) findViewById(R.id.c_clients_tv);
        tvCompetences = (TextView) findViewById(R.id.c_competences_tv);
        tvLocation = (TextView) findViewById(R.id.c_location_tv);
        tvNumberOfWorkers = (TextView) findViewById(R.id.c_number_of_workers_tv);
        tvMission = (TextView) findViewById(R.id.c_mission_tv);
        tvDescription = (TextView) findViewById(R.id.c_description_tv);
        pbFav = (ProgressBar) findViewById(R.id.c_fav_pb);
        bOffers = (Button) findViewById(R.id.c_offers_b);
        bFav = (Button) findViewById(R.id.c_fav_b);
    }

    private void setupViewsAndCallbacks() {
        tvEmail.setText(company.getEmail());

        if(offers != null && offers.size() > 0) {
            bOffers.setVisibility(View.VISIBLE);
            bOffers.setEnabled(false);
            bOffers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ShowCompanyProfileActivity.this, SearchOffer.class);
                    i.putExtra("companyId", company.getId());
                    startActivity(i);
                }
            });
        } else {
            bOffers.setVisibility(View.GONE);
        }

        List<Company> favCompanies;
        try {
            favCompanies = Session.getInstance().getFavCompanies();
        } catch (DataFormatException e) {
            throw new RuntimeException(); //TODO
        }
        boolean isFaved = false;
        if (favCompanies != null) {
            for (Company c : favCompanies) {
                if (c.getId() == company.getId()) {
                    isFaved = true;
                }
            }
        }
        if (isFaved) {
            setButtonForUnfav();

        } else {
            setButtonForFav();
        }

        tvEmail.setText(company.getEmail());
        String name = company.getName();
        if (name == null) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setText(company.getName());
        }

        String clients = company.getClientsToString(", ");
        if (clients == null) {
            tvClients.setVisibility(View.GONE);
        } else {
            tvClients.setText(clients);
        }

        Integer numberOfWorkers = company.getNumberOfWorkers();
        if (numberOfWorkers == null) {
            tvNumberOfWorkers.setVisibility(View.GONE);
        } else {
            tvNumberOfWorkers.setText(numberOfWorkers.toString());
        }

        String competences = company.getCompetencesToString(", ");
        if (competences == null) {
            tvCompetences.setVisibility(View.GONE);
        } else {
            tvCompetences.setText(competences);
        }

        String location = company.getLocation();
        if (location == null || location.equals("")) {
            tvLocation.setVisibility(View.GONE);
        } else {
            tvLocation.setText(location);
        }

        String mission = company.getMission();
        if (mission == null || mission.equals("")) {
            tvMission.setVisibility(View.GONE);
        } else {
            tvMission.setText(mission);
        }

        String description = company.getDescription();
        if (description == null || description.equals("")) {
            tvDescription.setVisibility(View.GONE);
        } else {
            tvDescription.setText(location);
        }

        String url = company.getLogoUrl();
        TransferController.download(getApplicationContext(), new String[]{url});
        tvName.setVisibility(View.INVISIBLE);
        pbLogoSpinner.setVisibility(ProgressBar.VISIBLE);
    }



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));
        registerReceiver(downloadfailed, new IntentFilter(DownloadModel.INTENT_DOWNLOAD_FAILED));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(downloadfailed);
        unregisterReceiver(downloadfinished);
        if(task1 != null){
            task1.cancel(true);
            task1 = null;
        }
        if(task2 != null){
            task2.cancel(true);
            task2 = null;
        }
        if(task3 != null){
            task3.cancel(true);
            task3 = null;
        }
        super.onPause();
    }

    private void setButtonForUnfav() {
        bFav.setOnClickListener(null);
        bFav.setText(getResources().getString(R.string.unfav));
        bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbFav.setVisibility(View.VISIBLE);
                bFav.setVisibility(View.INVISIBLE);
                task3 = Manager.deleteAFavouriteCompanyOfAStudent(studentLogged.getId(), company.getId(), new Manager.ResultProcessor<Integer>() {

                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void process(final Integer i, Exception e) {
                        pbFav.setVisibility(View.INVISIBLE);
                        bFav.setVisibility(View.VISIBLE);
                        try {
                            Session.getInstance().getFavCompanies().remove(company);
                        } catch (DataFormatException ee) {
                            throw new RuntimeException(ee);
                        }
                        setButtonForFav();
                    }
                });
            }
        });
    }

    private void setButtonForFav() {
        bFav.setOnClickListener(null);
        bFav.setText(getResources().getString(R.string.fav));
        bFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbFav.setVisibility(View.VISIBLE);
                bFav.setVisibility(View.INVISIBLE);
                task3 = Manager.addFavouriteCompanyForStudent(studentLogged.getId(), company.getId(), new Manager.ResultProcessor<Company>() {

                    @Override
                    public void cancel() {
                    }

                    @Override
                    public void process(final Company s, Exception e) {
                        pbFav.setVisibility(View.INVISIBLE);
                        bFav.setVisibility(View.VISIBLE);
                        try {
                            Session.getInstance().getFavCompanies().add(company);
                        } catch (DataFormatException ee) {
                            throw new RuntimeException(ee);
                        }
                        setButtonForUnfav();
                    }
                });
            }
        });
    }
}
