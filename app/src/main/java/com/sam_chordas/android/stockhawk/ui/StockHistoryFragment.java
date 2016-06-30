package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by DShrout on 6/24/2016.
 **/
public class StockHistoryFragment extends Fragment {


    public StockHistoryFragment() {
    }

    public static final String STOCK_SYMBOL = "symbol";
    private static final int STOCK_DETAIL_LOADER_ID = 1;
    private float minimumPrice = Float.MAX_VALUE;
    private float maximumPrice = Float.MIN_VALUE;

    private String stockSymbol;
    private LineChartView lineChartView;
    private LineSet mLineSet = new LineSet();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_line_graph, container, false);
        if (getArguments() != null) {
            stockSymbol = getArguments().getString(STOCK_SYMBOL);
            getActivity().setTitle(stockSymbol);
            lineChartView = (LineChartView) view.findViewById(R.id.linechart);
            renderData();
        }

        return view;
    }

    private void initData() {
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        for (int i = 0; i < 12; ++i) {
            float price = Float.parseFloat(df.format(random.nextFloat() + random.nextInt(4)));
            if (random.nextInt(100) % 3 == 0) {
                price *= -1;
            }
            String label = df.format(price);

            mLineSet.addPoint(label, price);
            minimumPrice = Math.min(minimumPrice, price);
            maximumPrice = Math.max(maximumPrice, price);
        }
    }

    public void renderData() {
        mLineSet.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});


        lineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(true)
                .setYAxis(true)
                .setAxisBorderValues((int)(minimumPrice - 1), (int)(maximumPrice + 1))
                .addData(mLineSet);

        Animation anim = new Animation();
        lineChartView.show(anim);
    }
}