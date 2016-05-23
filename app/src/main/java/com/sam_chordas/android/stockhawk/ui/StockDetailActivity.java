package com.sam_chordas.android.stockhawk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by chevelle on 4/25/16.
 */
public class StockDetailActivity extends AppCompatActivity {

    // Stock Keys
    public static final String STOCK_SYMBOL = "stockSymbol";
    public static final String STOCK_BID_PRICE = "stockBidPrice";
    public static final String STOCK_PRICE_CHANGE = "stockPriceChange";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_stock);
    }

}
