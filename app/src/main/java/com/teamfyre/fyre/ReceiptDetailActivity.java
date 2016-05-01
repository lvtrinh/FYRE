package com.teamfyre.fyre;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ReceiptDetailActivity extends AppCompatActivity {
    private Receipt receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle data = getIntent().getExtras();
        receipt = data.getParcelable(MainActivity.EXTRA_RECEIPT);

        /* Test */
        //System.out.println("ReceiptDetailActivity: ");
        //receipt.printReceipt();

        TextView merchantText = (TextView) findViewById(R.id.merchantText);
        TextView casholaText = (TextView) findViewById(R.id.casholaText);

        if (receipt.getStoreName() != null)
            merchantText.setText(receipt.getStoreName());
        if (receipt.getTotalPrice() != null)
            casholaText.setText("$" + receipt.getTotalPrice().toString());
    }

}
