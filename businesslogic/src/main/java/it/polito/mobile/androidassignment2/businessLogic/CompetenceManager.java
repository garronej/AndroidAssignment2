package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark9 on 16/05/15.
 */
public class CompetenceManager {


    public static List<String> getAllCompetences() throws IOException, RestApiException{
        String resp = RESTManager.send(RESTManager.GET, "competences/students", null);
        try{
            JSONArray json = (new JSONObject(resp)).getJSONArray("competences_students");

            List<String> competences = new ArrayList<String>();

            for( int i = 0; i < json.length(); i++){
                competences.add( json.getString(i) );
            }

            return competences;


        } catch (JSONException e) {
            throw new RestApiException(-1,"Internal Error CompetenceManager in getAllCompetences");
        }
    }
}
