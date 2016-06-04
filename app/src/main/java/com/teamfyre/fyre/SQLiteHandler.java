package com.teamfyre.fyre;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

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
    private static final String KEY_MEMO = "memo";

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
                + KEY_CHECKNUM + " TEXT," + KEY_ORDERNUM + " INTEGER," + KEY_MEMO + " TEXT)";

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
     * Update the account info in SQLite
     * */
    public void updateAccountLite(String userId, String name, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);

        long id = db.update(TABLE_USER, values, KEY_ID + "=" + userId, null);
        db.close();

        Log.d(TAG, "User info was updated: " + id);
    }


    /**
     * Update the memo in SQLite
     * */
    public void updateMemoLite(String receipt_id, String memo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEMO, memo);

        long id = db.update(TABLE_RECEIPT, values, KEY_RECEIPTID + "=" + receipt_id, null);
        db.close();

        Log.d(TAG, "Receipt memo was updated.");
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

    /**
     * From a receipt object store all of its values into sqlite for retrieval
     * @param id The receipt id given from mysql
     * @param r The receipt you want to add to sqlite
     */
    public void addReceiptLite(String id, Receipt r) {

        SQLiteDatabase db = this.getWritableDatabase();

        // add all of the values from the receipt to sql
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
        values.put(KEY_MEMO, r.getMemo());

        long receipt = db.insert(TABLE_RECEIPT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New receipt inserted into sqlite: " + receipt);
    }

    /**
     * From a receiptItem object add a receiptItem to the sqlite database attached to a receipt via
     * its id.
     * @param receipt_id The id of the receipt that this item is attached to
     * @param item_id The id of the item gotten back from MySQL
     * @param r The ReceiptItem to be added to the sqlite database
     */
    public void addReceiptItem(String receipt_id, String item_id, ReceiptItem r) {
        Log.d("INSERTING SQLite", item_id);
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

        try {
            long receipt = db.insertOrThrow(TABLE_RECEIPT_ITEM, null, values);
        } catch (SQLiteConstraintException e) {

        }
        db.close(); // Closing database connection

        Log.d(TAG, "New receiptItem inserted into sqlite: ");
    }

    /**
     * Get all of the receipts from the sqlite database in ArrayList format. This is to be used by
     * the recyclerview
     * @return All of the receipts in the sqlite database in an arraylist of receipts.
     */
    public ArrayList<Receipt> getAllReceipts() {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        // select all receipts ordered by descending date
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT + " ORDER BY date DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("getAllReceipts", "Getting all receipts...");
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                // populate a receipt object with all the fields from sqlite
                Receipt currReceipt = new Receipt();

                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(19) == null) {}
                else currReceipt.setMemo(cursor.getString(19));

                if (cursor.getString(0) == null) {}
                else {
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }

                // after the receipt is created add it to the list of receipts to return
                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        // Sort in java to make sure the dates are correct coming from SQLite
        Collections.sort(receipts, new Comparator<Receipt>() {
            public int compare(Receipt o1, Receipt o2) {
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });

        return receipts;
    }

    /**
     * From a receiptId get a specific receipt.
     * @param receiptId The receipt id you want to find in the sqlite database
     * @return The receipt that was found in the database
     */
    public Receipt getSpecificReceipt(String receiptId) {
        // SQLite select
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT + " WHERE receipt_id = " + receiptId;

        Receipt currReceipt = new Receipt();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
<<<<<<< HEAD
                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

=======
                // Create a receipt from all the data from sqlite
>>>>>>> 21ab25ce053dbc5ec28e893401ed82b1d960b631
                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(19) == null) {}
                else currReceipt.setMemo(cursor.getString(19));

                if (cursor.getString(0) == null) {}
                else {
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        return currReceipt;
    }

    /**
     * Given a search query return all the Receipts that have a receiptItem that contains the search
     * query
     * @param query The search query
     * @return An arrayList of receipts that contain the search query of the receiptItems
     */
    public ArrayList<Receipt> getSearchItems(String query) {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT_ITEM + " WHERE item_name like '%" + query + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(0) != null) receipts.add(getSpecificReceipt(cursor.getString(0)));

            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        return receipts;
    }

    /**
     * Given you want to filter by category, return all the receipts of that category.
     * @param query The query the user initially searched for
     * @param filter The filter coming from searchable activity
     * @param cat The category to return from
     * @return All the receipts in a specified category
     */
    public ArrayList<Receipt> getSearchReceiptsCategory(String query, String filter, String cat) {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT
                + " WHERE store_name like '%" + query + "%' " + filter;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                Receipt currReceipt = new Receipt();
                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(0) == null) {}
                else {
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }

                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        ArrayList<Receipt> itemResult = getSearchItems(query);
        if (itemResult.size() != 0) for (int i = 0; i < itemResult.size(); i++) {
            if (itemResult.get(i).getStoreCategory().equals(cat)) {
                receipts.add(itemResult.get(i));
            }
        }

        // make sure the receipts returned are in desc date order
        Collections.sort(receipts, new Comparator<Receipt>() {
            public int compare(Receipt o1, Receipt o2) {
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });

        return receipts;
    }

    /**
     * Given a price range filter out all of the receipts that are out of that range.
     * @param query The query the user initially searched for
     * @param filter The filter coming from the searchable activity to enter into the SQLite query
     * @param lo The low price to search between
     * @param hi The high price to search between
     * @return
     */
    public ArrayList<Receipt> getSearchReceiptsPrice(String query, String filter, BigDecimal lo, BigDecimal hi) {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT
                + " WHERE store_name like '%" + query + "%' " + filter;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                Receipt currReceipt = new Receipt();
                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(0) == null) { Log.d("WRONG", ""); }
                else {
                    Log.d("SOMETHING IMPORTANT", cursor.getString(0));
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }

                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        ArrayList<Receipt> itemResult = getSearchItems(query);
        if (itemResult.size() != 0) for (int i = 0; i < itemResult.size(); i++) {
            if (itemResult.get(i).getTotalPrice().compareTo(lo) == 1 && itemResult.get(i).getTotalPrice().compareTo(hi) == -1) {
                receipts.add(itemResult.get(i));
            }
        }

        Collections.sort(receipts, new Comparator<Receipt>() {
            public int compare(Receipt o1, Receipt o2) {
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });

        return receipts;
    }

    /**
     * Given a date filter out all of the receipts that are out of that range.
     * @param query The query the user initially searched for
     * @param lo The low date to search between
     * @param hi The high date to search between
     * @return
     */
    public ArrayList<Receipt> getSearchReceiptsDate(String query, GregorianCalendar lo, GregorianCalendar hi) {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT
                + " WHERE store_name like '%" + query + "%'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                Receipt currReceipt = new Receipt();

                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else {
                    currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));
                }

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(0) == null) { Log.d("WRONG", ""); }
                else {
                    Log.d("SOMETHING IMPORTANT", cursor.getString(0));
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }

                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        ArrayList<Receipt> itemResult = getSearchItems(query);
        if (itemResult.size() != 0) for (int i = 0; i < itemResult.size(); i++) {
            receipts.add(itemResult.get(i));
        }

        for (int i = 0; i < receipts.size(); i++) {
            Log.d("DATE THINGS HERE", receipts.get(i).getDate() + " lo: " + receipts.get(i).getDateTime().compareTo(lo) + " hi: " + receipts.get(i).getDateTime().compareTo(hi));
        }

        ArrayList<Receipt> goodReceipts = new ArrayList<Receipt>();

        if (receipts.size() != 0) for (int i = 0; i < receipts.size(); i++) {
            /**if (receipts.get(i).getDateTime().compareTo(lo) == -1 && receipts.get(i).getDateTime().compareTo(hi) == 1) {
                goodReceipts.add(receipts.get(i));
            }**/
            if (receipts.get(i).getDateTime().compareTo(lo) >= 0 && receipts.get(i).getDateTime().compareTo(hi) <= 0) {
                goodReceipts.add(receipts.get(i));
            }
            else if (receipts.get(i).getDateTime().compareTo(lo) == 1 && receipts.get(i).getDateTime().compareTo(hi) == -1) {
                goodReceipts.add(receipts.get(i));
            }
        }

        Collections.sort(goodReceipts, new Comparator<Receipt>() {
            public int compare(Receipt o1, Receipt o2) {
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });

        return goodReceipts;
    }

    /**
     * From the users previous search query sort by a given filter (date, price)
     * @param query The query the user initially searched for
     * @param filter The SQLite query to perform, based on the type of sorting the user wants to do
     * @return
     */
    public ArrayList<Receipt> getSearchReceiptsSort(String query, String filter) {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT
                + " WHERE store_name like '%" + query + "%' " + filter;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                Receipt currReceipt = new Receipt();

                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(19) == null) {}
                else currReceipt.setMemo(cursor.getString(19));

                if (cursor.getString(0) == null) { Log.d("WRONG", ""); }
                else {
                    Log.d("SOMETHING IMPORTANT", cursor.getString(0));
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }

                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        ArrayList<Receipt> itemResult = getSearchItems(query);
        if (itemResult.size() != 0) for (int i = 0; i < itemResult.size(); i++) {
            receipts.add(itemResult.get(i));
        }

        if (filter == "ORDER BY date ASC") {
            Collections.sort(receipts, new Comparator<Receipt>() {
                public int compare(Receipt o1, Receipt o2) {
                    return o1.getDateTime().compareTo(o2.getDateTime());
                }
            });
        }
        else if (filter == "ORDER BY total_price ASC") {
            Collections.sort(receipts, new Comparator<Receipt>() {
                public int compare(Receipt o1, Receipt o2) {
                    return o1.getTotalPrice().compareTo(o2.getTotalPrice());
                }
            });
        }
        else if (filter == "ORDER BY total_price DESC") {
            Collections.sort(receipts, new Comparator<Receipt>() {
                public int compare(Receipt o1, Receipt o2) {
                    return o2.getTotalPrice().compareTo(o1.getTotalPrice());
                }
            });
        }
        else {
            Collections.sort(receipts, new Comparator<Receipt>() {
                public int compare(Receipt o1, Receipt o2) {
                    return o2.getDateTime().compareTo(o1.getDateTime());
                }
            });
        }

        return receipts;
    }

    /**
     * Given a search query return all of the receipts and receiptItems that contain this query
     * @param query The query the user searched for
     * @return An ArrayList of receipts that contain the users search query.
     */
    public ArrayList<Receipt> getSearchReceipts(String query) {
        //ArrayList holds receipts to be displayed, HashSet ensures duplicate receipts not added
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        HashSet<Integer> receiptsHash = new HashSet<Integer>();

        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT
                + " WHERE store_name like '%" + query + "%' ORDER BY date DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                Receipt currReceipt = new Receipt();

                if (cursor.getString(0) == null) {}
                else currReceipt.setReceiptID(cursor.getString(0));

                if (cursor.getString(1) == null) {}
                else currReceipt.setStoreName(cursor.getString(1));

                if (cursor.getString(2) == null) {}
                else currReceipt.setStoreStreet(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currReceipt.setStoreCityState(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currReceipt.setStorePhone(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currReceipt.setStoreWebsite(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currReceipt.setStoreCategory(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currReceipt.setHereGo(cursor.getString(7));

                if (cursor.getString(8) == null) {}
                else currReceipt.setCardType(cursor.getString(8));

                if (cursor.getString(9) == null) {}
                else currReceipt.setCardNum(cursor.getString(9));

                if (cursor.getString(10) == null) {}
                else currReceipt.setPaymentMethod(cursor.getString(10));

                if (cursor.getString(11) == null) {}
                else currReceipt.setSubtotal(cursor.getString(11));

                if (cursor.getString(12) == null) {}
                else currReceipt.setTax(cursor.getString(12));

                if (cursor.getString(13) == null) {}
                else currReceipt.setTotalPrice(cursor.getString(13));

                if (cursor.getString(14) == null || cursor.getString(15) == null) {}
                else currReceipt.setDateTimeDB(cursor.getString(14), cursor.getString(15));

                if (cursor.getString(16) == null) {}
                else currReceipt.setCashier(cursor.getString(16));

                if (cursor.getString(17) == null) {}
                else currReceipt.setCheckNumber(cursor.getString(17));

                if (cursor.getString(18) == null) {}
                else currReceipt.setOrderNumber(cursor.getString(18));

                if (cursor.getString(19) == null) {}
                else currReceipt.setMemo(cursor.getString(19));

                if (cursor.getString(0) == null) { Log.d("WRONG", ""); }
                else {
                    Log.d("SOMETHING IMPORTANT", cursor.getString(0));
                    currReceipt.createItemList(getAllItemsID(cursor.getString(0)));
                }

                receipts.add(currReceipt);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        ArrayList<Receipt> itemResult = getSearchItems(query);

        //adds receipts to arrayList to be returned but checks to make sure no duplicates added
        if (itemResult.size() != 0) for (int i = 0; i < itemResult.size(); i++) {
            if(!receiptsHash.contains(itemResult.get(i).getReceiptID())) {
                receiptsHash.add(itemResult.get(i).getReceiptID());
                receipts.add(itemResult.get(i));
            }
        }

        Collections.sort(receipts, new Comparator<Receipt>(){
            public int compare(Receipt o1, Receipt o2){
                return o2.getDateTime().compareTo(o1.getDateTime());
            }
        });

        return receipts;
    }

    /**
     * Given a receiptId get all of the receiptItems that are attached to that receiptId
     * @param id The receiptId that you want to get receiptItems from
     * @return An ArrayList of all the receiptItems from the specified id.
     */
    public ArrayList<ReceiptItem> getAllItemsID(String id) {
        ArrayList<ReceiptItem> receiptItem = new ArrayList<ReceiptItem>();
        String selectQuery = "SELECT * FROM " + TABLE_RECEIPT_ITEM  + " WHERE receipt_id = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            //Log.d("RUNNING GOOD", "GOOD");
            do {
                ReceiptItem currItem = new ReceiptItem();
                //Log.d("GET CURSOR", cursor.getString(0));

                if (cursor.getString(2) == null) {}
                else currItem.setName(cursor.getString(2));

                if (cursor.getString(3) == null) {}
                else currItem.setItemDesc(cursor.getString(3));

                if (cursor.getString(4) == null) {}
                else currItem.setPrice(cursor.getString(4));

                if (cursor.getString(5) == null) {}
                else currItem.setItemNum(cursor.getString(5));

                if (cursor.getString(6) == null) {}
                else currItem.setQuantity(cursor.getString(6));

                if (cursor.getString(7) == null) {}
                else currItem.setTaxType(cursor.getString(7));

                receiptItem.add(currItem);
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.close();

        //Log.d("SOMETHING BELOW LOOK", "");
        //for (int i = 0; i < receiptItem.size(); i++) Log.d("BELOW", receiptItem.get(i).getName());
        return receiptItem;
    }

    /**
     * Delete all of the SQLite tables
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_RECEIPT, null, null);
        db.delete(TABLE_RECEIPT_ITEM, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}

