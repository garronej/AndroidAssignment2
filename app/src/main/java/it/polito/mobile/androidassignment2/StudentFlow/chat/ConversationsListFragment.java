package it.polito.mobile.androidassignment2.StudentFlow.chat;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.ChatHTTPClient;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.Conversation;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.Message;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.Util;
import it.polito.mobile.androidassignment2.context.AppContext;


public class ConversationsListFragment extends Fragment {
    private final String TAG = "ConversationsListFrag";
    private ListView lvConversations;
    //private Map<Integer, Conversation> conversationsMap = new HashMap<>();
    //private View selectedView;
    private int selectedItem;
    private List<Conversation> conversations;
    private List<AsyncTask<Integer, Void, List<Conversation>>> tList;

    public void onMessageSent(Message m) {
        if (((ConversationsActivity) getActivity()).getSelectedConversation() != null) {
            for (int i =0;i<conversations.size();++i) {
                if (((ConversationsActivity) getActivity()).getSelectedConversation().getId() == conversations.get(i).getId()) {
                    Conversation c = conversations.get(i);
                    c.setLastMessage(m);
                    conversations.remove(i);
                    conversations.add(0,c);
                    ((BaseAdapter)lvConversations.getAdapter()).notifyDataSetChanged();
                    lvConversations.setSelectionFromTop(0,0);
                    break;
                }
            }
        }
    }

    public interface Callbacks {
        void onItemClick(Conversation c);
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
                    Conversation c = (Conversation) adapterView.getItemAtPosition(i);

                    ((Callbacks) parentActivity).onItemClick(c);

                    lvConversations.invalidateViews();
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
        try {
            tList.add(
                    ChatHTTPClient.getConversationsForStudent(((AppContext) getActivity().getApplication()).getSession().getStudentLogged().getId(), new ChatHTTPClient.ResultProcessor<List<Conversation>>() {
                        @Override
                        public void process(List<Conversation> arg) {
                            getActivity().getSharedPreferences("notifications", getActivity().MODE_PRIVATE).edit().clear().commit();

                            conversations = arg;

                            if(conversations.size() == 0){
                                getActivity().getLayoutInflater().inflate(R.layout.empty_conversations_list, (ViewGroup) lvConversations.getParent(), true);
                            }else{
                                View emptyListView = ((ViewGroup) lvConversations.getParent()).findViewById(R.id.empty_conversation_list_container);
                                ((ViewGroup) lvConversations.getParent()).removeView(emptyListView);
                            }

                        /*final List<Map<String, Object>> conversations = buildMapFromConversations(arg);
                        SimpleAdapter simpleAdapter = new SimpleAdapter(
                                getActivity(),
                                conversations,
                                R.layout.conversation_list_item,
                                new String[]{"recipient", "last_message_time", "message"},
                                new int[]{R.id.recipient_tv, R.id.timestamp_tv, R.id.message_snippet_tv}
                        );*/

                            lvConversations.setAdapter(new BaseAdapter() {
                                @Override
                                public int getCount() {
                                    return conversations.size();
                                }

                                @Override
                                public Object getItem(int i) {
                                    return conversations.get(i);
                                }

                                @Override
                                public long getItemId(int i) {
                                    return 0;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    if (convertView == null) {
                                        convertView = ConversationsListFragment.this.getActivity().getLayoutInflater().inflate(R.layout.conversation_list_item, parent, false);
                                    }
                                    Conversation c = (Conversation) getItem(position);
                                    TextView recipient = (TextView) convertView.findViewById(R.id.recipient_tv);
                                    TextView last_message_time = (TextView) convertView.findViewById(R.id.timestamp_tv);
                                    TextView message = (TextView) convertView.findViewById(R.id.message_snippet_tv);

                                    if (c.isGroup()) {
                                        recipient.setText(c.getTitle());
                                    } else {
                                        //not really safe....
                                        try {
                                            recipient.setText(c.getStudents().get(0).getId() == ((AppContext) getActivity().getApplication()).getSession().getStudentLogged().getId() ? c.getStudents().get(1).getFullname() : c.getStudents().get(0).getFullname());

                                        } catch (Exception e) {

                                            //TODO: what to do?
                                        }
                                    }
                                    if (c.getLastMessage() != null) {
                                        SimpleDateFormat df = null;

                                        if (Util.isToday(c.getLastMessage().getDate()))
                                            df = new SimpleDateFormat("HH:mm");
                                        else
                                            df = new SimpleDateFormat("dd/MM/yyyy");
                                        last_message_time.setText(df.format(c.getLastMessage().getDate()));
                                        message.setText(c.getLastMessage().getMessage());
                                    } else {
                                        last_message_time.setText("");
                                        message.setText(getResources().getString(R.string.no_message));
                                    }
                                    if (((ConversationsActivity) getActivity()).getSelectedConversation() != null
                                            && ((ConversationsActivity) getActivity()).getSelectedConversation().getId() == c.getId()) {
                                        convertView.setBackgroundColor(Color.RED);
                                    } else {
                                        convertView.setBackgroundColor(Color.TRANSPARENT);
                                    }

                                    return convertView;
                                }
                            });
                            if (((ConversationsActivity) getActivity()).getSelectedConversation() != null) {
                                for (int i = 0; i < conversations.size(); ++i) {
                                    if (((ConversationsActivity) getActivity()).getSelectedConversation().getId() == conversations.get(i).getId()) {
                                        lvConversations.setSelectionFromTop(i, 0);
                                        break;
                                    }
                                }
                            }

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
        }catch(Exception e){
            //TODO: what to do?
        }

    }

    /*private List<Map<String, Object>> buildMapFromConversations(List<Conversation> cs) {
        List<Map<String, Object>> conversations = new ArrayList<Map<String, Object>>();
        for(Conversation c:cs){
            conversationsMap.put(c.getId(),c);
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
            m.put("isGroup", c.isGroup());
            conversations.add(m);
        }
        return conversations;
    }
*/
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
