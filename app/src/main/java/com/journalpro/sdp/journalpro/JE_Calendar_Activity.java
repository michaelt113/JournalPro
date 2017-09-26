package com.journalpro.sdp.journalpro;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import android.widget.CalendarView;

public class JE_Calendar_Activity extends JE_Base_Activity {


    boolean isSearchDate;
    CalendarPickerView calendarView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.je_calendar_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.je_calendar_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);

        Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendarView = findViewById(R.id.je_calendar_view);// get the reference of CalendarView
        Date today = new Date();

        Intent intent = getIntent();
        String intentValue = intent.getStringExtra("SearchDate");
        if (intentValue != null) {
            isSearchDate = true;
            calendarView.init(lastYear.getTime(), nextYear.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE);
        } else {
            isSearchDate = false;
            calendarView.init(lastYear.getTime(), nextYear.getTime());
        }
    }

    private Date getDateWithYearAndMonthForDay(int year, int month, int day) {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    private List<Date> getHighlightedDaysForMonth(int... month) {
        List<Date> dateList = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        for (int i = 0; i < month.length; i++) {
            for (int j = 0; j < 25; j++) {
                dateList.add(getDateWithYearAndMonthForDay(c.get(Calendar.YEAR), i, j));
            }
        }

        return dateList;
    }

    private static class StringDate {
        String dayOfTheWeek;
        String dayOfMonth;
        String monthString;
        String month;
        String year;

        StringDate(Date date) {
            dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
            dayOfMonth = (String) DateFormat.format("dd", date); // 20
            monthString = (String) DateFormat.format("MMM", date); // Jun
            month = (String) DateFormat.format("MM", date); // 06
            year = (String) DateFormat.format("yyyy", date); // 2013
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_ok) {
            Intent intent = new Intent();
            if (isSearchDate) {
                List<Date> selectedDates = calendarView.getSelectedDates();
                ArrayList<String> stringDates = new ArrayList<String>();
                for (Date index : selectedDates) {
                    StringDate time = new StringDate(index);
                    String stringTime = (time.dayOfMonth + "_" + time.month + "_" + time.year);
                    stringDates.add(stringTime);
                }
                intent.putStringArrayListExtra("dates", stringDates);
            } else {
                Date selectedDate = calendarView.getSelectedDate();
                StringDate time = new StringDate(selectedDate);
                String stringTime = (time.dayOfMonth + "_" + time.month + "_" + time.year);
                intent.putExtra("editTextValue", stringTime);
                Toast.makeText(JE_Calendar_Activity.this, "" + stringTime, Toast.LENGTH_LONG).show();
            }

            setResult(RESULT_OK, intent);
            finish();
        } else {
            //go back to je_main_home activity and give up the new page
            onBackPressed();
        }
        return true;
    }
}
