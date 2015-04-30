package it.polito.mobile.androidassignment2.businessLogic;

import java.io.IOException;


/**
 * Created by Joseph on 30/04/2015.
 */
public class Manager {



    //Interface to implement the post treatment of the method.
    public interface ResultProcessor<T> {
        public void process(T arg, Exception e);
    }


    /**
     * The method allows to retrieve all the informations
     * about a student (with a given id).
     *
     * @param id the id of the student to be retrieved
     * @return A Student object representing the student if it exists, null otherwise.
     * @throws Exception Network related exception can be thrown or error code, ( error code -1 is internal error ).
     */
    public static Student getStudentById(Integer id) throws RestApiException, IOException {
        return StudentManager.getStudentById(id);
    }

    /* Asycronical vertion */
    public static void getStudentById(Integer i, Manager.ResultProcessor<Student> postProcessor){
        (new Task.General(Task.GET_STUDENT_BY_ID, postProcessor)).execute(i);
    }

    /**
     *
     * @param newStudent a Student object representing the student to be inserted.
     * @return a Student object representing the inserted object or null if the operation failed.
     * @throws Exception Network related exceptions can be thrown
     */
    public static Student insertNewStudent(Student newStudent) throws RestApiException, IOException {
        return StudentManager.insertNewStudent(newStudent);
    }

    /*Asyncronical vertion */
    public static void insertNewStudent(Student newStudent,  Manager.ResultProcessor<Student> postProcessor){
        (new Task.General(Task.INSERT_NEW_STUDENT, postProcessor)).execute(newStudent);
    }


}
