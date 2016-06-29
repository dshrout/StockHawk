package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by DShrout on 6/24/2016.
 **/
public class StockHistoryActivity extends AppCompatActivity {
    private static final String FRAGMENT_TAG = "StockHistoryFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        StockHistoryFragment fragment;

        if(savedInstanceState == null) {
            Bundle args = new Bundle();
            args.putString("symbol", getIntent().getStringExtra("symbol"));

            fragment = new StockHistoryFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction() .add(R.id.fragment_history_container, fragment, FRAGMENT_TAG).commit();
        }
    }
}
