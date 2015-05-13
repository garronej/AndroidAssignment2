package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;


/**
 * Created by mark9 on 28/04/15.
 */


//Tdodo manage exception
public class Student {

    private Integer id = null;
    private String email = null;
    private String name = null;
    private String surname = null;
    private String photoUrl = null;
    private String cvUrl = null;
    private URL[] links = null;
    private String universityCareer = null;
    private String[] competences = null;
    private String[] hobbies = null;
    private Boolean available = null;
    private String password = null;


    public Student(){
        super();
    }


    public void manuallySetId(int id){
        this.id = id;
    }

    public void setEmail(String email) throws DataFormatException{

        this.email = Utils.formatEmail(email);

    }

    public void setName(String name) throws DataFormatException{

        this.name = Utils.formatName(name);

    }

    public void setSurname(String surname) throws DataFormatException{

        this.surname = Utils.formatName(surname);
    }

    public void setPhotoUrl(String photoUrl ){
        this.photoUrl = photoUrl;
    }


    public void setCvUrl(String cvUrl){

        this.cvUrl = cvUrl;

    }


    public void setLinks(URL[] links){
        this.links = links;
    }


    public void setUniversityCareer(String universityCareer) throws DataFormatException{

        this.universityCareer = Utils.toLowerCase(universityCareer);

    }

    public void setCompetences( String[] competences) throws DataFormatException{

        if( competences != null) {
            this.competences = new String[competences.length];
            for (int i = 0; i < competences.length; i++) {
                this.competences[i] = Utils.toLowerCase(competences[i]);
            }
        }
    }

    public void setHobbies( String[] hobbies) throws DataFormatException{

        if( hobbies != null ) {
            this.hobbies = new String[hobbies.length];
            for (int i = 0; i < hobbies.length; i++) {

                this.hobbies[i] = Utils.toLowerCase(hobbies[i]);

            }
        }

    }

    public void setAvailable( boolean available ){
        this.available = available;
    }

    public void setPassword( String password) throws DataFormatException{

        Utils.checkPassword(password);

        this.password=password;
    }




    //We asume the JSON object sended are well formed.
    protected Student(JSONObject json){


        try {

            String buff;

            this.id = json.getInt("id");


            this.email = json.getString("email");

            buff = json.getString("name");

            if( !buff.equals("null")){
                this.name = buff;
            }


            buff = json.getString("surname");

            if( !buff.equals("null")){
                this.surname = buff;
            }


            buff = json.getString("photo");

            if ( !buff.equals("null") ) {

                this.photoUrl = buff;

            }

            buff = json.getString("cv");

            if( !buff.equals("null")){
                this.cvUrl = buff;
            }

            buff = json.getString("university_career");

            if( !buff.equals("null")){
                this.universityCareer = buff;
            }


            this.available = json.getBoolean("availability");


            buff = json.getString("links");

            if( !buff.equals("null") ){

                JSONArray jsonLinks = json.getJSONArray("links");

                links = new URL[jsonLinks.length()];
                for (int i = 0; i < jsonLinks.length(); i++) {
                    links[i] = new URL(jsonLinks.getString(i));
                }

            }



            buff = json.getString("competences");



            if( !buff.equals("null")){

                JSONArray jsonComps = json.getJSONArray("competences");
                competences=new String[jsonComps.length()];
                for(int i = 0; i<jsonComps.length(); i++){
                    competences[i] = jsonComps.getString(i);
                }

            }

                buff = json.getString("hobbies");

            if( !buff.equals("null")) {


                JSONArray jsonHobbies = json.getJSONArray("hobbies");
                hobbies = new String[jsonHobbies.length()];
                for (int i = 0; i < jsonHobbies.length(); i++) {
                    hobbies[i] = jsonHobbies.getString(i);
                }

            }


        }catch (Exception e){

            //Should nor append
        }
    }


    public Integer getId() {
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

    public URL[] getLinks() {
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

    public Boolean isAvailable() {
        return available;
    }

    public Boolean isSetPassword() { return (this.password != null); }

    @Override
    public String toString() {
        return "Student{" + "\n" +
                "  id=" + id + ",\n" +
                "  email='" + email + "',\n" +
                "  name='" + name + "',\n" +
                "  surname='" + surname + "',\n" +
                "  photoUrl='" + photoUrl + "',\n" +
                "  cvUrl='" + cvUrl + "',\n" +
                "  links=" + Arrays.toString(links) + ",\n" +
                "  universityCareer='" + universityCareer + "',\n" +
                "  competences=" + Arrays.toString(competences) + ",\n" +
                "  hobbies=" + Arrays.toString(hobbies) + ",\n" +
                "  available=" + available + ",\n" +
                '}';
    }

    public Map<String,String> toFormParams(){
        Map<String,String> s = new HashMap<String,String>();
        if(email!=null){
            s.put("student[email]", email);
        }
        if(password!=null){
            s.put("student[password]", password);
        }
        if(name!=null){
            s.put("student[name]", name);
        }
        if(surname!=null){
            s.put("student[surname]", surname);
        }
        if(photoUrl!=null){
            s.put("student[photo]", photoUrl);
        }
        if(cvUrl!=null){
            s.put("student[cv]", cvUrl);
        }
        if(available!=null){
            s.put("student[availability]", available.toString());
        }
        if(universityCareer!=null){
            s.put("student[university_career]", universityCareer);
        }
        if(competences!=null && competences.length>0){
            String c=competences[0];
            for(int i=1;i<competences.length;i++){
                c+=","+competences[i];
            }

            s.put("student[competences]", c);
        }
        if(links!=null && links.length>0){
            String c=links[0].toString();
            for(int i=1;i<links.length;i++){
                c+=","+links[i].toString();
            }
            s.put("student[links]", c);
        }
        if(hobbies!=null && hobbies.length>0){
            String c=hobbies[0];
            for(int i=1;i<hobbies.length;i++){
                c+=","+hobbies[i];
            }
            s.put("student[hobbies]", c);
        }
        return s;
    }
}