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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // TODO create something that can hold/display many receipts, instead of just one
    private Receipt receipt;
    private SQLiteHandler db;
    private SessionManager session;
    private String jsonString;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static final String EXTRA_RECEIPT = "com.teamfyre.fyre.RECEIPT";
    public static final String DEMO_JSON_FILENAME = "chipotleDemo.json";

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

        // manual addition for a receipt
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            new AlertDialog.Builder(MainActivity.this) //changed to MainActivity.this from context
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout??")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            logoutUser();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
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

        /////////////////////////////////////////////////////
        //  recycler view stuff
        /////////////////////////////////////////////////////
        mRecyclerView = (RecyclerView) findViewById(R.id.receipts_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        int userId = Integer.parseInt(user.get("id"));
        List<Receipt> demoList = generateDemoList(userId);

        mAdapter = new ReceiptAdapter(demoList);
        mRecyclerView.setAdapter(mAdapter);

        ///////////////////////////////////////////////////
        // end recycler view stuff
        ///////////////////////////////////////////////////

        jsonString = loadJsonLocal(DEMO_JSON_FILENAME);
        // TODO put this into a variable that persists past onCreate
        // TODO and basically its own file, and thread
        Receipt testReceipt = parseJson(jsonString);

        ReceiptActivity add = new ReceiptActivity(db, session);
        //add.addReceipt(userId, testReceipt);

        //GetReceiptActivity get = new GetReceiptActivity(db, session);
        //get.getReceipts(userId);
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

            //receipt.printReceipt();

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
    public String loadJsonLocal(String rawJSON) {
        String json = null;
        try {
            InputStream is = getAssets().open(rawJSON);
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

    private List<Receipt> generateDemoList(int userId){
        List<Receipt> recList;
        GetReceiptActivity test = new GetReceiptActivity(db, session);


        //recList = test.getReceipts(userId);
        //test.getReceipts(userId);
        recList = db.getAllReceipts();
        /*
        recList = new ArrayList<>();
        Receipt demo1 = parseJson(loadJsonLocal(DEMO_JSON_FILENAME));
        recList.add(demo1);

        Receipt demo2 = parseJson(loadJsonLocal("chipotleDemo.json"));
        recList.add(demo2);

        Receipt demo3 = parseJson(loadJsonLocal("popeyesDemo.json"));
        recList.add(demo3);

        Receipt demo4 = parseJson(loadJsonLocal("dlush.json"));
        recList.add(demo4);

        Receipt demo5 = parseJson(loadJsonLocal("primosDemo.json"));
        recList.add(demo5);

        Receipt demo6 = parseJson(loadJsonLocal("innoutburger.json"));
        recList.add(demo6);

        Receipt demo7 = parseJson(loadJsonLocal("costcoDemo.json"));
        recList.add(demo7);

        Receipt demo8 = parseJson(loadJsonLocal("pandaexpress.json"));
        recList.add(demo8);

        Receipt demo9 = parseJson(loadJsonLocal("safewayDemo.json"));
        recList.add(demo9);

        Receipt demo10 = parseJson(loadJsonLocal("tastyGardenDemo.json"));
        recList.add(demo10);
        */

        return recList;
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            //finish();

        }
        // only if we're placing logout in the hamburger menu
        else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(MainActivity.this) //changed to MainActivity.this from context
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            logoutUser();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
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
