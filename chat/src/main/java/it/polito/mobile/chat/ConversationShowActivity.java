package it.polito.mobile.chat;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ConversationShowActivity extends AppCompatActivity {

    private final String TAG = "ConversationShowActivity";
    private int selectedConversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_show);
        selectedConversationId = getIntent().getIntExtra("conversationId", -1);
        if (selectedConversationId == -1) { throw new RuntimeException("conversationID required"); }
    }

    //used by child fragment to know who is selected
    public int getSelectedConversationId() {
        return selectedConversationId;
    }

}
