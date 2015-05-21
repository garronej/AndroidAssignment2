package it.polito.mobile.androidassignment2.businessLogic;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 *
 * Singleton class to login and store the information of the logged user
 *
 * Created by Joseph on 04/05/2015.
 */
public class Session {

    private static Session instance = null;

    private Class whoIsLogged;

    private Student studentLogged = null;
    private Company companyLogged = null;

    private List<Company> favCompanies = null;
    private List<Student> favStudents = null;

    //Represent ether the favourite offer of a student or the offer that belong to a company.
    private List<Offer> offers= null;
    private List<Offer> appliedOffers = null;

    //uri on the phone of the profile picture of logged company/student
    //this is set by the profile_activity or edit_profile_activity
    private Uri photoUri = null;

    public Class getWhoIsLogged(){
        return this.whoIsLogged;
    }

    public Student getStudentLogged() throws DataFormatException{

        if( this.whoIsLogged == Student.class) {
            return this.studentLogged;
        }else{
            throw new DataFormatException("Session : getStudentLogged, a company is logged, not a student");
        }
    }

    public Company getCompanyLogged() throws DataFormatException{

        if( this.whoIsLogged == Company.class) {
            return this.companyLogged;
        }else{
            throw new DataFormatException("Session : getCompanyLoged, a student is logged, not a company");
        }
    }

    public List<Offer> getFavoriteOffer() throws DataFormatException{

        if( this.whoIsLogged == Student.class) {
            return this.offers;
        }else{
            throw new DataFormatException("Session : getFavoriteOffer apply only when a student is logged.");
        }

    }


    public List<Offer> getOfferOfTheLoggedCompany() throws DataFormatException{

        if( this.whoIsLogged == Company.class) {
            return this.offers;
        }else{
            throw new DataFormatException("Session : getOfferOfTheLoggedCompany() apply only when a company is logged.");
        }

    }


    public List<Offer> getAppliedOffers() throws DataFormatException{


        if( this.whoIsLogged == Student.class ){
            return this.appliedOffers;
        }else{
            throw new DataFormatException("Session : getAppliedOffers() apply only when a company is logged.");
        }

    }


    public List<Company> getFavCompanies() throws DataFormatException{


        if( this.whoIsLogged == Student.class) {
            return this.favCompanies;
        }else{
            throw new DataFormatException("Session : getFavCompany() apply only when a student is logged.");
        }

    }


    public List<Student> getFavStudents() throws DataFormatException{


        if( this.whoIsLogged == Company.class) {
            return this.favStudents;
        }else{
            throw new DataFormatException("Session : getFavStudents() apply only when a company is logged.");
        }

    }

    //look at field definition above for more details
    public void setPhotoUri(Uri uri) {
        this.photoUri = uri;
    }
    //look at field definition above for more details
    public Uri getPhotoUri() {
        return this.photoUri;
    }


    //Synchronous version of login
    protected static synchronized Integer login( String email, String password) throws IOException,RestApiException, DataFormatException{
        Session.instance = new Session(email,password);
        return 0;
    }

    //First you have to Login
    //If login fail => ResApiException.
    //If Email malformed => DataFormatException
    //If network problem IOException
    public static Task.General login( String email, String password, Manager.ResultProcessor<Integer> postProcessor ){


        Task.General t = new Task.General(Task.Method.LOGIN, postProcessor);
        t.execute(email,password);
        return t;

    }


    //Then you can get the instance synchronously.
    public static Session getInstance() throws ExceptionInInitializerError {
        if (Session.instance == null)
            throw new ExceptionInInitializerError("Session error : login First !");

        return Session.instance;

    }


    //Method to use for update the value of the instance
    public Task.General update(Manager.ResultProcessor<Integer> postProcessor) throws ExceptionInInitializerError, IOException,RestApiException, DataFormatException {

        if (Session.instance == null)
            throw new ExceptionInInitializerError("Session error : login First !");


        Task.General t = new Task.General(Task.Method.LOGIN, postProcessor);
        t.execute(this.email,this.password);
        return t;

    }



    private String email = null;
    private String password = null;

    //Private constructor.
    public Session(String email, String password) throws IOException, RestApiException, DataFormatException{



        if ( email == null || password == null )
            throw new DataFormatException("Session : can't login with null email and/or password");

        this.email = email;
        this.password = password;

        String emailFormatted = Utils.formatEmail(email);


        Map<String, String> params = new HashMap<>();

        params.put("session[email]", emailFormatted);
        params.put("session[password]", password);


        String resp = RESTManager.send(RESTManager.POST, "session", params);


        Object obj;

        try {

            JSONObject objJson = new JSONObject(resp);

            if (objJson.has("student")) {

                obj = new Student(objJson.getJSONObject("student"));

            } else if (objJson.has("company")) {

                obj = new Company(objJson.getJSONObject("company"));

            } else {
                throw new RestApiException(404, "Login failed!");
            }

        }catch( JSONException exception){

            throw new RestApiException(-1,"Session : Internal error in session response");

        }



        if( obj.getClass() == Student.class){

            Log.d("Session", "Student logged");
            this.whoIsLogged = Student.class;

            this.studentLogged = (Student)obj;


            this.favCompanies = Manager.getFavouriteCompanyOfStudent(this.studentLogged.getId());
            this.offers = Manager.getFavouriteOfferOfStudent(this.studentLogged.getId());
            this.appliedOffers = Manager.getAppliedOfferOfStudent(this.studentLogged.getId(),null);


        }else if( obj.getClass() == Company.class ){

            Log.d("Session", "Company logged");

            this.whoIsLogged = Company.class;

            this.companyLogged = (Company)obj;
            this.favStudents = Manager.getFavouriteStudentOfCompany(this.companyLogged.getId());

            Offer criteria = new Offer();
            criteria.setCompanyId(this.companyLogged.getId());
            this.offers = Manager.getOffersMatchingCriteria(criteria);
        }


    }


}
