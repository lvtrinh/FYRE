/******************************************************************************
 * SessionManager.java
 *
 * This class handles the account session of a user on the database. It can be 
 * used to determine if a user is logged in.
 ******************************************************************************/
package com.teamfyre.fyre;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    /**************************************************************************
     * setLogin()
     * 
     * Changes the variable tracking whether or not the user is logged in.
     * 
     * @param isLoggedIn If the user is logged in
     **************************************************************************/
    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    
    /**************************************************************************
     * isLoggedIn()
     * 
     * Checks whether the user is logged in or not.
     **************************************************************************/
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
