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

    private String dateFrom;
    private String dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());

        handleIntent(getIntent());
    }

    /**@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchableActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(SearchableActivity.this);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_price, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }**/

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.date_asc) {
            recyclerList = db.getSearchReceiptsFilter(query, "ORDER BY date ASC");
            updateRecyclerView(recyclerList);
        }
        else if(id == R.id.date_desc) {
            recyclerList = db.getSearchReceipts(query);
            updateRecyclerView(recyclerList);
        }
        else if(id == R.id.price_asc) {
            recyclerList = db.getSearchReceiptsFilter(query, "ORDER BY total_price ASC");
            updateRecyclerView(recyclerList);
        }
        else if(id == R.id.price_desc) {
            recyclerList = db.getSearchReceiptsFilter(query, "ORDER BY total_price DESC");
            updateRecyclerView(recyclerList);
        }
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
                        case 0:
                            cat = types[0];
                            recyclerList = db.getSearchReceiptsFilter(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC");
                            updateRecyclerView(recyclerList);
                            break;
                        case 1:
                            cat = types[1];
                            recyclerList = db.getSearchReceiptsFilter(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC");
                            updateRecyclerView(recyclerList);
                            break;
                        case 2:
                            cat = types[2];
                            recyclerList = db.getSearchReceiptsFilter(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC");
                            updateRecyclerView(recyclerList);
                            break;
                        case 3:
                            cat = types[3];
                            recyclerList = db.getSearchReceiptsFilter(query, "AND store_category = '" + cat + "' ORDER BY total_price DESC");
                            updateRecyclerView(recyclerList);
                            break;
                    }
                }

            });

            b.show();
        }
        else if(id == R.id.filter_price) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchableActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(SearchableActivity.this);
            builder.setTitle("Filter by Price");

            final View layout = inflater.inflate(R.layout.dialog_price, null);
            builder.setView(layout);
            final EditText low = (EditText) layout.findViewById(R.id.start);
            final EditText high = (EditText) layout.findViewById(R.id.end);
            low.setRawInputType(Configuration.KEYBOARD_QWERTY);
            high.setRawInputType(Configuration.KEYBOARD_QWERTY);

            /**low.setInputType(InputType.TYPE_CLASS_NUMBER);
            high.setInputType(InputType.TYPE_CLASS_NUMBER);**/

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder
                    // Add action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            low1 = low.getText().toString();
                            high1 = high.getText().toString();

                            int length1 = low.length();
                            int length2 = high.length();
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

                            if(count1 > 1 || count2 > 1 || countAfter1 != 2 || countAfter2 != 2) {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid prices", Toast.LENGTH_LONG)
                                        .show();
                            }
                            else {
                                lowDec = new BigDecimal(low1);
                                highDec = new BigDecimal(high1);
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //LoginDialogFragment.this.getDialog().cancel();
                            dialog.cancel();
                        }
                    });
            builder.show();

            //******************************************
            //CJ,
            //big decimals to use are lowDec and highDec
            //*******************

            // pop something up here to determine
            /**
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setView(R.layout.filter_price);
            final TextView priceLoText = (TextView) findViewById(R.id.text_price_lo);
            final TextView priceHiText = (TextView) findViewById(R.id.text_price_hi);
            final EditText loInput = (EditText) findViewById(R.id.input_price_lo);
            final EditText hiInput = (EditText) findViewById(R.id.input_price_hi);

            b.setTitle("Filter by Price");

            b.show();

            int priceFrom = Integer.parseInt(loInput.getText().toString());
            int priceTo = Integer.parseInt(hiInput.getText().toString());
            recyclerList = db.getSearchReceiptsFilter(query, "AND total_price > " + priceFrom +  " AND " + priceTo + " ORDER BY date DESC");

             **/
        }
        else if(id == R.id.filter_date) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SearchableActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(SearchableActivity.this);
            builder.setTitle("Filter by Date");

            final View layout = inflater.inflate(R.layout.dialog_date, null);
            builder.setView(layout);
            final EditText from = (EditText) layout.findViewById(R.id.from);
            final EditText to = (EditText) layout.findViewById(R.id.to);
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

                            String[] parts1 = from1.split("-");
                            String[] parts2 = to1.split("-");
                            int size1 = parts1.length;
                            int size2 = parts2.length;

                            if(size1 != 3 || size2 != 3) {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid dates in the form MM-DD-YYYY", Toast.LENGTH_LONG)
                                        .show();
                            }

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

                            if(!goodDate) {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid dates in the form MM-DD-YYYY", Toast.LENGTH_LONG)
                                        .show();
                            }
                            else {
                                String yearFrom = parts1[2];
                                String monthFrom = parts1[0];
                                String dayFrom = parts1[1];
                                dateFrom = yearFrom + "-" + monthFrom + "-" + dayFrom;

                                String yearTo = parts2[2];
                                String monthTo = parts2[0];
                                String dayTo = parts2[1];
                                dateTo = yearTo + "-" + monthTo + "-" + dayTo;
                            }


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //LoginDialogFragment.this.getDialog().cancel();
                            dialog.cancel();
                        }
                    });
            builder.show();

            //******************
            //CJ,
            //fields you want to use are dateFrom and dateTo
            //*************

            /**
            // pop something up here to determine
            String dateFrom = "2011-00-11";
            String dateTo = "2014-00-12";
            recyclerList = db.getSearchReceiptsFilter(query, "AND total_price > " + dateFrom +  " AND " + dateTo + " ORDER BY date DESC");
             **/
        }

        return super.onOptionsItemSelected(item);
    }

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

    private List<Receipt> generateDemoList(String query) {
        List<Receipt> recList;
        GetReceiptActivity test = new GetReceiptActivity(db, session);


        //recList = test.getReceipts(userId);
        //test.getReceipts(userId);
        recList = db.getSearchReceipts(query);

        return recList;
    }
}