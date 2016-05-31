package com.teamfyre.fyre;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/******************************************************************************
 * AccountActivity.java
 *
 * This Activity handles a user's account updates such as changes to name, email,
 * and security question and answer.
 ******************************************************************************/
public class AccountActivity extends AppCompatActivity {

    private Button UpdateAccount;
    private Button RemoveAccount;
    private EditText name1;
    private EditText email1;
    private EditText password;
    private SessionManager session;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private String selectedQ;
    private String answer;
    private int qOption;
    private boolean ans;
    private SQLiteHandler db;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //associates file with screen
        setContentView(R.layout.activity_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //launches SQLite database
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        spinner = (Spinner) findViewById(R.id.security);
        adapter = ArrayAdapter.createFromResource(this, R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        //getting user's information to display on page
        final String name = user.get("name");
        final String email = user.get("email");
        final int id = Integer.parseInt(user.get("id"));

        //connecting to xml
        name1 = (EditText) findViewById(R.id.name);
        email1 = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        UpdateAccount = (Button) findViewById(R.id.updateAccount);
        RemoveAccount = (Button) findViewById(R.id.RemoveAccount);

        UpdateAccount.setBackgroundResource(0);
        RemoveAccount.setBackgroundResource(0);

        //setting EditText so that the user's current information is stored there
        name1.setText(name);
        email1.setText(email);

        //updates user's information.  handles if there are null fields or data is unchanged
        UpdateAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String n = name1.getText().toString();
                String e = email1.getText().toString();
                String p = password.getText().toString();

                if(ans)
                    //updateSecurityQuestion(qOption, answer);
                updateAccount(n, e, p, id);
            }
        });

        //removes account
        RemoveAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //gets email associated to account
                final String e = email;

                //creates alert double checking that user wants to remove their account
                new android.support.v7.app.AlertDialog.Builder(AccountActivity.this)
                        .setTitle(getString(R.string.remove_account))
                        .setMessage(getString(R.string.are_you_sure_remove))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // remove the account fromt he database
                                removeAccount(e);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        //spinner for security question
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //gets which question they selected
                selectedQ = parent.getSelectedItem().toString();

                //if it is not on "select one" prompt for an answer
                if (!selectedQ.equals(getString(R.string.select_one))) {
                    //alert to get answer to security question
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    builder.setTitle(selectedQ);

                    //assigns integer value to qOption to feed into update account because questions
                    //are stored as integers in the database
                    if (selectedQ.equals(getString(R.string.maiden_q)))
                        qOption = 1;
                    else if (selectedQ.equals(getString(R.string.food_q)))
                        qOption = 2;
                    else if (selectedQ.equals(getString(R.string.teacher_q)))
                        qOption = 3;
                    else if (selectedQ.equals(getString(R.string.city_q)))
                        qOption = 4;
                    else if (selectedQ.equals(getString(R.string.school_q)))
                        qOption = 5;
                    else if (selectedQ.equals(getString(R.string.street_q)))
                        qOption = 6;
                    else if (selectedQ.equals(getString(R.string.color_q)))
                        qOption = 7;

                    // Set up the input
                    final EditText input = new EditText(AccountActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            answer = input.getText().toString();
                            //if they inputted an answer into the text field
                            if (answer.length() > 0)
                                ans = true;
                                //or else prompt them to answer
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "Please enter valid answer to your question", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
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

                        session.setLogin(false);

                        // delete all the data that was on the phone
                        db.deleteUsers();

                        // Launching the login activity
                        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
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