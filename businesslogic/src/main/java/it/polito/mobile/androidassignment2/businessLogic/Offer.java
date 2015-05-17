package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Created by Joseph on 04/05/2015.
 */
public class Offer {

    private Integer id = null;

    private String kindOfContract= null;
    private String descriptionOfWork = null;
    private Integer durationMonths = null;

    private String code = null;
    private String location = null;
    private String[] competences = null;



    private Company company = new Company();


    public Company getCompany(){
        return this.company;
    }


    public void setCompanyName(String companyName) throws DataFormatException{

        if( this.id != null || this.company.getId() != null )
            throw new DataFormatException("Offer : Error in setCompanyName, the field" +
                    " companyIf and id are already filled, consistency error.");

        this.company.setName(companyName);

    }




    public Offer(){
        super();
    }

    public void manuallySetId( int id ) throws DataFormatException{

        if( this.company.getName() != null )
            throw new DataFormatException("Offer : Error in manuallySetId, the field" +
                    " companyName is filled, consistency error ");

        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }



    public void setCompanyId( int companyId ) throws DataFormatException{

        if( this.company.getName() != null )
            throw new DataFormatException("Offer : Error in setCompanyId, the field" +
                    " companyName is filled already, consistency error ");

        this.company.manuallySetId(companyId);
    }

    public Integer getCompanyId(){
        return this.company.getId();
    }

    public String getCompanyName() { return this.company.getName();}

    public void setKindOfContract( String kindOfContract ){
        this.kindOfContract = Utils.toLowerCase(kindOfContract);
    }

    public String getKindOfContract(){
        return this.kindOfContract;
    }

    public void setDescriptionOfWork( String descriptionOfWork){
        this.descriptionOfWork = Utils.toLowerCase(descriptionOfWork);
    }

    public String getDescriptionOfWork(){
        return this.descriptionOfWork;
    }

    public void setDurationMonths( int durationMonth){
        this.durationMonths = durationMonth;
    }

    public Integer getDurationMonths(){
        return this.durationMonths;
    }


    //We asume the JSON object sended are well formed.
    protected Offer(JSONObject json){


        try {

            String buff;

            this.id = json.getInt("id");

            this.company = new Company(json.getJSONObject("company"));

            this.kindOfContract = json.getString("kind_of_contract");


            buff = json.getString("description_of_work");

            if( !buff.equals("null")) {
                this.descriptionOfWork = json.getString("description_of_work");
            }

            buff = json.getString("duration_months");

            if( !buff.equals("null")) {
                this.durationMonths = json.getInt("duration_months");
            }
            buff = json.getString("code");

            if( !buff.equals("null")){
                this.code = buff;
            }
            buff = json.getString("location");

            if( !buff.equals("null")){
                this.location = buff;
            }

            buff = json.getString("competences");



            if( !buff.equals("null")){

                JSONArray jsonComps = json.getJSONArray("competences");
                competences=new String[jsonComps.length()];
                for(int i = 0; i<jsonComps.length(); i++){
                    competences[i] = jsonComps.getString(i);
                }

            }
        }catch (Exception e){
            //Should nor append
        }
    }

    @Override
    public String toString() {
        return "Offer{" + '\n' +
                " id=" + this.id + ",\n" +
                " companyId='" + this.company.getId() + '\'' + ",\n" +
                " kindOfContract='" + this.kindOfContract + '\'' + ",\n" +
                " descriptionOfWork='" + this.descriptionOfWork + '\'' + ",\n" +
                " durationMonth='" + this.durationMonths + '\'' + "\n" +
                '}';
    }



    public Map<String,String> toFormParams(){

        Map<String,String> s = new HashMap<String,String>();
        if(this.id!=null){
            s.put("offer[id]", this.id.toString());
        }
        if(this.company.getId()!=null){
            s.put("offer[company_id]", this.company.getId().toString());
        }
        if(this.kindOfContract!=null){
            s.put("offer[kind_of_contract]", this.kindOfContract);
        }
        if(this.descriptionOfWork!=null){
            s.put("offer[description_of_work]", this.descriptionOfWork);
        }
        if(this.durationMonths!=null){
            s.put("offer[duration_months]", this.durationMonths.toString());
        }

        if(this.company.getName()!=null){
            s.put("offer[company]", this.company.getName());
        }
        if(this.code!=null){
            s.put("offer[code]", this.code);
        }
        if(this.location!=null){
            s.put("offer[location]", this.location);
        }
        if(competences!=null && competences.length>0){
            String c=competences[0];
            for(int i=1;i<competences.length;i++){
                c+=","+competences[i];
            }

            s.put("offer[competences]", c);
        } else if (competences != null && competences.length == 0) {
            s.put("offer[competences]", "");
        }

        return s;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getCompetences() {
        return competences;
    }

    public void setCompetences(String[] competences) {
        this.competences = competences;
    }
}
