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

        if( criteria != null ) params = criteria.toFormParams();

        String resp = RESTManager.send(RESTManager.GET, BASE_URI + "/students", params);

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
            throw new RestApiException(422, "insertNewStudent : id is not set in the imputed student");
        }

        String resp = RESTManager.send(RESTManager.PUT, BASE_URI+"/"+studentToUpdate.getId(), studentToUpdate.toFormParams());

        try {
            return new Student(new JSONObject(resp).getJSONObject("student"));
        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error StudentManager in insertNewStudent");
        }

    }

    //to test
    protected static Integer deleteStudent( int id) throws IOException, RestApiException {

        RESTManager.send(RESTManager.DELETE, BASE_URI+"/"+id,null);
        return 0;

    }







}
