package it.polito.mobile.androidassignment2.businessLogic;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * Created by Joseph on 04/05/2015.
 */
public class Utils {

    //Check if it's an email and transform "joseph.garrone.GJ@gmail.COm" -> "joseph.garrone.gj@gmail.com"
    public static String formatEmail(String email)throws DataFormatException {

        if( email == null) return null;
        if( email == "" ) return null;//TODO: comparing references? rick
        if( email.equals("") ) return null;

        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        if(!mat.matches()) throw new DataFormatException("Email " + email + " is malformed");

        return toLowerCase(email);

    }


    //check for illegal character in a name and transform garRone into Garrone
    protected static String formatName(String name)throws DataFormatException {

        if( name == "" ) return "";//TODO: comparing references? rick
        if( name.equals("") ) return "";
        if( name == null ) return null;
        /*
        Pattern pattern = Pattern.compile("[A-Za-zéèêîùàò\\-]*");
        Matcher mat = pattern.matcher(name);

        if(!mat.matches()) throw new DataFormatException("Illegal character in : " + name);

        */
        StringBuilder s = new StringBuilder(toLowerCase(name));


        s.setCharAt(0, Character.toUpperCase(s.charAt(0)));

        return s.toString();

    }

    //To lower case
    protected static String toLowerCase( String in ){
        if( in == "" ) return "";//TODO: comparing references? rick
        if( in.equals("") ) return "";
        if( in == null ) return null;




        StringBuilder s = new StringBuilder(in);
        s.replace(0, s.length(), s.toString().toLowerCase());

        return s.toString();

    }


    //Check password having more than 4 char and no space.
    public static void checkPassword( String pwd) throws DataFormatException{

        if( pwd == null) return;//TODO: comparing references? rick
        if( pwd.equals("") ) return;

        Pattern pattern = Pattern.compile("^(?=\\S+$).{4,}$");
        Matcher mat = pattern.matcher(pwd);

        if(!mat.matches()) throw new DataFormatException("Week password : whitespace forbiden, and minumum 4 character");

    }


    public static String processException(Exception exception, String message){

        String out = message + "\n";


            //There where  problem during the request
            if (exception.getClass() == RestApiException.class) {

                //It was an error on the web service side.
                //Nb : err code -1 mean a internal bug, report if you experience.
                Integer errCode =  ((RestApiException)exception).getResponseCode();
                out += errCode.toString() + " / " + exception.getMessage();


            }else if(exception.getClass() == IOException.class){
                //It was an error with the internet conextion.
                out += "Network connexion problem :\n" + exception.getMessage();
            }else{
                out += "Unknown error";
            }


        return out;


    }

}
