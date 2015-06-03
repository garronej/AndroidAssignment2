package it.polito.mobile.androidassignment2.businessLogic.timetable;

import java.util.List;

/**
 * Created by Joseph on 01/06/2015.
 */
public interface Event {

    public abstract List<PracticalInformation> getPracticalInformation();
    public abstract String getTeacher();

}
