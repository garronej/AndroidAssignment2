package it.polito.mobile.androidassignment2.businessLogic.timetable;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import it.polito.mobile.androidassignment2.businessLogic.RestApiException;


/**
 * Created by Joseph on 01/06/2015.
 */
public class TimeTableData {


        private List<Course> courses;
        private List<Consultation> consultations;

        public Context appContext;





        private List<Course> getCourseTimeTableData() throws RestApiException, IOException {


            /** To uncomment once the endpoint is avalible.
            String resp = RESTManager.send(RESTManager.GET, "students/0/courses", null);
             **/


            //Temporary
            InputStream is = (this.appContext.getAssets()).open("sample_courses");
            Scanner scan = new Scanner(is);
            String resp = new String();
            while (scan.hasNext())
                resp += scan.nextLine();
            scan.close();




            List<Course> courses = new ArrayList<>();
            try {

                JSONArray coursesJson = (new JSONObject(resp)).getJSONArray("Courses");

                for (int i = 0; i < coursesJson.length(); i++)
                   courses.add( new Course(coursesJson.getJSONObject(i)));

                return courses;


            } catch (JSONException e) {
                throw new RuntimeException("Unexpected error in time table data getCourseTimeTableData," +
                        " Parse error in timeTable data");
            }


        }

    //TODO
    private static List<Course> getConsultationTimeTableData() throws RestApiException, IOException {return null;}


    //Should be called only from context/AppContext
        public TimeTableData(Context appContext) throws IOException, RestApiException{

            this.appContext = appContext;
            this.courses = getCourseTimeTableData();

        }


    }



