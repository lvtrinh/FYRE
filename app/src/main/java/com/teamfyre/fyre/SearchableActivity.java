package com.teamfyre.fyre;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SQLiteHandler db;
    private SessionManager session;

    private String query;

    private String cat;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        List<Receipt> recyclerList;

        //noinspection SimplifiableIfStatement
        if (id == R.id.date_asc) {
            recyclerList = db.getSearchReceiptsFilter(query, "ORDER BY date ASC");
        }
        else if(id == R.id.date_desc) {
            recyclerList = db.getSearchReceipts(query);
        }
        else if(id == R.id.price_asc) {
            recyclerList = db.getSearchReceiptsFilter(query, "ORDER BY total_price ASC");
        }
        else if(id == R.id.price_desc) {
            recyclerList = db.getSearchReceiptsFilter(query, "ORDER BY total_price DESC");
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
                            break;
                        case 1:
                            cat = types[1];
                            break;
                        case 2:
                            cat = types[2];
                            break;
                        case 3:
                            cat = types[3];
                            break;
                    }
                }

            });

            b.show();
            recyclerList = db.getSearchReceiptsFilter(query, "AND store_category = " + cat + " ORDER BY total_price DESC");
        }
        else if(id == R.id.filter_price) {
            // pop something up here to determine
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
        }
        else if(id == R.id.filter_date) {
            // pop something up here to determine
            String dateFrom = "2011-00-11";
            String dateTo = "2014-00-12";
            recyclerList = db.getSearchReceiptsFilter(query, "AND total_price > " + dateFrom +  " AND " + dateTo + " ORDER BY date DESC");
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