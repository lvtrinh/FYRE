package com.teamfyre.fyre;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
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
    private static final String KEY_ORDERNUM = "order_number";

    // Receipt Item table column names
    private static final String TABLE_RECEIPT_ITEM = "item";
    private static final String KEY_RECEIPTITEMID = "item_id";
    private static final String KEY_ITEMNAME = "item_name";
    private static final String KEY_ITEMDESC = "item_description";
    private static final String KEY_PRICE = "price";
    private static final String KEY_ITEMNUM = "item_num";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_TAXTYPE = "tax_type";


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
                + KEY_RECEIPTID + " INTEGER PRIMARY KEY,"
                + KEY_STORENAME + " TEXT," + KEY_STORESTREET + " TEXT,"
                + KEY_STORECITYSTATE + " TEXT," + KEY_STOREPHONE + " TEXT,"
                + KEY_STOREWEBSITE + " TEXT," + KEY_STORECATEGORY + " TEXT," + KEY_HEREGO + " INTEGER,"
                + KEY_CARDTYPE + " TEXT," + KEY_CARDNUM + " INTEGER,"
                + KEY_PAYMENTMETHOD + " TEXT," + KEY_SUBTOTAL + " REAL,"
                + KEY_TAX + " REAL," + KEY_TOTALPRICE + " REAL,"
                + KEY_DATE + " TEXT," + KEY_TIME + " TEXT," + KEY_CASHIER + " TEXT,"
                + KEY_CHECKNUM + " TEXT," + KEY_ORDERNUM + " INTEGER)";

        String CREATE_RECEIPT_ITEM_TABLE = "CREATE TABLE " + TABLE_RECEIPT_ITEM + "("
                + KEY_RECEIPTID + " INTEGER," + KEY_RECEIPTITEMID + " INTEGER PRIMARY KEY,"
                + KEY_ITEMNAME + " TEXT," + KEY_ITEMDESC + " TEXT," + KEY_PRICE + " REAL,"
                + KEY_ITEMNUM + " INTEGER," + KEY_QUANTITY + " INTEGER," + KEY_TAXTYPE + " TEXT)";



        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_RECEIPT_TABLE);
        db.execSQL(CREATE_RECEIPT_ITEM_TABLE);

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


    public void addReceiptLite(String id, Receipt r) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RECEIPTID, id);
        values.put(KEY_STORENAME, r.getStoreName());
        values.put(KEY_STORESTREET, r.getStoreStreet());
        values.put(KEY_STORECITYSTATE, r.getStoreCityState());
        values.put(KEY_STOREPHONE, r.getStorePhone());
        values.put(KEY_STOREWEBSITE, r.getStoreWebsite());
        values.put(KEY_STORECATEGORY, r.getStoreCategory());
        values.put(KEY_HEREGO, String.valueOf(r.getHereGo()));
        values.put(KEY_CARDTYPE, r.getCardType());
        values.put(KEY_CARDNUM, String.valueOf(r.getCardNum()));
        values.put(KEY_SUBTOTAL, String.valueOf(r.getSubtotal()));
        values.put(KEY_TAX, String.valueOf(r.getTax()));
        values.put(KEY_TOTALPRICE, String.valueOf(r.getTotalPrice()));
        values.put(KEY_DATE, r.getDate());
        values.put(KEY_TIME, r.getTime());
        values.put(KEY_CASHIER, r.getCashier());
        values.put(KEY_CHECKNUM, r.getCheckNumber());
        values.put(KEY_ORDERNUM, String.valueOf(r.getOrderNumber()));

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
        if (cursor.moveToFirst()) {
            do {


                System.out.println("Id: " + cursor.getString(0));
                System.out.println("Receipt Id: " + cursor.getString(1));
                System.out.println("Store Name: " + cursor.getString(2));
                System.out.println("Store Street: " + cursor.getString(3));
                System.out.println("Store City State: " + cursor.getString(4));
                System.out.println("Item Number: " + cursor.getString(5));
                System.out.println("Quantity: " + cursor.getString(6));
                System.out.println("Tax Type: " + cursor.getString(7));
                System.out.println("Tax Type: " + cursor.getString(8));
                System.out.println("Tax Type: " + cursor.getString(9));
                System.out.println("Tax Type: " + cursor.getString(10));
                System.out.println("Tax Type: " + cursor.getString(11));
                System.out.println("Tax Type: " + cursor.getString(12));
                System.out.println("Tax Type: " + cursor.getString(13));
                System.out.println("Tax Type: " + cursor.getString(14));
                System.out.println("Tax Type: " + cursor.getString(15));
                System.out.println("Tax Type: " + cursor.getString(16));
                System.out.println("Tax Type: " + cursor.getString(17));
                System.out.println("Tax Type: " + cursor.getString(18));
                //System.out.println("Tax Type: " + cursor.getString(19));
            } while (cursor.moveToNext());
        }
        /*if (cursor.getCount() > 0) {
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
            receipt.put("order_number", cursor.getString(19));
        }*/

        cursor.close();

        db.close();
        // return user
        Log.d(TAG, "Fetching receipt from Sqlite: " + receipt.toString());

        return receipt;
    }

    public void addReceiptItem(String receipt_id, String item_id, ReceiptItem r) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RECEIPTID, receipt_id);
        values.put(KEY_RECEIPTITEMID, item_id);
        values.put(KEY_ITEMNAME, r.getName());
        values.put(KEY_ITEMDESC, r.getItemDesc());
        values.put(KEY_PRICE, String.valueOf(r.getPrice()));
        values.put(KEY_ITEMNUM, r.getItemNum());
        values.put(KEY_QUANTITY, r.getQuantity());
        values.put(KEY_TAXTYPE, String.valueOf(r.getTaxType()));

        long receipt = db.insert(TABLE_RECEIPT_ITEM, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New receiptItem inserted into sqlite: " + receipt);
    }

    //Get receipt details from SQLite database
    //TODO RETURN ARRAYLIST OF RECEIPTS INSTEAD?
    public HashMap<String, String> getReceiptItemDetails() {
        HashMap<String, String> receipt = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT_ITEM;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                System.out.println("Receipt Id: " + cursor.getString(0));
                System.out.println("Item Id: " + cursor.getString(1));
                System.out.println("Item Name: " + cursor.getString(2));
                System.out.println("Item Description: " + cursor.getString(3));
                System.out.println("Price: " + cursor.getString(4));
                System.out.println("Item Number: " + cursor.getString(5));
                System.out.println("Quantity: " + cursor.getString(6));
                System.out.println("Tax Type: " + cursor.getString(7));
            } while (cursor.moveToNext());
        }
        /*if (cursor.getCount() > 0) {
            receipt.put("receipt_id", cursor.getString(0));
            receipt.put("item_id", cursor.getString(1));
            receipt.put("item_name", cursor.getString(2));
            receipt.put("item_description", cursor.getString(3));
            receipt.put("price",cursor.getString(4));
            receipt.put("item_num",cursor.getString(5));
            receipt.put("quantity",cursor.getString(6));
            receipt.put("tax_type",cursor.getString(7));
        }*/

        cursor.close();

        db.close();
        // return user
        Log.d(TAG, "Fetching receipt from Sqlite: " + receipt.toString());

        return receipt;
    }

    public ArrayList<Receipt> getAllReceipts() {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                Receipt currReceipt = new Receipt();
                System.out.println("Id: " + cursor.getString(0));
                currReceipt.setStoreName(cursor.getString(1));
                currReceipt.setStoreStreet(cursor.getString(2));
                currReceipt.setStoreCityState(cursor.getString(3));
                currReceipt.setStorePhone(cursor.getString(4));
                currReceipt.setStoreWebsite(cursor.getString(5));
                currReceipt.setStoreCategory(cursor.getString(6));
                currReceipt.setHereGo(cursor.getString(7));
                currReceipt.setCardType(cursor.getString(8));
                currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) System.out.print("NULL");
                //System.out.println("GOGOGOGOGO: " + cursor.getString(10));
                //currReceipt.setPaymentMethod(cursor.getString(10));
                currReceipt.setSubtotal(cursor.getString(11));
                currReceipt.setTax(cursor.getString(12));
                currReceipt.setTotalPrice(cursor.getString(13));
                currReceipt.setDateTime(cursor.getString(14), cursor.getString(15));
                currReceipt.setCashier(cursor.getString(16));
                currReceipt.setCheckNumber(cursor.getString(17));
                currReceipt.setOrderNumber(cursor.getString(18));
                currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        return receipts;
    }

    public ArrayList<ReceiptItem> getAllItemsID(String id) {
        ArrayList<ReceiptItem> receiptItem = new ArrayList<ReceiptItem>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT_ITEM + " WHERE " + KEY_RECEIPTID + " = '" + id + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                ReceiptItem currItem = new ReceiptItem();
                System.out.println("Receipt Id: " + cursor.getString(0));
                System.out.println("Item Id: " + cursor.getString(1));
                currItem.setName(cursor.getString(2));
                if (cursor.getString(3) == null) System.out.println("NULL");
                //currItem.setItemDesc(cursor.getString(3));
                currItem.setPrice(cursor.getString(4));
                currItem.setItemNum(cursor.getString(5));
                currItem.setQuantity(cursor.getString(6));
                currItem.setTaxType(cursor.getString(7));
                receiptItem.add(currItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        return receiptItem;
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

