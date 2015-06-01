package it.polito.mobile.androidassignment2.businessLogic.timetable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Joseph on 01/06/2015.
 */
public class PracticalInformation {


        class Time {
            private int hour;
            private int min;

            public Time(int hour, int min) {
                this.hour = hour;
                this.min = min;
            }

            public int getHour() {
                return this.hour;
            }

            public int getMin() {
                return this.min;
            }

            @Override
            public String toString() {
                return new String(Integer.toString(this.hour) + ':' + Integer.toString(this.min));
            }

            public void add(int min) {
                this.hour += min / 60 + (this.min + (min % 60)) / 60;
                this.min = (this.min + min) % 60;
            }
        }


        //ex Calendar.MONDAY
        private int day;


        private Time startTime;

        //Duration of the lecture in minute.
        private int duration;


        private String room;



        protected PracticalInformation(JSONObject practicalInformationJson){

            try {

                String day = practicalInformationJson.getString("day");

                if ( day.equals("monday") ) {
                    this.day = Calendar.MONDAY;
                } else if (day.equals("tuesday")) {
                    this.day = Calendar.TUESDAY;
                } else if (day.equals("wednesday")) {
                    this.day = Calendar.WEDNESDAY;
                } else if (day.equals("thursday")) {
                    this.day = Calendar.THURSDAY;
                } else if (day.equals("friday")) {
                    this.day = Calendar.FRIDAY;
                } else {
                    this.day = Calendar.SUNDAY;
                }


                String[] time = practicalInformationJson.getString("startTime").split(":");

                String[] duration = practicalInformationJson.getString("duration").split(":");

                this.startTime = new PracticalInformation.Time(Integer.valueOf(time[0]), Integer.valueOf(time[1]));

                this.duration = Integer.valueOf(duration[0]) * 60 + Integer.valueOf(duration[1]);


                this.room = practicalInformationJson.getString("room");



            }catch( JSONException exception){
                throw new RuntimeException("Error parsing PracticalInformation_json");
            }



        }



        //Return the duration in minute.
        public int getDuration(){
            return this.duration;
        }

        public String getRoom(){
            return this.room;
        }

        //Return something like 8h30 : 11h30
        public String getRange() {

            PracticalInformation.Time end = new PracticalInformation.Time(this.startTime.getHour(), this.startTime.getMin());

            end.add(this.duration);

            return new String(this.startTime.toString() + " : " + end.toString());

        }

        //Return the offset that represent the number of minute between 8am and the start of the course
        public int getOffset() {
            return (this.startTime.getHour() - 8)*60 + this.startTime.getMin();
        }





    }




