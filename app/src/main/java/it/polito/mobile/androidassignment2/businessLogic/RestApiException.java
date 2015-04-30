package it.polito.mobile.androidassignment2.businessLogic;

/**
 * Created by Joseph on 30/04/2015.
 */
public class RestApiException extends Exception{

    private int respCode;


    public RestApiException(int respCode) {
            super("REST API Error Code");
            this.respCode = respCode;
        }

    public RestApiException(String message) {
        super(message);
        this.respCode = -1;
    }

    public int getRespCode(){

        return this.respCode;

    }



}
