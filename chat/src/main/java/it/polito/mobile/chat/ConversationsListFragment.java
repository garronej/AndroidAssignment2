package it.polito.mobile.chat;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.chat.model.ChatHTTPClient;
import it.polito.mobile.chat.model.Conversation;
import it.polito.mobile.chat.model.Util;

public class ConversationsListFragment extends Fragment {
    private final String TAG = "ConversationsListFrag";
    private ListView lvConversations;
    private View selectedView;
    private List<AsyncTask<Integer, Void, List<Conversation>>> tList;

    public interface Callbacks {
        void onItemClick(int i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        lvConversations = (ListView) view.findViewById(R.id.conversations_lv);
        //this should fetch from backend and prepare the list for being shown in the listView


        lvConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Activity parentActivity = getActivity();
                if (parentActivity instanceof ConversationsListFragment.Callbacks) {
                    Map<String, Object> m = (Map<String, Object>) adapterView.getItemAtPosition(i);
                    if (selectedView != null) {
                        selectedView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedView = view;
                    selectedView.setBackgroundColor(Color.RED);
                    ((Callbacks) parentActivity).onItemClick((Integer) m.get("conversationId"));
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tList = new ArrayList<>();
        refreshConversations();
    }

    @Override
    public void onPause() {
        super.onPause();
        for(AsyncTask<?,?,?> t:tList){
            if(!t.isCancelled() && (t.getStatus()== AsyncTask.Status.RUNNING||t.getStatus()== AsyncTask.Status.PENDING)){
                t.cancel(true);
            }
        }
    }

    public void refreshConversations(){
        tList.add(
                ChatHTTPClient.getConversationsForStudent(FakeStudent.getId(), new ChatHTTPClient.ResultProcessor<List<Conversation>>() {
                    @Override
                    public void process(List<Conversation> arg) {
                        //TODO init the proper conversation with the selection
                        List<Map<String, Object>> conversations = buildMapFromConversations(arg);
                        SimpleAdapter simpleAdapter = new SimpleAdapter(
                                getActivity(),
                                conversations,
                                R.layout.conversation_list_item,
                                new String[]{"recipient", "last_message_time", "message"},
                                new int[]{R.id.recipient_tv, R.id.timestamp_tv, R.id.message_snippet_tv}
                        );

                        lvConversations.setAdapter(simpleAdapter);

                    }

                    @Override
                    public void onException(Exception e) {
                        //TODO: handle exceptions

                    }

                    @Override
                    public void cancel() {
                        //do nothing
                    }

                })
        );


    }

    private List<Map<String, Object>> buildMapFromConversations(List<Conversation> cs) {
        List<Map<String, Object>> conversations = new ArrayList<Map<String, Object>>();
        for(Conversation c:cs){
            Map<String, Object> m = new HashMap<String, Object>();
            if(c.isGroup()) {
                m.put("recipient", c.getTitle());
            }else{
                //not really safe....
                //TODO change the student with the logged one
                m.put("recipient", c.getStudents().get(0).getId()==FakeStudent.getId()?c.getStudents().get(1).getFullname():c.getStudents().get(0).getFullname());
            }
            if(c.getLastMessage()!=null) {
                SimpleDateFormat df = null;

                if(Util.isToday(c.getLastMessage().getDate()))
                    df=new SimpleDateFormat("HH:mm");
                else
                    df=new SimpleDateFormat("dd/MM/yyyy");
                m.put("last_message_time", df.format(c.getLastMessage().getDate()));
                m.put("message", c.getLastMessage().getMessage());
            }else{
                m.put("last_message_time","");
                m.put("message",getResources().getString(R.string.no_message));
            }
            m.put("conversationId", c.getId());
            conversations.add(m);
        }
        return conversations;
    }

    private List<Map<String, Object>> buildConversations() {
        List<Map<String, Object>> conversations = new ArrayList<Map<String, Object>>();
        Map<String, Object> c1 = new HashMap<String, Object>();
        c1.put("recipient", "marco gaido");
        c1.put("last_message_time", "13:30");
        c1.put("message", "bella zio come cazzo stai oh??");
        c1.put("conversationId", 11);
        Map<String, Object> c2 = new HashMap<String, Object>();
        c2.put("message", "ma figgggggggggggataa!");
        c2.put("last_message_time", "16:30");
        c2.put("recipient", "riccardo odone");
        c2.put("conversationId", 22);
        Map<String, Object> c3 = new HashMap<String, Object>();
        c3.put("message", "ieri te l'ho inviato cazzo!");
        c3.put("last_message_time", "1d");
        c3.put("recipient", "joseph garro");
        c3.put("conversationId", 33);
        conversations.add(c1);
        conversations.add(c2);
        conversations.add(c3);
        return conversations;
    }

}
