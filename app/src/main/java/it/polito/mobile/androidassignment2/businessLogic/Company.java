package it.polito.mobile.androidassignment2.businessLogic;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Created by Joseph on 04/05/2015.
 */
public class Company {

    private Integer id = null;
    private String email = null;
    private String name = null;
    private URL logoUrl = null;
    private String mission = null;
    private Integer numberOfWorkers = null;
    private String[] clients = null;
    private String location = null;
    private String description = null;
    private String password = null;





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

    public void setLogoUrl(URL logoUrl ){
        this.logoUrl = logoUrl;
    }


    public void setMission( String mission){
        this.mission = null;
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

    //When it is done store somewhere


    public void getOffer(Manager.ResultProcessor<List<Offer>> postProcessor) throws DataFormatException{
        this.getOffer(null,null,postProcessor);
    }


    public void getOffer(String kindOfContract,
                                  String descriptionOfWork,
                                  Manager.ResultProcessor<List<Offer>> postProcessor) throws DataFormatException{



        if( this.id == null ) throw new DataFormatException("Company, getBelongingOffer : Company id is not set !");

        Offer criteria = new Offer();

        criteria.setCompanyId(this.id);
        criteria.setKindOfContract(kindOfContract);
        criteria.setDescriptionOfWork(descriptionOfWork);

        (new Task.General(Task.Method.GET_OFFER_MATCHING_CRITERIA, postProcessor)).execute(criteria);

    }






    //We asume the JSON object sended are well formed.
    protected Company(JSONObject json){


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

                this.logoUrl = new URL(buff);

            }

            buff = json.getString("mission");

            if( !buff.equals("null")){
                this.mission = buff;
            }



            this.numberOfWorkers = json.getInt("number_of_workers");



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




        }catch (Exception e){

            Log.v("azert", "creating company from json : " + e.getMessage());
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

   public URL getLogoUrl(){
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
            s.put("company[logo]", this.logoUrl.toString());
        }

        if(this.mission!=null){
            s.put("company[mission]", this.mission);
        }

        if( this.numberOfWorkers != null){
            s.put("company[number_of_workers]", this.numberOfWorkers.toString());
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

}
