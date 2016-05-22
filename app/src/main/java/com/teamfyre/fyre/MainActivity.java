/******************************************************************************
 * MainActivity.java
 *
 * This is the main activity, if you couldn't tell. This is the activity that
 * the user is taken to once they log in (if they're already logged in, they
 * go directly to this screen). This screen, by default, shows a list of the
 * user's receipts in order from newest to oldest (X number of receipts are
 * loaded at a time).
 *
 * The hamburger menu is accessible from this menu.
 * From the hamburger menu, the user can access this page, the search page,
 * the settings page, and TODO other things we really should finalize soon
 *
 ******************************************************************************/
package com.teamfyre.fyre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // TODO create something that can hold/display many receipts, instead of just one
    private Receipt receipt;
    private SQLiteHandler db;
    private SessionManager session;
    private String jsonString;

    public static final String EXTRA_RECEIPT = "com.teamfyre.fyre.RECEIPT";
    public static final String DEMO_JSON_FILENAME = "baguetteBrosDemo.json";

    /**************************************************************************
     * onCreate()
     *
     * This function sets up the activity. It produces and populates the list
     * of receipts, as well as enabling the hamburger menu for use.
     *
     * This function is called when the activity starts. For more on what this
     * means, see:
     * http://developer.android.com/training/basics/activity-lifecycle/starting.html
     * (protip: ctrl/cmd-click in android studio to open the link!)
     *
     * @param savedInstanceState The saved instance state
     **************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            /*
                TODO: change code within onClick() to make smaller action buttons

             */
           public void onClick(View view) {
                //Snackbar.make(view, "TODO: add smaller action buttons", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, ReceiptManualActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // set navigation header text
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        View navHeader = navigationView.getHeaderView(0);
        TextView headerMain = (TextView) navHeader.findViewById(R.id.nav_header_maintext);
        TextView headerSub = (TextView) navHeader.findViewById(R.id.nav_header_subtext);

        headerMain.setText(name);
        headerSub.setText(email);

        jsonString = loadJsonLocal();
        // TODO put this into a variable that persists past onCreate
        // TODO and basically its own file, and thread
        Receipt testReceipt = parseJson(jsonString);
        int userId = Integer.parseInt(user.get("id"));
        System.out.println(userId);
        String storeName = testReceipt.getStoreName();
        String storeStreet = testReceipt.getStoreStreet();
        String storeCityState = testReceipt.getStoreCityState();
        String storePhone = testReceipt.getStorePhone();
        String storeWebsite = testReceipt.getStoreWebsite();
        String storeCategory = testReceipt.getStoreCategory();
        Integer hereGo = testReceipt.getHereGo();
        String cardType = testReceipt.getCardType();
        int cardNum = testReceipt.getCardNum();
        String paymentMethod = testReceipt.getPaymentMethod();
        BigDecimal subtotal = testReceipt.getSubtotal();
        BigDecimal tax = testReceipt.getTax();
        BigDecimal totalPrice = testReceipt.getTotalPrice();
        String date = testReceipt.getDate();
        String time = testReceipt.getTime();
        String cashier = testReceipt.getCashier();
        String checkNumber = testReceipt.getCheckNumber();
        int orderNumber = testReceipt.getOrderNumber();
        ArrayList<ReceiptItem> itemList = testReceipt.getItemList();

        ReceiptActivity add = new ReceiptActivity();
        add.addReceipt(userId, storeName, storeStreet, storeCityState, storePhone, storeWebsite, storeCategory, hereGo, cardType, cardNum, paymentMethod, subtotal, tax, totalPrice, date, time, cashier, checkNumber, orderNumber);

        for (int j = 0; j < itemList.size(); j++) {
            add.addItem(itemList.get(j));
        }
    }

    /**************************************************************************
     * parseJson()
     *
     * Takes in a json file in the form of a string to make a receipt object
     * with all of the fields for it filled in if possible.
     *
     * @param jsonString a json file in string format that goes in
     * @return Receipt - a receipt object with all the data input from a json file in it
     **************************************************************************/
    public Receipt parseJson(String jsonString) {
        try {
            // create jsonobject
            JSONObject obj = new JSONObject(jsonString);

            receipt = new Receipt();

            // set everything since constructors would suck?
            receipt.setStoreName(obj.get("storeName"));
            receipt.setStoreStreet(obj.get("storeStreet"));
            receipt.setStoreCityState(obj.get("storeCityState"));
            receipt.setStorePhone(obj.get("storePhone"));
            receipt.setStoreWebsite(obj.get("storeWebsite"));
            receipt.setStoreCategory(obj.get("storeCategory"));
            receipt.setHereGo(obj.get("hereGo"));
            receipt.setCardType(obj.get("cardType"));
            receipt.setCardNum(obj.get("cardNum"));
            receipt.setPaymentMethod(obj.get("paymentMethod"));
            receipt.setSubtotal(obj.get("subtotal"));
            receipt.setTax(obj.get("tax"));
            receipt.setTotalPrice(obj.get("totalPrice"));
            receipt.setCashBack(obj.get("cashBack"));
            receipt.setDateTime(obj.get("date"), obj.get("time"));
            receipt.setCashier(obj.get("cashier"));
            receipt.setCheckNumber(obj.get("checkNumber"));
            receipt.setOrderNumber(obj.get("orderNumber"));

            // get the itemList array
            JSONArray arr = obj.getJSONArray("itemList");

            ArrayList<ReceiptItem> indivItemArr = new ArrayList<ReceiptItem>();
            // print all the data from the array
            for (int i = 0; i < arr.length(); i++) {
                JSONObject arrObj = arr.getJSONObject(i);
                ReceiptItem tmpReceipt = new ReceiptItem();
                tmpReceipt.setName(arrObj.get("name"));
                tmpReceipt.setItemDesc(arrObj.get("itemDesc"));
                tmpReceipt.setPrice(arrObj.get("price"));
                tmpReceipt.setItemNum(arrObj.get("itemNum"));
                tmpReceipt.setQuantity(arrObj.get("quantity"));
                tmpReceipt.setTaxType(arrObj.get("taxType"));
                indivItemArr.add(tmpReceipt);
            }

            receipt.createItemList(indivItemArr);

            receipt.printReceipt();

            return receipt;

        } catch (JSONException js) {
            js.printStackTrace();
            return null;
        }
    }

    /**************************************************************************
     * loadJsonLocal()
     *
     * Reads json formatted data into a string from a local assets file
     * @return String - json data
     **************************************************************************/
    public String loadJsonLocal() {
        String json = null;
        try {
            InputStream is = getAssets().open(DEMO_JSON_FILENAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO display one receipt
    // TODO make a button or something (temporary) to get into detailed receipt
    // TODO make a "detailed receipt" activity (this is probably gonna be permanent

    /**************************************************************************
     * onCardClicked()
     *
     * This method opens up the receipt's info (ReceiptDetailActivity.java).
     *
     * This method is called when the CardView is clicked
     * (via xml:onClick)
     * Don't use this method on non-CardViews.
     *
     * @param view The CardView that was clicked
     **************************************************************************/
    public void onCardClicked(View view) {
        Intent detailIntent = new Intent(this, ReceiptDetailActivity.class);
        detailIntent.putExtra(EXTRA_RECEIPT, receipt);
        startActivity(detailIntent);
    }

    /*
        TODO: fill in actions once we implement them (start activity, most likely)
        TODO: remove @SuppressWarnings once everything's implemented
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_receipt) {
            // Handle the action
        } else if (id == R.id.nav_categories) {

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_settings) {

        }
        // only if we're placing logout in the hamburger menu
        else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**************************************************************************
     * logoutUser()
     *
     * Logs out the user. Also deletes the database data, because we shouldn't
     * be keeping that if the user logged out, right?
     **************************************************************************/
    private void logoutUser() {
        session.setLogin(false);

        // delete all the data that was on the phone
        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
