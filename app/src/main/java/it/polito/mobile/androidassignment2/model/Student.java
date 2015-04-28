package it.polito.mobile.androidassignment2.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


/**
 * Created by mark9 on 28/04/15.
 */
public class Student {

    private int id;
    private String email;
    private String name;
    private String surname;
    private String photoUrl;
    private String cvUrl;
    private String[] links;
    private String universityCareer;
    private String[] competences;
    private String[] hobbies;
    private boolean available;


    public Student(JSONObject json){

        try {
            if (json.has("id")) {
                id = json.getInt("id");
            }
            if (json.has("email")) {
                email = json.getString("email");
            }
            if (json.has("name")) {
                name = json.getString("name");
            }
            if (json.has("surname")) {
                surname = json.getString("surname");
            }
            if (json.has("photo")) {
                photoUrl = json.getString("photo");
            }
            if (json.has("cv")) {
                cvUrl = json.getString("cv");
            }
            if (json.has("university_career")) {
                universityCareer = json.getString("university_career");
            }
            if (json.has("availability")) {
                available = json.getBoolean("availability");
            }
            if (json.has("links")) {
                JSONArray jsonLinks = json.getJSONArray("links");
                links=new String[jsonLinks.length()];
                for(int i = 0; i<jsonLinks.length(); i++){
                    links[i] = jsonLinks.getString(i);
                }
            }
            if (json.has("competences")) {
                JSONArray jsonComps = json.getJSONArray("competences");
                competences=new String[jsonComps.length()];
                for(int i = 0; i<jsonComps.length(); i++){
                    competences[i] = jsonComps.getString(i);
                }
            }
            if (json.has("hobbies")) {
                JSONArray jsonHobbies = json.getJSONArray("hobbies");
                hobbies=new String[jsonHobbies.length()];
                for(int i = 0; i<jsonHobbies.length(); i++){
                    hobbies[i] = jsonHobbies.getString(i);
                }
            }
        }catch (JSONException e){
            //should never be here
        }
    }


    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public String[] getLinks() {
        return links;
    }

    public String getUniversityCareer() {
        return universityCareer;
    }

    public String[] getCompetences() {
        return competences;
    }

    public String[] getHobbies() {
        return hobbies;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", cvUrl='" + cvUrl + '\'' +
                ", links=" + Arrays.toString(links) +
                ", universityCareer='" + universityCareer + '\'' +
                ", competences=" + Arrays.toString(competences) +
                ", hobbies=" + Arrays.toString(hobbies) +
                ", available=" + available +
                '}';
    }
}
