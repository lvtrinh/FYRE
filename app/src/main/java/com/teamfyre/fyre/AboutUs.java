/******************************************************************************
 * AboutUs.java
 *
 * This class shows the "about us" page.
 * If it looks pretty static, it is! Everything's pretty much done in the xml
 * layout files, this class just puts it on the screen.
 *
 * (✿◠‿◠)
 ******************************************************************************/
package com.teamfyre.fyre;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
