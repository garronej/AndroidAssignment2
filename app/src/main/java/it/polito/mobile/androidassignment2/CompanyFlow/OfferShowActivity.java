package it.polito.mobile.androidassignment2.CompanyFlow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Company;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.businessLogic.Offer;
import it.polito.mobile.androidassignment2.businessLogic.Session;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.s3client.models.DownloadModel;
import it.polito.mobile.androidassignment2.s3client.network.TransferController;

public class OfferShowActivity extends AppCompatActivity implements Communicator{


    private DownloadReceiver downloadfinished;
    private View pbLogoSpinner;
    private Button candidatesButton;
    private boolean isStudentFlow;
    class DownloadReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String filePath = intent.getStringExtra(DownloadModel.EXTRA_FILE_URI);
            pbLogoSpinner.setVisibility(ProgressBar.GONE);//gone=invisible+view does not take space
            Uri logoUri = Uri.parse(filePath);
            Session.getInstance().setPhotoUri(logoUri);
            photo.setImageURI(logoUri);
        }
    }



    private AsyncTask<Object, Void, Object> task=null;
    private Button editOfferButton;
    private TextView code;
    private TextView description;
    private TextView contractType;
    private TextView numberOfMonths;
    private TextView location;
    private ImageView photo;
    private TextView competences;
    private LinearLayout studentActions;
    private Button applyButton;
    private Button addToFavouriteButton;
    private TextView companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_show);
        editOfferButton = (Button) findViewById(R.id.edit_offer_button);

        candidatesButton = (Button) findViewById(R.id.show_all_candidates);

        code = (TextView) findViewById(R.id.job_offer_code);
        description = (TextView) findViewById(R.id.job_offer_description);
        location = (TextView) findViewById(R.id.location);
        numberOfMonths = (TextView) findViewById(R.id.number_of_months);
        contractType = (TextView) findViewById(R.id.kind_of_contract);
        competences = (TextView) findViewById(R.id.competences);
        studentActions = (LinearLayout) findViewById(R.id.student_actions);
        applyButton = (Button) findViewById(R.id.apply_button);
        addToFavouriteButton = (Button) findViewById(R.id.add_to_favourite);
        companyName = (TextView) findViewById(R.id.offer_company_fullname);
        pbLogoSpinner = findViewById(R.id.photo_spinner);
        photo=(ImageView)findViewById(R.id.photo);
        downloadfinished= new DownloadReceiver();

        isStudentFlow = getIntent().getBooleanExtra("student_flow",false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final int offerId=getIntent().getIntExtra("offerId", -1);
        if(offerId==-1){
            finish();
            return;
        }
        editOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OfferEditActivity.class);
                i.putExtra("offerId", offerId);
                startActivity(i);
            }
        });

        candidatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SearchStudents.class);
                i.putExtra("offerId", offerId);
                startActivity(i);
            }
        });

        registerReceiver(downloadfinished, new IntentFilter(DownloadModel.INTENT_DOWNLOADED));



        task=Manager.getOfferById(offerId, new Manager.ResultProcessor<Offer>() {
            @Override
            public void process(final Offer arg, Exception e) {
                task=null;
                if(e!=null){
                    //TODO: show some error messge
                    finish();
                    return;
                }
                TransferController.download(getApplicationContext(), new String[]{arg.getCompany().getLogoUrl()});

                code.setText(arg.getCode());
                location.setText(arg.getLocation());
                companyName.setText(arg.getCompanyName());
                description.setText(arg.getDescriptionOfWork());

                String c="";
                if(arg.getCompetences()!=null && arg.getCompetences().length>0){
                    c=arg.getCompetences()[0];
                    for(int i=1;i<arg.getCompetences().length;i++){
                        c+=","+arg.getCompetences()[i];
                    }

                }
                competences.setText(c);
                contractType.setText(arg.getKindOfContract());
                if(arg.getDurationMonths() != null)
                    numberOfMonths.setText(arg.getDurationMonths().toString());



                try {
                    if (Session.getInstance().getWhoIsLogged() == Company.class
                            && arg.getCompanyId() == Session.getInstance().getCompanyLogged().getId()) {
                        editOfferButton.setVisibility(View.VISIBLE);
                        candidatesButton.setVisibility(View.VISIBLE);

                    }


                    //TODO: test
                    if(Session.getInstance().getWhoIsLogged() == Student.class){
                        studentActions.setVisibility(View.VISIBLE);
                        if(Session.getInstance().getFavoriteOffer().contains(arg)){
                            setButtonToUnfavStudent(arg);
                        }else{
                            setButtonToFavStudent(arg);
                        }

                        Manager.getAppliedOfferOfStudent(Session.getInstance().getStudentLogged().getId(), arg,
                                new Manager.ResultProcessor<List<Offer>>() {
                                    @Override
                                    public void process(List<Offer> l, Exception e) {
                                        if(e!=null){
                                            //TODO: show some error..

                                            return;
                                        }


                                        if(l.size()>0){ // it should be 1 or 0
                                            applyButton.setText(getResources().getText(R.string.applied));
                                            applyButton.setBackgroundColor(getResources().getColor(R.color.green_ok));

                                        }else{
                                            applyButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    try {
                                                        Manager.subscribeStudentOfJobOffer(arg.getId(), Session.getInstance().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
                                                            @Override
                                                            public void process(Integer r, Exception e) {
                                                                if(e!=null){
                                                                    //TODO: show error message...
                                                                    return;
                                                                }

                                                                applyButton.setText(getResources().getText(R.string.applied));
                                                                applyButton.setBackgroundColor(getResources().getColor(R.color.green_ok));
                                                                applyButton.setEnabled(false);
                                                            }

                                                            @Override
                                                            public void cancel() {

                                                            }
                                                        });
                                                    } catch (DataFormatException e1) {
                                                        //never here
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void cancel() {

                                    }
                                });




                    }
                }catch (Exception exc){
                    //never here
                }



            }

            @Override
            public void cancel() {
                task=null;
            }
        });






    }


    private void setButtonToUnfavStudent(final Offer arg){
        addToFavouriteButton.setText(R.string.unfav_offer);
        addToFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Manager.deleteAFavouriteOfferOfAStudent(Session.getInstance().getStudentLogged().getId(), arg.getId(),
		                    new Manager.ResultProcessor<Integer>() {
			                    @Override
			                    public void process(Integer r, Exception e) {
				                    if (e != null) {
					                    //TODO: show error message
					                    return;
				                    }
				                    try {
					                    Session.getInstance().getFavoriteOffer().remove(arg);
				                    } catch (DataFormatException e1) {
					                    //never here
				                    }
				                    setButtonToFavStudent(arg);
			                    }

			                    @Override
			                    public void cancel() {

			                    }
		                    });
                } catch (DataFormatException e1) {
                    //never here
                }
            }
        });

    }

    private void setButtonToFavStudent(final Offer arg){
        addToFavouriteButton.setText(R.string.add_to_favourite);

        addToFavouriteButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        try {
			        Manager.addFavouriteOfferForStudent(Session.getInstance().getStudentLogged().getId(), arg.getId(), new Manager.ResultProcessor<Offer>() {
				        @Override
				        public void process(Offer arg, Exception e) {
					        if (e != null) {
						        //TODO: show error message
						        return;
					        }
					        try {
						        Session.getInstance().getFavoriteOffer().add(arg);
					        } catch (DataFormatException e1) {
						        //never here
					        }
					        setButtonToUnfavStudent(arg);
				        }

				        @Override
				        public void cancel() {

				        }
			        });
		        } catch (Exception e) {
		        }
	        }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadfinished);
        if(task!=null){
            task.cancel(true);
            task=null;
        }
    }@Override
     public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!isStudentFlow){getMenuInflater().inflate(R.menu.menu_offer_show, menu);
		return true;}
		else return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_offer) {
	        showConfirmAlerter(2);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showConfirmAlerter(int kind) {
        AlertYesNo alert = new AlertYesNo();
        Bundle info = new Bundle();
        if (kind == 2)
            info.putString("message", getResources().getString(R.string.delete_offer_message));
       else return;
        info.putString("title", getResources().getString(R.string.confirm));
        info.putInt("kind", kind);
        alert.setCommunicator(this);
        alert.setArguments(info);
        alert.show(getSupportFragmentManager(), "Confirm");

    }

    @Override
    public void goSearch(int kind) {

    }

    @Override
    public void respond(int itemIndex, int kind) {

    }

    @Override
    public void dialogResponse(int result, int kind) {
        if (result == 1 && kind ==3) {
            //TODO delete offer
            }
        }
    }


