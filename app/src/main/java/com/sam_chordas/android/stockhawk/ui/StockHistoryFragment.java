package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by DShrout on 6/24/2016.
 **/
public class StockHistoryFragment extends Fragment {


    public StockHistoryFragment() {
    }

    public static final String STOCK_SYMBOL = "symbol";

    // initialize these to the opposite end of the spectrum, respectively
    private float mMinPrice = Float.MAX_VALUE;
    private float mMaxPrice = Float.MIN_VALUE;

    private final OkHttpClient mHttpClient = new OkHttpClient();
    final String URL_BASE = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    final String URL_QUERY_STRING = "/chartdata;type=quote;range=1m/json";

    private String mStockSymbol;
    private LineSet mLineSet = new LineSet();

    private LineChartView mLineChartView;
    private TextView mStockNameView;
    private TextView mStockSymbolView;
    private TextView mCurrencyView;
    private TextView mLastCloseView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mStockSymbol = getArguments().getString(STOCK_SYMBOL);
        try {
            getHistoricalData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_line_graph, container, false);
        if (getArguments() != null) {
            getActivity().setTitle(mStockSymbol);
            mLineChartView = (LineChartView) view.findViewById(R.id.linechart);
            mStockNameView = ((TextView) view.findViewById(R.id.stock_name));
            mStockSymbolView = ((TextView) view.findViewById(R.id.stock_symbol));
            mCurrencyView = ((TextView) view.findViewById(R.id.currency));
            mLastCloseView = ((TextView) view.findViewById(R.id.previous_close));
            renderData();
        }

        return view;
    }

    private void getHistoricalData() throws IOException {
        String url = URL_BASE + mStockSymbol + URL_QUERY_STRING;
        Request request = new Request.Builder().url(url).build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                parseData(response.body().string());
            }
        });
    }

    private void parseData(String jsonData) {
        // strip off the outer jsonp function
        String json = jsonData.substring(jsonData.indexOf("(") + 1, jsonData.lastIndexOf(")"));
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray seriesArray = jsonObject.getJSONArray("series");
            DecimalFormat df = new DecimalFormat("#.###");
            df.setRoundingMode(RoundingMode.CEILING);

            mLineSet = new LineSet();
            for (int i = 0; i < seriesArray.length(); ++i) {
                JSONObject series = seriesArray.getJSONObject(i);
                float close = Float.parseFloat(df.format((float)series.getDouble("close")));
                Point point = new Point(df.format(close), close);
                mLineSet.addPoint(point);
                mMinPrice = Math.min(mMinPrice, close);
                mMaxPrice = Math.max(mMaxPrice, close);
            }

            JSONObject meta = jsonObject.getJSONObject("meta");
            mStockNameView.setText(meta.getString("Company-Name"));
            mStockSymbolView.setText(meta.getString("ticker"));
            mCurrencyView.setText(meta.getString("currency"));
            mLastCloseView.setText(meta.getString("previous_close_price"));

            renderData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void renderData() {
        if (mLineSet.size() > 0) {
            mLineSet.setColor(Color.parseColor("#758cbb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#758cbb"))
                    .setThickness(4)
                    .setDashed(new float[]{10f, 10f});


            mLineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                    .setYLabels(AxisController.LabelPosition.OUTSIDE)
                    .setXLabels(AxisController.LabelPosition.OUTSIDE)
                    .setLabelsColor(Color.parseColor("#6a84c3"))
                    .setXAxis(true)
                    .setYAxis(true)
                    .setAxisBorderValues((int)(mMinPrice - 1), (int)(mMaxPrice + 1))
                    .addData(mLineSet);

            Animation anim = new Animation();
            mLineChartView.show(anim);
        }
    }
}

//    Random random = new Random();
//    DecimalFormat df = new DecimalFormat("#.###");
//    df.setRoundingMode(RoundingMode.CEILING);
//
//        for (int i = 0; i < 12; ++i) {
//        float price = Float.parseFloat(df.format(random.nextFloat() + random.nextInt(4)));
//        if (random.nextInt(100) % 3 == 0) {
//        price *= -1;
//        }
//        String label = df.format(price);
//
//        mLineSet.addPoint(label, price);
//        mMinPrice = Math.min(mMinPrice, price);
//        mMaxPrice = Math.max(mMaxPrice, price);
//        }
