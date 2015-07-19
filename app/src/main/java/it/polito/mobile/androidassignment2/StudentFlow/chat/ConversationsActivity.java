package it.polito.mobile.androidassignment2.StudentFlow.chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.AlertYesNo;
import it.polito.mobile.androidassignment2.Communicator;
import it.polito.mobile.androidassignment2.LoginActivity;
import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.NavigationDrawerFragment;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.Conversation;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.Message;
import it.polito.mobile.androidassignment2.businessLogic.Manager;
import it.polito.mobile.androidassignment2.context.AppContext;
import it.polito.mobile.androidassignment2.gcm.UnregistrationManager;


public class ConversationsActivity extends AppCompatActivity implements ConversationsListFragment.Callbacks, Communicator {

    private final String TAG = "ConversationsActivity";
    private final int SELECT_RECIPIENTS = 149;
    private Conversation selectedConversation;
    private AsyncTask<?, ?, ?> task;
    private String mTitle;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position",2));

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        onSectionAttached(3);

        setTitle(mTitle);
        mNavigationDrawerFragment.setTitle(mTitle);
    }


    public void onSectionAttached(int number) {
		switch (number) {
			case 1:
				mTitle = getString(R.string.title_section1);
				break;
			case 2:
				mTitle = getString(R.string.title_section2);
				break;
			case 3:
				mTitle = getString(R.string.title_section3);
				break;
			case 4:
				mTitle = getString(R.string.title_section4);
				break;
		}
	}

    @Override
    protected void onPause() {
        super.onPause();
        if(task!=null && (task.getStatus()== AsyncTask.Status.RUNNING || task.getStatus()== AsyncTask.Status.PENDING)){
            task.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i = new Intent(ConversationsActivity.this, SelectRecipientsActivity.class);
        switch(id){
            case R.id.action_group_message:
                i.putExtra("isMultipleSelection", true);
                startActivityForResult(i, SELECT_RECIPIENTS);
                return true;
            case R.id.action_message:
                i.putExtra("isMultipleSelection", false);
                startActivityForResult(i, SELECT_RECIPIENTS);
                return true;
            case R.id.action_logout:
                showConfirmAlerter(0);
                return true;

            case R.id.action_delete:
                showConfirmAlerter(1);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }



    public void showConfirmAlerter(int kind) {
        AlertYesNo alert = new AlertYesNo();
        Bundle info = new Bundle();
        if (kind == 0)
            info.putString("message", getResources().getString(R.string.logout_message));
        else info.putString("message", getResources().getString(R.string.delete_user_message));

        info.putString("title", getResources().getString(R.string.confirm));
        info.putInt("kind", kind);
        alert.setCommunicator(this);
        alert.setArguments(info);
        alert.show(getSupportFragmentManager(), "Confirm");

    }

    @Override
    public void goSearch(int kind) {

    }

    @Override
    public void respond(int itemIndex, int kind) {

    }

    @Override
    public void dialogResponse(int result, int kind) {
        if (result == 1) {
            switch (kind) {
                case 0://logout
                    new UnregistrationManager(ConversationsActivity.this).unregisterGcm();
                    getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1://delete account
                    try {
                        task = Manager.deleteStudent(((AppContext) getApplication()).getSession().getStudentLogged().getId(), new Manager.ResultProcessor<Integer>() {
                            @Override
                            public void process(Integer arg, Exception e) {
                                task = null;
                                if (e != null) {
                                    Log.d(ConversationsActivity.class.getSimpleName(), "Error deleteing user");
                                    return;
                                }
                                getSharedPreferences("login_pref", MODE_PRIVATE).edit().clear().commit();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                                finish();
                            }

                            @Override
                            public void cancel() {
                                task = null;
                            }
                        });
                    } catch (DataFormatException|ExceptionInInitializerError e) {
                        ((AppContext)getApplication()).redirectToLogin(ConversationsActivity.class);
                    }
                    break;
                default:
                    return;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_RECIPIENTS) {

                // fetch again from backend and select it with onItemClick
                onItemClick((Conversation)data.getSerializableExtra("conversation"));

            }
        }
    }



    @Override
    public void onItemClick(Conversation conversation) {
        Fragment fragConversationShow = getSupportFragmentManager()
                .findFragmentById(R.id.conversation_show_fragment);
        if (fragConversationShow != null && fragConversationShow.isVisible()) {
            selectedConversation = conversation;
            ((ConversationShowFragment) fragConversationShow ).onItemClick();
        } else {
            Intent i = new Intent(ConversationsActivity.this, ConversationShowActivity.class);
            i.putExtra("conversation", conversation);
            startActivity(i);
        }
    }

    public void onNewMessageSent(Message m){
        ConversationsListFragment fragConversationShow = (ConversationsListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.conversations_list_fragment);
        if (fragConversationShow != null && fragConversationShow.isVisible()) {
            fragConversationShow.onMessageSent(m);
        }
    }

    //used by child fragment to know who is selected
    public Conversation getSelectedConversation() {
        return selectedConversation;
    }
}
