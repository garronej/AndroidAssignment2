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


public class ConversationsActivity extends AppCompatActivity implements ConversationsListFragment.Callbacks {

    private final String TAG = "ConversationsActivity";
    private final int SELECT_RECIPIENTS = 149;
    private Conversation selectedConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        //TODO added by marco, to be decommented to join with the app module
        /*mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.selectItem(getIntent().getIntExtra("position",1));

        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        onSectionAttached(2);

        setTitle(mTitle);
        mNavigationDrawerFragment.setTitle(mTitle);*/
    }

    //TODO added by marco, to be decommented to join with the app module
    /*
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
	}*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversations, menu);
        //TODO added by marco, to be decommented to join with the app module
        //getMenuInflater().inflate(R.menu.global, menu);
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
            //TODO added by marco, to be decommented to join with the app module
            /*case R.id.action_logout:
                showConfirmAlerter(0);
                return true;

            case R.id.action_delete:
                showConfirmAlerter(1);
                return true;*/

        }

        return super.onOptionsItemSelected(item);
    }


    //TODO added by marco, to be decommented to join with the app module
    /*
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
    public void dialogResponse(int result, int kind) {
        if (result == 1) {
            switch (kind) {
                case 0://logout
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
                                    Log.d(CompaniesFavouritesActivity.class.getSimpleName(), "Error deleteing user");
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
                    } catch (DataFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    return;
            }
        }
    }
    */

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

    //used by child fragment to know who is selected
    public Conversation getSelectedConversation() {
        return selectedConversation;
    }
}
