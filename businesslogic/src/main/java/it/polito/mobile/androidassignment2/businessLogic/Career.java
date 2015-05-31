package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mark9 on 30/05/15.
 */
public class Career {

    private String career;
    private int mark;
    private String date;

    public Career(JSONObject o) {
        try {
            career = o.getString("career");
            if(o.getString("date")!="null") {
                date = o.getString("date");
            }
            if(o.getString("final_grade")!="null") {
                mark = o.getInt("final_grade");
            }
        }catch(JSONException e){
            throw new RuntimeException("Career json object received is not well formed: "+e.getMessage());
        }


    }

    public Career() {
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormattedMark(){
        if(mark==0) return "";
        if(mark==111){
            return "110L";
        }else{
            return String.valueOf(mark);
        }
    }
}
