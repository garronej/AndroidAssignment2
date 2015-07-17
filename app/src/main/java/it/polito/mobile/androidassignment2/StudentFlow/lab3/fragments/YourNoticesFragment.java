package it.polito.mobile.androidassignment2.StudentFlow.lab3.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.notice.EditNoticeActivity;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.notice.ShowNoticeActivity;
import it.polito.mobile.androidassignment2.StudentFlow.lab3.noticesList.NoticesListView;
import it.polito.mobile.androidassignment2.businessLogic.Notice;
import it.polito.mobile.androidassignment2.businessLogic.RESTManager;
import it.polito.mobile.androidassignment2.context.AppContext;


/**
 * Created by mark9 on 17/06/15.
 */
public class YourNoticesFragment extends Fragment implements NoticeFragment {
    private View root;
    private NoticesListView list;

    private TextView tvNoNotices;
    private ProgressBar pb;

    private List<AsyncTask<?,?,?>> pendingTasks = new ArrayList<>();

    public YourNoticesFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.your_notices, container, false);

        list = ((NoticesListView) root.findViewById(R.id.your_notices_list));
        {


            root.findViewById(R.id.btn_new_rent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditNoticeActivity.class);
                intent.putExtra("role", "create");
                startActivity(intent);
            }
        });

        }
        pb = (ProgressBar) root.findViewById(R.id.progress_bar);
        tvNoNotices = (TextView) root.findViewById(R.id.no_notices_tv);

        initWithData();

        return root;

    }

    private void initWithData() {
        //fill with initial data
        AsyncTask<Integer, Integer, List<Notice>> t = new AsyncTask<Integer, Integer, List<Notice>>() {
            Exception e=null;
            @Override
            protected List<Notice> doInBackground(Integer... integers) {
                Map<String, String> params = new HashMap<>();


                List<Notice> notices = new ArrayList<>();
                try {
                    params.put("notice[student_id]", "" + ((AppContext) getActivity().getApplication()).getSession().getStudentLogged().getId());

                    String response = RESTManager.send(RESTManager.GET, "notices", params);
                    JSONArray obj = (new JSONObject(response)).getJSONArray("notices");
                    for(int i=0;i<obj.length();i++){
                        notices.add(new Notice(obj.getJSONObject(i)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    this.e=e;
                }
                return notices;
            }

            @Override
            protected void onPostExecute(List<Notice> notices) {
                super.onPostExecute(notices);
                if(e!=null){
                    Toast.makeText(YourNoticesFragment.this.getActivity(), getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }
                initList(notices);

            }
        };

        t.execute();
        pendingTasks.add(t);

    }

    @Override
    public void refresh() {
        initWithData();
    }


    private void initList(List<Notice> notices){
        if(list!=null){
            pb.setVisibility(View.GONE);
            if (notices.size() == 0) {
                tvNoNotices.setVisibility(View.VISIBLE);
            }
            list.setContent(YourNoticesFragment.this.getActivity(), notices);
            //add the listener
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getActivity(), ShowNoticeActivity.class);
                    intent.putExtra("noticeId", (int)l);
                    startActivity(intent);

                }
            });
        }

    }



    @Override
    public void onDetach() {
        super.onDetach();
        for(AsyncTask<?,?,?> t : pendingTasks){
            if(t.getStatus()== AsyncTask.Status.PENDING
                    || t.getStatus()== AsyncTask.Status.RUNNING)
                t.cancel(true);
        }
        pendingTasks.clear();
    }
}
