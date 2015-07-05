package it.polito.mobile.chat;

/**
 * Created by Joseph on 30/04/2015.
 */


public class RestApiException extends Exception{

    private int responseCode;


    protected RestApiException(int responseCode, String responseMessage){

        super(responseMessage);

        this.responseCode = responseCode;

    }

    public int getResponseCode(){
        return this.responseCode;
    }






}
