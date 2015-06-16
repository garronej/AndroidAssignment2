package it.polito.mobile.laboratory3;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by mark9 on 16/05/15.
 */
public class TagsCompletionTextView extends TokenCompleteTextView {

    public TagsCompletionTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }
    public TagsCompletionTextView(Context context) {
        super(context);
        setup();
    }

    public TagsCompletionTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        setup();

    }


    private void setup(){
        char[] splitChar = {',', ';'};
        this.setSplitChar(splitChar);
        this.setTextColor(this.getHintTextColors());
    }

    @Override
    protected View getViewForObject(Object o) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.tag_token, (ViewGroup)TagsCompletionTextView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(o.toString());

        return view;
    }

    @Override
    protected Object defaultObject(String s) {
        return s;
    }
}
