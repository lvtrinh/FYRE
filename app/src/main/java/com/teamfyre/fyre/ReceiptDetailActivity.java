/******************************************************************************
 * ReceiptDetailActivity.java
 *
 * This activity shows the full details of a particular receipt. A receipt is
 * passed in with the intent starting this activity.
 *
 * TODO populate memo data on load
 * TODO save memo data on close (in onClose() method)
 * TODO format date/time string in case of missing fields
 ******************************************************************************/
package com.teamfyre.fyre;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReceiptDetailActivity extends AppCompatActivity {
    private Receipt receipt;
    private GridLayout layout;

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
        layout = (GridLayout) findViewById(R.id.itemized_layout);
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
        TextView merchantPhone = (TextView) findViewById(R.id.rec_detail_merchant_phone);
        TextView merchantWeb = (TextView) findViewById(R.id.rec_deatil_merchant_web);
        TextView merchantCategory = (TextView) findViewById(R.id.rec_detail_merchant_category);

        TextView purchaseDateTime = (TextView) findViewById(R.id.rec_detail_purchase_date_time);
        TextView purchaseCashier = (TextView) findViewById(R.id.rec_detail_purchase_cashier);
        TextView purchaseOrderNum = (TextView) findViewById(R.id.rec_detail_purchase_order_number);

        TextView purchaseSubtotal = (TextView) findViewById(R.id.rec_detail_purchase_subtotal);
        TextView purchaseTax = (TextView) findViewById(R.id.rec_detail_purchase_tax);
        TextView purchaseTotal = (TextView) findViewById(R.id.rec_detail_purchase_total);

        TextView paymentType = (TextView) findViewById(R.id.rec_detail_payment_type);
        // may need to delete these two, if not card
        TextView paymentCardMethod = (TextView) findViewById(R.id.rec_detail_payment_card_method);
        TextView paymentCardNum = (TextView) findViewById(R.id.rec_detail_payment_card_num);


        // replace data
        // for now, let's say everything's required and we can implement removing null fields later

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
            date_header.setText(receipt.getDateUI());
            purchaseDateTime.setText(receipt.getDateUI());
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

        if (receipt.getStorePhone() != null) {
            merchantPhone.setText(receipt.getStorePhone());
        }
        if (receipt.getStoreWebsite() != null) {
            merchantWeb.setText(receipt.getStoreWebsite());
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

        fillItemList();

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

    /**************************************************************************
     * fillItemList()
     *
     * This method handles the generation and display of a Receipt's item list.
     * The name and price of each ReceiptItem is inserted into a separate row.
     * An additional row is inserted for each ReceiptItem that contains
     * additional data, such as a description.
     *
     * This method is called within fillReceipt().
     **************************************************************************/
    private void fillItemList() {
        ArrayList<ReceiptItem> itemList = receipt.getItemList();
        // this will get the number of rows we need to insert
        int rowCounter = itemList.size();
        ArrayList<ReceiptItem> hasDesc = new ArrayList<ReceiptItem>();

        // if the receipt item has a description add a row because we will need it
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getItemDesc() != null) {
                rowCounter++;
                hasDesc.add(itemList.get(i));
            }
        }
        layout.setColumnCount(5);
        layout.setRowCount(rowCounter);

        int j = -1;
        for (int i = 0; i < itemList.size(); i++) {
            j++;
            if (itemList.get(i).getTaxType() != 'Z') {
                addTextView(String.valueOf(itemList.get(i).getTaxType()), j, 0, 1);
            }
            if (itemList.get(i).getItemNum() != -1) {
                addTextView(String.valueOf(itemList.get(i).getItemNum()), j, 1, 1);
            }
            if (itemList.get(i).getQuantity() != -1) {
                addTextView(String.valueOf(itemList.get(i).getQuantity()), j, 2, 1);
            }
            if (itemList.get(i).getName() != null) {
                addTextView(itemList.get(i).getName(), j, 3, 4);
            }
            if (itemList.get(i).getPrice() != null) {
                addTextViewPrice(itemList.get(i).getPrice().toString(), j, 4);
            }
            if (itemList.get(i).getItemDesc() != null) {
                j++;
                addTextViewDesc("    " + itemList.get(i).getItemDesc(), j, 3, 1);
            }
        }
    }

    /**************************************************************************
     * addTextView()
     *
     * This method creates a TextView to display ReceiptItem data.
     * This method inserts the TextView into the GridLayout.
     * This method should not be used to display price, use addTextViewPrice for that.
     *
     * This method is called within fillItemList().
     *
     * @param text The text to insert into the TextView
     * @param row The row the TextView should be in the GridLayout
     * @param col The column the TextView should be in the GridLayout
     * @param weight The weight of the TextView
     ***************************************************************************/
    private void addTextView(String text, int row, int col, float weight) {
        TextView toAdd = new TextView(this);
        GridLayout.Spec columnSpec = GridLayout.spec(col, GridLayout.LEFT, weight);
        GridLayout.Spec rowSpec = GridLayout.spec(row);

        toAdd.setText(text);
        toAdd.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        toAdd.setTextColor(getResources().getColor(R.color.text));
        layout.addView(toAdd, new GridLayout.LayoutParams(rowSpec, columnSpec));
    }

    /**************************************************************************
     * addTextViewPrice()
     *
     * This method creates a TextView to display a ReceiptItem's price and
     * inserts it into the GridLayout.
     *
     * This method is called within fillItemList().
     *
     * @param text The price to display
     * @param row The row the TextView should be in the GridLayout
     * @param col The column the TextView should be in the GridLayout
     **************************************************************************/
    private void addTextViewPrice(String text, int row, int col) {
        TextView toAdd = new TextView(this);
        GridLayout.Spec columnSpec = GridLayout.spec(col, GridLayout.RIGHT);
        GridLayout.Spec rowSpec = GridLayout.spec(row);

        toAdd.setText("$" + text);
        toAdd.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        toAdd.setTextColor(getResources().getColor(R.color.text));
        layout.addView(toAdd, new GridLayout.LayoutParams(rowSpec, columnSpec));
    }

    /**************************************************************************
     * addTextViewDesc()
     *
     * This method creates a TextView containing the ReceiptItem's description,
     * and adds it to the GridLayout. I dunno why we need a separate function
     * call for this, but ¯\_(ツ)_/¯
     *
     * This method is called from within fillItemList().
     *
     * @param text The description to display
     * @param row The row the TextView should be in
     * @param col The column the TextView should be in
     * @param weight The TextView's weight
     **************************************************************************/
    private void addTextViewDesc(String text, int row, int col, float weight) {
        TextView toAdd = new TextView(this);
        GridLayout.Spec columnSpec = GridLayout.spec(col, GridLayout.LEFT, weight);
        GridLayout.Spec rowSpec = GridLayout.spec(row);

        toAdd.setText(text);
        toAdd.setTextAppearance(this, android.R.style.TextAppearance_Small);
        layout.addView(toAdd, new GridLayout.LayoutParams(rowSpec, columnSpec));
    }


    /**************************************************************************
     * onStop()
     *
     * This function override saves the memo data to the local SQLite database
     * and the online SQL database before closing the activity normally.
     **************************************************************************/
    @Override
    protected void onStop() {
        super.onStop();

        // TODO save memo data (to local app? to database?)
    }
}
