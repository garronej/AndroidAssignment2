package it.polito.mobile.androidassignment2.businessLogic.timetable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 01/06/2015.
 */
public class Course implements Event {



        private String teacher;
        private String title;
        private List<PracticalInformation> lectures;



        protected Course( JSONObject courseJson ){

            try {
                this.teacher = courseJson.getString("teacherName");
                this.title = courseJson.getString("courseTitle");
                this.lectures = new ArrayList<>();


                JSONArray lecturesJson = courseJson.getJSONArray("lectures");


                for (int i = 0; i < lecturesJson.length(); i++)
                    this.lectures.add(new PracticalInformation(lecturesJson.getJSONObject(i)));




            }catch(JSONException exception){
                throw new RuntimeException("Error parsing course");
            }

        }



        @Override
        public String getTeacher(){
            return this.teacher;
        }

        public String getTitle(){
            return this.title;
        }

        public List<PracticalInformation> getLectures(){
            return this.lectures;
        }

        @Override
        public List<PracticalInformation> getPracticalInformation(){
            return this.getLectures();
        }




    }



