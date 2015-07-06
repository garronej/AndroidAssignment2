package it.polito.mobile.chat.model;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polito.mobile.chat.RESTManager;


/**
 * Created by mark9 on 06/07/15.
 */
public class ChatHTTPClient {

    public interface ResultProcessor<T>{
        void process(T arg);
        void onException(Exception e);
        void cancel();
    }

    public static AsyncTask<Integer, Void, List<Conversation>>
        getConversationsForStudent(int studentId, final ResultProcessor<List<Conversation>> processor){

        AsyncTask<Integer, Void, List<Conversation>>
                t = new AsyncTask<Integer, Void, List<Conversation>>() {
            Exception e = null;
            @Override
            protected List<Conversation> doInBackground(Integer... studentId) {
                List<Conversation> convs = new ArrayList<>();
                HashMap<String, String> params = new HashMap<>();
                params.put("student[student_id]", ""+studentId[0]);
                try {
                    String response = RESTManager.send(RESTManager.GET, "conversations/", params);
                    JSONArray obj = (new JSONObject(response)).getJSONArray("conversations");
                    for(int i=0;i<obj.length();i++){
                        convs.add(new Conversation(obj.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return convs;
            }

            @Override
            protected void onPostExecute(List<Conversation> conversations) {
                if(this.e==null) {
                    processor.process(conversations);
                }else{
                    processor.onException(this.e);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                processor.cancel();
            }
        };
        t.execute(studentId);
        return t;
    }

    public static AsyncTask<Integer, Void, Conversation>
        getConversation(int conversationId, final ResultProcessor<Conversation> processor){

        AsyncTask<Integer, Void, Conversation>
                t = new AsyncTask<Integer, Void, Conversation>() {
            Exception e = null;
            @Override
            protected Conversation doInBackground(Integer... conversationId) {
                Conversation conv = null;

                try {
                    String response = RESTManager.send(RESTManager.GET, "conversations/"+conversationId[0], null);

                    conv = new Conversation((new JSONObject(response)).getJSONObject("conversation"));
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return conv;
            }

            @Override
            protected void onPostExecute(Conversation conversations) {
                if(this.e==null) {
                    processor.process(conversations);
                }else{
                    processor.onException(this.e);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                processor.cancel();
            }
        };
        t.execute(conversationId);
        return t;
    }


    public static AsyncTask<Message, Void, Message>
        sendMessage(Message message, final ResultProcessor<Message> processor){

        AsyncTask<Message, Void, Message>
                t = new AsyncTask<Message, Void, Message>() {
            Exception e = null;
            @Override
            protected Message doInBackground(Message... m) {
                Message conv = null;

                try {
                    String response = RESTManager.send(RESTManager.POST, "messages/", m[0].toFormParams());

                    conv = new Message((new JSONObject(response)).getJSONObject("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return conv;
            }

            @Override
            protected void onPostExecute(Message conversations) {
                if(this.e==null) {
                    processor.process(conversations);
                }else{
                    processor.onException(this.e);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                processor.cancel();
            }
        };
        t.execute(message);
        return t;
    }



    public static AsyncTask<Conversation, Void, Conversation>
        createConversation(Conversation message, final ResultProcessor<Conversation> processor){

        AsyncTask<Conversation, Void, Conversation>
                t = new AsyncTask<Conversation, Void, Conversation>() {
            Exception e = null;
            @Override
            protected Conversation doInBackground(Conversation... c) {
                Conversation conv = null;

                try {
                    String response = RESTManager.send(RESTManager.POST, "conversations/", c[0].toFormParams());

                    conv = new Conversation((new JSONObject(response)).getJSONObject("conversation"));
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return conv;
            }

            @Override
            protected void onPostExecute(Conversation conversations) {
                if(this.e==null) {
                    processor.process(conversations);
                }else{
                    processor.onException(this.e);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                processor.cancel();
            }
        };
        t.execute(message);
        return t;
    }

}
