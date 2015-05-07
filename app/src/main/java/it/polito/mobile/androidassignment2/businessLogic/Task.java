package it.polito.mobile.androidassignment2.businessLogic;

import android.os.AsyncTask;

import java.util.List;


/**
 * Created by Joseph on 30/04/2015.
 */
class Task {

    protected enum Method {
        GET_STUDENT_BY_ID,
        GET_COMPANY_BY_ID,

        INSERT_NEW_STUDENT,
        INSERT_NEW_COMPANY,

        GET_STUDENTS_MATCHING_CRITERIA,
        GET_COMPANIES_MATCHING_CRITERIA,

        UPDATE_STUDENT,
        UPDATE_COMPANY,

        DELETE_STUDENT,
        DELETE_COMPANY,

        LOGIN_STUDENT,
        LOGIN
    }



    protected static class General extends AsyncTask<Object, Void, Object> {

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

                    case GET_COMPANY_BY_ID:
                        out = Manager.getCompanyById((Integer) params[0]);
                        break;
                    case DELETE_COMPANY:
                        out = Manager.deleteCompany((Integer) params[0]);
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
                    case LOGIN:
                        out = Session.login((Session.LoginInfo)params[0]);

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
                    case GET_STUDENT_BY_ID: case INSERT_NEW_STUDENT: case UPDATE_STUDENT:
                        ((Manager.ResultProcessor<Student>)this.postProcessor).process((Student)out,this.exception);
                        break;

                    case GET_COMPANY_BY_ID: case INSERT_NEW_COMPANY: case UPDATE_COMPANY:
                        ((Manager.ResultProcessor<Company>)this.postProcessor).process((Company)out,this.exception);
                        break;

                    case DELETE_STUDENT: case DELETE_COMPANY:
                        ((Manager.ResultProcessor<Integer>)this.postProcessor).process((Integer)out,this.exception);
                        break;

                    case GET_STUDENTS_MATCHING_CRITERIA:
                        ((Manager.ResultProcessor<List<Student>>)this.postProcessor).process((List<Student>)out,this.exception);
                        break;

                    case GET_COMPANIES_MATCHING_CRITERIA:
                        ((Manager.ResultProcessor<List<Company>>)this.postProcessor).process((List<Company>)out,this.exception);
                        break;
                    case LOGIN:
                        ((Manager.ResultProcessor<Integer>)this.postProcessor).process((Integer)out,this.exception);
                        break;

                }
            }
        }




    }




}
