package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joseph on 04/05/2015.
 */
public class Offer {

    private Integer id = null;
    private Integer companyId =null;
    private String kindOfContract= null;
    private String descriptionOfWork = null;
    private Integer durationMonths = null;

    public Offer(){
        super();
    }

    public void manuallySetId( int id ){
        this.id = id;
    }

    public Integer getId(){
        return this.id;
    }



    public void setCompanyId( int companyId ){
        this.companyId = companyId;
    }

    public Integer getCompanyId(){
        return this.companyId;
    }

    public void setKindOfContract( String kindOfContract ){
        this.kindOfContract = Utils.toLowerCase(kindOfContract);
    }

    public String getKindOfContract(){
        return this.kindOfContract;
    }

    public void setDescriptionOfWork( String descriptionOfWork){
        this.descriptionOfWork = descriptionOfWork;
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

            this.companyId = json.getInt("company_id");

            this.kindOfContract = json.getString("kind_of_contract");

            this.descriptionOfWork = json.getString("description_of_work");

            this.durationMonths = json.getInt("duration_months");


        }catch (Exception e){

            //Should nor append
        }
    }

    @Override
    public String toString() {
        return "Student{" + '\n' +
                "id=" + this.id + '\n' +
                ", companyId='" + this.companyId + '\'' + '\n' +
                ", kindOfContract='" + this.kindOfContract + '\'' + '\n' +
                ", durationMonth='" + this.durationMonths + '\'' + '\n' +
                '}';
    }



    public Map<String,String> toFormParams(){

        Map<String,String> s = new HashMap<String,String>();
        if(this.id!=null){
            s.put("offer[id]", this.id.toString());
        }
        if(this.companyId!=null){
            s.put("offer[company_id]", this.companyId.toString());
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

        return s;
    }





}
