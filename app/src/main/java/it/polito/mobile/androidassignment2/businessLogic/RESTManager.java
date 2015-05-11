package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by mark9 on 28/04/15.
 */
class RESTManager {


    private static final String BASE_URI = "https://poli-jobs.herokuapp.com/api/v1/";

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";




    public static String send(String method, String relURI, Map<String, String> urlParameters) throws RestApiException, IOException{


        String url = BASE_URI + relURI;
        String queryString = "";
        String arg;


        if (urlParameters != null && !urlParameters.isEmpty()) {

            for (String key : urlParameters.keySet()) {


                try {

                    if ( method.equals("GET")){

                        arg = key.substring(key.indexOf("[")+1,key.indexOf("]"));

                    }else{
                        arg = URLEncoder.encode(key, "UTF-8");
                    }



                    queryString += arg  + "=" + URLEncoder.encode(urlParameters.get(key), "UTF-8") + "&";
                } catch (UnsupportedEncodingException e) {
                    throw new RestApiException(-1, "Internal Error RESTManager" + e.getMessage());
                }


            }
            queryString = queryString.substring(0, queryString.length() - 1);
        }



        Log.v("REST","method = " + method + ", url = " + url + ",queryString = " + queryString);




        if (method.equals(RESTManager.GET) && !queryString.isEmpty()) {
            url += "?" + queryString;
        }




        URL obj = null;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            throw new RestApiException(-1, "Internal Error RESTManager" + e.getMessage());
        }



        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);


        if ( !method.equals(RESTManager.GET) && !queryString.isEmpty()) {



            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


            con.setDoOutput(true);


            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

                wr.writeBytes(queryString);
                wr.flush();
                wr.close();

        }

        //con.setRequestProperty("User-Agent", USER_AGENT);


        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {


            BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));


            return RESTManager.bufferedReaderToString(in);

        } else {

            throw new RestApiException(con.getResponseCode(), con.getResponseMessage());

        }


    }

    private static String bufferedReaderToString(BufferedReader in) throws IOException{

            if( in == null ) return null;

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();

    }

}
