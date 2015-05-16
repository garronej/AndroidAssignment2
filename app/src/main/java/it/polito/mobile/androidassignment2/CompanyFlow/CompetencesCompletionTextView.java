package it.polito.mobile.androidassignment2.CompanyFlow;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import java.util.List;

import it.polito.mobile.androidassignment2.R;

/**
 * Created by mark9 on 16/05/15.
 */
public class CompetencesCompletionTextView extends TokenCompleteTextView {

    public CompetencesCompletionTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }
    public CompetencesCompletionTextView(Context context) {
        super(context);
        setup();
    }

    public CompetencesCompletionTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        setup();

    }


    private void setup(){
        this.setPrefix(getResources().getString(R.string.studentKeyword) + ": ");
        this.setTextColor(this.getHintTextColors());
    }

    @Override
    protected View getViewForObject(Object o) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.competence_token, (ViewGroup)CompetencesCompletionTextView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(o.toString());

        return view;
    }

    @Override
    protected Object defaultObject(String s) {
        return "";
    }
}
