package it.polito.mobile.chat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectRecipientsActivity extends ActionBarActivity {

    private final String TAG = "select recipients";
    private ListView lvRecipients;
    private Button bCreateGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isMultipleSelection = getIntent().getBooleanExtra("isMultipleSelection", false);
        setContentView(R.layout.activity_select_recipients);
        lvRecipients = (ListView) findViewById(R.id.recipients_lv);
        bCreateGroup = (Button) findViewById(R.id.create_group_b);

        //these should be fetched from backend
        String recipients[] = new String[] { "marco gaido", "riccardo odone" };
        int item_layout;

        if (isMultipleSelection) {
            item_layout = android.R.layout.simple_list_item_multiple_choice;
            lvRecipients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            bCreateGroup.setVisibility(View.VISIBLE);

        } else {
            item_layout = android.R.layout.simple_list_item_1;
            bCreateGroup.setVisibility(View.GONE);
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                SelectRecipientsActivity.this,
                item_layout,
                recipients
                );
        lvRecipients.setAdapter(arrayAdapter);

        if (!isMultipleSelection) {
            lvRecipients.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, adapterView.getItemAtPosition(i).toString());
                    //create conversation on backend using loggedUser id and selected one
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("conversationId", 1);
                    returnIntent.putExtra("isGroup", false);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        } else {
            bCreateGroup.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    SparseBooleanArray checked = lvRecipients.getCheckedItemPositions();
                    ArrayList<String> selectedItems = new ArrayList<String>();
                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in adapter
                        int position = checked.keyAt(i);
                        // Add sport if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i))
                            selectedItems.add(arrayAdapter.getItem(position));
                    }

                    String[] outputStrArr = new String[selectedItems.size()];

                    for (int i = 0; i < selectedItems.size(); i++) {
                        outputStrArr[i] = selectedItems.get(i);
                    }
                    //create group conversation on backend on backend using loggedUser id and selected ones
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("conversationId", 2);
                    returnIntent.putExtra("isGroup", true);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
    }
}
