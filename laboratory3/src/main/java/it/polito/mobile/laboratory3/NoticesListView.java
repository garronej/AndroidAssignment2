package it.polito.mobile.laboratory3;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import it.polito.mobile.laboratory3.R;

/**
 * TODO: document your custom view class.
 */
public class NoticesListView extends ListView {

    private List<Notice> notices;

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
                if(n.getTags()!=null && n.getTags().length>0) {
                    ((TextView) convertView.findViewById(R.id.descrption)).setText(Arrays.toString(n.getTags()));

                }
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
