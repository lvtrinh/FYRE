/**
 * SearchableActivity.java is a controller file that handles our search and sorting filter on our
 * search results page.  It allows users to better organize their receipts or view them in a
 * custom way.
 */

package com.teamfyre.fyre;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SQLiteHandler db;
    private SessionManager session;
    private List<Receipt> recyclerList;

    private String query;

    private String cat;

    private String low1;
    private String high1;

    private String from1;
    private String to1;

    private BigDecimal lowDec;
    private BigDecimal highDec;

    GregorianCalendar dateFrom;
    GregorianCalendar dateTo;

    /**
     * Sets up search filter
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //links controller to xml file
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_filter_menu, menu);

        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        ///SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
        //searchView.setQueryHint(getResources().getString(R.string.search_hint));
        return true;
    }

    private void updateRecyclerView(List<Receipt> recyclerList) {
        mRecyclerView = (RecyclerView) findViewById(R.id.receipts_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ReceiptAdapter(recyclerList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * @param item item they click on the drop down
     * The onOptionsSelected method handles which drop down they select when filtering or sorting
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //each if/if else statement controls when an each option from the drop down is selected
        //sort by ascending date
        if (id == R.id.date_asc) {
            recyclerList = db.getSearchReceiptsSort(query, "ORDER BY date ASC");
            updateRecyclerView(recyclerList);
        }
        //sort by descending date
        else if(id == R.id.date_desc) {
            recyclerList = db.getSearchReceipts(query);
            updateRecyclerView(recyclerList);
        }
        //sort by ascending price
        else if(id == R.id.price_asc) {
            recyclerList = db.getSearchReceiptsSort(query, "ORDER BY total_price ASC");
            updateRecyclerView(recyclerList);
        }
        //sort by descending price
        else if(id == R.id.price_desc) {
            recyclerList = db.getSearchReceiptsSort(query, "ORDER BY total_price DESC");
            updateRecyclerView(recyclerList);
        }
        //selects which category to show
        else if(id == R.id.filter_category) {
            // pop something up here to determine a spinner
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Select Category to Filter By");
            final String[] types = {"Food and Drink", "Grocery", "Retail", "Misc"};

            b.setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                    switch(which){
                        //if food an drink, show all food and drink receipts
                        case 0:
                            cat = types[0];
                            recyclerList = db.getSearchReceiptsCategory(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC", cat);
                            updateRecyclerView(recyclerList);
                            break;
                        //if grocery, show all grocery receipts
                        case 1:
                            cat = types[1];
                            recyclerList = db.getSearchReceiptsCategory(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC", cat);
                            updateRecyclerView(recyclerList);
                            break;
                        //if retail, show all retail receipts
                        case 2:
                            cat = types[2];
                            recyclerList = db.getSearchReceiptsCategory(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC", cat);
                            updateRecyclerView(recyclerList);
                            break;
                        //if misc, show all misc receipts
                        case 3:
                            cat = types[3];
                            recyclerList = db.getSearchReceiptsCategory(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC", cat);
                            updateRecyclerView(recyclerList);
                            break;
                    }
                }

            });

            b.show();
        }
        //filters search results by price
        else if(id == R.id.filter_price) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchableActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(SearchableActivity.this);
            builder.setTitle("Filter by Price");

            //get layout
            final View layout = inflater.inflate(R.layout.dialog_price, null);
            builder.setView(layout);

            //getting access to layout items on screen
            final EditText low = (EditText) layout.findViewById(R.id.start);
            final EditText high = (EditText) layout.findViewById(R.id.end);

            //controlling input
            low.setRawInputType(Configuration.KEYBOARD_QWERTY);
            high.setRawInputType(Configuration.KEYBOARD_QWERTY);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder
                    // Add action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            low1 = low.getText().toString();
                            high1 = high.getText().toString();

                            //length of inputs
                            int length1 = low.length();
                            int length2 = high.length();

                            //count1 and count2 keep track of how many '.' there are in the input-
                            // there can only be one
                            //countAfter1 and countAfter2 determines how many characters are after
                            // the '.'- it should only be two
                            int count1 = 0;
                            int countAfter1 = 0;
                            int count2 = 0;
                            int countAfter2 = 0;
                            for(int i = 0; i < length1; i++) {
                                if(count1 == 1)
                                    countAfter1++;
                                if(low1.charAt(i) == '.')
                                    count1++;

                            }
                            for(int i = 0; i < length2; i++) {
                                if(count2 == 1)
                                    countAfter2++;
                                if(high1.charAt(i) == '.')
                                    count2++;
                            }

                            //checks if the input is valid if it is not give an error
                            if(count1 > 1 || count2 > 1 || length1 == 0 || length2 == 0 || countAfter1 != 2 || countAfter2 != 2 ) {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid prices", Toast.LENGTH_LONG)
                                        .show();
                            }
                            //if the input is valid continue with filter process
                            else {
                                //assigns values for the two prices for the filter to be bounded by
                                lowDec = new BigDecimal(low1);
                                highDec = new BigDecimal(high1);

                                //prompts search results
                                recyclerList = db.getSearchReceiptsPrice(query, "AND total_price > " + lowDec.toString() +  " AND total_price < " + highDec.toString() + " ORDER BY date DESC", lowDec, highDec);
                                updateRecyclerView(recyclerList);
                            }
                        }
                    })
                    //if cancel button is clicked
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //LoginDialogFragment.this.getDialog().cancel();
                            dialog.cancel();
                        }
                    });
            builder.show();
        }
        //filters search results by date
        else if(id == R.id.filter_date) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchableActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(SearchableActivity.this);
            builder.setTitle("Filter by Date");

            //getting screen dialog should be
            final View layout = inflater.inflate(R.layout.dialog_date, null);
            builder.setView(layout);

            //getting access to fields on screen
            final EditText from = (EditText) layout.findViewById(R.id.from);
            final EditText to = (EditText) layout.findViewById(R.id.to);

            //controlling input type
            from.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            to.setRawInputType(InputType.TYPE_CLASS_NUMBER);


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder
                    // Add action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            from1 = from.getText().toString();
                            to1 = to.getText().toString();

                            //split the string inputs around '-'
                            String[] parts1 = from1.split("-");
                            String[] parts2 = to1.split("-");

                            //gets number of splits
                            int size1 = parts1.length;
                            int size2 = parts2.length;

                            //should only be 3 splits- month, day, year.  if not give an error
                            if(size1 != 3 || size2 != 3) {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid dates in the form MM-DD-YYYY", Toast.LENGTH_LONG)
                                        .show();
                            }

                            //checks to make sure correct format- ie ##-##-####
                            boolean goodDate = true;
                            for(int i = 0; i < size1; i++) {
                                if(i == 0 || i == 1) {
                                    if(parts1[i].length() != 2 || parts2[i].length() != 2)
                                        goodDate = false;
                                }
                                if(i == 2) {
                                    if(parts1[i].length() != 4 || parts2[i].length() != 4)
                                        goodDate = false;
                                }
                            }

                            //if not in correct format give an error
                            if(!goodDate) {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid dates in the form MM-DD-YYYY", Toast.LENGTH_LONG)
                                        .show();
                            }
                            //if in good format create gregorian calendar objects to continue with filter
                            else {
                                String yearFrom = parts1[2];
                                String monthFrom = parts1[0];
                                int mFrom = Integer.parseInt(monthFrom)-1;
                                String dayFrom = parts1[1];

                                dateFrom = new GregorianCalendar(Integer.parseInt(yearFrom), mFrom, Integer.parseInt(dayFrom));

                                String yearTo = parts2[2];
                                String monthTo = parts2[0];
                                int mTo = Integer.parseInt(monthTo)-1;
                                String dayTo = parts2[1];
                                dateTo = new GregorianCalendar(Integer.parseInt(yearTo), mTo, Integer.parseInt(dayTo));

                                recyclerList = db.getSearchReceiptsDate(query, dateFrom, dateTo);
                                updateRecyclerView(recyclerList);
                            }


                        }
                    })
                    //if click cancel, stop filter process
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //LoginDialogFragment.this.getDialog().cancel();
                            dialog.cancel();
                        }
                    });
            builder.show();


            // pop something up here to determine
            // also we really need to add something in sqlite handler to get things working well, not just for dates but also categoy and price
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * handleIntent loads receipts into the recycler view to be displayed
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            HashMap<String, String> user = db.getUserDetails();

            /////////////////////////////////////////////////////
            //  recycler view stuff
            /////////////////////////////////////////////////////
            mRecyclerView = (RecyclerView) findViewById(R.id.receipts_recycler_view);

            mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            List<Receipt> demoList = generateDemoList(query);

            mAdapter = new ReceiptAdapter(demoList);
            mRecyclerView.setAdapter(mAdapter);

            ///////////////////////////////////////////////////
            // end recycler view stuff
            ///////////////////////////////////////////////////
        }
    }

    /**
     * generateDemoList takes in a search query that is called to the SQLite database
     * @param query
     * @return an array list to feed to the recycler view
     */
    private List<Receipt> generateDemoList(String query) {
        List<Receipt> recList;
        GetReceiptActivity test = new GetReceiptActivity(db, session);
        recList = db.getSearchReceipts(query);

        return recList;
    }
}