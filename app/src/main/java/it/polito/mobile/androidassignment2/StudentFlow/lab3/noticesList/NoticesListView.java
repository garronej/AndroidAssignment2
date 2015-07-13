package it.polito.mobile.androidassignment2.StudentFlow.lab3.noticesList;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Notice;


/**
 * Created by mark9 on 04/06/15.
 */
public class NoticesListView extends ListView {

    private List<Notice> notices = new ArrayList<>();

    public NoticesListView(Context context) {
        super(context);
        init(null, 0);
    }

    public NoticesListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NoticesListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        this.setDivider(getResources().getDrawable(R.drawable.items_divider));

    }

    public void setContent(final Activity ctx, List<Notice> n){
        notices = n;
        this.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return notices.size();
            }

            @Override
            public Object getItem(int i) {
                return notices.get(i);
            }

            @Override
            public long getItemId(int i) {
                return notices.get(i).getId();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = ctx.getLayoutInflater().inflate(R.layout.notice_item, parent, false);
                }
                Notice n = (Notice) getItem(position);
                ((TextView) convertView.findViewById(R.id.mainName)).setText(n.getTitle());
                String s ="";
                if(n.getTags()!=null && n.getTags().length>0) {
                    s=n.getTags()[0];
                    for(int i = 1 ; i < n.getTags().length; i++){
                        s+=","+n.getTags()[i];
                    }

                }
                ((TextView) convertView.findViewById(R.id.descrption)).setText(s);
                return convertView;
            }
        });

    }

    public void addNotices(List<Notice> n){
        notices.addAll(n);
        ((BaseAdapter)this.getAdapter()).notifyDataSetChanged();
    }

    public  int getNumberOfNotices(){
        if(notices == null) return 0;
        return notices.size();
    }



}
