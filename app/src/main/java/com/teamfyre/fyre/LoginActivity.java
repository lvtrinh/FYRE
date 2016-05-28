/******************************************************************************
 * LoginActivity.java
 *
 * This activity handles the logging in of a user.
 ******************************************************************************/
package com.teamfyre.fyre;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private Button forgot;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        forgot = (Button) findViewById(R.id.forgot);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Enter Email");

                // Set up the input
                final EditText input = new EditText(LoginActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if email exists
                        String email = input.getText().toString();
                        if (true) {

                            ////// start of second dialog
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this);
                            builder2.setTitle("Security Question");
                            //pull security question
                            builder2.setMessage("this is the question");

                            // Set up the input
                            final EditText input = new EditText(LoginActivity.this);
                            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder2.setView(input);

                            // Set up the buttons
                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String securityAnswer = input.getText().toString();
                                    //check question answer
                                }
                            });
                            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder2.show();
                            //////
                        } else {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(),
                                    "Security question answer is incorrect", Toast.LENGTH_LONG)
                                    .show();
                        }


                        //prompt for security question

                        //if security question right send email and notify that email has been sent

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
        });

    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");
                        String id = jObj.getString("id");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(id, name, email, uid, created_at);

                        GetReceiptActivity get = new GetReceiptActivity(db, session);
                        get.getReceipts(Integer.parseInt(id));

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        intent.putExtra("flag", "justLoggedIn");
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
