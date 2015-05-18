package it.polito.mobile.androidassignment2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by mark9 on 16/05/15.
 */
public class LinksCompletionTextView extends TokenCompleteTextView {

    public LinksCompletionTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }
    public LinksCompletionTextView(Context context) {
        super(context);
        setup();
    }

    public LinksCompletionTextView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        setup();

    }

    @Override
    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);
        Log.d("tg", "focus " + hasFocus);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d("tg", "focus window " + hasWindowFocus);
    }

    private void setup(){
        char[] splitChar = {',', ';', ' '};
        this.setSplitChar(splitChar);
        this.setTextColor(this.getHintTextColors());
    }

    @Override
    protected View getViewForObject(Object o) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.link_token, (ViewGroup)LinksCompletionTextView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(o.toString());

        return view;
    }

    @Override
    protected Object defaultObject(String s) {
        return s;
    }
}
