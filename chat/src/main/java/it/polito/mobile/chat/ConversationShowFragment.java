package it.polito.mobile.chat;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.chat.model.Message;

import static it.polito.mobile.chat.R.*;

public class ConversationShowFragment extends Fragment {
    private final String TAG = "ConversationShowFrag";
    private TextView tv;
    private ListView messageList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_conversation_show, container, false);
        tv = (TextView) view.findViewById(id.tv);
        messageList = (ListView)view.findViewById(id.message_list);
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

    List<Message> testMessages(){
        List<Message> l = new ArrayList<>();
        Message m = new Message();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis()-(long)(Math.random()*6000000));
        m.setDate(cal.getTime());
        Student s1 = new Student();
        s1.manuallySetId(1);
        m.setSender(s1);
        m.setMessage("This is a test message which should be replaced by the real messages");

        Message m1 = new Message();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(System.currentTimeMillis() - (long) (Math.random() * 6000000));
        m1.setDate(cal1.getTime());
        Student s2 = new Student();
        s2.manuallySetId(2);
        m1.setSender(s2);
        m1.setMessage("Short message");

        l.add(m);
        l.add(m1);
        return l;

    }

    //only called from ConversationsActivity when both frags are visible at the same time
    public void onItemClick() {
        ConversationsActivity parentActivity = (ConversationsActivity) getActivity();
        int conversationId = parentActivity.getSelectedConversationId();
        //fetch from backend and show
        tv.setText("conversationId: " + String.valueOf(conversationId));

        final List<Message> messages = testMessages();
        messageList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return messages.size();
            }

            @Override
            public Object getItem(int i) {
                return messages.get(i);
            }

            @Override
            public long getItemId(int i) {
                return (long) messages.get(i).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = ConversationShowFragment.this.getActivity().getLayoutInflater().inflate(layout.message_layout, parent, false);
                }
                Message n = (Message) getItem(position);
                ((TextView) convertView.findViewById(id.message_text)).setText(n.getMessage());
                SimpleDateFormat df = null;
                Calendar cal = Calendar.getInstance();

// set the calendar to start of today
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

// and get that as a Date
                Date today = cal.getTime();
                if (n.getDate().after(today))
                    df = new SimpleDateFormat("HH:mm");
                else
                    df = new SimpleDateFormat("dd/MM/YYYY");
                ((TextView) convertView.findViewById(id.message_date)).setText(df.format(n.getDate()));
                LinearLayout container = (LinearLayout) convertView.findViewById(id.message_container);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) container.getLayoutParams();
                if (n.getSender().getId() == FakeStudent.getId()) {
                    lp.gravity = Gravity.RIGHT;
                    lp.setMargins((int)ConversationShowFragment.this.getActivity().getResources().getDisplayMetrics().density*30,0,0,0);
                    container.setBackgroundResource(drawable.speech_bubble_green);
                } else {
                    lp.gravity = Gravity.LEFT;
                    lp.setMargins(0,0,(int)ConversationShowFragment.this.getActivity().getResources().getDisplayMetrics().density*30,0);
                    container.setBackgroundResource(drawable.speech_bubble_brown);
                }
                container.setLayoutParams(lp);

                return convertView;
            }
        });
    }
}
