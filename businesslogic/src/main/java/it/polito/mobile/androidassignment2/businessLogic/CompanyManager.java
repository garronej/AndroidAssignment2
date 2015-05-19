package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Joseph on 04/05/2015.
 */
class CompanyManager {


    private static final String BASE_URI = "companies";



    protected static Company getCompanyById(int id) throws RestApiException, IOException {
        Company s = null;

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+id,null);

        try {
            return new Company(new JSONObject(resp).getJSONObject("company"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompanyManager");
        }

    }

    /**
     *
     * @param newCompany a Company object representing the student to be inserted.
     * @return a Company object representing the inserted object or null if the operation failed.
     * @throws Exception Network related exceptions can be thrown
     */
    protected static Company insertNewCompany(Company newCompany) throws RestApiException, IOException {

        if( newCompany == null ) throw new RestApiException(422,"insertNewCompany : input company is null");

        if( newCompany.getEmail() == null || !newCompany.isSetPassword() ){
            throw new RestApiException(422, "insertNewCompany : Email and password are compulsory");
        }

        String resp = RESTManager.send(RESTManager.POST, BASE_URI,newCompany.toFormParams());



        try {
            return new Company(new JSONObject(resp).getJSONObject("company"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompanyManager in insertNewCompany" + e.getMessage());
        }

    }



    //To test
    protected static List<Company> getCompaniesMatchingCriteria( Company criteria ) throws RestApiException, IOException {

        Map<String,String> params = null;

        if( criteria != null ){

            if( criteria.getName() == null &&
                    criteria.getLocation() == null &&
                    criteria.getDescription() == null){
                throw new RestApiException(422, "getCompaniesMatchingCriteria : no valid search criteria had been specified");
            }

            params = criteria.toFormParams();
        }

        String resp = RESTManager.send(RESTManager.GET, BASE_URI, params);



        try{


            JSONArray companiesJson = (new JSONObject(resp)).getJSONArray("companies");

            List<Company> companies = new ArrayList<Company>();



            for( int i = 0; i < companiesJson.length(); i++){

                JSONObject companyJson = companiesJson.getJSONObject(i);



                Company tmp = new Company(companyJson);





                companies.add( new Company(companyJson) );
            }

            return companies;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompanyManager in getCompaniesMatchingCriteria");
        }

    }

    //to test
    protected static Company updateCompany( Company companyToUpdate ) throws IOException, RestApiException {

        if( companyToUpdate == null ) throw new RestApiException(422,"updateCompany : input company is null");

        if( companyToUpdate.getId() == null ){
            throw new RestApiException(422, "updateCompany : id is not set in the imputed company");
        }

        String resp = RESTManager.send(RESTManager.PUT, BASE_URI+"/"+companyToUpdate.getId(), companyToUpdate.toFormParams());

        try {
            return new Company(new JSONObject(resp).getJSONObject("company"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompanyManager in updateCompany");
        }

    }

    //to test
    protected static Integer deleteCompany( int id) throws IOException, RestApiException {

        RESTManager.send(RESTManager.DELETE, BASE_URI+"/"+id,null);
        return 0;

    }


    protected static List<Offer> getOfferMatchingCriteria( Offer criteria ) throws RestApiException, IOException {

        Map<String,String> params = null;

        if( criteria != null ){

            if( criteria.getKindOfContract() == null  &&
                    criteria.getDescriptionOfWork() == null &&
                    criteria.getCompanyName() == null)
                throw new RestApiException(422, "OfferManager : getOfferMatchingCriteria :" +
                        " no valid search criteria had been specified");


            params = criteria.toFormParams();
        }

        String resp = RESTManager.send(RESTManager.GET, BASE_URI, params);

        try{


            JSONArray offersJson = (new JSONObject(resp)).getJSONArray("offers");

            List<Offer> offers = new ArrayList<Offer>();



            for( int i = 0; i < offersJson.length(); i++){

                JSONObject offerJson = offersJson.getJSONObject(i);

                offers.add( new Offer(offerJson) );
            }

            return offers;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error OfferManager in getOfferMatchingCriteria");
        }

    }










        protected static List<Student> getFavouriteStudentOfCompany(int companyId) throws IOException, RestApiException{

        List<Student> students = new ArrayList<Student>();

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+ companyId + "/favs/students", null);


        try {
            JSONObject obj = new JSONObject(resp);

            JSONArray arr = obj.getJSONArray("fav_students");

            for( int i = 0; i<arr.length(); i++)
                students.add( new Student(arr.getJSONObject(i).getJSONObject("student")));

            return students;

        }catch(JSONException e){
            throw new RestApiException(-1,"Internal Error CompanyManager in getFavouriteStudentOfCompany()");
        }

    }


    protected static Student addFavouriteStudentForCompany(int companyId, int studentId)
            throws IOException, RestApiException{

        Map<String,String> param = new HashMap<String, String>();

        param.put("fav_student[student_id]", Integer.toString(studentId));

        String resp = RESTManager.send(RESTManager.POST, BASE_URI+"/"+ companyId + "/favs/students", param);

        try{

            JSONObject obj = new JSONObject(resp);

            return new Student(obj.getJSONObject("fav_student").getJSONObject("student"));


        }catch(JSONException e){
            throw new RestApiException(-1,"Internal Error CompanyManager in addFavouriteStudentForCompany()");
        }
    }


    protected static Integer deleteAFavouriteStudentOfACompany( int companyId, int studentId)
            throws IOException, RestApiException{


        RESTManager.send(RESTManager.DELETE, BASE_URI + "/" + companyId + "/favs/students/" + studentId, null);


        return 0;

    }


    protected static List<String> getAllCompetences() throws IOException, RestApiException{
        String resp = RESTManager.send(RESTManager.GET, "competences/companies", null);
        try{
            JSONArray json = (new JSONObject(resp)).getJSONArray("competences_companies");

            List<String> competences = new ArrayList<String>();

            for( int i = 0; i < json.length(); i++){
                competences.add( json.getString(i) );
            }

            return competences;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompetenceManager in getAllCompetences");
        }
    }







}
