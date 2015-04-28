package it.polito.mobile.androidassignment2.model;

import android.util.Log;

import org.json.JSONObject;


/**
 * Created by mark9 on 28/04/15.
 */
public class StudentManager {

    private static String BASE_URI = "students";

    public static Student getStudentInfo(int id) throws Exception {
        Student s = null;

            RESTManager.Response resp = RESTManager.send(RESTManager.GET, BASE_URI+"/"+id,"");
            if(resp.getRespCode()==200) {
                Log.d("poliJob", resp.getContent());

                s = new Student(new JSONObject(resp.getContent()).getJSONObject("student"));
            }

        return s;

    }


}
