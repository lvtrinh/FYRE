/******************************************************************************
 * ReceiptManualActivity.java
 *
 * This is the activity the user is taken to when they wish to manually add a
 * receipt.
 *
 ******************************************************************************/

package com.teamfyre.fyre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.view.View;
import android.graphics.Color;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class ReceiptManualActivity extends AppCompatActivity implements OnItemSelectedListener {

    private Receipt receipt;
    private SQLiteHandler db;
    private SessionManager session;

    private EditText inputStore;
    private EditText inputDate;
    private EditText inputPrice;
    private Spinner spinCategory;
    private EditText inputMemo;
    private TextView txtStore;
    private TextView txtDate;
    private TextView txtPrice;
    private Button btnSaveReceipt;
    private ImageButton btnTakePicture;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String item;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    // Private method to launch image capture software
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**************************************************************************
     * onCreate()
     * <p/>
     * This function sets up the activity. It populates the screen with the input
     * fields for the receipt's manual additions.
     * <p/>
     * This function is called when the activity starts. For more on what this
     * means, see:
     * http://developer.android.com/training/basics/activity-lifecycle/starting.html
     * (protip: ctrl/cmd-click in android studio to open the link!)
     *
     * @param savedInstanceState The saved instance state
     **************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_receipt);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputStore = (EditText) findViewById(R.id.store);
        inputDate = (EditText) findViewById(R.id.date);
        inputPrice = (EditText) findViewById(R.id.price);
        spinCategory = (Spinner) findViewById(R.id.storeCategory);
        inputMemo = (EditText) findViewById(R.id.memo);

        txtStore = (TextView) findViewById(R.id.card_store);
        txtDate = (TextView) findViewById(R.id.card_date);
        txtPrice = (TextView) findViewById(R.id.card_price);

        btnSaveReceipt = (Button) findViewById(R.id.save);
        btnTakePicture = (ImageButton) findViewById(R.id.image_button);

        // Session manager
        session = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        // live update for store text on card
        inputStore.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtStore.setText(inputStore.getText().toString().trim());
            }
        });

        // live update for date text on card
        inputDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtDate.setText(inputDate.getText().toString().trim());
            }
        });

        // live update for price text on card
        inputPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                txtPrice.setText('$' + inputPrice.getText().toString().trim());
            }
        });


        //click spinner
        spinCategory.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select a Category");
        categories.add("Food and Drink");
        categories.add("Grocery");
        categories.add("Retail");
        categories.add("Misc");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<String>(this, R.layout.activity_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.activity_spinner_item);

        // attaching data adapter to spinner
        spinCategory.setAdapter(dataAdapter);


        // camera button, not implemented yet
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        // save button
        btnSaveReceipt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // grabs the user's inputted text
                String store = inputStore.getText().toString().trim();
                StringBuilder date = new StringBuilder(inputDate.getText().toString().trim());
                String price = inputPrice.getText().toString().trim();
                String category = item;
                String memo = inputMemo.getText().toString().trim();


                // fixes input for date
                for (int x = 0; x < date.toString().length(); x++) {
                    if (date.toString().charAt(x) == '/') {
                        date.setCharAt(x, '-');
                    }
                }

                // user did not fill out all fields, display warning
                if (store.isEmpty() || date.toString().isEmpty() || price.isEmpty() || item == "Select a Category") {
                    Toast.makeText(getApplicationContext(),
                            "Please fill out all fields",
                            Toast.LENGTH_LONG).show();
                }

                // user had incorrect date format, display warning
                else if (!date.toString().matches("\\d{2}-\\d{2}-\\d{4}")) {
                    Toast.makeText(getApplicationContext(),
                            "Incorrect date format",
                            Toast.LENGTH_LONG).show();
                }

                // user properly added information, begin receipt addition process
                else {
                    // hides keyboard after user entry complete
                    //InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    // fixes input for price
                    boolean decimalFound = false;
                    for (int x = 0; x < price.length(); x++) {
                        if (price.charAt(x) == '.') {
                            decimalFound = true;
                        }
                    }

                    if (!decimalFound) {
                        price += ".00";
                    }

                    receipt = new Receipt();
                    receipt.setDateTime(date.toString(), "00:00");
                    receipt.setStoreName(store);
                    receipt.setTotalPrice(price);
                    receipt.setStoreCategory(category);
                    receipt.setMemo(memo);
                    receipt.setStoreStreet("");
                    receipt.setStoreStreet("");
                    receipt.setStoreCityState("");
                    receipt.setStorePhone("");
                    receipt.setStoreWebsite("");
                    receipt.setHereGo(0);
                    receipt.setCardType("");
                    receipt.setCardNum(0);
                    receipt.setPaymentMethod("");
                    receipt.setSubtotal(BigDecimal.ZERO);
                    receipt.setTax(BigDecimal.ZERO);
                    receipt.setCashBack(BigDecimal.ZERO);
                    receipt.setCashier("");
                    receipt.setCheckNumber("");
                    receipt.setOrderNumber(-1);
                    receipt.setStarred(false);

                    HashMap<String, String> user = db.getUserDetails();
                    String id = user.get("id");

                    // adds receipt to database
                    ReceiptActivity receiptActivity = new ReceiptActivity(db, session);
                    receiptActivity.addReceipt(Integer.parseInt(id), receipt);

                    Toast.makeText(getApplicationContext(),
                            "Receipt saved",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(
                            ReceiptManualActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        // ((TextView)parent.getChildAt(position)).setTextColor(Color.parseColor("#808080"));
        item = parent.getItemAtPosition(position).toString();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ReceiptManual Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.teamfyre.fyre/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ReceiptManual Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.teamfyre.fyre/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
