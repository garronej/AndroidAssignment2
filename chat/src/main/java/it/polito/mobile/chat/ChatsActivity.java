package it.polito.mobile.chat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class ChatsActivity extends ActionBarActivity {

    private final String TAG = "chats";
    private final int SELECT_RECIPIENTS = 149;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean isGroup = id == R.id.action_group_message;
        Intent i = new Intent(ChatsActivity.this, SelectRecipientsActivity.class);
        i.putExtra("isMultipleSelection", isGroup);
        startActivityForResult(i, SELECT_RECIPIENTS);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_RECIPIENTS) {
                Log.d(TAG, String.valueOf(data.getIntExtra("conversationId", -1)));
                if (data.getBooleanExtra("isGroup", false)) {
                    Log.d(TAG, "is group conversation");
                } else {
                    Log.d(TAG, "is private conversation");
                }
                //startActivity "conversation"
            }
        }
    }
}
