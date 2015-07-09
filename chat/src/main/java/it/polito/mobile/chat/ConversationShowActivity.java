package it.polito.mobile.chat;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import it.polito.mobile.chat.model.Conversation;


public class ConversationShowActivity extends AppCompatActivity {

    private final String TAG = "ConversationShowActivity";
    private Conversation selectedConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_show);
        /*selectedConversation = new Conversation();
        selectedConversation.setId(getIntent().getIntExtra("conversationId", -1));
        if (selectedConversation.getId() == -1) { throw new RuntimeException("conversationID required"); }
        selectedConversation.setGroup(getIntent().getBooleanExtra("isGroup", false));*/
        selectedConversation = (Conversation)getIntent().getSerializableExtra("conversation");
        if(selectedConversation==null) throw new RuntimeException("conversation required");
    }

    //used by child fragment to know who is selected
    public Conversation getSelectedConversation() {
        return selectedConversation;
    }

}
