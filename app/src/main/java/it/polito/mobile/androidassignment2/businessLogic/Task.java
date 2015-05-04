package it.polito.mobile.androidassignment2.businessLogic;

import android.os.AsyncTask;

import java.util.List;


/**
 * Created by Joseph on 30/04/2015.
 */
class Task {

    protected enum Method {
        GET_STUDENT_BY_ID,
        INSERT_NEW_STUDENT,
        GET_STUDENTS_MATCHING_CRITERIA,
        UPDATE_STUDENT,
        DELETE_STUDENT
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
                    case GET_STUDENT_BY_ID:  case DELETE_STUDENT:
                        out = Manager.getStudentById((Integer) params[0]);
                        break;
                    case INSERT_NEW_STUDENT: case GET_STUDENTS_MATCHING_CRITERIA: case UPDATE_STUDENT:
                        out = Manager.insertNewStudent((Student)params[0]);
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
                    case GET_STUDENT_BY_ID: case INSERT_NEW_STUDENT: case UPDATE_STUDENT:
                        ((Manager.ResultProcessor<Student>)this.postProcessor).process((Student)out,this.exception);
                        break;
                    case DELETE_STUDENT:
                        ((Manager.ResultProcessor<Integer>)this.postProcessor).process((Integer)out,this.exception);
                        break;
                    case GET_STUDENTS_MATCHING_CRITERIA:
                        ((Manager.ResultProcessor<List<Student>>)this.postProcessor).process((List<Student>)out,this.exception);
                        break;
                }
            }
        }




    }




}
