package it.polito.mobile.chat;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationShowFragment extends Fragment {
    private final String TAG = "ConversationShowFrag";
    private TextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_show, container, false);
        tv = (TextView) view.findViewById(R.id.tv);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int conversationId;
        if (getActivity() instanceof ConversationShowActivity) {
            ConversationShowActivity parentActivity = (ConversationShowActivity) getActivity();
            conversationId = parentActivity.getSelectedConversationId();
        }
        else {
            ConversationsActivity parentActivity = (ConversationsActivity) getActivity();
            conversationId = parentActivity.getSelectedConversationId();
        }
        //fetch from backend and show
        tv.setText("conversationId: " + String.valueOf(conversationId));
    }

    //only called from ConversationsActivity when both frags are visible at the same time
    public void onItemClick() {
        ConversationsActivity parentActivity = (ConversationsActivity) getActivity();
        int conversationId = parentActivity.getSelectedConversationId();
        //fetch from backend and show
        tv.setText("conversationId: " + String.valueOf(conversationId));
    }
}
