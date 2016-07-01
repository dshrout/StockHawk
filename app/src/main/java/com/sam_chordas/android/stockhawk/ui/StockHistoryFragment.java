package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    private Handler mHandler;
    private final int NO_STEP = 1;
    private final int HIGH_RANGE = 20;
    private final int MEDIUM_RANGE = 10;
    private final int HIGH_NTH = 3;
    private final int LOW_NTH = 1;

    // initialize these to the opposite end of the spectrum, respectively
    private int mMinPrice = Integer.MAX_VALUE;
    private int mMaxPrice = Integer.MIN_VALUE;

    private final OkHttpClient mHttpClient = new OkHttpClient();
    private final String URL_BASE = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    private final String URL_QUERY_STRING = "/chartdata;type=quote;range=1m/json";

    private String mStockSymbol;
    private LineSet mLineSet = new LineSet();

    private LineChartView mLineChartView;
    private TextView mStockNameView;
    private TextView mStockSymbolView;
    private TextView mCurrencyView;
    private TextView mLastCloseView;

    private String mStockName;
    private String mCurrency;
    private String mLastClose;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mStockSymbol = getArguments().getString(StockHistoryActivity.STOCK_SYMBOL);
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
        mHandler = new Handler(Looper.getMainLooper());
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
            boolean limitLabels = seriesArray.length() >= 10;
            for (int i = 0; i < seriesArray.length(); ++i) {
                JSONObject series = seriesArray.getJSONObject(i);
                float close = Float.parseFloat(df.format((float)series.getDouble("close")));
                String label = df.format(close);
                if (limitLabels) {
                    label = (i % 3 != 0) ? "" : label;
                }
                Point point = new Point(label, close);
                mLineSet.addPoint(point);
                mMinPrice = (int)Math.min(mMinPrice, close);
                mMaxPrice = (int)Math.max(mMaxPrice, close);
            }

            // move min and max price by one to give the graph some elbow room
            --mMinPrice;
            ++mMaxPrice;

            JSONObject meta = jsonObject.getJSONObject("meta");
            mStockName = meta.getString("Company-Name");
            mCurrency = meta.getString("currency");
            mLastClose = meta.getString("previous_close_price");

            renderData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void renderData() {
        if (mLineSet.size() > 0) {
            int priceRange = mMaxPrice - mMinPrice;
            int nth = priceRange >= HIGH_RANGE ? HIGH_NTH : LOW_NTH;
            int step = priceRange <= MEDIUM_RANGE ? NO_STEP : findNthLowestFactor(priceRange, nth);

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
                    .setAxisBorderValues(mMinPrice, mMaxPrice, step)
                    .addData(mLineSet);

            Animation anim = new Animation();
            mLineChartView.show(anim);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mStockNameView.setText(mStockName);
                    mStockSymbolView.setText(mStockSymbol);
                    mCurrencyView.setText(mCurrency);
                    mLastCloseView.setText(mLastClose);
                }
            });
        }
    }

    // findNthLowestFactor finds the factors of 'range' (excluding 1) and returns the 'nth' factor
    // if there are not 'nth' number of factors, it returns the highest factor it could find (excluding 'range')
    // if all else fails, it returns 1 (NO_STEP)
    private int findNthLowestFactor(int range, int nth)
    {
        int prevFactor = NO_STEP;
        int factorCount = 0;
        if(range <= NO_STEP)  return NO_STEP;
        for(int i = 2; i < range; ++i) {
            if (range % i == 0) {
                if (++factorCount == nth)
                    return i;
                else
                    prevFactor = i;
            }
        }
        return prevFactor;
    }
}
