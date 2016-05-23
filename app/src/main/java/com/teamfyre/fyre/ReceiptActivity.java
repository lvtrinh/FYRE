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

    public ReceiptActivity(SQLiteHandler database, SessionManager currSession) {
        //pDialog = new ProgressDialog(this);
        //pDialog.setCancelable(false);

        // Session manager
        session = currSession;

        // SQLite database handler
        db = database;
    }


    public void addReceipt(final int userId, final Receipt r) {
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
                        String id = receipt.getString("receiptId");

                        //db.addReceiptLite(id, r);
                        for (int i = 0; i < r.getItemList().size(); i++) {
                            addItem(r.getItemList().get(i), Integer.parseInt(id));
                        }
                        System.out.println("On response receipt was added!");
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
                params.put("storeName", r.getStoreName());
                params.put("storeStreet", r.getStoreStreet());
                params.put("storeCityState", r.getStoreCityState());
                params.put("storePhone", r.getStorePhone());
                params.put("storeWebsite", r.getStoreWebsite());
                params.put("storeCategory", r.getStoreCategory());
                params.put("hereGo", String.valueOf(r.getHereGo()));
                params.put("cardType", r.getCardType());
                params.put("cardNum", String.valueOf(r.getCardNum()));
                params.put("paymentMethod", r.getPaymentMethod());
                params.put("subtotal", String.valueOf(r.getSubtotal()));
                params.put("tax", String.valueOf(r.getTax()));
                params.put("totalPrice", String.valueOf(r.getTotalPrice()));
                params.put("date", r.getDate());
                params.put("time", r.getTime());
                params.put("cashier", r.getCashier());
                params.put("checkNumber", r.getCheckNumber());
                params.put("orderNumber", String.valueOf(r.getOrderNumber()));

                Iterator it = params.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if (pair.getValue() == null) {
                        params.put((pair.getKey()).toString(), "");
                    }
                }

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    public void addItem(final ReceiptItem item, final int receiptId) {
        // Tag used to cancel the request
        String tag_string_req = "req_additem";

        final int id = receiptId;
        final String name = item.getName();
        final String itemDesc = item.getItemDesc();
        final BigDecimal price = item.getPrice();
        final Integer itemNum = item.getItemNum();
        final Integer quantity = item.getQuantity();
        final Character taxType = item.getTaxType();
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

                        JSONObject receipt = jObj.getJSONObject("item");
                        String itemId = receipt.getString("itemId");
                        System.out.println(itemId);
                        //db.addReceiptItem(String.valueOf(receiptId), itemId, item);
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

