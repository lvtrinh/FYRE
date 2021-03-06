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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceiptDetailActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Receipt receipt;
    private GridLayout layout;
    private EditText inputMemo;
    private SessionManager session;
    private SQLiteHandler db;
    private NumberFormat d = new DecimalFormat("'$'0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        db = new SQLiteHandler(getApplicationContext());

        Bundle data = getIntent().getExtras();
        receipt = data.getParcelable(MainActivity.EXTRA_RECEIPT);

        inputMemo = (EditText) findViewById(R.id.rec_detail_memo);

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

        TextView staticTax = (TextView) findViewById(R.id.rec_detail_purchase_tax_lit);
        TextView staticSubtotal = (TextView) findViewById(R.id.rec_detail_purchase_subtotal_lit);

        TextView paymentType = (TextView) findViewById(R.id.rec_detail_payment_type);
        // may need to delete these two, if not card
        TextView paymentCardMethod = (TextView) findViewById(R.id.rec_detail_payment_card_method);
        TextView paymentCardNum = (TextView) findViewById(R.id.rec_detail_payment_card_num);

        // replace data
        // for now, let's say everything's required and we can implement removing null fields later

        if (receipt.getStoreName() != "") {
            merchantName_header.setText(receipt.getStoreName());
            merchantName.setText(receipt.getStoreName());
        }
        else {
            ((ViewGroup) merchantName.getParent()).removeView(merchantName);
        }

        if (receipt.getTotalPrice().toString() != "") {
            price_header.setText(d.format(receipt.getTotalPrice()));
            purchaseTotal.setText(d.format(receipt.getTotalPrice()));
        }
        else {
            ((ViewGroup) purchaseTotal.getParent()).removeView(purchaseTotal);
        }

        if (receipt.getDate() != "-1--1--1") {
            date_header.setText(receipt.getDateUI());
            purchaseDateTime.setText(receipt.getDateUI());
        }
        else {
            ((ViewGroup) purchaseDateTime.getParent()).removeView(purchaseDateTime);
        }

        if (receipt.getTime() != null && !receipt.getTime().equals("0:0")) {
           purchaseDateTime.append(" " + receipt.getTime());
        }

        if (!receipt.getStoreStreet().equals("")) {
            merchantAddress.setText(receipt.getStoreStreet());
        }
        else {
            ((ViewGroup) merchantAddress.getParent()).removeView(merchantAddress);
        }

        if (!receipt.getStoreCityState().equals("")) {
            merchantCityState.setText(receipt.getStoreCityState());
        }
        else {
            ((ViewGroup) merchantCityState.getParent()).removeView(merchantCityState);
        }

        if (!receipt.getStorePhone().equals("")) {
            merchantPhone.setText(receipt.getStorePhone());
        }
        else {
            ((ViewGroup) merchantPhone.getParent()).removeView(merchantPhone);
            merchantPhone.setVisibility(View.GONE);
        }

        if (!receipt.getStoreWebsite().equals("")) {
            merchantWeb.setText(receipt.getStoreWebsite());
        }
        else {
            merchantWeb.setVisibility(View.GONE);
        }

        if (!receipt.getStoreCategory().equals("")) {
            merchantCategory.setText("Category: " + receipt.getStoreCategory());
        }
        else {
            merchantCategory.setVisibility(View.GONE);
        }

        if (!receipt.getCashier().equals("")) {
            purchaseCashier.setText("Cashier: " + receipt.getCashier());
        }
        else {
            ((ViewGroup) purchaseCashier.getParent()).removeView(purchaseCashier);
        }

        if (receipt.getOrderNumber() != -1) {
            purchaseOrderNum.setText("Order Number: " + receipt.getOrderNumber());
        }
        else {
            ((ViewGroup) purchaseOrderNum.getParent()).removeView(purchaseOrderNum);
        }

        fillItemList();

        if (receipt.getSubtotal().toString() != "0") {
            purchaseSubtotal.setText(d.format(receipt.getSubtotal()));
        }
        else {
            ((ViewGroup) purchaseSubtotal.getParent()).removeView(purchaseSubtotal);
            ((ViewGroup) staticSubtotal.getParent()).removeView(staticSubtotal);
        }

        if (receipt.getTax().toString() != "0") {
            purchaseTax.setText(d.format(receipt.getTax()));
        }
        else {
            ((ViewGroup) purchaseTax.getParent()).removeView(purchaseTax);
            ((ViewGroup) staticTax.getParent()).removeView(staticTax);
        }

        if (!receipt.getCardType().equals("")){
            paymentType.setText("Paid with: " + receipt.getCardType());
        }
        else {
            ((ViewGroup) paymentType.getParent()).removeView(paymentType);
        }

        if (receipt.getPaymentMethod() != null && !receipt.getPaymentMethod().equals("")) {
            paymentCardMethod.setText("Card method: " + receipt.getPaymentMethod());
        }
        else {
            ((ViewGroup) paymentCardMethod.getParent()).removeView(paymentCardMethod);
        }

        if (receipt.getCardNum() != 0) {
            paymentCardNum.setText("Card ending in " + receipt.getCardNum());
        }
        else {
            ((ViewGroup) paymentCardNum.getParent()).removeView(paymentCardNum);
        }

        if (receipt.getMemo() != null && !receipt.getMemo().equals("")) {
            inputMemo.setText(receipt.getMemo());
        }
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
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

        if (receipt.getMemo() == null) {
            receipt.setMemo("");
        }

        // if the memo was changed, update it in the DB
        if (!receipt.getMemo().equals(inputMemo.getText().toString().trim())) {

            // memo input working, need to update receipt in DB here though
            updateMemo(receipt.getReceiptID(), inputMemo.getText().toString().trim());
            db.updateMemoLite(String.valueOf(receipt.getReceiptID()), inputMemo.getText().toString().trim());

            Toast.makeText(getApplicationContext(),
                    "Memo saved",
                    Toast.LENGTH_LONG).show();
        }
    }


    //Updates the memo in the database
    private void updateMemo(final int receipt_id, final String memo) {
        String tag_string_req = "req_setmemo";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATEMEMO, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        System.out.println("Memo updated");

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Memo Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("receipt_id", String.valueOf(receipt_id));
                params.put("memo", memo);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
