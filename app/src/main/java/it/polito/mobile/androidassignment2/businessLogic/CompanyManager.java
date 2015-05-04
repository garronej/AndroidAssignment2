package it.polito.mobile.androidassignment2.businessLogic;

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
            throw new RestApiException(422, "insertNewStudent : Email and password are compulsory");
        }

        String resp = RESTManager.send(RESTManager.POST, BASE_URI,newCompany.toFormParams());



        try {
            return new Company(new JSONObject(resp).getJSONObject("company"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompanyManager in insertNewCompany");
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



}
