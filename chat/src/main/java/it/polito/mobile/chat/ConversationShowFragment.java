package it.polito.mobile.chat;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.chat.model.ChatHTTPClient;
import it.polito.mobile.chat.model.Conversation;
import it.polito.mobile.chat.model.Message;
import it.polito.mobile.chat.model.Util;

import static it.polito.mobile.chat.R.*;

public class ConversationShowFragment extends Fragment {
    //TODO for testing...put to 20 or something similar before the delivery..
    private static int NUMBER_OF_MESSAGES_PER_PAGE = 5;
    private final String TAG = "ConversationShowFrag";
    private TextView membersTitle;
    private ListView messageList;
    private EditText messageText;
    private List<Message> messages = new ArrayList<>();
    private View header;
    private AsyncTask<Conversation, Void, List<Message>> t;
    private AsyncTask<Message, Void, Message> t1;
    private AsyncTask<Conversation, Void, List<Message>> t2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_conversation_show, container, false);
        membersTitle = (TextView) view.findViewById(id.members_tv);
        messageList = (ListView)view.findViewById(id.message_list);

        messageText = (EditText)view.findViewById(id.message_et);
        return view;
    }

    private void sendMessage() {
        if(messageText.getText().toString().trim().equals(""))return;
        final Message m = new Message();
        Student s = new Student();
        s.manuallySetId(FakeStudent.getId()); //TODO: put the real student logged
        m.setSender(s);

        m.setConversation(getConversation());
        m.setMessage(messageText.getText().toString().trim());
        messageText.setText("");
        t1=ChatHTTPClient.sendMessage(m, new ChatHTTPClient.ResultProcessor<Message>() {
            @Override
            public void process(Message arg) {
                messages.add(arg);
                ((BaseAdapter)((HeaderViewListAdapter)messageList.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                messageList.setSelection(messageList.getAdapter().getCount() - 1);

            }

            @Override
            public void onException(Exception e) {
                //TODO manage exception... this time we have to be careful to this...
                //mettere toastino o cosa simile...
                messageText.setText(m.getMessage());
            }

            @Override
            public void cancel() {

            }
        });

    }

    private Conversation getConversation(){
        Conversation conversationId;
        if (getActivity() instanceof ConversationShowActivity) {
            ConversationShowActivity parentActivity = (ConversationShowActivity) getActivity();
            conversationId = parentActivity.getSelectedConversation();
        }
        else {
            ConversationsActivity parentActivity = (ConversationsActivity) getActivity();
            conversationId = parentActivity.getSelectedConversation();
        }
        return conversationId;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Conversation conversation = getConversation();

        if(conversation == null){
            messageList.removeHeaderView(header);
            messageList.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return 0;
                }

                @Override
                public Object getItem(int i) {
                    return null;
                }

                @Override
                public long getItemId(int i) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    return null;
                }
            });
            messageText.setVisibility(View.INVISIBLE);
        }else {

            updateMembersListTV();
            messageText.setVisibility(View.VISIBLE);
            initMessageList();
        }

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

    private void initMessageList(){
        if(messageList.getHeaderViewsCount()==0) {
            header = getActivity().getLayoutInflater().inflate(R.layout.header_more_messages, messageList, false);
            messageList.addHeaderView(header, null, false);
            header.findViewById(id.btn_more_messages).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    t2 = ChatHTTPClient.getMessages(getConversation(), (int)Math.ceil(messages.size() / (double) NUMBER_OF_MESSAGES_PER_PAGE), NUMBER_OF_MESSAGES_PER_PAGE, new ChatHTTPClient.ResultProcessor<List<Message>>() {
                        @Override
                        public void process(List<Message> arg) {

                            messages.addAll(0,arg);
                            ((BaseAdapter)((HeaderViewListAdapter)messageList.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                            //TODO we should prevent the scroll to the bottom..

                        }

                        @Override
                        public void onException(Exception e) {
                            //TODO handle exception
                        }

                        @Override
                        public void cancel() {
                            //nothing to do
                        }
                    });
                }
            });
        }
        /*messageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.d("marco", "Ime :"+i+"; key event: "+keyEvent);
                if (i == EditorInfo.IME_ACTION_DONE) {
                    sendMessage();
                    return true;
                }
                return true;
            }
        });*/
        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String string = s.toString();
                if (string.length() > 0 && string.charAt(string.length() - 1) == '\n') {
                    sendMessage();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        /*messageText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                Log.d("marco", "Key pressed :"+keyCode+"; key event: "+keyEvent);
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        sendMessage();
                        return true;
                    }
                }
                return false;
            }
        });*/

        t = ChatHTTPClient.getMessages(getConversation(), 0, NUMBER_OF_MESSAGES_PER_PAGE, new ChatHTTPClient.ResultProcessor<List<Message>>() {
            @Override
            public void process(List<Message> arg) {
                messages = arg;
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
                    public boolean isEnabled(int position) {
                        return false;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = ConversationShowFragment.this.getActivity().getLayoutInflater().inflate(layout.message_layout, parent, false);
                        }
                        Message n = (Message) getItem(position);
                        ((TextView) convertView.findViewById(id.message_text)).setText(n.getMessage());
                        SimpleDateFormat df = null;

                        if(Util.isToday(n.getDate()))
                            df = new SimpleDateFormat("HH:mm");
                        else
                            df = new SimpleDateFormat("dd/MM/yyyy");
                        ((TextView) convertView.findViewById(id.message_date)).setText(df.format(n.getDate()));
                        LinearLayout container = (LinearLayout) convertView.findViewById(id.message_container);
                        TextView tvSender = (TextView)container.findViewById(id.message_sender);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) container.getLayoutParams();
                        if (n.getSender().getId() == FakeStudent.getId()) {
                            //lp.gravity = Gravity.RIGHT;
                            ((LinearLayout)convertView).setGravity(Gravity.RIGHT);
                            lp.setMargins((int) ConversationShowFragment.this.getActivity().getResources().getDisplayMetrics().density * 30, 0, 0, 0);
                            container.setBackgroundResource(drawable.speech_bubble_green);
                            tvSender.setVisibility(View.GONE);

                        } else {
                            //lp.gravity = Gravity.LEFT;
                            ((LinearLayout)convertView).setGravity(Gravity.LEFT);
                            lp.setMargins(0, 0, (int) ConversationShowFragment.this.getActivity().getResources().getDisplayMetrics().density * 30, 0);
                            container.setBackgroundResource(drawable.speech_bubble_brown);
                            if(getConversation().isGroup()){
                                tvSender.setVisibility(View.VISIBLE);

                                int senderIndex = getConversation().getStudents().indexOf(n.getSender());
                                tvSender.setText(getConversation().getStudents().get(senderIndex).getFullname());
                            }else{
                                tvSender.setVisibility(View.GONE);
                            }
                        }
                        container.setLayoutParams(lp);

                        return convertView;
                    }
                });
                messageList.setSelection(messageList.getAdapter().getCount() - 1);
            }

            @Override
            public void onException(Exception e) {
                //TODO handle exception
            }

            @Override
            public void cancel() {
                //nothing to do
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if(t!=null && (t.getStatus()== AsyncTask.Status.RUNNING || t.getStatus() == AsyncTask.Status.PENDING)){
            t.cancel(true);
        }
        if(t1!=null && (t1.getStatus()== AsyncTask.Status.RUNNING || t1.getStatus() == AsyncTask.Status.PENDING)){
            t1.cancel(true);
        }
        if(t2!=null && (t2.getStatus()== AsyncTask.Status.RUNNING || t2.getStatus() == AsyncTask.Status.PENDING)){
            t2.cancel(true);
        }
    }

    //only called from ConversationsActivity when both frags are visible at the same time
    public void onItemClick() {
        messageText.setText("");//to clear the message edit area when
                                //changing conversation
        messageText.setVisibility(View.VISIBLE);

        updateMembersListTV();
        initMessageList();

    }

    private void updateMembersListTV(){
        String membersList ="";
        for(Student s : getConversation().getStudents()){
            if(s.getId() == FakeStudent.getId()) continue; //TODO replace with logged student
            membersList+=s.getFullname()+",";
        }
        membersTitle.setText(membersList.substring(0,membersList.length()-1));
    }
}
