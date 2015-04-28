package it.polito.mobile.androidassignment2.model;

import android.os.AsyncTask;


/**
 * Created by mark9 on 28/04/15.
 */
public class StudentTasks {
    public static class StudentInfoGetter extends AsyncTask<Integer, Integer, Student> {

        private ResultProcessor<Student> postProcessor;

        public StudentInfoGetter(ResultProcessor<Student> postProcessor){
            this.postProcessor=postProcessor;
        }


        @Override
        protected Student doInBackground(Integer... params) {
            Student s = null;
            try {
                s=StudentManager.getStudentInfo(params[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(Student student) {
            if(postProcessor!=null){
                postProcessor.process(student);
            }
        }
    }

    public static class StudentInserter extends AsyncTask<Student, Integer, Student> {

        private ResultProcessor<Student> postProcessor;

        public StudentInserter(ResultProcessor<Student> postProcessor){
            this.postProcessor=postProcessor;
        }


        @Override
        protected Student doInBackground(Student... params) {
            Student s = null;
            try {
                s=StudentManager.insertNewStudent(params[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return s;
        }

        @Override
        protected void onPostExecute(Student student) {
            if(postProcessor!=null){
                postProcessor.process(student);
            }
        }
    }
}
