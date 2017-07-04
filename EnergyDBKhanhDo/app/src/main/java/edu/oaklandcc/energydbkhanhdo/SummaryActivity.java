package edu.oaklandcc.energydbkhanhdo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The SummaryActivity class is a class that presents the user with a UI containing a summary of the
 * number of entries and their average energy level.
 *
 * @author Khanh Do
 * @version December 8, 2016
 * CIS 2818
 */

public class SummaryActivity extends Activity {
    private int numberEntries;
    private double energyAve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        numberEntries = intent.getIntExtra("number of entries", numberEntries);
        energyAve = intent.getDoubleExtra("average energy", energyAve);

        TextView textEntries = (TextView) findViewById(R.id.textEntries);
        textEntries.setText(String.format("Number of Entries: %d", numberEntries));

        TextView textEnergyAverage = (TextView) findViewById(R.id.textEnergyAverage);
        textEnergyAverage.setText( String.format("Energy Average: %.1f", energyAve) );
    }
}
