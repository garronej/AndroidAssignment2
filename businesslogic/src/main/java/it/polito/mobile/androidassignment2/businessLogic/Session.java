package it.polito.mobile.androidassignment2.businessLogic;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
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

    private static Object restLogin( String email, String password ) throws RestApiException, IOException, DataFormatException{


        if ( email == null || password == null )
            throw new DataFormatException("Session : can't login with null email and/or password");
        String emailFormatted = Utils.formatEmail(email);


        Map<String, String> params = new HashMap<String,String>();

        params.put("session[email]", emailFormatted);
        params.put("session[password]", password);


        String resp = RESTManager.send(RESTManager.POST, "session",params);

        try {

            JSONObject obj = new JSONObject(resp);

            if (obj.has("student")) {

                return new Student(obj.getJSONObject("student"));

            } else if (obj.has("company")) {

                return new Company(obj.getJSONObject("company"));

            } else {
                throw new RestApiException(404, "Login failed!");
            }

        }catch( JSONException exception){

            throw new RestApiException(-1,"Session : Internal error");

        }

    }


    private Session(String email, String password) throws IOException, RestApiException, DataFormatException{


        Object obj = Session.restLogin(email,password);

        if( obj.getClass() == Student.class){
            this.whoIsLogged = Student.class;

            this.studentLogged = (Student)obj;


            this.favCompanies = Manager.getFavouriteCompanyOfStudent(this.studentLogged.getId());
            this.offers = Manager.getFavouriteOfferOfStudent(this.studentLogged.getId());
            this.appliedOffers = Manager.getAppliedOfferOfStudent(this.studentLogged.getId(),null);

        }else if( obj.getClass() == Company.class ){
            this.whoIsLogged = Company.class;

            this.companyLogged = (Company)obj;
            this.favStudents = Manager.getFavouriteStudentOfCompany(this.companyLogged.getId());

            Offer criteria = new Offer();
            criteria.setCompanyId(this.companyLogged.getId());
            this.offers = Manager.getOffersMatchingCriteria(criteria);
        }



    }

    protected static class LoginInfo{

        public String email;
        public String password;

    }

    protected static synchronized Integer login( LoginInfo log) throws IOException,RestApiException, DataFormatException{

        Session.instance = new Session(log.email,log.password);

        return 0;

    }

    //First Login
    //If login fail => ResApiException.
    //If Email malformed => DataFormatException
    //If network problem IOException
    public static Task.General login( String email, String password, Manager.ResultProcessor<Integer> postProcessor ){

        LoginInfo log = new LoginInfo();
        log.email = email;
        log.password = password;
        Task.General t = new Task.General(Task.Method.LOGIN, postProcessor);
        t.execute(log);
        return t;

    }


    //Then we can get the instance synchronously.
    public static Session getInstance() throws ExceptionInInitializerError {
        if (Session.instance == null)
            throw new ExceptionInInitializerError("Session error : login First !");

        return Session.instance;

    }

    //look at field definition above for more details
    public void setPhotoUri(Uri uri) {
        this.photoUri = uri;
    }
    //look at field definition above for more details
    public Uri getPhotoUri() {
        return this.photoUri;
    }
    public Student s;
    public Session() {
        try {
            s = new Student();
            s.setEmail("Joseph.garrOne.gj@gmail.com");
            s.setName("GaRRone");
            s.setSurname("Joseph");
            s.setPassword("stupid2");
            s.setCvUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/esercitazione2.pdf");
            s.setUniversityCareer("computer engineering");
            s.setAvailable(false);
            s.setCompetences(new String[]{"porn knowelage", "fast masturbation"});
            s.setHobbies(new String[]{"porn", "masturbation"});
            s.setLinks(new URL[]{new URL("http://seedbox.garrone.org"), new URL("http://etophy.fr")});
            s.setPhotoUrl("eu-west-1:3f1af8e8-7e5e-4210-b9eb-f4f29f7b66ab/photo/student3/jos.png");
            s.setSex("m");
            s.setLocation("torino");
        } catch(Exception e) {}
    } //TODO REMOVE
    public static Session getInstanceFake() {
        if (Session.instance == null) {
            Session.instance = new Session();
        }
        return Session.instance;
    }

}
