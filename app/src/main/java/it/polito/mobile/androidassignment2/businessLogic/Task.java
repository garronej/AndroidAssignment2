package it.polito.mobile.androidassignment2.businessLogic;

import android.os.AsyncTask;


/**
 * Created by Joseph on 30/04/2015.
 */
class Task {

    protected static int GET_STUDENT_BY_ID = 0;
    protected static int INSERT_NEW_STUDENT = 1;

    protected static class General extends AsyncTask<Object, Void, Object> {

        private Object postProcessor;
        private Exception exception = null;
        private int method;

        public General(int method, Object postProcessor){
            this.postProcessor=postProcessor;
            this.method = method;
        }


        @Override
        protected Object doInBackground(Object... params) {
            Object out = null;
            try {

                if( this.method == Task.GET_STUDENT_BY_ID ) {
                    //Ici il faut faire un case switch.
                    out = Manager.getStudentById((Integer) params[0]);
                }


                if( this.method == Task.INSERT_NEW_STUDENT ) {
                    //Ici il faut faire un case switch.
                    out = Manager.insertNewStudent((Student)params[0]);
                }


            } catch (Exception exception) {

                this.exception = exception;

            }

            return out;

        }

        @Override
        protected void onPostExecute(Object out) {
            if(postProcessor!=null){

                if( this.method == Task.GET_STUDENT_BY_ID || this.method == Task.INSERT_NEW_STUDENT ) {

                    Manager.ResultProcessor<Student> pp = (Manager.ResultProcessor<Student>)this.postProcessor;
                    pp.process((Student)out,this.exception);
                }




            }
        }
    }




}
