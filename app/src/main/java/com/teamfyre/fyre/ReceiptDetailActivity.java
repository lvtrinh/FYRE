package com.teamfyre.fyre;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ReceiptDetailActivity extends AppCompatActivity {
    private Receipt receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        fillReceipt();
    }

    /**************************************************************************
     * fillReceipt()
     *
     * Fills the TextViews with the receipt's data
     * Inserts each ReceiptItem as a new TextView
     * Removes blank/unnecessary fields (e.g. card number if paid with cash)
     *************************************************************************/
    private void fillReceipt(){

        // gather ALL the TextViews
        // findViewById is supposed to be expensive, so I'm a little uneasy about this
        TextView merchantName_header = (TextView) findViewById(R.id.rec_detail_merchant_name_header);
        TextView price_header = (TextView) findViewById(R.id.rec_detail_price_header);
        TextView date_header = (TextView) findViewById(R.id.rec_detail_date_header);

        TextView merchantName = (TextView) findViewById(R.id.rec_detail_merchant_name);
        TextView merchantAddress = (TextView) findViewById(R.id.rec_detail_merchant_address);
        TextView merchantCityState = (TextView) findViewById(R.id.rec_detail_merchant_city_state);
        TextView merchantPhoneWeb = (TextView) findViewById(R.id.rec_detail_merchant_phone_web);
        TextView merchantCategory = (TextView) findViewById(R.id.rec_detail_merchant_category);

        TextView purchaseDateTime = (TextView) findViewById(R.id.rec_detail_purchase_date_time);
        TextView purchaseCashier = (TextView) findViewById(R.id.rec_detail_purchase_cashier);
        TextView purchaseOrderNum = (TextView) findViewById(R.id.rec_detail_purchase_order_number);

        // TODO itemlist

        TextView purchaseSubtotal = (TextView) findViewById(R.id.rec_detail_purchase_subtotal);
        TextView purchaseTax = (TextView) findViewById(R.id.rec_detail_purchase_tax);
        TextView purchaseTotal = (TextView) findViewById(R.id.rec_detail_purchase_total);

        TextView paymentType = (TextView) findViewById(R.id.rec_detail_payment_type);
        // may need to delete these two, if not card
        TextView paymentCardMethod = (TextView) findViewById(R.id.rec_detail_payment_card_method);
        TextView paymentCardNum = (TextView) findViewById(R.id.rec_detail_payment_card_num);


        // replace data
        // for now, let's say everything's required and we can implement removing fields later

        if (receipt.getStoreName() != null) {
            merchantName_header.setText(receipt.getStoreName());
            merchantName.setText(receipt.getStoreName());
        }

        if (receipt.getTotalPrice() != null) {
            price_header.setText("$" + receipt.getTotalPrice().toString());
            purchaseTotal.setText("$" + receipt.getTotalPrice().toString());
        }

        // TODO build a string for date time first, replace body view
        if (receipt.getDate() != null) {
            date_header.setText(receipt.getDate());
            purchaseDateTime.setText(receipt.getDate());
        }

        if (receipt.getTime() != null) {
           purchaseDateTime.append(" " + receipt.getTime());
        }

        if (receipt.getStoreStreet() != null) {
            merchantAddress.setText(receipt.getStoreStreet());
        }

        if (receipt.getStoreCityState() != null) {
            merchantCityState.setText(receipt.getStoreCityState());
        }

        // TODO build a string for phone | web first, replace view
        if (receipt.getStorePhone() != null) {
            merchantPhoneWeb.setText(receipt.getStorePhone());
        }
        if (receipt.getStoreWebsite() != null) {
            merchantPhoneWeb.append(" | " + receipt.getStoreWebsite());
        }
        if (receipt.getStoreCategory() != null) {
            merchantCategory.setText("Category: " + receipt.getStoreCategory());
        }

        if (receipt.getCashier() != null) {
            purchaseCashier.setText("Cashier: " + receipt.getCashier());
        }

        if (receipt.getOrderNumber() != 0) {
            purchaseOrderNum.setText("Order Number: " + receipt.getOrderNumber());
        }

        if (receipt.getSubtotal() != null) {
            purchaseSubtotal.setText("$" + receipt.getSubtotal().toString());
        }

        if (receipt.getTax() != null) {
            purchaseTax.setText("$" + receipt.getTax().toString());
        }

        if (receipt.getCardType() != null){
            paymentType.setText("Paid with: " + receipt.getCardType());
        }

        if (receipt.getPaymentMethod() != null) {
            paymentCardMethod.setText("Card method: " + receipt.getPaymentMethod());
        }

        if (receipt.getCardNum() != null) {
            paymentCardNum.setText("Card ending in " + receipt.getCardNum());
        }

        // TODO if memo has data in it, populate the memo
    }


    @Override
    protected void onStop() {
        super.onStop();

        // TODO save memo data (to local app? to database?)
    }
}
