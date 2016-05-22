package com.teamfyre.fyre;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by claytonyamaguchi on 5/22/16.
 */
public class GetReceiptActivity {
    private static final String TAG = ReceiptActivity.class.getSimpleName();
    private static ArrayList<ReceiptItem> itemList = new ArrayList<ReceiptItem>();

    public GetReceiptActivity() {}

    public void getReceipts(final int userId) {
        // Tag used to cancel the request
        String tag_string_req = "req_getreceipts";

        //pDialog.setMessage("Adding Receipt ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETRECEIPTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Receipt Response: " + response.toString());
                //hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("SUCCESS", "Receipts were succesfully retrieved.");
                        JSONArray receiptsArr = jObj.getJSONArray("receipts");

                        ArrayList<Receipt> receipt = new ArrayList<Receipt>();
                        // print all the data from the array
                        for (int i = 0; i < receiptsArr.length(); i++) {
                            JSONObject arrObj = receiptsArr.getJSONObject(i);
                            Receipt tmpReceipt = new Receipt();
                            int receiptId = Integer.parseInt(arrObj.get("receipt_id").toString());
                            tmpReceipt.setStoreName(arrObj.get("store_name"));
                            tmpReceipt.setStoreStreet(arrObj.get("store_street"));
                            tmpReceipt.setStoreCityState(arrObj.get("store_city_state"));
                            tmpReceipt.setStorePhone(arrObj.get("store_phone"));
                            tmpReceipt.setStoreWebsite(arrObj.get("store_website"));
                            tmpReceipt.setStoreCategory(arrObj.get("store_category"));
                            tmpReceipt.setHereGo(arrObj.get("here_go"));
                            tmpReceipt.setCardType(arrObj.get("card_type"));
                            tmpReceipt.setCardNum(arrObj.get("card_num"));
                            tmpReceipt.setPaymentMethod(arrObj.get("payment_method"));
                            tmpReceipt.setSubtotal(arrObj.get("subtotal"));
                            tmpReceipt.setTax(arrObj.get("tax"));
                            tmpReceipt.setTotalPrice(arrObj.get("total_price"));
                            tmpReceipt.setDateTime(arrObj.get("date"), arrObj.get("time"));
                            tmpReceipt.setCashier(arrObj.get("cashier"));
                            tmpReceipt.setCheckNumber(arrObj.get("check_number"));
                            tmpReceipt.setOrderNumber(arrObj.get("order_number"));
                            tmpReceipt.createItemList(getItems(68));
                            receipt.add(tmpReceipt);


                            tmpReceipt.printReceipt();
                        }

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



    public ArrayList<ReceiptItem> getItems(final int receiptId) {
        // Tag used to cancel the request
        String tag_string_req = "req_getitems";
        itemList.clear();

        //pDialog.setMessage("Adding Receipt ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETITEMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Item Response: " + response.toString());
                //hideDialog();


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("SUCCESS", "Items were succesfully retrieved.");
                        JSONArray itemsArr = jObj.getJSONArray("receipts");

                        ArrayList<ReceiptItem> items = new ArrayList<ReceiptItem>();
                        // print all the data from the array
                        for (int i = 0; i < itemsArr.length(); i++) {
                            JSONObject arrObj = itemsArr.getJSONObject(i);
                            ReceiptItem tmpReceiptItem = new ReceiptItem();
                            tmpReceiptItem.setName(arrObj.get("name"));
                            tmpReceiptItem.setItemDesc(arrObj.get("itemDesc"));
                            tmpReceiptItem.setPrice(arrObj.get("price"));
                            tmpReceiptItem.setItemNum(arrObj.get("itemNum"));
                            tmpReceiptItem.setQuantity(arrObj.get("quantity"));
                            tmpReceiptItem.setTaxType(arrObj.get("taxType"));
                            itemList.add(tmpReceiptItem);
                        }


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
                System.out.println("WORK " + receiptId);
                params.put("id", String.valueOf(receiptId));

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
        return itemList;
    }
}