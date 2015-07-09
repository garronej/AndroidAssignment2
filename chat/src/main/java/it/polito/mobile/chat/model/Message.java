package it.polito.mobile.chat.model;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import it.polito.mobile.androidassignment2.businessLogic.Student;

/**
 * Created by mark9 on 06/07/15.
 */
public class Message {

    private int id;
    private Student sender;
    private Conversation conversation;
    private String message;
    private Date date;
    public Message(){}
    public Message(JSONObject json) {
        try{
            id=json.getInt("id");
            sender = new Student();
            sender.manuallySetId(json.getInt("sender_id"));
            conversation=new Conversation();
            conversation.setId(json.getInt("conversation_id"));

            date = Util.convertStringToDate(json.getString("created_at"));
            message = json.getString("message");
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Message wrong format: "+json.toString());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getSender() {
        return sender;
    }

    public void setSender(Student sender) {
        this.sender = sender;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<String,String> toFormParams(){
        Map<String, String> m = new HashMap<>();
        m.put("message[message]", getMessage());
        m.put("message[sender_id]", ""+getSender().getId());
        m.put("message[conversation_id]", ""+getConversation().getId());
        return m;
    }
}
