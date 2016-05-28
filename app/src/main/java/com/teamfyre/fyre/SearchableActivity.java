package com.teamfyre.fyre;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Debug;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private SQLiteHandler db;
    private SessionManager session;

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

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

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