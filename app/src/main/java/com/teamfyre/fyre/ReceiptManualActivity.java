package com.teamfyre.fyre;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by daniel on 5/22/16.
 */
public class ReceiptManualActivity extends Activity {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_receipt);

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

        // live update for receipt details fields
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

        // live update for receipt details fields
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

        // live update for receipt details fields
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

        // camera button
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),
                        "Function not yet implemented",
                        Toast.LENGTH_LONG).show();
            }
        });

        // save button
        btnSaveReceipt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                String store = inputStore.getText().toString().trim();
                StringBuilder date = new StringBuilder(inputDate.getText().toString().trim());
                String price = inputPrice.getText().toString().trim();

                // fixes input for date
                for (int x = 0; x < date.toString().length(); x++) {
                    if (date.toString().charAt(x) == '/') {
                        date.setCharAt(x, '-');
                    }
                }

                // user did not properly insert info, display warning
                if (store.isEmpty() || date.toString().isEmpty() || price.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill out all fields",
                            Toast.LENGTH_LONG).show();
                }

                // user had incorrect date format
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
