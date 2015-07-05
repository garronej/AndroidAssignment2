package it.polito.mobile.chat;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationsListFragment extends Fragment {
    private final String TAG = "ConversationsListFrag";
    private ListView lvConversations;
    private List<Map<String, Object>> conversations;
    private View selectedView;

    public interface Callbacks {
        void onItemClick(int i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);
        lvConversations = (ListView) view.findViewById(R.id.conversations_lv);
        //this should fetch from backend and prepare the list for being shown in the listView
        conversations = buildConversations();
        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getActivity(),
                conversations,
                R.layout.conversation_list_item,
                new String[] { "recipient", "last_message_time", "message" },
                new int[] { R.id.recipient_tv, R.id.timestamp_tv, R.id.message_snippet_tv }
        );
        lvConversations.setAdapter(simpleAdapter);

        lvConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Activity parentActivity = getActivity();
                if (parentActivity instanceof ConversationsListFragment.Callbacks) {
                    Map<String, Object> m = (Map<String,Object>) adapterView.getItemAtPosition(i);
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
