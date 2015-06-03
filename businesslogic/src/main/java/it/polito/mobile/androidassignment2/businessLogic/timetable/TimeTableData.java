package it.polito.mobile.androidassignment2.businessLogic.timetable;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import it.polito.mobile.androidassignment2.businessLogic.RESTManager;
import it.polito.mobile.androidassignment2.businessLogic.RestApiException;


/**
 * Created by Joseph on 01/06/2015.
 */
public class TimeTableData {


    private List<Course> courses;
    private List<Consultation> consultations;

    public List<Course> getCourses(){
        return this.courses;
    }

    public List<Consultation> getConsultations(){return this.consultations;}

        private Context appContext;





        private List<Course> getCourseTimeTableData() throws RestApiException, IOException {


            /** To uncomment once the endpoint is avalible.**/
            String resp = RESTManager.send(RESTManager.GET, "students/0/courses", null);



            //Temporary

            /**
            InputStream is = (this.appContext.getAssets()).open("sample_courses.json");
            Scanner scan = new Scanner(is);
            String resp = new String();
            while (scan.hasNext())
                resp += scan.nextLine();
            scan.close();
             **/




            List<Course> courses = new ArrayList<>();
            try {

                JSONArray coursesJson = (new JSONObject(resp)).getJSONArray("courses");

                for (int i = 0; i < coursesJson.length(); i++)
                   courses.add( new Course(coursesJson.getJSONObject(i)));

                return courses;


            } catch (JSONException e) {
                throw new RuntimeException("Unexpected error in time table data getCourseTimeTableData," +
                        " Parse error in timeTable data");
            }


        }


    private List<Consultation> getConsultationTimeTableData() throws RestApiException, IOException {


        /** To uncomment once the endpoint is available. **/
         String resp = RESTManager.send(RESTManager.GET, "consulting_hours", null);



        List<Consultation> consultations = new ArrayList<>();
        try {

            JSONArray consultationsJson = (new JSONObject(resp)).getJSONArray("consulting_hours");

            for (int i = 0; i < consultationsJson.length(); i++)
                consultations.add( new Consultation(consultationsJson.getJSONObject(i)));

            return consultations;


        } catch (JSONException e) {
            throw new RuntimeException("Unexpected error in time table data getCourseTimeTableData," +
                    " Parse error in timeTable data");
        }



    }


    //Should be called only from context/AppContext
        public TimeTableData(Context appContext) throws IOException, RestApiException{

            this.appContext = appContext;
            this.courses = this.getCourseTimeTableData();
            this.consultations = this.getConsultationTimeTableData();

        }


    }



