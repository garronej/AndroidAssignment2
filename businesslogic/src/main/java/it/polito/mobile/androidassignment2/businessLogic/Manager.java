package it.polito.mobile.androidassignment2.businessLogic;

import java.io.IOException;
import java.util.List;


/**
 * Created by Joseph on 30/04/2015.
 */
public class Manager {



    /*Interface to implement the post treatment of the asynchronous version of the Manager method.*/
    public interface  ResultProcessor<T> {
        void process(T arg, Exception e);
        void cancel();
    }




     /* --------------------- Company -----------------------*/



    /**
     *
     * Get Company object from it's id.
     *
     * @param id the id of the company.
     * @return Company retrieved.
     * @throws RestApiException when known, unpredictable error occur server side ( e.g. Trying to retreve unexsisting Offer).
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Company getCompanyById(int id) throws RestApiException, IOException {
        return CompanyManager.getCompanyById(id);
    }

    /* Asynchronous version */
    public static Task.General getCompanyById(int i, ResultProcessor<Company> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_COMPANY_BY_ID, postProcessor);
        t.execute(i);
        return t;
    }


    /**
     *
     * Insert a new company in the database.
     *
     * @param newCompany, Company object that describe the offer to be inserted.
     *                  The mandatory field are :
     *                      -email ( e.g. call newCompany.setEmail("company1@gamil.com"))
     *                      -password ( e.g call newCompany.setPassword("company1pass") )
     *                  Optional field are :
     *                      -name
     *                      -logo
     *                      -mission
     *                      -number_of_worker
     *                      -clients
     *                      -location
     *                      -description
     *
     *                    Note : Do not set the company id ( with newCompany.manuallySetId(...) ),
     *                    the id is going to be automaticaly generated server side.

     *
     * @return the Company object created witch correspond to the object passed in input but with
     * the id field set ( e.g outOffer.getId() != null ).
     *
     * @throws RestApiException when known, unpredictable error occur server side.
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Company insertNewCompany(Company newCompany) throws RestApiException, IOException {
        return CompanyManager.insertNewCompany(newCompany);
    }

    /* Asynchronous version */
    public static Task.General insertNewCompany(Company newCompany,  ResultProcessor<Company> postProcessor){
        Task.General t = new Task.General(Task.Method.INSERT_NEW_COMPANY, postProcessor);
        t.execute(newCompany);
        return t;
    }


    /**
     *
     * Search for company matching criteria.
     *
     * @param criteria, company object that describe the search criteria to be matched.
     *
     *                  It can be null if you want to retrieve all offer of the database.
     *
     *                  Possible search criteria are :
     *                      -name
     *                      -location
     *                      -description
     *                      -mission
     *
     *                  Note : id of the newOffer object must not be set, if you want to retreve a
     *                  specific company use getCompanyById ( e.g. do not call newOffer.manuallySetId(666); )
     *
     * @return List of offer matching ALL criteria specified.
     *
     * @throws RestApiException when known, unpredictable error occur server side.
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     *
     */
    public static List<Company> getCompaniesMatchingCriteria( Company criteria ) throws RestApiException, IOException {
        return CompanyManager.getCompaniesMatchingCriteria(criteria);
    }

    /* Asynchronous version */
    public static Task.General getCompaniesMatchingCriteria( Company criteria, ResultProcessor<List<Company>> postProcessor ){
        Task.General t = new Task.General(Task.Method.GET_COMPANIES_MATCHING_CRITERIA, postProcessor);
        t.execute(criteria);
        return t;
    }


    /**
     *
     * Update an company of the database.
     *
     * @param companyToUpdate, company object witch specify what to update.
     *
     *                  It can't be null.
     *                  You must manually set the id of the companyToUpdate object to match the
     *                  id value of the company you want to update in the database ( e.g. call
     *                  companyToUpdate.manuallySetId(34) )
     *
     *                  Possible modifiable field :
     *                      -name
     *                      -location
     *                      -description
     *                      -mission
     *
     * @return The company after modification.
     *
     * @throws RestApiException when known, unpredictable error occur server side.
     * if error code ( e.getResponseCode() == -1 ) an unknown error occurred => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Company updateCompany( Company companyToUpdate ) throws IOException, RestApiException {
        return CompanyManager.updateCompany(companyToUpdate);
    }

    /* Asynchronous version */
    public static Task.General updateCompany(  Company companyToUpdate, ResultProcessor<Company> postProcessor ){
        Task.General t = new Task.General(Task.Method.UPDATE_COMPANY, postProcessor);
        t.execute(companyToUpdate);
        return t;
    }


    /**
     *
     * Delete a company with specific id.
     *
     * @param id the id of the company to delete.
     * @return 0 if success.
     * @throws RestApiException when known, unpredictable error occur server side ( e.g. Trying to retreve unexsisting Offer).
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Integer deleteCompany( int id) throws IOException, RestApiException {
        return CompanyManager.deleteCompany(id);
    }

    /* Asynchronous version */
    public static Task.General deleteCompany(  int id, ResultProcessor<Integer> postProcessor ){
        Task.General t = new Task.General(Task.Method.DELETE_COMPANY, postProcessor);
        t.execute(id);
        return t;
    }




    /* --------------------- Offer -----------------------*/
    /**
     *
     * Get offer object from it's id.
     *
     * @param id the id of the offer.
     * @return Offer retrieved.
     * @throws RestApiException when known, unpredictable error occur server side ( e.g. Trying to retreve unexsisting Offer).
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Offer getOfferById(int id) throws RestApiException, IOException {
        return OfferManager.getOfferById(id);
    }

    public static Task.General getOfferById( int id, ResultProcessor<Offer> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_OFFER_BY_ID, postProcessor);
        t.execute(id);
        return t;
    }


    /**
     *
     * Insert a new offer in the database.
     *
     * @param newOffer, Offer object that describe the offer to be inserted.
     *                  The mandatory field are :
     *                      -company_id ( e.g. call newOffer.setCompanyId(23)) must refer to an existing company's id.
     *                      -kind_of_contract ( e.g call newOffer.setKindOfContract("internship") )
     *                      -description_of_work ( e.g call newOffer.setDescriptionOfWork("Do some programing work") )
     *                      -duration_months ( e.g. call newOffer.setDuratiionMonths(4) )
     *
     *                  Note : id of the newOffer object must not be set, it will be automaticaly generated
     *                  server side ( e.g. do not call newOffer.manuallySetId(666); )
     *
     * @return the offer object created witch corespond to the object passed in input but with the id set
     *          ( e.g outOffer.getId() != null ).
     *
     * @throws RestApiException when known, unpredictible error occur server side.
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOEcxception when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Offer insertNewOffer(Offer newOffer) throws RestApiException, IOException {

        return OfferManager.insertNewOffer(newOffer);

    }

    /* Asynchronous version */
    public static Task.General insertNewOffer(Offer newOffer, ResultProcessor<Offer> postProcessor){
        Task.General t = new Task.General(Task.Method.INSERT_NEW_OFFER, postProcessor);
        t.execute(newOffer);
        return t;
    }



    /**
     *
     * Search for offers matching criteria.
     *
     * @param criteria, Offer object that describe the search criteria.
     *
     *                  It can be null if you want to retreve all offer of the database.
     *
     *                  Possible search criteria are :
     *                      -company_id OR company_name ( e.g. call criteria.setCompanyId(23) or criteria.setCompanyName("Apple") )
     *                      -kind_of_contract ( e.g call criteria.setKindOfContract("internship") )
     *                      -description_of_work ( e.g call criteria.setDescriptionOfWork("Do some programing work") )
     *                      -duration_months ( e.g. call criteria.setDuratiionMonths(4) )
     *
     *                  Note : id of the newOffer object must not be set, it will be automaticaly generated
     *                  server side ( e.g. do not call newOffer.manuallySetId(666); )
     *
     * @return List of offer matching ALL criteria specified.
     *
     * @throws RestApiException when known, unpredictable error occur server side.
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static List<Offer> getOffersMatchingCriteria( Offer criteria ) throws RestApiException, IOException {
        return OfferManager.getOfferMatchingCriteria(criteria);
    }

    /* Asynchronous version */
    public static Task.General getOffersMatchingCriteria( Offer criteria, ResultProcessor<List<Offer>> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_OFFER_MATCHING_CRITERIA, postProcessor);
        t.execute(criteria);
        return t;
    }




    /**
     *
     * Update an offer in the database.
     *
     * @param offerToUpdate, Offer object witch specyfy what to update.
     *
     *                  It can't be null! You must manualy set the id of offerToUpdate to match the
     *                  id value of the offer you want to update in the database ( e.g. call
     *                  offerToUpdate.manuallySetId(34) )
     *
     *                  Possible modifiable field :
     *                      -company_id ( and not company_name ! ) ( e.g. call offerToUpdate.setCompanyId(23))
     *                      -kind_of_contract ( e.g call offerToUpdate.setKindOfContract("internship") )
     *                      -description_of_work ( e.g call offerToUpdate.setDescriptionOfWork("Do some programing work") )
     *                      -duration_months ( e.g. call offerToUpdate.setDurationMonths(4) )
     *
     *                  Note : id of the newOffer object must not be set, it will be automaticaly generated
     *                  server side ( e.g. do not call newOffer.manuallySetId(666); )
     *
     * @return The offer after modification.
     *
     * @throws RestApiException when known, unpredictable error occur server side.
     * if error code ( e.getResponseCode() == -1 ) an unknown error occurred => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Offer updateOffer( Offer offerToUpdate ) throws IOException, RestApiException {
        return OfferManager.updateOffer(offerToUpdate);
    }

    /* Asynchronous version */
    public static Task.General updateOffer( Offer offerToUpdate, ResultProcessor<Offer> postProcessor){
        Task.General t=new Task.General(Task.Method.UPDATE_OFFER,postProcessor);
        t.execute(offerToUpdate);
        return t;
    }



    /**
     *
     * Delet an offer with specific id.
     *
     * @param id the id of the offer to delete.
     * @return 0 if success.
     * @throws RestApiException when known, unpredictable error occur server side ( e.g. Trying to retreve unexsisting Offer).
     * if error code ( e.getResponseCode() == -1 ) an unknown error occured => Must report to be corrected.
     *          IOException when there is a problem with the network connection ( e.g. DNS resolution fail )
     */
    public static Integer deleteOffer( int id ) throws IOException, RestApiException {
        return OfferManager.deleteOffer(id);
    }

    /* Asynchronous version */
    public static Task.General deleteOffer( int id, ResultProcessor<Integer> postProcessor){
        Task.General t = new Task.General(Task.Method.DELETE_OFFER,postProcessor);
        t.execute(id);
        return t;
    }








    /* --------------------- Student -----------------------*/


    public static Student getStudentById(int id) throws RestApiException, IOException {
        return StudentManager.getStudentById(id);
    }


    public static Task.General getStudentById(int i, ResultProcessor<Student> postProcessor){

        Task.General t = new Task.General(Task.Method.GET_STUDENT_BY_ID, postProcessor);
        t.execute(i);
        return t;

    }



    public static Student insertNewStudent(Student newStudent) throws RestApiException, IOException {
        return StudentManager.insertNewStudent(newStudent);
    }


    public static Task.General insertNewStudent(Student newStudent,  ResultProcessor<Student> postProcessor){
        Task.General t = new Task.General(Task.Method.INSERT_NEW_STUDENT, postProcessor);
        t.execute(newStudent);
        return t;
    }



    public static List<Student> getStudentsMatchingCriteria( Student criteria ) throws RestApiException, IOException {
        return StudentManager.getStudentsMatchingCriteria(criteria);
    }


    public static Task.General getStudentsMatchingCriteria( Student criteria, ResultProcessor<List<Student>> postProcessor ){
        Task.General t = new Task.General(Task.Method.GET_STUDENTS_MATCHING_CRITERIA, postProcessor);
        t.execute(criteria);
        return t;
    }



    public static Student updateStudent( Student studentToUpdate ) throws IOException, RestApiException {
        return StudentManager.updateStudent(studentToUpdate);
    }


    public static Task.General updateStudent(  Student studentToUpdate, ResultProcessor<Student> postProcessor ){
        Task.General t = new Task.General(Task.Method.UPDATE_STUDENT, postProcessor);
        t.execute(studentToUpdate);
        return t;
    }



    public static Integer deleteStudent( int id) throws IOException, RestApiException {
        return StudentManager.deleteStudent(id);
    }


    public static Task.General deleteStudent(  int id, ResultProcessor<Integer> postProcessor ){
        Task.General t = new Task.General(Task.Method.DELETE_STUDENT, postProcessor);
        t.execute(id);
        return t;
    }



    /* --------------------- Favourite management -----------------------*/




    public static List<Student> getFavouriteStudentOfCompany(int companyId) throws IOException, RestApiException{
        return CompanyManager.getFavouriteStudentOfCompany(companyId);
    }


    public static Task.General getFavouriteStudentOfCompany(int companyId, ResultProcessor<List<Student>> postProcessor){
        Task.General t = new Task.General(Task.Method.GET_FAVOURITE_STUDENT_OF_COMPANY,postProcessor);
        t.execute(companyId);
        return t;
    }


    public static Student addFavouriteStudentForCompany(int companyId, int studentId) throws IOException, RestApiException{
        return CompanyManager.addFavouriteStudentForCompany(companyId, studentId);
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
        return StudentManager.deleteAFavouriteCompanyOfAStudent(studentId, companyId);
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
        return StudentManager.addFavouriteOfferForStudent(studentId, offerId);
    }

    public static Task.General addFavouriteOfferForStudent(int studentId, int offerId, ResultProcessor<Offer> postProcessor){
        Task.General t = new Task.General(Task.Method.ADD_FAVOURITE_OFFER_FOR_STUDENT,postProcessor);
        t.execute(studentId, offerId);
        return t;
    }


    public static Integer deleteAFavouriteOfferOfAStudent( int studentId, int offerId) throws IOException, RestApiException{
        return StudentManager.deleteAFavouriteOfferOfAStudent(studentId, offerId);
    }

    public static Task.General deleteAFavouriteOfferOfAStudent( int studentId, int offerId, ResultProcessor<Integer> postProcessor){
        Task.General t = new Task.General(Task.Method.DELETE_A_FAVOURITE_OFFER_OF_A_STUDENT,postProcessor);
        t.execute(studentId, offerId);
        return t;

    }




    public static Integer subscribeStudentOfJobOffer( int offerId, int studentId) throws IOException, RestApiException{
        return StudentManager.subscribeStudentOfJobOffer(offerId, studentId);
    }


    //TODO test
    public static Task.General subscribeStudentOfJobOffer( int offerId, int studentId, ResultProcessor<Integer> postProcessor){
        Task.General t= new Task.General(Task.Method.SUBSCRIBE_STUDENTS_OF_JOB_OFFER,postProcessor);
        t.execute(offerId, studentId);
        return t;
    }




    public static Integer unsubscribeStudentOfJobOffer( int offerId, int studentId ) throws IOException, RestApiException {
        return StudentManager.unsubscribeStudentOfJobOffer(offerId,studentId);
    }


    //TODO test
    public static Task.General unsubscribeStudentOfJobOffer( int offerId, int studentId, ResultProcessor<Integer> postProcessor){
        Task.General t= new Task.General(Task.Method.UNSUBSCRIBE_STUDENTS_OF_JOB_OFFER,postProcessor);
        t.execute(offerId, studentId);
        return t;
    }




    public static List<Student> getStudentsOfJobOffer( int jobOffer, Student criteria ) throws IOException, RestApiException {
        return StudentManager.getStudentsOfJobOffer(jobOffer,criteria);
    }

    //TODO test
    public static Task.General getStudentsOfJobOffer( int jobOffer, Student criteria, ResultProcessor<Student> postProcessor){
        Task.General t= new Task.General(Task.Method.GET_STUDENTS_OF_JOB_OFFER,postProcessor);
        t.execute(jobOffer, criteria);
        return t;
    }



}