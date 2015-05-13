package it.polito.mobile.androidassignment2.businessLogic;

import java.io.IOException;
import java.util.List;


/**
 * Created by Joseph on 30/04/2015.
 */
public class Manager {



    //Interface to implement the post treatment of the method.
    public interface  ResultProcessor<T> {
        public void process(T arg, Exception e);

        public void cancel();
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
    public static Task.General getStudentById(int i, ResultProcessor<Student> postProcessor){

        Task.General t = new Task.General(Task.Method.GET_STUDENT_BY_ID, postProcessor);
        t.execute(i);
        return t;

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
    public static Task.General insertNewStudent(Student newStudent,  ResultProcessor<Student> postProcessor){
        Task.General t = new Task.General(Task.Method.INSERT_NEW_STUDENT, postProcessor);
        t.execute(newStudent);
        return t;
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

    /*Asyncronical version */
    public static Task.General getStudentsMatchingCriteria( Student criteria, ResultProcessor<List<Student>> postProcessor ){
        Task.General t = new Task.General(Task.Method.GET_STUDENTS_MATCHING_CRITERIA, postProcessor);
        t.execute(criteria);
        return t;
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
    public static Task.General updateStudent(  Student studentToUpdate, ResultProcessor<Student> postProcessor ){
        Task.General t = new Task.General(Task.Method.UPDATE_STUDENT, postProcessor);
        t.execute(studentToUpdate);
        return t;
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
    public static Task.General deleteStudent(  int id, ResultProcessor<Integer> postProcessor ){
        Task.General t = new Task.General(Task.Method.DELETE_STUDENT, postProcessor);
        t.execute(id);
        return t;
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
    public static Task.General getCompanyById(int i, ResultProcessor<Company> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_COMPANY_BY_ID, postProcessor);
        t.execute(i);
        return t;
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
    public static Task.General insertNewCompany(Company newCompany,  ResultProcessor<Company> postProcessor){
        Task.General t = new Task.General(Task.Method.INSERT_NEW_COMPANY, postProcessor);
        t.execute(newCompany);
        return t;
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
    public static Task.General getCompaniesMatchingCriteria( Company criteria, ResultProcessor<List<Company>> postProcessor ){
        Task.General t = new Task.General(Task.Method.GET_COMPANIES_MATCHING_CRITERIA, postProcessor);
        t.execute(criteria);
        return t;
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
    public static Task.General updateCompany(  Company companyToUpdate, ResultProcessor<Company> postProcessor ){
        Task.General t = new Task.General(Task.Method.UPDATE_COMPANY, postProcessor);
        t.execute(companyToUpdate);
        return t;
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
    public static Task.General deleteCompany(  int id, ResultProcessor<Integer> postProcessor ){
        Task.General t = new Task.General(Task.Method.DELETE_COMPANY, postProcessor);
        t.execute(id);
        return t;
    }


    /* --------------------------- Offer -----------------------*/


    public static Offer getOfferById(int id) throws RestApiException, IOException {
        return OfferManager.getOfferById(id);
    }

    public static Task.General getOfferById( int id, ResultProcessor<Offer> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_OFFER_BY_ID, postProcessor);
        t.execute(id);
        return t;
    }



    public static Offer insertNewOffer(Offer newOffer) throws RestApiException, IOException {

        return OfferManager.insertNewOffer(newOffer);

    }


    public static Task.General insertNewOffer(Offer newOffer, ResultProcessor<Offer> postProcessor){
        Task.General t = new Task.General(Task.Method.INSERT_NEW_OFFER, postProcessor);
        t.execute(newOffer);
        return t;
    }



    public static List<Offer> getOffersMatchingCriteria( Offer criteria ) throws RestApiException, IOException {
        return OfferManager.getOfferMatchingCriteria(criteria);
    }

    public static Task.General getOffersMatchingCriteria( Offer criteria, ResultProcessor<List<Offer>> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_OFFER_MATCHING_CRITERIA, postProcessor);
        t.execute(criteria);
        return t;
    }


    public static Offer updateOffer( Offer offerToUpdate ) throws IOException, RestApiException {
        return OfferManager.updateOffer(offerToUpdate);
    }

    public static Task.General updateOffer( Offer offerToUpdate, ResultProcessor<Offer> postProcessor){
        Task.General t=new Task.General(Task.Method.UPDATE_OFFER,postProcessor);
        t.execute(offerToUpdate);
        return t;
    }



    public static Integer deleteOffer( int id ) throws IOException, RestApiException {
        return OfferManager.deleteOffer(id);
    }

    public static Task.General deleteOffer( int id, ResultProcessor<Integer> postProcessor){
        Task.General t = new Task.General(Task.Method.DELETE_OFFER,postProcessor);
        t.execute(id);
        return t;
    }


    public static List<Company> getFavouriteCompanyOfStudent(int studentId) throws IOException, RestApiException{
        return StudentManager.getFavouriteCompanyOfStudent(studentId);
    }

    public static Task.General getFavouriteCompanyOfStudent(int studentId, ResultProcessor<List<Company>> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_FAVOURITE_COMPANY_OF_STUDENT,postProcessor);
        t.execute(studentId);
        return t;
    }



    public static Company addFavouriteCompanyForStudent(int studentId, int companyId) throws IOException, RestApiException{
        return StudentManager.addFavouriteCompanyForStudent(studentId, companyId);
    }

    public static Task.General addFavouriteCompanyForStudent(int studentId, int companyId, ResultProcessor<Company> postProcessor){
        Task.General t = new Task.General(Task.Method.ADD_FAVOURITE_COMPANY_FOR_STUDENT,postProcessor);
        t.execute(studentId, companyId);
        return t;
    }


    public static Integer deleteAFavouriteCompanyOfAStudent( int studentId, int companyId) throws IOException, RestApiException{
        return StudentManager.deleteAFavouriteCompanyOfAStudent(studentId,companyId);
    }


    public static Task.General deleteAFavouriteCompanyOfAStudent( int studentId, int companyId, ResultProcessor<Integer> postProcessor){
        Task.General t =new Task.General(Task.Method.DELETE_A_FAVOURITE_COMPANY_OF_A_STUDENT,postProcessor);
        t.execute(studentId, companyId);
        return t;
    }

    public static List<Offer> getFavouriteOfferOfStudent(int studentId) throws IOException, RestApiException{
        return StudentManager.getFavouriteOfferOfStudent(studentId);
    }

    public static Task.General getFavouriteOfferOfStudent(int studentId, ResultProcessor<List<Offer>> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_FAVOURITE_OFFER_OF_STUDENT,postProcessor);
        t.execute(studentId);
        return t;
    }

    public static Offer addFavouriteOfferForStudent(int studentId, int offerId) throws IOException, RestApiException{
        return StudentManager.addFavouriteOfferForStudent(studentId,offerId);
    }

    public static Task.General addFavouriteOfferForStudent(int studentId, int offerId, ResultProcessor<Offer> postProcessor){
        Task.General t = new Task.General(Task.Method.ADD_FAVOURITE_OFFER_FOR_STUDENT,postProcessor);
        t.execute(studentId, offerId);
        return t;
    }


    public static Integer deleteAFavouriteOfferOfAStudent( int studentId, int offerId) throws IOException, RestApiException{
        return StudentManager.deleteAFavouriteOfferOfAStudent(studentId,offerId);
    }

    public static Task.General deleteAFavouriteOfferOfAStudent( int studentId, int offerId, ResultProcessor<Integer> postProcessor){
        Task.General t = new Task.General(Task.Method.DELETE_A_FAVOURITE_OFFER_OF_A_STUDENT,postProcessor);
        t.execute(studentId, offerId);
        return t;

    }


    public static List<Student> getFavouriteStudentOfCompany(int companyId) throws IOException, RestApiException{
        return CompanyManager.getFavouriteStudentOfCompany(companyId);
    }


    public static Task.General getFavouriteStudentOfCompany(int companyId, ResultProcessor<List<Student>> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_FAVOURITE_STUDENT_OF_COMPANY,postProcessor);
        t.execute(companyId);
        return t;
    }



    public static Student addFavouriteStudentForCompany(int companyId, int studentId) throws IOException, RestApiException{
        return CompanyManager.addFavouriteStudentForCompany(companyId,studentId);
    }

    public static Task.General addFavouriteStudentForCompany(int companyId, int studentId, ResultProcessor<Student> postProcessor ){
        Task.General t = new Task.General(Task.Method.ADD_FAVOURITE_STUDENT_FOR_COMPANY,postProcessor);
        t.execute(companyId, studentId);
        return t;
    }


    public static Integer deleteAFavouriteStudentOfACompany( int companyId, int studentId) throws IOException, RestApiException{
        return CompanyManager.deleteAFavouriteStudentOfACompany(companyId, studentId);
    }

    public static Task.General deleteAFavouriteStudentOfACompany( int companyId, int studentId, ResultProcessor<Integer> postProcessor){
        Task.General t= new Task.General(Task.Method.DELETE_A_FAVOURITE_STUDENT_OF_A_COMPANY,postProcessor);
        t.execute(companyId, studentId);
        return t;
    }

    public static Task.General getStudentsForJobOffer( int jobOffer, Student student, ResultProcessor<Integer> postProcessor){
        Task.General t= new Task.General(Task.Method.GET_STUDENTS_FOR_JOB_OFFER,postProcessor);
        t.execute(jobOffer, student);
        return t;
    }

    public static Task.General subscribeStudentForJobOffer( int jobOffer, Student student, ResultProcessor<Integer> postProcessor){
        Task.General t= new Task.General(Task.Method.SUBSCRIBE_STUDENTS_FOR_JOB_OFFER,postProcessor);
        t.execute(jobOffer, student.getId());
        return t;
    }
    public static Task.General unsubscribeStudentForJobOffer( int jobOffer, Student student, ResultProcessor<Integer> postProcessor){
        Task.General t= new Task.General(Task.Method.UNSUBSCRIBE_STUDENTS_FOR_JOB_OFFER,postProcessor);
        t.execute(jobOffer, student.getId());
        return t;
    }


}