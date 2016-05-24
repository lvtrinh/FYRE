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
    public void onCreate(Bundle savedInstanceState) {
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

                updateAccount(n, e, p, id);
            }
        });

        RemoveAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                removeAccount(email);
            }
        });

    }


    //Updates a user account information
    private void updateAccount(final String name, final String email, final String password, final int id) {
        String tag_string_req = "req_updatename";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATEACCOUNT, new Response.Listener<String>() {

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
                        String name = jObj.getString("name");
                        String email = jObj.getString("email");

                        System.out.println("User info succesfully updated.");

                        //Update the user account in SQLite
                        db.updateAccountLite(id, name, email);

                        // Launch login activity
                        Intent intent = new Intent(
                                AccountActivity.this,
                                SettingsActivity.class);
                        startActivity(intent);
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
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("id", String.valueOf(id));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    //Updates a user account information
    private void removeAccount(final String email) {
        String tag_string_req = "req_updatename";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REMOVEACCOUNT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        //User succesfully deleted from MYSQL

                        // Launch login activity
                        Intent intent = new Intent(
                                AccountActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
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

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}