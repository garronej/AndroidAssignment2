package it.polito.mobile.androidassignment2.businessLogic.timetable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 01/06/2015.
 */
public class Consultation implements Event {



        private String teacher;

        private List<PracticalInformation> info;



        protected Consultation( JSONObject consultationJson ){

            try {
                this.teacher = consultationJson.getString("teacherName");
                this.info = new ArrayList<>();


                JSONArray allPracticalInformationJson = consultationJson.getJSONArray("timePlace");


                for (int i = 0; i < allPracticalInformationJson.length(); i++)
                    this.info.add(new PracticalInformation(allPracticalInformationJson.getJSONObject(i)));




            }catch(JSONException exception){
                throw new RuntimeException("Error parsing course");
            }

        }



    @Override
        public String getTeacher(){
            return this.teacher;
        }


        public List<PracticalInformation> getInfo(){
            return this.info;
        }

    @Override
    public List<PracticalInformation> getPracticalInformation(){
        return this.getInfo();
    }






    }

