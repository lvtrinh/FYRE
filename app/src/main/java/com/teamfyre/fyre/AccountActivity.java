package com.teamfyre.fyre;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

/**
 * Created by taylortanita on 5/22/16.
 */
public class AccountActivity extends AppCompatActivity {

    private Button UpdateAccount;
    private Button RemoveAccount;
    private EditText email1;
    private EditText password;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        final String email = user.get("email");

        email1 = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        UpdateAccount = (Button) findViewById(R.id.updateAccount);
        RemoveAccount = (Button) findViewById(R.id.RemoveAccount);

        email1.setText(email);

    }

    //need a method to update account info based on new info inputted
    private void updateAccount(String Email, String Password){

    }

}
