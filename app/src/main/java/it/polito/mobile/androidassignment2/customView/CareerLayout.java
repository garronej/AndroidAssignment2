package it.polito.mobile.androidassignment2.customView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import it.polito.mobile.androidassignment2.R;
import it.polito.mobile.androidassignment2.businessLogic.Career;

/**
 * Created by mark9 on 31/05/15.
 */
public class CareerLayout extends LinearLayout {


    private TextView carMark;
    private TextView carDate;
    private TextView carTitle;

    public CareerLayout(Context context) {
        super(context);
        init();
    }

    public CareerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CareerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.career_layout, this);
        carTitle = (TextView) this.findViewById(R.id.career_title);
        carMark = (TextView) this.findViewById(R.id.career_mark);
        carDate = ((TextView) this.findViewById(R.id.career_date));

    }




    public void initializeValues(Career c){
        carDate.setText(c.getDate());
        carTitle.setText(c.getCareer());
        carMark.setText(c.getFormattedMark());
    }


}
