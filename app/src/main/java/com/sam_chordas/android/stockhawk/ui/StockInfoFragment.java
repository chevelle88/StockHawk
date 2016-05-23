package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;

/**
 * Created by chevelle on 5/9/16.
 */
public class StockInfoFragment extends Fragment {

    public StockInfoFragment() {
        // Empty constructor required
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_stock_info, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent stockInfo = getActivity().getIntent();
        TextView symbolView = (TextView)getActivity().findViewById(R.id.stockSymbol);
        TextView bidPriceView = (TextView)getActivity().findViewById(R.id.bidPrice);
        TextView priceChangeView = (TextView)getActivity().findViewById(R.id.stockChange);

        symbolView.setText(stockInfo.getStringExtra(StockDetailActivity.STOCK_SYMBOL));
        bidPriceView.setText("Bid Price: " + stockInfo.getStringExtra(StockDetailActivity.STOCK_BID_PRICE));
        priceChangeView.setText("Price Change: " + stockInfo.getStringExtra(StockDetailActivity.STOCK_PRICE_CHANGE));
    }
}
