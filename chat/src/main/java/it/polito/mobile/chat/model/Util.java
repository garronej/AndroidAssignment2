package it.polito.mobile.chat.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by mark9 on 09/07/15.
 */
public class Util {

    public static Date convertStringToDate(String date){
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            return f.parse(date);
        }catch (ParseException e){
            e.printStackTrace();
            throw new RuntimeException("Wrong date format");
        }

    }
    public static Date getTodayMidnight(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();
    }

    public static boolean isToday(Date date){
        return date.after(Util.getTodayMidnight());
    }
}
