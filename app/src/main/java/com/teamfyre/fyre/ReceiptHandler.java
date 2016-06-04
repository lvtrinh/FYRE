package com.teamfyre.fyre;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
public class ReceiptHandler extends AppCompatActivity {
    private static final String TAG = ReceiptHandler.class.getSimpleName();
    private SessionManager session;
    private SQLiteHandler db;
    int receiptId;

    /**
     * Creates a ReceiptHandler by passing in the database and session
     * @param database The database to be used (SQLite)
     * @param currSession The session of the app
     */
    public ReceiptHandler(SQLiteHandler database, SessionManager currSession) {

        // Session manager
        session = currSession;

        // SQLite database handler
        db = database;
    }

    /**
     * Add a receipt to the mySQL database as well as the SQLite database.
     * @param userId The id of the user in the mySQL database
     * @param r The receipt that you are adding
     */
    public void addReceipt(final int userId, final Receipt r) {
        // Tag used to cancel the request
        String tag_string_req = "req_addreceipt";

        // Get the URL for add receipt and add it to the database
        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_ADDRECEIPT, new Response.Listener<String>() {

            // Get the response from the addReceipt.php file.
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

                        db.addReceiptLite(id, r);
                        if (r.getItemList() != null && r.getItemList().size() != 0) {
                            for (int i = 0; i < r.getItemList().size(); i++) {
                                addItem(r.getItemList().get(i), Integer.parseInt(id));
                            }
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
                // Posting params to addreceipt url. Puts all of these in the mySQL database
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
                params.put("memo", r.getMemo());

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

    /**
     * Add a receipt item to the mySQL and SQLite databases
     * @param item The receiptItem to add to the mySQL and SQLite database
     * @param receiptId The id of the receipt to attach the ReceiptItem to
     */
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
                        //System.out.println(itemId);
                        GetReceiptActivity test = new GetReceiptActivity(db, session);
                        test.getItems(receiptId);
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

}
