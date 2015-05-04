package it.polito.mobile.androidassignment2.businessLogic;

import java.io.IOException;
import java.util.List;


/**
 * Created by Joseph on 30/04/2015.
 */
public class Manager {



    //Interface to implement the post treatment of the method.
    public interface ResultProcessor<T> {
        public void process(T arg, Exception e);
    }





    /* -------------- Students --------------------- */

    /**
     * The method allows to retrieve all the informations
     * about a student (with a given id).
     *
     * @param id the id of the student to be retrieved
     * @return A Student object representing the student if it exists, null otherwise.
     * @throws Exception Network related exception can be thrown or error code, ( error code -1 is internal error ).
     */
    public static Student getStudentById(int id) throws RestApiException, IOException {
        return StudentManager.getStudentById(id);
    }

    /* Asycronical vertion */
    public static void getStudentById(int i, Manager.ResultProcessor<Student> postProcessor){
        (new Task.General(Task.Method.GET_STUDENT_BY_ID, postProcessor)).execute(i);
    }






    /**
     *
     * Insert a new student in the database
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
        (new Task.General(Task.Method.INSERT_NEW_STUDENT, postProcessor)).execute(newStudent);
    }




    /**
     *
     * Get all student matching seatch criteria in the database
     *
     * @param criteria a Student object representing the criteria to be matched in the search.
     * @return A list of student.
     * @throws Exception Network related exceptions can be thrown
     */
    public static List<Student> getStudentsMatchingCriteria( Student criteria ) throws RestApiException, IOException {
        return StudentManager.getStudentsMatchingCriteria(criteria);
    }

    /*Asyncronical vertion */
    public static void getStudentsMatchingCriteria( Student criteria, Manager.ResultProcessor<List<Student>> postProcessor ){
        (new Task.General(Task.Method.GET_STUDENTS_MATCHING_CRITERIA, postProcessor)).execute(criteria);
    }


    /**
     *
     * Get all student matching seatch criteria in the database
     *
     * @param studentToUpdate a Student object, id must be set by using the mauallySetId, .
     * @return the new student.
     * @throws Exception Network related exceptions can be thrown
     */
    protected static Student updateStudent( Student studentToUpdate ) throws IOException, RestApiException {
        return StudentManager.updateStudent(studentToUpdate);
    }

    /*Asyncronical vertion */
    public static void updateStudent(  Student studentToUpdate, Manager.ResultProcessor<Student> postProcessor ){
        (new Task.General(Task.Method.UPDATE_STUDENT, postProcessor)).execute(studentToUpdate);
    }


    /**
     *
     * Delete a specific student
     *
     * @param id the id of the student to delete.
     * @return 0 if success.
     * @throws Exception Network related exceptions can be thrown
     */
    protected static Integer deleteStudent( int id) throws IOException, RestApiException {
        return StudentManager.deleteStudent(id);
    }

    /*Asyncronical vertion */
    public static void deleteStudent(  int id, Manager.ResultProcessor<Integer> postProcessor ){
        (new Task.General(Task.Method.DELETE_STUDENT, postProcessor)).execute(id);
    }


}
