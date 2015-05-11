package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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

        Log.v("CoMa", "resp = " + resp);

        try{


            JSONArray companiesJson = (new JSONObject(resp)).getJSONArray("companies");

            List<Company> companies = new ArrayList<Company>();



            for( int i = 0; i < companiesJson.length(); i++){

                JSONObject companyJson = companiesJson.getJSONObject(i);

                Log.v("CoMa","companyJson.toString() = " + companyJson.toString());

                Company tmp = new Company(companyJson);

                Log.v("CoMa","tmp.toString() = " + tmp.toString());



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




    /*

    TODO  : Intervert company and student !



        protected static List<Company> getFavouriteCompanyOfStudent(int studentId) throws IOException, RestApiException{

        List<Company> companies = new ArrayList<Company>();

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+ studentId + "/favs/companies", null);


        try {
            JSONObject obj = new JSONObject(resp);

            JSONArray arr = obj.getJSONArray("fav_companies");

            for( int i = 0; i<arr.length(); i++)
                companies.add( new Company(arr.getJSONObject(i).getJSONObject("company")));

            return companies;

        }catch(JSONException e){
            throw new RestApiException(-1,"Internal Error StudentManager in getFavouriteCompanyOfStudent()");
        }

    }


    protected static Company addFavouriteCompanyForStudent(int studentId, int companyId)
            throws IOException, RestApiException{

        Map<String,String> param = new HashMap<String, String>();

        param.put("fav_company[company_id]", Integer.toString(companyId));

        String resp = RESTManager.send(RESTManager.POST, BASE_URI+"/"+ studentId + "/favs/companies", param);

        try{

            JSONObject obj = new JSONObject(resp);

            return new Company(obj.getJSONObject("fav_company").getJSONObject("company"));


        }catch(JSONException e){
            throw new RestApiException(-1,"Internal Error StudentManager in addFavouriteCompanyForStudent()");
        }
    }


    protected static Integer deleteAFavouriteCompanyOfAStudent( int studentId, int companyId)
            throws IOException, RestApiException{


        RESTManager.send(RESTManager.DELETE, BASE_URI+"/"+ studentId + "/favs/companies/" + companyId, null);


        return 0;

    }





     */





}
