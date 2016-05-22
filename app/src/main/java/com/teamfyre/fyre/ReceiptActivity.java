package com.teamfyre.fyre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.math.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Created by claytonyamaguchi on 5/12/16.
 */
public class ReceiptActivity extends Activity {
    private static final String TAG = ReceiptActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    int receiptId;

    public ReceiptActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Progress dialog
        //pDialog = new ProgressDialog(this);
        //pDialog.setCancelable(false);

        // Session manager
        //session = new SessionManager(getApplicationContext());

        // SQLite database handler
        //db = new SQLiteHandler(getApplicationContext());
    }


    public void addReceipt(final int userId, final String storeName, final String storeStreet, final String storeCityState, final String storePhone, final String storeWebsite, final String storeCategory,
                            final Integer hereGo, final String cardType, final Integer cardNum, final String paymentMethod, final BigDecimal subtotal, final BigDecimal tax, final BigDecimal totalPrice,
                            final String date, final String time, final String cashier, final String checkNumber, final Integer orderNumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_addreceipt";

        //pDialog.setMessage("Adding Receipt ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_ADDRECEIPT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Receipt Response: " + response.toString());
                //hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("SUCCESS", "Receipt was succesfully added.");

                        //Get the MySQL receipt id
                        JSONObject receipt = jObj.getJSONObject("receipt");
                        int id = receipt.getInt("receiptId");
                        setReceiptId(id);
                        System.out.println(getReceiptId());
                        System.out.println("The receipt was inserted at index" + String.valueOf(receipt.getInt("receiptId")));
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d("FAILURE", errorMsg);
                        //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(userId));
                params.put("storeName", storeName);
                params.put("storeStreet", storeStreet);
                params.put("storeCityState", storeCityState);
                params.put("storePhone", storePhone);
                params.put("storeWebsite", storeWebsite);
                params.put("storeCategory", storeCategory);
                params.put("hereGo", String.valueOf(hereGo));
                params.put("cardType", cardType);
                params.put("cardNum", String.valueOf(cardNum));
                params.put("paymentMethod", paymentMethod);
                params.put("subtotal", String.valueOf(subtotal));
                params.put("tax", String.valueOf(tax));
                params.put("totalPrice", String.valueOf(totalPrice));
                params.put("date", date);
                params.put("time", time);
                params.put("cashier", cashier);
                params.put("checkNumber", checkNumber);
                params.put("orderNumber", String.valueOf(orderNumber));

                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if (pair.getValue() == null) {
                        params.put((pair.getKey()).toString(), "");
                    }
                }

                return params;
            }

            /*private Map<String, String> checkParams(Map<String, String> map){
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if(pair.getValue()==null){
                        map.put((pair.getKey()).toString(), "");
                    }
                }
                return map;
            }*/

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void addItem(ReceiptItem item) {
        // Tag used to cancel the request
        String tag_string_req = "req_additem";
        final int id = this.getReceiptId();
        final String name = item.getName();
        final String itemDesc = item.getItemDesc();
        final BigDecimal price = item.getPrice();
        final Integer itemNum = item.getItemNum();
        final Integer quantity = item.getQuantity();
        final Character taxType = item.getTaxType();

        System.out.println("Receipt id: " + String.valueOf(id));
        System.out.println("Item name: " + name);
        System.out.println("Item Description: " + itemDesc);
        System.out.println("Price: " + String.valueOf(price));
        System.out.println("Item Number: " + String.valueOf(itemNum));
        System.out.println("Quantity: " + String.valueOf(quantity));
        System.out.println("Tax Type: " + String.valueOf(taxType));
        //pDialog.setMessage("Adding Receipt ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_ADDITEM, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Item Response: " + response.toString());
                //hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("SUCCESS", "Item was succesfully added.");
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Log.d("FAILURE", errorMsg);
                        //Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("receiptId", String.valueOf(id));
                params.put("itemName", name);
                params.put("itemDescription", itemDesc);
                params.put("itemNum", String.valueOf(itemNum));
                params.put("price", String.valueOf(price));
                params.put("quantity", String.valueOf(quantity));
                params.put("taxType", String.valueOf(taxType));


                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if (pair.getValue() == null) {
                        params.put((pair.getKey()).toString(), "");
                    }
                }

                return params;
            }

            /*private Map<String, String> checkParams(Map<String, String> map){
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if(pair.getValue()==null){
                        map.put((pair.getKey()).toString(), "");
                    }
                }
                return map;
            }*/

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setReceiptId (int id) {
        this.receiptId = id;
    }

    private int getReceiptId () {return this.receiptId;}

    /*private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }*/


}

