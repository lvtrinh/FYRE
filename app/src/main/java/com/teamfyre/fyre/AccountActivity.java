package com.teamfyre.fyre;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

/**
 * Created by taylortanita on 5/22/16.
 */
public class AccountActivity extends AppCompatActivity {

    private Button UpdateAccount;
    private Button RemoveAccount;
    private EditText name1;
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

        final String name = user.get("name");
        final String email = user.get("email");

        name1 = (EditText) findViewById(R.id.name);
        email1 = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        UpdateAccount = (Button) findViewById(R.id.updateAccount);
        RemoveAccount = (Button) findViewById(R.id.RemoveAccount);

        name1.setText(name);
        email1.setText(email);

        UpdateAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String n = name1.getText().toString();
                String e = email1.getText().toString();
                String p = password.getText().toString();

                //checking if email is in correct format
                int emailLength = e.length();
                boolean at = false;
                int atIndex = 0;
                boolean dot = false;
                for(int i = 0; i < emailLength; i++){
                    if(i > 0 && e.charAt(i) == '@') {
                        at = true;
                        atIndex = i;
                    }
                    if(i > atIndex + 1 && i > 1 && e.charAt(i) == '.')
                        dot = true;
                }

                if(n.length() != 0)
                    updateName(n);
                if(e.length() != 0 && at && dot)
                    updateEmail(e);
                if(p.length() != 0)
                    updatePassword(p);
            }
        });

    }

    //need a method to update account info based on new info inputted
    private void updateName(String Name){

    }

    private void updateEmail(String Email){

    }

    private void updatePassword(String Password){

    }

}
