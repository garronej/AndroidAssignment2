package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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


        String resp = RESTManager.send(RESTManager.GET, "session",params);

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
            this.studentLogged = (Student)obj;
        }else if( obj.getClass() == Company.class ){
            this.companyLogged = (Company)obj;
        }

        //Get favourite

    }

    protected static class LoginInfo{

        public String email;
        public String password;

    }

    protected static synchronized Integer login( Session.LoginInfo log) throws IOException,RestApiException, DataFormatException{

        Session.instance = new Session(log.email,log.password);

        return 0;

    }

    //First Login
    //If login fail => ResApiException.
    //If Email malformed => DataFormatException
    //If network problem IOException
    public static void login( String email, String password, Manager.ResultProcessor<Integer> postProcessor ){

        Session.LoginInfo log = new Session.LoginInfo();
        log.email = email;
        log.password = password;

        (new Task.General(Task.Method.LOGIN, postProcessor)).execute(log);

    }


    //Then we can get the instance synchronously.
    public static Session getInstance() throws ExceptionInInitializerError {
        if (Session.instance == null)
            throw new ExceptionInInitializerError("Session error : login First !");

        return Session.instance;

    }


}
