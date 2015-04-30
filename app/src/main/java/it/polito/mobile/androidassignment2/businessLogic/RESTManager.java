package it.polito.mobile.androidassignment2.businessLogic;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by mark9 on 28/04/15.
 */
class RESTManager {


    private static String BASE_URI = "https://poli-jobs.herokuapp.com/api/v1/";

    public static String GET = "GET";
    public static String POST = "POST";


    public static String send(String method, String relURI, Map<String, String> urlParameters) throws RestApiException, IOException {
        Log.d("poliJob", "Send method invoked with method "+method);
        String url = BASE_URI+relURI;
        String queryString = "";
        if(!urlParameters.isEmpty()) {

            for (String key : urlParameters.keySet()) {


                try {
                    queryString += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(urlParameters.get(key), "UTF-8") + "&";
                }catch(UnsupportedEncodingException e){
                    throw new RestApiException("Internal Error : queryString += URLEncoder.encode(key, \"UTF-8\") + \"=\" + URLEncoder.encode(urlParameters.get(key), \"UTF-8\") + \"&\";" + e.getMessage());
                }


            }
            queryString = queryString.substring(0, queryString.length() - 1);
        }


        if(method.equals(RESTManager.GET) && !queryString.isEmpty()){
            url+="?"+queryString;
        }

        Log.d("poliJob", "Sending request to  "+url);

        URL obj = null;
        try {
            obj = new URL(url);
        }catch(MalformedURLException e){
            throw new RestApiException("Internal Error RESTManager : obj = new URL(url);");
        }

        //Throw IO Exception
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();


        //Throw, should be catched

        try {
            con.setRequestMethod(method);
        } catch (ProtocolException e) {
            throw new RestApiException("Internal Error RESTManager : con.setRequestMethod(method);" + e.getMessage());
        }


        if(!method.equals(RESTManager.GET) && !queryString.isEmpty()){
            Log.d("poliJob", "The request has this as params : "+queryString);

            //catch it
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //catch it
            con.setDoOutput(true);



            try {
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(queryString);
                wr.flush();
                wr.close();
            } catch (IOException e) {

                throw new RestApiException("Internal Error RESTManager : DataOutputStream wr = new DataOutputStream(con.getOutputStream());" + e.getMessage());
            }

        }

        //con.setRequestProperty("User-Agent", USER_AGENT);


        //Send IO exception
        int responseCode = con.getResponseCode();
        Log.d("poliJob", "Response of HTTP request is "+responseCode);
        String content="";


        if(responseCode == 200) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();

        }else{

            throw new RestApiException(responseCode);

        }


    }

}
