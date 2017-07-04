package edu.oaklandcc.energydbkhanhdo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The EnergyDatabaseHelper class is a subclass of the SQLiteOpenHelper class, which creates the
 * database and provides methods to insert or update entries in the database.
 *
 * @author Khanh Do
 * @version December 8, 2016
 * CIS 2818
 */

public class EnergyDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "ENERGY";
    public static final int DB_VERSION = 1;
    public static final String TIME_COL = "time";
    public static final String RATING_COL = "rating";

    public EnergyDatabaseHelper (Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + DB_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, " +
                        "rating REAL);"
        );
        //insertEnergy(db, "2016-10-30 14:34:00", 2.5);
        //insertEnergy(db, "2016-11-02 12:00:00", 1.0 );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertEnergy(SQLiteDatabase db, String time, double rating) {
        ContentValues values = new ContentValues();
        values.put(TIME_COL, time);
        values.put(RATING_COL, rating);
        db.insert(DB_NAME, null, values);
        db.close();
    }

    public void updateEnergy(SQLiteDatabase db, Long id, String time, double rating) {
        ContentValues values = new ContentValues();
        values.put(TIME_COL, time);
        values.put(RATING_COL, rating);
        db.update(DB_NAME, values, "_id = ?", new String[] {String.valueOf(id)});
        db.close();
    }
}
