package com.teamfyre.fyre;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
    private static final String TAG = RegisterActivity.class.getSimpleName();

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
        final int id = Integer.parseInt(user.get("id"));

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
                for (int i = 0; i < emailLength; i++) {
                    if (i > 0 && e.charAt(i) == '@') {
                        at = true;
                        atIndex = i;
                    }
                    if (i > atIndex + 1 && i > 1 && e.charAt(i) == '.')
                        dot = true;
                }

                if (n.length() != 0)
                    updateName(n);
                if (e.length() != 0 && at && dot)
                    updateEmail(e, id);
                if (p.length() != 0)
                    updatePassword(e, p);
            }
        });

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
                    updateEmail(e, id);
                if(p.length() != 0)
                    updatePassword(e,p);
            }
        });

    }

    //need a method to update account info based on new info inputted
    private void updateEmail(final String email, final int id){
        String tag_string_req = "req_updateemail";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATEEMAIL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String id = jObj.getString("id");
                        //String name = jObj.getString("name");
                        String email = jObj.getString("email");

                        System.out.println("Email succesfully updated.");
                        System.out.println("Email: " + email);

                        db.updateEmail(id, email);

                        // Going to need to add somethng here for SQLite
                        //db.addUser(id, name, email, uid, created_at);

                        // Launch login activity
                        Intent intent = new Intent(
                                AccountActivity.this,
                                SettingsActivity.class);
                        startActivity(intent);
                        //finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("id", String.valueOf(id));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void updatePassword(final String email, final String password) {
        String tag_string_req = "req_updatepassword";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATEPASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String id = jObj.getString("id");
                        //String name = jObj.getString("name");
                        String email = jObj.getString("email");

                        System.out.println("Password succesfully updated.");
                        //System.out.println("Email: " + email);

                        //db.updatePassword(email, password);

                        // Going to need to add somethng here for SQLite
                        //db.addUser(id, name, email, uid, created_at);

                        // Launch login activity
                        Intent intent = new Intent(
                                AccountActivity.this,
                                SettingsActivity.class);
                        startActivity(intent);
                        //finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateName(String name) {

    }

}
