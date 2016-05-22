package com.teamfyre.fyre;

import android.app.Activity;
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

        // gathers user ID from database
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String id = user.get("id");

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
                String date = inputDate.getText().toString().trim();
                String price = inputPrice.getText().toString().trim();

                // user properly added information, begin receipt addition process
                if (!store.isEmpty() && !date.isEmpty() && !price.isEmpty()) {
                    // hides keyboard after user entry complete
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                    // creates receipt object from information
                    receipt = new Receipt();
                    receipt.setStoreName(store);
                    receipt.setDateTime(date, "00:00");
                    receipt.setTotalPrice(price);

                    Toast.makeText(getApplicationContext(),
                            "Saved receipt",
                            Toast.LENGTH_LONG).show();
                }

                // user did not properly insert info, display warning
                else {
                    Toast.makeText(getApplicationContext(),
                            "Please fill out all fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
