package it.polito.mobile.androidassignment2.businessLogic;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Created by Joseph on 04/05/2015.
 */
public class Company implements Serializable {

    private Integer id = null;
    private String email = null;
    private String name = null;
    private String logoUrl = null;
    private String mission = null;
    private Integer numberOfWorkers = null;
    private String[] clients = null;
    private String location = null;
    private String description = null;
    private String password = null;
    private String[] competences=null;





    public Company(){
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

    public void setLogoUrl(String logoUrl ){
        this.logoUrl = logoUrl;
    }


    public void setMission( String mission){
        this.mission = mission;
    }

    public void setNumberOfWorkers( Integer numberOfWorkers){
        this.numberOfWorkers = numberOfWorkers;
    }


    public void setClients(String[] clients){

        if( clients != null ) {
            this.clients = new String[clients.length];
            for (int i = 0; i < clients.length; i++) {

                this.clients[i] = Utils.toLowerCase(clients[i]);

            }
        }

    }

    public String getCompetencesToString(String separator) {
        String s = "";
        String[] competences = getCompetences();
        if (competences != null && competences.length > 0) {
            String lastCompetence = competences[competences.length - 1];
            for (String competence : competences) {
                s += competence;
                if (competence != lastCompetence) {
                    s += separator;
                }
            }
        }
        if (s.equals("")) { return null; }
        return s;
    }

    public String getClientsToString(String separator) {
        String s = "";
        String[] clients = getClients();
        if (clients != null && clients.length > 0) {
            String lastClient = clients[clients.length - 1];
            for (String client : clients) {
                s += client;
                if (client != lastClient) {
                    s += separator;
                }
            }
        }
        if (s.equals("")) { return null; }
        return s;
    }


    public void setLocation(String location){
        this.location = Utils.toLowerCase(location);
    }

    public void setDescription( String description){
        this.description = description;
    }


    public void setPassword( String password) throws DataFormatException{

        Utils.checkPassword(password);

        this.password=password;
    }



    //We asume the JSON object sended are well formed.
    protected Company(JSONObject json) throws RestApiException{


        try {

            String buff;

            this.id = json.getInt("id");


            this.email = json.getString("email");

            buff = json.getString("name");

            if( !buff.equals("null")){
                this.name = buff;
            }



            buff = json.getString("logo");

            if ( !buff.equals("null") ) {

                this.logoUrl = buff;

            }

            buff = json.getString("mission");

            if( !buff.equals("null")){
                this.mission = buff;
            }



            buff = json.getString("number_of_workers");


            if( !buff.equals("null")){
                this.numberOfWorkers = json.getInt("number_of_workers");
            }


            buff = json.getString("clients");

            if( !buff.equals("null")){

                JSONArray jsonClients = json.getJSONArray("clients");
                this.clients=new String[jsonClients.length()];
                for(int i = 0; i<jsonClients.length(); i++)
                    this.clients[i] = jsonClients.getString(i);

            }

            buff = json.getString("location");

            if( !buff.equals("null")){
                this.location = buff;
            }



            buff = json.getString("description");

            if( !buff.equals("null")){
                this.description = buff;
            }

            buff = json.getString("competences");

            if( !buff.equals("null")){

                JSONArray jsonCompetences = json.getJSONArray("competences");
                this.competences=new String[jsonCompetences.length()];
                for(int i = 0; i<jsonCompetences.length(); i++)
                    this.competences[i] = jsonCompetences.getString(i);

            }


        }catch (Exception e){

            throw new  RestApiException(-1,"Internal error if Company parsing Json response to create object :\n" + e.getMessage());
        }
    }


    public Integer getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

   public String getLogoUrl(){
       return this.logoUrl;
   }

    public String getMission(){

      return this.mission;
    }

    public Integer getNumberOfWorkers(){

        return this.numberOfWorkers;

    }

    public String[] getClients(){

        return this.clients;

    }

    public String getLocation(){

        return this.location;
    }

    public String getDescription(){

        return this.description;
    }



    public Boolean isSetPassword() { return (this.password != null); }

    @Override
    public String toString() {
        return "Company{" + ",\n" +
                " id=" + this.id + ",\n" +
                " email='" + this.email + '\'' + ",\n" +
                " name='" + this.name + '\'' + ",\n" +
                " logoUrl='" + this.logoUrl + '\'' + ",\n" +
                " mission='" + this.mission + '\'' + ",\n" +
                " number_of_workers=" + this.numberOfWorkers + ",\n" +
                " clients=" + Arrays.toString(this.clients) + ",\n" +
                " location='" + this.location + '\'' + ",\n" +
                " description='" + this.description + '\'' + ",\n" +
                '}';
    }

    public Map<String,String> toFormParams(){
        Map<String,String> s = new HashMap<String,String>();
        if(this.email!=null){
            s.put("company[email]", this.email);
        }
        if(this.password!=null){
            s.put("company[password]", this.password);
        }
        if(this.name!=null){
            s.put("company[name]", this.name);
        }

        if(this.logoUrl!=null){
            s.put("company[logo]", this.logoUrl);
        }

        if(this.mission!=null){
            s.put("company[mission]", this.mission);
        }

        if( this.numberOfWorkers != null){
            s.put("company[number_of_workers]", this.numberOfWorkers.toString());
        }

        if(this.competences!=null && this.competences.length>0){
            String c=this.competences[0];
            for(int i=1;i<this.competences.length;i++){
                c+=","+this.competences[i];
            }

            s.put("company[competences]", c);

        }



        if(this.clients!=null && this.clients.length>0){
            String c=this.clients[0];
            for(int i=1;i<this.clients.length;i++){
                c+=","+this.clients[i];
            }

            s.put("company[clients]", c);
        }


        if(this.location!=null){
            s.put("company[location]", this.location);
        }

        if(this.description!=null){
            s.put("company[description]", this.description);
        }

        return s;
    }

    public String[] getCompetences() {
        return competences;
    }

    public void setCompetences(String[] competences) {
        this.competences = competences;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Company){
            Company c = (Company) o;
            return c.getId()==this.getId();
        }
        return false;
    }
}
