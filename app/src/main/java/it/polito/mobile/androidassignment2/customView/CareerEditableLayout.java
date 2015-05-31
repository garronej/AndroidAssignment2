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
public class CareerEditableLayout extends LinearLayout {


    private TextView carMark;
    private CheckBox laudeCb;
    private TextView carDate;
    private AutoCompleteTextView carTitle;

    public CareerEditableLayout(Context context) {
        super(context);
        init();
    }

    public CareerEditableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CareerEditableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.career_layout_editable, this);
        carTitle = (AutoCompleteTextView) this.findViewById(R.id.career_title);
        carMark = (TextView) this.findViewById(R.id.career_mark);
        laudeCb = (CheckBox) this.findViewById(R.id.laude_checkbox);
        carMark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (carMark.getText().toString().equals("110")) {
                        laudeCb.setEnabled(true);
                    } else {
                        laudeCb.setEnabled(false);
                        laudeCb.setChecked(false);
                    }
                }
            }
        );
        carDate = ((TextView) this.findViewById(R.id.career_date));
        carDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] ss = carDate.getText().toString().split("-");
                Calendar cal = Calendar.getInstance();
                int aaaa = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH);
                int gg = cal.get(Calendar.DAY_OF_MONTH);

                if (ss.length == 3) {
                    aaaa = Integer.parseInt(ss[0]);
                    mm = Integer.parseInt(ss[1]) - 1;
                    gg = Integer.parseInt(ss[2]);
                }

                DatePickerDialog dp = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                carDate.setText(String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                            }
                        }, aaaa, mm, gg);

                dp.show();
            }

        });
        this.findViewById(R.id.delete_career).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup p = ((ViewGroup) CareerEditableLayout.this.getParent());
                int index = p.indexOfChild(CareerEditableLayout.this) + 1;
                if (p.getChildCount() > index) {
                    if (!(p.getChildAt(index) instanceof CareerEditableLayout)) {
                        p.removeViewAt(index);
                    }
                } else {
                    index = index - 2;
                    if (index > 0) {
                        if (!(p.getChildAt(index) instanceof CareerEditableLayout)) {
                            p.removeViewAt(index);
                        }
                    }
                }
                p.removeView(CareerEditableLayout.this);
            }
        });
    }


    public String getGraduationMark(){
        return carMark.getText().toString();
    }
    public String getCareerTitle(){
        return carTitle.getText().toString();
    }
    public boolean isSetLaude(){
        return laudeCb.isChecked();
    }
    public String getDate(){
        return carDate.getText().toString();
    }

    public void setCareerTitleAdapter(ArrayAdapter<String> careers){
        carTitle.setAdapter(careers);
    }

    public void initializeValues(Career c){
        carDate.setText(c.getDate());
        carTitle.setText(c.getCareer());
        carMark.setText(c.getMark() == 111 ? "110" : c.getFormattedMark());

        if(c.getMark()==111){
            laudeCb.setChecked(true);
        }
        if(carMark.getText().toString().equals("110")){
            laudeCb.setEnabled(true);
        }




    }


}
