package it.polito.mobile.androidassignment2.businessLogic;

import android.os.AsyncTask;

import java.util.List;


/**
 * Created by Joseph on 30/04/2015.
 */
public class Task {

    protected enum Method {
        GET_STUDENT_BY_ID,
        GET_COMPANY_BY_ID,
        GET_OFFER_BY_ID,

        INSERT_NEW_STUDENT,
        INSERT_NEW_COMPANY,
        INSERT_NEW_OFFER,

        GET_STUDENTS_MATCHING_CRITERIA,
        GET_COMPANIES_MATCHING_CRITERIA,
        GET_OFFER_MATCHING_CRITERIA,

        UPDATE_STUDENT,
        UPDATE_COMPANY,
        UPDATE_OFFER,

        DELETE_STUDENT,
        DELETE_COMPANY,
        DELETE_OFFER,


        GET_FAVOURITE_COMPANY_OF_STUDENT,
        ADD_FAVOURITE_COMPANY_FOR_STUDENT,
        DELETE_A_FAVOURITE_COMPANY_OF_A_STUDENT,
        GET_FAVOURITE_OFFER_OF_STUDENT,
        ADD_FAVOURITE_OFFER_FOR_STUDENT,
        DELETE_A_FAVOURITE_OFFER_OF_A_STUDENT,


        GET_FAVOURITE_STUDENT_OF_COMPANY,
        ADD_FAVOURITE_STUDENT_FOR_COMPANY,
        DELETE_A_FAVOURITE_STUDENT_OF_A_COMPANY,

        LOGIN
    }



    public static class General extends AsyncTask<Object, Void, Object> {

        private Object postProcessor;
        private Exception exception = null;
        private Method method;

        public General(Method method, Object postProcessor){
            this.postProcessor=postProcessor;
            this.method = method;
        }


        @Override
        protected Object doInBackground(Object... params) {
            Object out = null;
            try {

                switch( this.method){
                    case GET_STUDENT_BY_ID:
                        out = Manager.getStudentById((Integer) params[0]);
                        break;
                    case DELETE_STUDENT:
                        out = Manager.deleteStudent((Integer) params[0]);
                        break;
                    case INSERT_NEW_STUDENT:
                        out = Manager.insertNewStudent((Student)params[0]);
                        break;
                    case GET_STUDENTS_MATCHING_CRITERIA:
                        out = Manager.getStudentsMatchingCriteria((Student)params[0]);
                        break;
                    case UPDATE_STUDENT:
                        out = Manager.updateStudent((Student)params[0]);
                        break;

                    case INSERT_NEW_COMPANY:
                        out = Manager.insertNewCompany((Company)params[0]);
                        break;
                    case GET_COMPANIES_MATCHING_CRITERIA:
                        out = Manager.getCompaniesMatchingCriteria((Company)params[0]);
                        break;
                    case UPDATE_COMPANY:
                        out = Manager.updateCompany((Company)params[0]);
                        break;

                    case GET_COMPANY_BY_ID:
                        out = Manager.getCompanyById((Integer) params[0]);
                        break;
                    case DELETE_COMPANY:
                        out = Manager.deleteCompany((Integer) params[0]);
                        break;


                    case GET_OFFER_BY_ID:
                        out = Manager.getOfferById((Integer) params[0]);
                        break;
                    case DELETE_OFFER:
                        out = Manager.deleteOffer((Integer) params[0]);
                        break;
                    case INSERT_NEW_OFFER:
                        out = Manager.insertNewOffer((Offer)params[0]);
                        break;
                    case GET_OFFER_MATCHING_CRITERIA:
                        out = Manager.getOffersMatchingCriteria((Offer)params[0]);
                        break;
                    case UPDATE_OFFER:
                        out = Manager.updateOffer((Offer)params[0]);
                        break;

                    case GET_FAVOURITE_COMPANY_OF_STUDENT :
                        out = Manager.getFavouriteCompanyOfStudent((Integer) params[0]);
                        break;
                    case ADD_FAVOURITE_COMPANY_FOR_STUDENT :
                        out = Manager.addFavouriteCompanyForStudent((Integer)params[0],(Integer)params[1]);
                        break;
                    case DELETE_A_FAVOURITE_COMPANY_OF_A_STUDENT :
                        out = Manager.deleteAFavouriteCompanyOfAStudent((Integer)params[0],(Integer)params[1]);
                        break;
                    case GET_FAVOURITE_OFFER_OF_STUDENT :
                        out = Manager.getFavouriteOfferOfStudent((Integer) params[0]);
                        break;
                    case ADD_FAVOURITE_OFFER_FOR_STUDENT:
                        out = Manager.addFavouriteOfferForStudent((Integer)params[0],(Integer)params[1]);
                        break;
                    case DELETE_A_FAVOURITE_OFFER_OF_A_STUDENT :
                        out = Manager.deleteAFavouriteOfferOfAStudent((Integer)params[0],(Integer)params[1]);
                        break;



                    case GET_FAVOURITE_STUDENT_OF_COMPANY :
                        out = Manager.getFavouriteStudentOfCompany((Integer)params[0]);
                        break;

                    case ADD_FAVOURITE_STUDENT_FOR_COMPANY :
                        out = Manager.addFavouriteOfferForStudent((Integer)params[0],(Integer)params[1]);
                        break;

                    case DELETE_A_FAVOURITE_STUDENT_OF_A_COMPANY :
                        out = Manager.deleteAFavouriteStudentOfACompany((Integer)params[0],(Integer)params[1]);
                        break;



                    case LOGIN:
                        out = Session.login((Session.LoginInfo)params[0]);
                        break;

                }

            } catch (Exception exception) {

                this.exception = exception;

            }

            return out;

        }

        @Override
        protected void onPostExecute(Object out) {
            if(postProcessor!=null){

                switch( this.method ){
                    case GET_STUDENT_BY_ID:
                    case INSERT_NEW_STUDENT:
                    case UPDATE_STUDENT:
                    case ADD_FAVOURITE_STUDENT_FOR_COMPANY :
                        ((Manager.ResultProcessor<Student>)this.postProcessor).process((Student)out,this.exception);
                        break;

                    case GET_COMPANY_BY_ID:
                    case INSERT_NEW_COMPANY:
                    case UPDATE_COMPANY:
                    case ADD_FAVOURITE_COMPANY_FOR_STUDENT :
                        ((Manager.ResultProcessor<Company>)this.postProcessor).process((Company)out,this.exception);
                        break;
                    case GET_OFFER_BY_ID:
                    case INSERT_NEW_OFFER:
                    case UPDATE_OFFER:
                    case ADD_FAVOURITE_OFFER_FOR_STUDENT:
                        ((Manager.ResultProcessor<Offer>)this.postProcessor).process((Offer)out,this.exception);
                        break;

                    case DELETE_STUDENT:
                    case DELETE_COMPANY:
                    case DELETE_OFFER:
                    case DELETE_A_FAVOURITE_COMPANY_OF_A_STUDENT :
                    case DELETE_A_FAVOURITE_OFFER_OF_A_STUDENT :
                    case DELETE_A_FAVOURITE_STUDENT_OF_A_COMPANY :
                        ((Manager.ResultProcessor<Integer>)this.postProcessor).process((Integer)out,this.exception);
                        break;

                    case GET_STUDENTS_MATCHING_CRITERIA:
                    case GET_FAVOURITE_STUDENT_OF_COMPANY :
                        ((Manager.ResultProcessor<List<Student>>)this.postProcessor).process((List<Student>)out,this.exception);
                        break;

                    case GET_COMPANIES_MATCHING_CRITERIA:
                    case GET_FAVOURITE_COMPANY_OF_STUDENT :
                        ((Manager.ResultProcessor<List<Company>>)this.postProcessor).process((List<Company>)out,this.exception);
                        break;

                    case GET_OFFER_MATCHING_CRITERIA:
                    case GET_FAVOURITE_OFFER_OF_STUDENT :
                        ((Manager.ResultProcessor<List<Offer>>)this.postProcessor).process((List<Offer>)out,this.exception);
                        break;

                    case LOGIN:
                        ((Manager.ResultProcessor<Integer>)this.postProcessor).process((Integer)out,this.exception);
                        break;

                }
            }


        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            ((Manager.ResultProcessor<Integer>)this.postProcessor).cancel();
        }
    }




}
