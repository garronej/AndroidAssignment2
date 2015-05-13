package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mark9 on 28/04/15.
 */
class StudentManager {

    private static final String BASE_URI = "students";



    protected static Student getStudentById(int id) throws RestApiException, IOException {
        Student s = null;

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+id,null);

        try {
            return new Student(new JSONObject(resp).getJSONObject("student"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error StudentManager");
        }

    }

    /**
     *
     * @param newStudent a Student object representing the student to be inserted.
     * @return a Student object representing the inserted object or null if the operation failed.
     * @throws Exception Network related exceptions can be thrown
     */
    protected static Student insertNewStudent(Student newStudent) throws RestApiException, IOException {

        if( newStudent == null ) throw new RestApiException(422,"insertNewStudent : input student is null");

        if( newStudent.getEmail() == null || !newStudent.isSetPassword() ){
            throw new RestApiException(422, "insertNewStudent : Email and password are compulsory");
        }

        String resp = RESTManager.send(RESTManager.POST, BASE_URI,newStudent.toFormParams());



        try {


            return new Student(new JSONObject(resp).getJSONObject("student"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error StudentManager in insertNewStudent");
        }

    }



    //To test
    protected static List<Student> getStudentsMatchingCriteria( Student criteria ) throws RestApiException, IOException {

        Map<String,String> params = null;

        if( criteria != null ){

            if( criteria.getName() == null  &&
                    criteria.getSurname() == null &&
                    criteria.getUniversityCareer() == null &&
                    criteria.getCompetences() == null &&
                    criteria.isAvailable() == null ){
                throw new RestApiException(422, "getStudentsMatchingCriteria : no valid search criteria had been specified");
            }

            params = criteria.toFormParams();

        }

        String resp = RESTManager.send(RESTManager.GET, BASE_URI, params);

        try{


            JSONArray studentsJson = (new JSONObject(resp)).getJSONArray("students");

            List<Student> students = new ArrayList<Student>();



            for( int i = 0; i < studentsJson.length(); i++){

                JSONObject studentJson = studentsJson.getJSONObject(i);

                students.add( new Student(studentJson) );
            }

            return students;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error StudentManager in getStudentMatchingCriteria");
        }

    }

    //to test
    protected static Student updateStudent( Student studentToUpdate ) throws IOException, RestApiException {

        if( studentToUpdate == null ) throw new RestApiException(422,"updateStudent : input student is null");

        if( studentToUpdate.getId() == null ){
            throw new RestApiException(422, "updateStudent : id is not set in the imputed student");
        }

        String resp = RESTManager.send(RESTManager.PUT, BASE_URI+"/"+studentToUpdate.getId(), studentToUpdate.toFormParams());

        try {
            return new Student(new JSONObject(resp).getJSONObject("student"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error StudentManager in updateStudent");
        }

    }

    //to test
    protected static Integer deleteStudent( int id) throws IOException, RestApiException {

        RESTManager.send(RESTManager.DELETE, BASE_URI+"/"+id,null);
        return 0;

    }








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


        RESTManager.send(RESTManager.DELETE, BASE_URI + "/" + studentId + "/favs/companies/" + companyId, null);


        return 0;

    }




    protected static List<Offer> getFavouriteOfferOfStudent(int studentId) throws IOException, RestApiException{

        List<Offer> companies = new ArrayList<Offer>();

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+ studentId + "/favs/offers", null);


        try {
            JSONObject obj = new JSONObject(resp);

            JSONArray arr = obj.getJSONArray("fav_offers");

            for( int i = 0; i<arr.length(); i++)
                companies.add( new Offer(arr.getJSONObject(i).getJSONObject("offer")));

            return companies;

        }catch(JSONException e){
            throw new RestApiException(-1,"Internal Error StudentManager in getFavouriteOfferOfStudent()");
        }

    }


    protected static Offer addFavouriteOfferForStudent(int studentId, int offerId)
            throws IOException, RestApiException{

        Map<String,String> param = new HashMap<String, String>();

        param.put("fav_offer[offer_id]", Integer.toString(offerId));

        String resp = RESTManager.send(RESTManager.POST, BASE_URI+"/"+ studentId + "/favs/offers", param);

        try{

            JSONObject obj = new JSONObject(resp);

            return new Offer(obj.getJSONObject("fav_offer").getJSONObject("offer"));


        }catch(JSONException e){
            throw new RestApiException(-1,"Internal Error StudentManager in addFavouriteOfferForStudent()");
        }
    }


    protected static Integer deleteAFavouriteOfferOfAStudent( int studentId, int offerId)
            throws IOException, RestApiException{


        RESTManager.send(RESTManager.DELETE, BASE_URI + "/" + studentId + "/favs/companies/" + offerId, null);


        return 0;

    }



    protected static List<Student> getStudentsForJobOffer( Integer idJobOffer, Student criteria ) throws RestApiException, IOException {

        Map<String,String> params = new HashMap<>();

        if( criteria != null ){

            params = criteria.toFormParams();

        }

        params.put("offer_applied_id", idJobOffer.toString());



        String resp = RESTManager.send(RESTManager.GET, BASE_URI, params);

        try{


            JSONArray studentsJson = (new JSONObject(resp)).getJSONArray("students");

            List<Student> students = new ArrayList<Student>();



            for( int i = 0; i < studentsJson.length(); i++){

                JSONObject studentJson = studentsJson.getJSONObject(i);

                students.add( new Student(studentJson) );
            }

            return students;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error StudentManager in getStudentMatchingCriteria");
        }

    }


    protected static Integer subscribeJobOffer(Integer idJobOffer, Integer idStudentOffer)
            throws IOException, RestApiException{

        HashMap<String, String> params = new HashMap<>();
        params.put("application[offer_id]", idJobOffer.toString());

        RESTManager.send(RESTManager.POST, BASE_URI + "/" + idStudentOffer + "/applications/offers/", params);
        return 0;

    }


    protected static Integer unsubscribeJobOffer(Integer idJobOffer, Integer idStudentOffer)
            throws IOException, RestApiException{

        RESTManager.send(RESTManager.DELETE, BASE_URI + "/" + idStudentOffer + "/applications/offers/" + idJobOffer, null);
        return 0;

    }





}
