package it.polito.mobile.androidassignment2.model;

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

    protected static class Response{

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


    protected static Response send(String method, String relURI, Map<String, String> urlParameters) throws Exception {
        Log.d("poliJob", "Send method invoked with method "+method);
        String url = BASE_URI+relURI;
        String queryString = "";
        if(!urlParameters.isEmpty()) {

            for (String key : urlParameters.keySet()) {
                queryString += URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(urlParameters.get(key), "UTF-8") + "&";
            }
            queryString = queryString.substring(0, queryString.length() - 1);
        }
        if(method.equals(RESTManager.GET) && !queryString.isEmpty()){
            url+="?"+queryString;
        }

        Log.d("poliJob", "Sending request to  "+url);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(method);

        if(!method.equals(RESTManager.GET) && !queryString.isEmpty()){
            Log.d("poliJob", "The request has this as params : "+queryString);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(queryString);
            wr.flush();
            wr.close();
        }

        //con.setRequestProperty("User-Agent", USER_AGENT);

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
            content=response.toString();
        }
        return new Response(responseCode, content);

    }

}
