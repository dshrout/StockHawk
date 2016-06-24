package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by DShrout on 6/24/2016.
 **/
public class StockHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        if(savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString("symbol", getIntent().getDataString());

            StockHistoryFragment fragment = new StockHistoryFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_history_container, fragment)
                    .commit();
        }
    }
}
