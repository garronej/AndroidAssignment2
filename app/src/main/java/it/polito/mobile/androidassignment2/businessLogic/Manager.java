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
     * @return A Student object representing the student if it exists.
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
     * @return a Student object representing the inserted object.
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
     * Update the information about a specific student in the database.
     *
     * @param studentToUpdate a Student object, id must be set by using the mauallySetId, .
     * @return the new student.
     * @throws Exception Network related exceptions can be thrown
     */
    public static Student updateStudent( Student studentToUpdate ) throws IOException, RestApiException {
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
    public static Integer deleteStudent( int id) throws IOException, RestApiException {
        return StudentManager.deleteStudent(id);
    }

    /*Asyncronical vertion */
    public static void deleteStudent(  int id, Manager.ResultProcessor<Integer> postProcessor ){
        (new Task.General(Task.Method.DELETE_STUDENT, postProcessor)).execute(id);
    }




    /*---------------------Companies ----------------------*/




    /**
     * The method allows to retrieve all the informations
     * about a company (with a given id).
     *
     * @param id the id of the company to be retrieved
     * @return A Company object representing the company if it exists, null otherwise.
     * @throws Exception Network related exception can be thrown or error code, ( error code -1 is internal error ).
     */
    public static Company getCompanyById(int id) throws RestApiException, IOException {
        return CompanyManager.getCompanyById(id);
    }

    /* Asycronical vertion */
    public static void getCompanyById(int i, Manager.ResultProcessor<Company> postProcessor){
        (new Task.General(Task.Method.GET_COMPANY_BY_ID, postProcessor)).execute(i);
    }



    /**
     *
     * Insert a new company in the database
     *
     * @param newCompany a Company object representing the student to be inserted.
     * @return a Company object representing the inserted object.
     * @throws Exception Network related exceptions can be thrown
     */
    public static Company insertNewCompany(Company newCompany) throws RestApiException, IOException {
        return CompanyManager.insertNewCompany(newCompany);
    }

    /*Asyncronical vertion */
    public static void insertNewCompany(Student newCompany,  Manager.ResultProcessor<Company> postProcessor){
        (new Task.General(Task.Method.INSERT_NEW_COMPANY, postProcessor)).execute(newCompany);
    }


    /**
     *
     * Get all companies matching search criteria in the database
     *
     * @param criteria a Company object representing the criteria to be matched in the search.
     * @return A list of Company.
     * @throws Exception Network related exceptions can be thrown
     */
    public static List<Company> getCompaniesMatchingCriteria( Company criteria ) throws RestApiException, IOException {
        return CompanyManager.getCompaniesMatchingCriteria(criteria);
    }

    /*Asyncronical vertion */
    public static void getCompaniesMatchingCriteria( Company criteria, Manager.ResultProcessor<List<Company>> postProcessor ){
        (new Task.General(Task.Method.GET_COMPANIES_MATCHING_CRITERIA, postProcessor)).execute(criteria);
    }


    /**
     *
     * Update the information about a specific company in the database.
     *
     * @param companyToUpdate a Company object, id must be set by using the mauallySetId, .
     * @return the new company.
     * @throws Exception Network related exceptions can be thrown
     */
    public static Company updateCompany( Company companyToUpdate ) throws IOException, RestApiException {
        return CompanyManager.updateCompany(companyToUpdate);
    }

    /*Asyncronical vertion */
    public static void updateCompany(  Company companyToUpdate, Manager.ResultProcessor<Company> postProcessor ){
        (new Task.General(Task.Method.UPDATE_COMPANY, postProcessor)).execute(companyToUpdate);
    }


    /**
     *
     * Delete a specific company
     *
     * @param id the id of the company to delete.
     * @return 0 if success.
     * @throws Exception Network related exceptions can be thrown
     */
    public static Integer deleteCompany( int id) throws IOException, RestApiException {
        return CompanyManager.deleteCompany(id);
    }

    /*Asyncronical vertion */
    public static void deleteCompany(  int id, Manager.ResultProcessor<Integer> postProcessor ){
        (new Task.General(Task.Method.DELETE_COMPANY, postProcessor)).execute(id);
    }


    /* --------------------------- Offer -----------------------*/


    public static Offer getOfferById(int id) throws RestApiException, IOException {
        return OfferManager.getOfferById(id);
    }

    public static void getOfferById( int id, ResultProcessor<Offer> postProcessor){
        (new Task.General(Task.Method.GET_OFFER_BY_ID, postProcessor)).execute(id);
    }



    public static Offer insertNewOffer(Offer newOffer) throws RestApiException, IOException {

        return OfferManager.insertNewOffer(newOffer);

    }


    public static void insertNewOffer(Offer newOffer, ResultProcessor<Offer> postProcessor){

        (new Task.General(Task.Method.INSERT_NEW_OFFER, postProcessor)).execute(newOffer);

    }



    public static List<Offer> getOfferMatchingCriteria( Offer criteria ) throws RestApiException, IOException {
        return OfferManager.getOfferMatchingCriteria(criteria);
    }

    public static void getOfferMatchingCriteria( Offer criteria, ResultProcessor<List<Offer>> postProcessor){

        (new Task.General(Task.Method.GET_OFFER_MATCHING_CRITERIA, postProcessor)).execute(criteria);
    }


    public static Offer updateOffer( Offer offerToUpdate ) throws IOException, RestApiException {
        return OfferManager.updateOffer(offerToUpdate);
    }

    public static void updateOffer( Offer offerToUpdate, ResultProcessor<Offer> postProcessor){
        (new Task.General(Task.Method.UPDATE_OFFER,postProcessor)).execute(offerToUpdate);
    }



    protected static Integer deleteOffer( int id ) throws IOException, RestApiException {
        return OfferManager.deleteOffer(id);
    }

    protected static void deleteOffer( int id, ResultProcessor<Integer> postProcessor){
        (new Task.General(Task.Method.DELETE_OFFER,postProcessor)).execute(id);
    }




}