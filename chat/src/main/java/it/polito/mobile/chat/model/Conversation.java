package it.polito.mobile.chat.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.androidassignment2.businessLogic.Student;

/**
 * Created by mark9 on 06/07/15.
 */
public class Conversation implements Serializable {

    private int id;
    private List<Student> students;
    private String title;
    private boolean group;
    private Message lastMessage;

    public Conversation(){}

    public Conversation(JSONObject json){
        try {
            id=json.getInt("id");
            group = json.getBoolean("is_group");
            title = json.getString("title");
            title = title.equals("null")?null:title;
            if(json.getString("last_message").equals("null")){
                lastMessage=null;
            }else{
                lastMessage=new Message(json.getJSONObject("last_message"));
            }

            JSONArray sArr = json.getJSONArray("students");
            students=new ArrayList<>();
            for(int i = 0; i < sArr.length(); ++i){
                students.add(new Student(sArr.getJSONObject(i)));
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Conversation wrong format: "+json.toString());

        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String, String> toFormParams() {
        Map<String, String> m = new HashMap<>();
        m.put("conversation[is_group]", String.valueOf(isGroup()));
        m.put("conversation[title]", title==null?"":title);
        int i=0;
        for (Student s : students){
            m.put("conversation[student_ids]["+(i++)+"]", ""+s.getId());
        }
        return m;
    }
}
