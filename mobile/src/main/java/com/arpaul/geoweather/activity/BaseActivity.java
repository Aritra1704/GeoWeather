package com.arpaul.geoweather.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.internal.util.Predicate;
import com.arpaul.customalertlibrary.dialogs.CustomDialog;
import com.arpaul.customalertlibrary.popups.statingDialog.CustomPopupType;
import com.arpaul.customalertlibrary.popups.statingDialog.PopupListener;
import com.arpaul.geoweather.R;
import com.arpaul.geoweather.common.AppConstants;
import com.arpaul.utilitieslib.CalendarUtils;
import com.arpaul.utilitieslib.ColorUtils;
import com.arpaul.utilitieslib.UnCaughtException;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Aritra on 15-08-2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements PopupListener {

    public LayoutInflater baseInflater;
    public LinearLayout llBody;
//    private CustomPopup cPopup;
    private CustomDialog cDialog;
    public DecimalFormat degreeFormat;
    public Typeface tfMyriadProRegular;

    public int text_pattern = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(BaseActivity.this,"aritra1704@gmail.com",getString(R.string.app_name)));
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_base);

        initialiseBaseControls();

        bindBaseControls();

        initialize(savedInstanceState);
    }

    public abstract void initialize(Bundle savedInstanceState);

    private void bindBaseControls(){
        degreeFormat = new DecimalFormat("##");
        degreeFormat.setRoundingMode(RoundingMode.CEILING);
        degreeFormat.setMinimumFractionDigits(0);
        degreeFormat.setMaximumFractionDigits(0);


    }

    @Override
    protected void onResume() {
        super.onResume();

        setupBaseView();

        if(tfMyriadProRegular == null)
            createTypeFace();
    }

    private void setupBaseView(){
        String currentTime = CalendarUtils.getDateinPattern(CalendarUtils.TIME_FORMAT);
        String morningTime = "10:00 am";
        String noonTime = "4:00 pm";
        String eveningTime = "8:00 pm";
        String nightTime = "11:59 pm";
        String dawnTime = "4:00 am";

        if(CalendarUtils.getDiffBtwDatesPattern(dawnTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
            text_pattern = AppConstants.TEXT_PATTERN_LIGHT;
            llBody.setBackgroundColor(ColorUtils.getColor(BaseActivity.this, R.color.colorNight));
        } else if(CalendarUtils.getDiffBtwDatesPattern(morningTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
            text_pattern = AppConstants.TEXT_PATTERN_DARK;
            llBody.setBackgroundColor(ColorUtils.getColor(BaseActivity.this, R.color.colorMorning));
        } else if(CalendarUtils.getDiffBtwDatesPattern(noonTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
            text_pattern = AppConstants.TEXT_PATTERN_DARK;
            llBody.setBackgroundColor(ColorUtils.getColor(BaseActivity.this, R.color.colorNoon));
        } else if(CalendarUtils.getDiffBtwDatesPattern(eveningTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
            text_pattern = AppConstants.TEXT_PATTERN_LIGHT;
            llBody.setBackgroundColor(ColorUtils.getColor(BaseActivity.this, R.color.colorEvening));
        } else if(CalendarUtils.getDiffBtwDatesPattern(nightTime, currentTime, CalendarUtils.DIFF_TYPE.TYPE_MINUTE, CalendarUtils.TIME_FORMAT) < 0){
            text_pattern = AppConstants.TEXT_PATTERN_LIGHT;
            llBody.setBackgroundColor(ColorUtils.getColor(BaseActivity.this, R.color.colorNight));
        }
    }

    /**
     * Shows Dialog with user defined buttons.
     * @param title
     * @param message
     * @param okButton
     * @param noButton
     * @param from
     * @param isCancelable
     */
    public void showCustomDialog(final String title, final String message, final String okButton, final String noButton, final String from, boolean isCancelable){
        runOnUiThread(new RunShowDialog(title,message,okButton,noButton,from, isCancelable));
    }

    public void showCustomDialog(final String title, final String message, final String okButton, final String noButton, final String from, CustomPopupType dislogType, boolean isCancelable){
        runOnUiThread(new RunShowDialog(title,message,okButton,noButton,from, dislogType, isCancelable));
    }

    public void hideCustomDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cDialog != null && cDialog.isShowing())
                    cDialog.dismiss();
            }
        });
    }

    class RunShowDialog implements Runnable {
        private String strTitle;// FarmName of the materialDialog
        private String strMessage;// Message to be shown in materialDialog
        private String firstBtnName;
        private String secondBtnName;
        private String from;
        private String params;
        private boolean isCancelable=false;
        private CustomPopupType dislogType = CustomPopupType.DIALOG_NORMAL;
        private int isNormal = 0;
        public RunShowDialog(String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable)
        {
            this.strTitle 		= strTitle;
            this.strMessage 	= strMessage;
            this.firstBtnName 	= firstBtnName;
            this.secondBtnName	= secondBtnName;
            this.isCancelable 	= isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";

            isNormal = 0;
        }

        public RunShowDialog(String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, CustomPopupType dislogType, boolean isCancelable)
        {
            this.strTitle 		= strTitle;
            this.strMessage 	= strMessage;
            this.firstBtnName 	= firstBtnName;
            this.secondBtnName	= secondBtnName;
            this.dislogType     = dislogType;
            this.isCancelable 	= isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";

            isNormal = 1;
        }

        @Override
        public void run() {
            if(isNormal > 0)
                showNotNormal();
            else
                showNormal();
        }

        private void showNotNormal(){
            try{
                if (cDialog != null && cDialog.isShowing())
                    cDialog.dismiss();

                cDialog = new CustomDialog(BaseActivity.this, BaseActivity.this,strTitle,strMessage,
                        firstBtnName, secondBtnName, from, dislogType);

                cDialog.show();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        private void showNormal(){
            try{
                if (cDialog != null && cDialog.isShowing())
                    cDialog.dismiss();

                cDialog = new CustomDialog(BaseActivity.this, BaseActivity.this,strTitle,strMessage,
                        firstBtnName, secondBtnName, from, CustomPopupType.DIALOG_NORMAL);

                cDialog.show();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnButtonYesClick(String from) {
        dialogYesClick(from);
    }

    @Override
    public void OnButtonNoClick(String from) {
        dialogNoClick(from);
    }

    public void dialogYesClick(String from) {

    }

    public void dialogNoClick(String from) {
        if(from.equalsIgnoreCase("")){

        }
    }

    public static <T> Collection<T> filter(Collection<T> col, Predicate<T> predicate) {

        Collection<T> result = new ArrayList<T>();
        if(col!=null)
        {
            for (T element : col) {
                if (predicate.apply(element)) {
                    result.add(element);
                }
            }
        }
        return result;
    }

    public static ViewGroup getParentView(View v) {
        ViewGroup vg = null;

        if(v != null)
            vg = (ViewGroup) v.getRootView();

        return vg;
    }

    public static void applyTypeface(ViewGroup v, Typeface f, int style) {
        if(v != null) {
            int vgCount = v.getChildCount();
            for(int i=0;i<vgCount;i++) {
                if(v.getChildAt(i) == null) continue;
                if(v.getChildAt(i) instanceof ViewGroup)
                    applyTypeface((ViewGroup)v.getChildAt(i), f, style);
                else {
                    View view = v.getChildAt(i);
                    if(view instanceof TextView)
                        ((TextView)(view)).setTypeface(f, style);
                    else if(view instanceof EditText)
                        ((EditText)(view)).setTypeface(f, style);
                    else if(view instanceof Button)
                        ((Button)(view)).setTypeface(f, style);
                }
            }
        }
    }

    private void createTypeFace(){
        tfMyriadProRegular   = Typeface.createFromAsset(this.getAssets(),"fonts/Myriad Pro Regular.ttf");
    }

    private void initialiseBaseControls(){
        baseInflater            = 	this.getLayoutInflater();
        llBody                  =   (LinearLayout) findViewById(R.id.llBody);

        createTypeFace();
    }
}
