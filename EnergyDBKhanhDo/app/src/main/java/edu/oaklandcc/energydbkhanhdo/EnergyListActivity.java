package edu.oaklandcc.energydbkhanhdo;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * The EnergyListActivity class is an application the allows a user to create, edit, delete and
 * summarize their energy levels.  The application uses a SQLite database to manage the data.
 *
 * @author Khanh Do
 * @version December 8, 2016
 * CIS 2818
 */

public class EnergyListActivity extends ListActivity {
    private ListView listView;
    private SQLiteDatabase db;
    private Cursor cursor;
    protected static boolean toEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = getListView();
        registerForContextMenu(listView);
        try {
            EnergyDatabaseHelper helper = new EnergyDatabaseHelper(this);
            db = helper.getReadableDatabase();

            cursor = db.query(
                    EnergyDatabaseHelper.DB_NAME,
                    new String[]{"_id", "rating", "time"},
                    null, null, null, null, null
            );
            CursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2,
                    cursor,
                    new String[] {"rating", "time"},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0);
            listView.setAdapter(listAdapter);
        }catch (SQLiteException e){
            Toast t = Toast.makeText(this, "Database Unavailable ", Toast.LENGTH_LONG);
            t.show();
        }
    }

    public void onRestart() {
        super.onRestart();
        try {
            EnergyDatabaseHelper helperRestart = new EnergyDatabaseHelper(this);
            db = helperRestart.getReadableDatabase();

            Cursor newCursor = db.query(
                    EnergyDatabaseHelper.DB_NAME,
                    new String[]{"_id", "rating", "time"},
                    null, null, null, null, null
            );
            CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
            adapter.changeCursor(newCursor);
            cursor = newCursor;
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this, "Database Unavailable ", Toast.LENGTH_LONG);
            toast.show();
        }
        toEdit = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_summary:
                int numEntries = 0;
                double average = 0.0;

                Cursor cursorCount = db.query(EnergyDatabaseHelper.DB_NAME,
                        new String[] {"COUNT(_id) AS count"},
                        null, null, null, null, null);
                if (cursorCount.moveToFirst()) {
                    numEntries = cursorCount.getInt(0);
                }
                cursorCount.close();
                Cursor cursorAverage = db.query(EnergyDatabaseHelper.DB_NAME,
                        new String[] {"AVG(rating) AS avg"},
                        null, null, null, null, null);
                if (cursorAverage.moveToFirst() && numEntries != 0) {
                    average = cursorAverage.getDouble(0);
                }
                cursorAverage.close();
                Intent intentSummary = new Intent(this, SummaryActivity.class);
                intentSummary.putExtra("number of entries", numEntries);
                intentSummary.putExtra("average energy", average);
                startActivity(intentSummary);
                return true;

            case R.id.item_delete_all:
                try {
                    EnergyDatabaseHelper helper = new EnergyDatabaseHelper(this);
                    db = helper.getWritableDatabase();
                    db.delete(EnergyDatabaseHelper.DB_NAME, null, null);
                    Cursor newCursor = db.query(
                            EnergyDatabaseHelper.DB_NAME,
                            new String[]{"_id", "rating", "time"},
                            null, null, null, null, null
                    );
                    CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
                    adapter.changeCursor(newCursor);
                    cursor = newCursor;
                } catch(SQLiteException e) {
                    Toast t = Toast.makeText(this, "Database Unavailable ", Toast.LENGTH_LONG);
                    t.show();
                }
                return true;
            case R.id.action_create:
                toEdit = false;
                Intent intentCreate = new Intent(this, EnergyDetailsActivity.class);
                startActivity(intentCreate);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.energy_list_activity_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = info.id;

        switch (item.getItemId()) {
            case R.id.delete_rating:
                try {
                    EnergyDatabaseHelper helper = new EnergyDatabaseHelper(this);
                    db = helper.getWritableDatabase();
                    db.delete(EnergyDatabaseHelper.DB_NAME, "_id = ?", new String[] { String.valueOf(id)});
                    Cursor newCursor = db.query(
                            EnergyDatabaseHelper.DB_NAME,
                            new String[]{"_id", "rating", "time"},
                            null, null, null, null, null
                    );
                    CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
                    adapter.changeCursor(newCursor);
                    cursor = newCursor;
                } catch(SQLiteException e) {
                    Toast s = Toast.makeText(this, "Database Unavailable ", Toast.LENGTH_LONG);
                    s.show();
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        toEdit = true;
        Intent intentEdit = new Intent(this, EnergyDetailsActivity.class);
        intentEdit.putExtra(EnergyDetailsActivity.EXTRA_ITEM_NUM, id);
        startActivity(intentEdit);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}