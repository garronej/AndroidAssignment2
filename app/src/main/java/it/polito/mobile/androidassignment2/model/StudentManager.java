package it.polito.mobile.androidassignment2.model;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by mark9 on 28/04/15.
 */
public class StudentManager {

    private static String BASE_URI = "students";

    /**
     * The method allows to retrieve all the informations
     * about a student (with a given id).
     *
     * @param id the id of the student to be retrieved
     * @return A Student object representing the student if it exists, null otherwise.
     * @throws Exception Network related exception can be thrown.
     */
    public static Student getStudentInfo(int id) throws Exception {
        Student s = null;

            RESTManager.Response resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+id,new HashMap<String, String>());
            if(resp.getRespCode()==200) {
                Log.d("poliJob", resp.getContent());

                s = new Student(new JSONObject(resp.getContent()).getJSONObject("student"));
            }

        return s;

    }

    /**
     *
     * @param newStudent a Student object representing the student to be inserted.
     * @return a Student object representing the inserted object or null if the operation failed.
     * @throws Exception Network related exceptions can be thrown
     */
    public static Student insertNewStudent(Student newStudent) throws Exception {
        Student s = null;

        RESTManager.Response resp = RESTManager.send(RESTManager.POST, BASE_URI,newStudent.toFormParams());
        if(resp.getRespCode()==200) {
            Log.d("poliJob", resp.getContent());

            s = new Student(new JSONObject(resp.getContent()).getJSONObject("student"));
        }

        return s;

    }



}
