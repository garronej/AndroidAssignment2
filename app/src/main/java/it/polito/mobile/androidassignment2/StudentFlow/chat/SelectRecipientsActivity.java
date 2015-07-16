package it.polito.mobile.androidassignment2.StudentFlow.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.ChatHTTPClient;
import it.polito.mobile.androidassignment2.StudentFlow.chat.model.Conversation;
import it.polito.mobile.androidassignment2.businessLogic.Student;
import it.polito.mobile.androidassignment2.context.AppContext;


public class SelectRecipientsActivity extends ActionBarActivity {

    private final String TAG = "select recipients";
    private ListView lvRecipients;
    private Button bCreateGroup;
    BaseAdapter arrayAdapter = null;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean isMultipleSelection = getIntent().getBooleanExtra("isMultipleSelection", false);
        setContentView(R.layout.activity_select_recipients);
        if (isMultipleSelection) {
            setTitle(R.string.select_recipients_title);
        } else {
            setTitle(R.string.select_recipient_title);
        }

        lvRecipients = (ListView) findViewById(R.id.recipients_lv);
        bCreateGroup = (Button) findViewById(R.id.create_group_b);
        final View groupLL = findViewById(R.id.groupLinearLayout);

        //these should be fetched from backend

        Student loggedStudent = new Student();
        try{
            loggedStudent.manuallySetId(((AppContext) getApplication()).getSession().getStudentLogged().getId());
        }catch (Exception e){
            loggedStudent.manuallySetId(-1);
        }
        ChatHTTPClient.getAvailableStudents(loggedStudent, new ChatHTTPClient.ResultProcessor<List<Student>>() {
            @Override
            public void process(final List<Student> arg) {

                final int item_layout;
                if (isMultipleSelection) {
                    item_layout = R.layout.simple_list_item_multiple_choice_custom;
                    lvRecipients.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    groupLL.setVisibility(View.VISIBLE);

                } else {
                    item_layout = android.R.layout.simple_list_item_1;
                    groupLL.setVisibility(View.GONE);
                }

                arrayAdapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return arg.size();
                    }

                    @Override
                    public Object getItem(int i) {
                        return arg.get(i);
                    }

                    @Override
                    public long getItemId(int i) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = SelectRecipientsActivity.this.getLayoutInflater().inflate(item_layout, parent, false);
                        }
                        Student n = (Student) getItem(position);
                        ((TextView) convertView.findViewById(android.R.id.text1)).setText(n.getFullname());

                        if (convertView instanceof CheckedTextView) {
                            SparseBooleanArray checked = lvRecipients.getCheckedItemPositions();
                            if (checked.size() > position) {
                                ((CheckedTextView) convertView).setChecked(checked.valueAt(position));
                            }
                        }
                        return convertView;
                    }

                };
                lvRecipients.setAdapter(arrayAdapter);
                if (savedInstanceState != null && savedInstanceState.getIntegerArrayList("selectedItems") != null) {
                    for (Integer pItem : savedInstanceState.getIntegerArrayList("selectedItems")) {
                        lvRecipients.setItemChecked(pItem, true);
                    }

                }
            }

            @Override
            public void onException(Exception e) {
                Toast.makeText(SelectRecipientsActivity.this, R.string.network_error, Toast.LENGTH_SHORT);
            }

            @Override
            public void cancel() {
                //nothing to do
            }
        });


        if (!isMultipleSelection) {
            lvRecipients.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d(TAG, adapterView.getItemAtPosition(i).toString());
                    //create conversation on backend using loggedUser id and selected one
                    //or return one if already existing
                    try {
                        List<Student> students = new ArrayList<Student>();
                        Student loggedStudent = new Student();
                        loggedStudent.manuallySetId(((AppContext) getApplication()).getSession().getStudentLogged().getId());
                        students.add(loggedStudent);
                        students.add((Student) adapterView.getItemAtPosition(i));
                        Conversation newConversation = new Conversation();
                        newConversation.setGroup(false);
                        newConversation.setStudents(students);

                        ChatHTTPClient.createConversation(newConversation, new ChatHTTPClient.ResultProcessor<Conversation>() {
                            @Override
                            public void process(Conversation arg) {
                                //create group conversation on backend on backend using loggedUser id and selected ones
                                //or return one if already existing
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("conversation", arg);
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            }

                            @Override
                            public void onException(Exception e) {
                                Toast.makeText(SelectRecipientsActivity.this, R.string.network_error, Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void cancel() {
                                //???
                            }
                        });
                    }catch(Exception e){
                        //TODO: what to do?
                        throw new RuntimeException(e);
                    }

                }
            });
        } else {
            bCreateGroup.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SparseBooleanArray checked = lvRecipients.getCheckedItemPositions();
                    ArrayList<Student> selectedItems = new ArrayList<Student>();
                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in adapter
                        int position = checked.keyAt(i);
                        // Add sport if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i))
                            selectedItems.add((Student) arrayAdapter.getItem(position));
                    }
                    EditText title_tv = (EditText) findViewById(R.id.groupTitle);
                    if (selectedItems.size() == 0 || title_tv.getText().toString().trim().equals("")) {
                        if (selectedItems.size() == 0) {
                            Toast.makeText(SelectRecipientsActivity.this, R.string.no_student_selected, Toast.LENGTH_LONG).show();
                        }
                        if (title_tv.getText().toString().trim().equals("")) {
                            title_tv.setError(getResources().getString(R.string.empty_title_error));
                        }

                    } else {
                         /*String[] outputStrArr = new String[selectedItems.size()];

                        for (int i = 0; i < selectedItems.size(); i++) {
                            outputStrArr[i] = selectedItems.get(i);
                        }*/
                        try {
                            Student loggedStudent = new Student();
                            loggedStudent.manuallySetId(((AppContext) getApplication()).getSession().getStudentLogged().getId());
                            selectedItems.add(loggedStudent);
                            Conversation newConversation = new Conversation();
                            newConversation.setGroup(true);
                            newConversation.setStudents(selectedItems);
                            newConversation.setTitle(((EditText) findViewById(R.id.groupTitle)).getText().toString());

                            ChatHTTPClient.createConversation(newConversation, new ChatHTTPClient.ResultProcessor<Conversation>() {
                                @Override
                                public void process(Conversation arg) {
                                    //create group conversation on backend on backend using loggedUser id and selected ones
                                    //or return one if already existing
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("conversation", arg);
                                    setResult(RESULT_OK, returnIntent);
                                    finish();
                                }

                                @Override
                                public void onException(Exception e) {
                                    Toast.makeText(SelectRecipientsActivity.this, R.string.network_error, Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void cancel() {
                                    //TODO???
                                }
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                    }


                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SparseBooleanArray checked = lvRecipients.getCheckedItemPositions();
        ArrayList<Integer> items = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i))
                items.add(position);
        }
        outState.putIntegerArrayList("selectedItems", items);
    }
}
