package com.teamfyre.fyre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Receipt table
    private static final String TABLE_RECEIPT = "receipt";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";

    // Receipt table column names
    private static final String KEY_RECEIPTID = "receipt_id";
    private static final String KEY_STORENAME = "store_name";
    private static final String KEY_STORESTREET = "store_street";
    private static final String KEY_STORECITYSTATE = "store_city_state";
    private static final String KEY_STOREPHONE = "store_phone";
    private static final String KEY_STOREWEBSITE = "store_website";
    private static final String KEY_STORECATEGORY = "store_category";
    private static final String KEY_HEREGO = "here_go";
    private static final String KEY_CARDTYPE = "card_type";
    private static final String KEY_CARDNUM = "card_num";
    private static final String KEY_PAYMENTMETHOD = "payment_method";
    private static final String KEY_SUBTOTAL = "subtotal";
    private static final String KEY_TAX = "tax";
    private static final String KEY_TOTALPRICE = "total_price";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_CASHIER = "cashier";
    private static final String KEY_CHECKNUM = "check_number";
    private static final String KEY_ORDERNUM = "order_numer";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";

        String CREATE_RECEIPT_TABLE = "CREATE TABLE " + TABLE_RECEIPT + "("
                + KEY_ID + " INTEGER," + KEY_RECEIPTID + " INTEGER PRIMARY KEY,"
                + KEY_STORENAME + " TEXT," + KEY_STORESTREET + " TEXT,"
                + KEY_STORECITYSTATE + " TEXT," + KEY_STOREPHONE + " TEXT,"
                + KEY_STOREWEBSITE + " TEXT," + KEY_STORECATEGORY + " TEXT," + KEY_HEREGO + " INTEGER,"
                + KEY_CARDTYPE + " TEXT," + KEY_CARDNUM + " INTEGER,"
                + KEY_PAYMENTMETHOD + " TEXT," + KEY_SUBTOTAL + " REAL,"
                + KEY_TAX + " REAL," + KEY_TOTALPRICE + " REAL,"
                + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_CASHIER + " TEXT,"
                + KEY_CHECKNUM + " TEXT," + KEY_ORDERNUM + " INTEGER)";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_RECEIPT_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String userId, String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, userId);
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }


    public void addReceiptLite(String id, String receipt_id, String store_name, String store_street,
                           String store_city_state, String store_phone, String store_website,
                           String store_category, String here_go, String card_type,
                           String card_num, String payment_method, String subtotal,
                           String tax, String total_price, String date, String time,
                           String cashier, String check_number, String order_number) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_RECEIPTID, receipt_id);
        values.put(KEY_STORENAME, store_name);
        values.put(KEY_STORESTREET, store_street);
        values.put(KEY_STORECITYSTATE, store_city_state);
        values.put(KEY_STOREPHONE, store_phone);
        values.put(KEY_STOREWEBSITE, store_website);
        values.put(KEY_STORECATEGORY, store_category);
        values.put(KEY_HEREGO, here_go);
        values.put(KEY_CARDTYPE, card_type);
        values.put(KEY_CARDNUM, card_num);
        values.put(KEY_SUBTOTAL, subtotal);
        values.put(KEY_TAX, tax);
        values.put(KEY_TOTALPRICE, total_price);
        values.put(KEY_DATE, date);
        values.put(KEY_TIME, time);
        values.put(KEY_CASHIER, cashier);
        values.put(KEY_CHECKNUM, check_number);
        values.put(KEY_ORDERNUM, order_number);

        long receipt = db.insert(TABLE_RECEIPT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New receipt inserted into sqlite: " + receipt);
    }

    //Get receipt details from SQLite database
    public HashMap<String, String> getReceiptDetails() {
        HashMap<String, String> receipt = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            receipt.put("id", cursor.getString(0));
            receipt.put("receipt_id", cursor.getString(1));
            receipt.put("store_name", cursor.getString(2));
            receipt.put("store_street", cursor.getString(3));
            receipt.put("store_city_state", cursor.getString(4));
            receipt.put("store_phone", cursor.getString(5));
            receipt.put("store_website", cursor.getString(6));
            receipt.put("store_category", cursor.getString(7));
            receipt.put("here_go", cursor.getString(8));
            receipt.put("card_type", cursor.getString(9));
            receipt.put("card_num", cursor.getString(10));
            receipt.put("payment_method", cursor.getString(11));
            receipt.put("subtotal", cursor.getString(12));
            receipt.put("tax", cursor.getString(13));
            receipt.put("total_price", cursor.getString(14));
            receipt.put("date", cursor.getString(15));
            receipt.put("time", cursor.getString(16));
            receipt.put("cashier", cursor.getString(17));
            receipt.put("check_number", cursor.getString(18));
            receipt.put("oreder_number", cursor.getString(19));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching receipt from Sqlite: " + receipt.toString());

        return receipt;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}

