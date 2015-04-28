package it.polito.mobile.androidassignment2.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mark9 on 28/04/15.
 */
public class RESTManager {

    public static class Response{

        private int respCode;
        private String content;

        public Response(int respCode, String content) {
            this.respCode = respCode;
            this.content = content;
        }

        public int getRespCode() {
            return respCode;
        }

        public String getContent() {
            return content;
        }
    }

    private static String BASE_URI = "https://poli-jobs.herokuapp.com/api/v1/";

    public static String GET = "GET";
    public static String POST = "POST";


    public static Response send(String method, String relURI, String urlParameters) throws Exception {
        Log.d("poliJob", "Send method invoked");
        String url = BASE_URI+relURI;
        if(method.equals("GET") && !urlParameters.isEmpty()){
            url+="?"+urlParameters;
        }

        Log.d("poliJob", "Sending request to  "+url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);

        if(!method.equals("GET")){
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        }

        //con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        Log.d("poliJob", "Response of HTTP request is "+responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new Response(responseCode, response.toString());

    }

}
