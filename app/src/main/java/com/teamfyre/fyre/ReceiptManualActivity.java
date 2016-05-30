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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.view.View;


import java.util.HashMap;

public class ReceiptManualActivity extends AppCompatActivity {

    private Receipt receipt;
    private SQLiteHandler db;
    private SessionManager session;

    private EditText inputStore;
    private EditText inputDate;
    private EditText inputPrice;
    private TextView txtStore;
    private TextView txtDate;
    private TextView txtPrice;
    private Button btnSaveReceipt;
    private ImageButton btnTakePicture;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Private method to launch image capture software
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**************************************************************************
     * onCreate()
     *
     * This function sets up the activity. It populates the screen with the input
     * fields for the receipt's manual additions.
     *
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                txtStore.setText(inputStore.getText().toString().trim());
            }
        });

        // live update for date text on card
        inputDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                txtDate.setText(inputDate.getText().toString().trim());
            }
        });

        // live update for price text on card
        inputPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                txtPrice.setText('$' + inputPrice.getText().toString().trim());
            }
        });

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

                // fixes input for date
                for (int x = 0; x < date.toString().length(); x++) {
                    if (date.toString().charAt(x) == '/') {
                        date.setCharAt(x, '-');
                    }
                }

                // user did not fill out all fields, display warning
                if (store.isEmpty() || date.toString().isEmpty() || price.isEmpty()) {
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
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

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

                    // creates receipt object from information
                    receipt = new Receipt();
                    receipt.setStoreName(store);
                    receipt.setDateTime(date.toString(), "00:00");
                    receipt.setTotalPrice(price);

                    HashMap<String, String> user = db.getUserDetails();
                    String id = user.get("id");

                    // adds receipt to database
                    ReceiptActivity receiptActivity = new ReceiptActivity(db, session);
                    receiptActivity.addReceipt(Integer.parseInt(id), receipt);

                    Toast.makeText(getApplicationContext(),
                            "Receipt saved",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
