package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.rest.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController.LabelPosition;

/**
 * Created by chevelle on 5/8/16.
 */
public class StockGraphFragment extends Fragment {

    // Stock Price Types
    private static String STOCK_OPEN_PRICE = "Open";
    private static String STOCK_CLOSE_PRICE = "Close";
    private static String STOCK_HIGH_PRICE = "High";
    private static String STOCK_LOW_PRICE = "Low";

    private static float LINE_THICKNESS = 1.5f;

    // YQL Base URL
    private static String YQL_URL = "https://query.yahooapis.com/v1/public/yql";

    /*
     * Stock History Query Format:
     *  0 - stock symbol
     *  1 - start date
     *  2 - end date
     */
    private static String HISTORY_SELECT_STMT =
            "select * from yahoo.finance.historicaldata where symbol=\"{0}\" and startDate=\"{1}\" and endDate=\"{2}\"";

    // Stock Time Frame
    private static int PAST_30_DAYS = -30;

    private SimpleDateFormat dateFormatter;

    public StockGraphFragment() {
        // Empty constructor required
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String historyDateFormat = getActivity().getString(R.string.history_date_format);
        dateFormatter = new SimpleDateFormat(historyDateFormat);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_line_graph, container, false);
    }

    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);

        Intent stockInfo = getActivity().getIntent();
        String stockSymbol = stockInfo.getStringExtra(Utils.STOCK_SYMBOL);

        new StockHistoryTask().execute(stockSymbol);
    }

    private void updateStockGraph(JSONObject stockHistory) {
        LineSet lineData;
        ArrayList<ChartSet> chartData = new ArrayList<ChartSet>();
        ChartView lineChart = (LineChartView)getActivity().findViewById(R.id.linechart);

        try {
            JSONObject query = stockHistory.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONArray quotes = results.getJSONArray("quote");

            lineData = getStockPrices(STOCK_OPEN_PRICE, R.color.open_price_color, quotes);
            chartData.add(lineData);

            lineData = getStockPrices(STOCK_CLOSE_PRICE, R.color.close_price_color, quotes);
            chartData.add(lineData);

            lineData = getStockPrices(STOCK_HIGH_PRICE, R.color.high_price_color, quotes);
            chartData.add(lineData);

            lineData = getStockPrices(STOCK_LOW_PRICE, R.color.low_price_color, quotes);
            chartData.add(lineData);
        }
        catch (Exception anyError) {
            System.out.println("Error: " + anyError.getMessage());
        }
        finally {
            if (chartData.size() > 0) {

                lineChart.setXAxis(false).setXLabels(LabelPosition.OUTSIDE)
                        .setYAxis(false).setYLabels(LabelPosition.OUTSIDE);

                lineChart.addData(chartData);
                lineChart.show();
            }
        }

    }

    private LineSet getStockPrices(String priceType, int priceColor, JSONArray quotes) throws Exception {
        JSONObject quote = null;
        LineSet prices = new LineSet()
                .setColor(priceColor)
                .setSmooth(true)
                .setThickness(LINE_THICKNESS)
                .setDotsColor(priceColor);

        for (int idx = 0; idx < quotes.length(); idx++) {
            quote = quotes.getJSONObject(idx);
            prices.addPoint(quote.getString("Date"),
                    Float.parseFloat(quote.getString(priceType)));
        }

        return prices;
    }

    class StockHistoryTask extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String ... stocks) {
            JSONObject data = null;

            try {
                OkHttpClient yqlClient = new OkHttpClient();
                String historyUrl = getStockHistoryUrl(stocks[0]);

                Request yqlRequest = new Request.Builder()
                        .url(historyUrl)
                        .build();

                Response yqlResponse = yqlClient.newCall(yqlRequest).execute();

                if (yqlResponse.isSuccessful()) {
                    data = new JSONObject(yqlResponse.body().string());
                }
            }
            catch (Exception anyError) {
                System.out.println("Error: " + anyError.getMessage());
            }

            return data;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            updateStockGraph(jsonObject);
        }

        private String getStockHistoryUrl(String stock) throws Exception {
            GregorianCalendar endDate = new GregorianCalendar();
            GregorianCalendar startDate = new GregorianCalendar();
            StringBuilder url = new StringBuilder(YQL_URL);

            // Set the start date 30 days before the current date.
            startDate.add(GregorianCalendar.DATE, PAST_30_DAYS);

            // Format query statment.
            String selectStmt = MessageFormat.format(HISTORY_SELECT_STMT,
                    stock.toUpperCase(), dateFormatter.format(startDate.getTime()),
                    dateFormatter.format(endDate.getTime()));

            // Add URL parameters.

            url.append("?q=" + URLEncoder.encode(selectStmt, "UTF-8"));
            url.append("&format=json&diagnostics=true");
            url.append("&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

            System.out.println("Stock History Url: " + url.toString());

            return url.toString();
        }
    }

}
