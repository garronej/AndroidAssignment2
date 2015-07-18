package it.polito.mobile.androidassignment2.businessLogic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark9 on 07/06/15.
 */
public class Notice {

    private int id;
    private int studentId;
    private String title;
    private String description;
    private double price;
    private double latitude;
    private double longitude;
    private String address;
    private String telephone;
    private String[] tags;
    private String creationDate;
    private int countInappropriate;
    private String[] pictures;
    private String email;

    public Notice() {
    }
    public Notice(JSONObject json) {
        String buff="";
        try {
            buff = json.getString("title");


            buff = json.getString("id");

            if( !buff.equals("null")){
                this.id = json.getInt("id");
            }

            buff = json.getString("student_id");

            if( !buff.equals("null")){
                this.studentId = json.getInt("student_id");
            }
            buff = json.getString("title");

            if( !buff.equals("null")){
                this.title = buff;
            }

            buff = json.getString("description");

            if( !buff.equals("null")){
                this.description = buff;
            }

            buff = json.getString("price");

            if( !buff.equals("null")){
                this.price = json.getDouble("price");
            }


            buff = json.getString("latitude");


            if( !buff.equals("null")){
                this.latitude = json.getDouble("latitude");
            }
            buff = json.getString("longitude");

            if( !buff.equals("null")){
                this.longitude = json.getDouble("longitude");
            }

            buff = json.getString("full_location");

            if( !buff.equals("null")){
                this.address = buff;
            }

            buff = json.getString("telephone_number");

            if( !buff.equals("null")){
                this.telephone = buff;
            }
            buff = json.getString("created_at");

            if( !buff.equals("null")){
                this.creationDate = buff;
            }

            buff = json.getString("count_of_inappropriate");

            if( !buff.equals("null")){
                this.countInappropriate = json.getInt("count_of_inappropriate");
            }

            buff = json.getString("tags");

            if( !buff.equals("null") ){

                JSONArray jsonLinks = json.getJSONArray("tags");

                tags = new String[jsonLinks.length()];
                for (int i = 0; i < jsonLinks.length(); i++) {

                    tags[i] =jsonLinks.getString(i);

                }

            }
            buff = json.getString("pictures");

            if( !buff.equals("null") ){

                JSONArray jsonLinks = json.getJSONArray("pictures");

                pictures = new String[jsonLinks.length()];
                for (int i = 0; i < jsonLinks.length(); i++) {

                    pictures[i] =jsonLinks.getString(i);

                }

            }

            buff = json.getString("email");

            if( !buff.equals("null") ){

                this.email = json.getString("email");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Notice not well formed");
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }


    public int getCountInappropriate() {
        return countInappropriate;
    }

    public void setCountInappropriate(int countInappropriate) {
        this.countInappropriate = countInappropriate;
    }

    public String[] getPictures() {
        return pictures;
    }

    public void setPictures(String[] pictures) {
        this.pictures = pictures;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", creationDate='" + creationDate + '\'' +
                ", countInappropriate=" + countInappropriate +
                ", pictures=" + Arrays.toString(pictures) +
                '}';
    }

    public Map<String, String> toFormParams() {
        Map<String,String> s = new HashMap<String,String>();

        s.put("notice[student_id]", ""+studentId);
        if(title!=null){
            s.put("notice[title]", title);
        }
        if(description!=null){
            s.put("notice[description]", description);
        }
        //if(price!=null){
            s.put("notice[price]", ""+price);
        //}
        //if(longitude!=null){
            s.put("notice[longitude]", ""+longitude);
        //}
        //if(latitude!=null){
            s.put("notice[latitude]", ""+latitude);
        //}
        if(address!=null){
            s.put("notice[full_location]", address);
        }
        if(telephone!=null){
            s.put("notice[telephone_number]", telephone);
        }

        if(tags!=null && tags.length>0){
            String c=tags[0];
            for(int i=1;i<tags.length;i++){
                c+=","+tags[i];
            }

            s.put("notice[tags]", c);
        } else if (tags != null && tags.length == 0) {
            s.put("notice[tags]", "");
        }
        if(pictures!=null && pictures.length>0){
            String c=pictures[0];
            for(int i=1;i<pictures.length;i++){
                c+=","+pictures[i];
            }

            s.put("notice[pictures]", c);
        } else if (pictures != null && pictures.length == 0) {
            s.put("notice[pictures]", "");
        }
        return s;
    }


    public String getEmail() {
        return email;
    }
}
