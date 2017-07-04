package edu.oaklandcc.energydbkhanhdo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * The EnergyDetailsActivity class is a class that allows the user to save new or edit the time and/or
 * the energy rating of existing entries.
 *
 * @author Khanh Do
 * @version December 8, 2016
 * CIS 2818
 */

public class EnergyDetailsActivity extends Activity implements TimePickerDialog.OnTimeSetListener{
    public static final float DEFAULT_RATING = 5;
    public static final String EXTRA_ITEM_NUM = "itemNum";
    private SQLiteDatabase db;
    private Cursor cursor;
    Calendar calCurrent;
    Button buttonTime;
    RatingBar ratingEnergy;
    String dateToEdit;
    private int oldHour;
    private int oldMinutes;
    private long id;
    private boolean timeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_details);
        ratingEnergy = (RatingBar) findViewById(R.id.ratingEnergy);
        ratingEnergy.setMax(7);
        ratingEnergy.setNumStars(7);
        ratingEnergy.setStepSize(0.5F);
        buttonTime = (Button) findViewById(R.id.buttonTime);

        if (!EnergyListActivity.toEdit) {
            ratingEnergy.setRating(DEFAULT_RATING);
            calCurrent = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("h:mm a");
            String date = df.format(calCurrent.getTime());  // String date = df.format(Calendar.getInstance().getTime());
            buttonTime.setText(date);
        } else {
            Intent intentEdit = getIntent();
            long itemID = (Long) intentEdit.getExtras().get(EXTRA_ITEM_NUM);
            calCurrent = Calendar.getInstance();
            try {
                EnergyDatabaseHelper helperEdit = new EnergyDatabaseHelper(this);
                db = helperEdit.getWritableDatabase();
                cursor = db.query(
                        EnergyDatabaseHelper.DB_NAME,
                        new String[]{"time", "rating"},
                        "_id = ?",
                        new String [] {String.valueOf(itemID)},
                        null, null, null
                );

                if (cursor.moveToFirst()) {
                    dateToEdit = cursor.getString(0);
                    Float ratingToEdit = cursor.getFloat(1);

                    StringTokenizer spaceTokenizer = new StringTokenizer(dateToEdit);
                    String oldDate = spaceTokenizer.nextToken();
                    String oldTime = spaceTokenizer.nextToken();
                    StringTokenizer colonTokenizer = new StringTokenizer(oldTime, ":");
                    oldHour = Integer.parseInt(colonTokenizer.nextToken());
                    oldMinutes = Integer.parseInt(colonTokenizer.nextToken());

                    String amOrPm = " AM";
                    String mm_precede = "";
                    String timeFormat;
                    if (oldHour >= 12) {
                        amOrPm = " PM";
                        if (oldHour >= 13 && oldHour < 24) {
                            oldHour -= 12;
                        } else {
                            oldHour = 12;
                        }
                    } else if (oldHour == 0) {
                        oldHour = 12;
                    }
                    if (oldMinutes < 10)
                        mm_precede = "0";
                    timeFormat = oldHour + ":" + mm_precede + oldMinutes + amOrPm;
                    ratingEnergy.setRating(ratingToEdit);
                    buttonTime.setText(timeFormat);
                }
                cursor.close();
                db.close();
            } catch(SQLiteException e) {
                Toast t = Toast.makeText(this, "Database Unavailable ", Toast.LENGTH_LONG);
                t.show();
            }
        }
    }

    public void onClickTime(View view) {
        if (!EnergyListActivity.toEdit) {
            TimePickerDialog tp1 = new TimePickerDialog(this, this, calCurrent.get(Calendar.HOUR_OF_DAY), calCurrent.get(Calendar.MINUTE), false);
            tp1.show();
        } else {
            TimePickerDialog tp2 = new TimePickerDialog(this, this, oldHour, oldMinutes, false);
            tp2.show();
            timeChanged = true;
        }
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calCurrent.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calCurrent.set(Calendar.MINUTE, minute);

        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(calCurrent.getTime());
        buttonTime.setText(date);
    }

    public void onClickNow(View view) {
        timeChanged = true;
        calCurrent = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("h:mm a");
        String date = df.format(calCurrent.getTime());
        buttonTime.setText(date);
    }

    public void onClickSave(View view) {
        float rating = ratingEnergy.getRating();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateAndTime = df.format(calCurrent.getTime());

        try {
            EnergyDatabaseHelper helper = new EnergyDatabaseHelper(this);
            db = helper.getWritableDatabase();
            if (!EnergyListActivity.toEdit){
                helper.insertEnergy(db, dateAndTime, rating);
            } else {
                Intent intentEdit = getIntent();
                id = intentEdit.getLongExtra(EXTRA_ITEM_NUM, id);
                if (timeChanged) {
                    helper.updateEnergy(db, id, dateAndTime, rating);
                } else {
                    helper.updateEnergy(db, id, dateToEdit, rating);
                }
            }
        } catch(SQLiteException e) {
            Toast t = Toast.makeText(this, "Database Unavailable ", Toast.LENGTH_LONG);
            t.show();
        }
        db.close();
    }
}
