package com.hfad.deardairy.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.hfad.deardairy.Db.Models.DataModel;
import com.hfad.deardairy.Db.ViewModels.DataViewModel;
import com.hfad.deardairy.Db.WorkManager.DropboxRemoteDb;
import com.hfad.deardairy.Dropbox_access.DropboxActivity;
import com.hfad.deardairy.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class CalendarActivity extends DropboxActivity {

    MaterialCalendarView calendarView;
    private String monthTitle;
    private Calendar calendar;
    HashSet<Date> datesOfMonth = new HashSet<>();
    SharedPreferences preferences;
    Boolean isSynced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sync with Dropbox(download backup), if we already logged in
        preferences = getSharedPreferences("dropbox", MODE_PRIVATE);
            //download backup only once during lifecycle of CalendarActivity
        try {
            isSynced = savedInstanceState.getBoolean("isSynced", false);
        } catch (Exception e) {

        }
        Boolean dropboxSync = preferences.getBoolean("dropboxSync", false);
        if (dropboxSync && !isSynced) {
            DropboxRemoteDb.downloadDb();
        }
        //Get CalendarView
        calendarView = findViewById(R.id.calendarView);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        calendar = Calendar.getInstance();
        //add dates with data for this month
        int month = calendar.get(MONTH)+1;
        int year = calendar.get(YEAR);
        monthTitle = String.valueOf("%"+month+"-"+year);
        //save date for DataActivity
        int day = calendar.get(DAY_OF_MONTH);
        String currentDate = String.valueOf(day+"-"+month+"-"+year);
        preferences.edit().putString("date", currentDate).apply();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int month = date.getMonth()+1;
                int year = date.getYear();
                monthTitle = String.valueOf("%"+month+"-"+year);
                datesOfMonth = getDatasDateForMonth();
                setDatasDateForMonth(datesOfMonth);
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int month = date.getMonth()+1;
                String dateTitle = String.valueOf(date.getDay()+"-"+month+"-"+date.getYear());
                Intent intent = new Intent(widget.getContext(), DataActivity.class);
                intent.putExtra("date", (String) dateTitle);
                startActivity(intent);
                calendarView.setDateSelected(date, false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }



    public HashSet<Date> getDatasDateForMonth() {
        datesOfMonth.clear();
        //add current date to selected dates
        datesOfMonth.add(calendar.getTime());
        DataViewModel dataViewModel = new DataViewModel(getApplication());
        List<DataModel> dataModels = dataViewModel.getDatasForMonth(monthTitle);
        for(int i = 0; i < dataModels.size(); i++) {
            DataModel dataModel = dataModels.get(i);
            String dateString = dataModel.getDate();
            try {
                Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                datesOfMonth.add(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.v("dates", datesOfMonth.toString());
        return datesOfMonth;
    }

    public void setDatasDateForMonth(HashSet<Date> datesOfMonth) {
        for(Date thisDate : datesOfMonth) {
            calendarView.setDateSelected(thisDate, true);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isSynced", true);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        datesOfMonth = getDatasDateForMonth();
        setDatasDateForMonth(datesOfMonth);
    }

    //These methods are only for landscape orientation, where we use buttons instead of toolbar.
    public void onDropboxClick(View view) {
        syncWithDropboxButton();
    }

    public void onTitleButtonClick(View view) {
        startTitleSelectionActivity();
    }


}
