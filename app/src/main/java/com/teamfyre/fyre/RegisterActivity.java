/******************************************************************************
 * RegisterActivity.java
 *
 * ThisActivity handles a user's registration by presenting the visual view and
 * access to the database to create the user's account.
 ******************************************************************************/
package com.teamfyre.fyre;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity{
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private String selectedQ;
    private String answer;
    private int qOption;
    private boolean ans;
    
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

        //links controller to view
        setContentView(R.layout.activity_register);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        spinner = (Spinner) findViewById(R.id.security);
        adapter = ArrayAdapter.createFromResource(this, R.array.security_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //variable indicates whether an answer for the security question has been inputted
        ans = false;

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                //which question they selected
                int question = qOption;

                //what their answer was to the security question
                String qAnswer = answer;

                //as long as all required fields have been filled out, register the new user
                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && ans) {
                    registerUser(name, email, password, question, qAnswer);
                } else {
                    //or else throw an error
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        //handles security question field
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                //gets which question they selected to answer
                selectedQ = parent.getSelectedItem().toString();

                //if they have picked one other than default "select one" prompt for an answer
                if(!selectedQ.equals(getString(R.string.select_one))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(selectedQ);

                    if(selectedQ.equals(getString(R.string.maiden_q)))
                        qOption = 1;
                    else if(selectedQ.equals(getString(R.string.food_q)))
                        qOption = 2;
                    else if(selectedQ.equals(getString(R.string.teacher_q)))
                        qOption = 3;
                    else if(selectedQ.equals(getString(R.string.city_q)))
                        qOption = 4;
                    else if(selectedQ.equals(getString(R.string.school_q)))
                        qOption = 5;
                    else if(selectedQ.equals(getString(R.string.street_q)))
                        qOption = 6;
                    else if(selectedQ.equals(getString(R.string.color_q)))
                        qOption = 7;

                    // Set up the input
                    final EditText input = new EditText(RegisterActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            answer = input.getText().toString();

                            //if the user puts in an answer
                            if(answer.length() > 0)
                                ans = true;

                            //if no answer is entered, ie box is left blank, prompts user to answer
                            else {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.answer_correctly), Toast.LENGTH_LONG)
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
            }
        });
    }

    /**************************************************************************
     * registerUser()
     * 
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url.
     **************************************************************************/
    private void registerUser(final String name, final String email,
                              final String password, final int security_question, final String security_answer) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        System.out.println("RUN");

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        String id = jObj.getString("id");
                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(id, name, email, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    
                    else {
                        // Error occurred in registration. Get the errormessage
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("security_question", String.valueOf(security_question));
                params.put("security_answer", security_answer);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**************************************************************************
     * showDialog()
     * 
     * Function to create the progress dialog on the screen.
     **************************************************************************/
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**************************************************************************
     * hideDialog()
     * 
     * Function to hide the progress dialog on the screen.
     **************************************************************************/
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

