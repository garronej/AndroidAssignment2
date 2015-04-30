package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


/**
 * Created by mark9 on 28/04/15.
 */
class StudentManager {

    private static String BASE_URI = "students";



    protected static Student getStudentById(Integer id) throws RestApiException, IOException {
        Student s = null;

        String resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+id,new HashMap<String, String>());

        try {
            return new Student(new JSONObject(resp).getJSONObject("student"));
        } catch (JSONException e) {
            throw new RestApiException("Internal Error StudentManager");
        }

    }

    /**
     *
     * @param newStudent a Student object representing the student to be inserted.
     * @return a Student object representing the inserted object or null if the operation failed.
     * @throws Exception Network related exceptions can be thrown
     */
    protected static Student insertNewStudent(Student newStudent) throws RestApiException, IOException {

        String resp = RESTManager.send(RESTManager.POST, BASE_URI,newStudent.toFormParams());

        try {
            return new Student(new JSONObject(resp).getJSONObject("student"));
        } catch (JSONException e) {
            throw new RestApiException("Internal Error StudentManager");
        }




    }



}
