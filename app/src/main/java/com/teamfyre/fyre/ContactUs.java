/******************************************************************************
 * ContactUs.java
 *
 * Activity that allows the user to send feedback straight to the inbox of our
 * very own Christopher Lew! DO IT
 *
 * (◕‿◕✿)
 ******************************************************************************/
package com.teamfyre.fyre;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactUs extends AppCompatActivity {

    Button sendBtn;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = (EditText) findViewById(R.id.message);
                String message = text.getText().toString();
                sendBtn(message);
            }
        });
    }

    /**************************************************************************
     * sendBtn
     *
     * callback method when the user clicks the button. Produces an implicit
     * intent to send an email. This will take the user to their email app.
     *
     * @param message The message Chris should receive
     **************************************************************************/
    private void sendBtn(String message) {
        String[] to = new String[]{"lew.christoph@gmail.com"};
        String subject = ("Feedback from FYRE");
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email"));
    }
}
