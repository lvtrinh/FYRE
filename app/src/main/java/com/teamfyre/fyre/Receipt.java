/******************************************************************************
 * Receipt.java
 *
 * An internal class to create the Receipt object. It has all the basic needs
 * of a receipt.
 *
 * Parcelable: used to store Receipt data into an intent
 ******************************************************************************/
package com.teamfyre.fyre;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;
import java.math.BigDecimal;

public class Receipt implements Parcelable{

    private String storeName;
    private String storeStreet;
    private String storeCityState;
    private String storePhone;
    private String storeWebsite;
    private String storeCategory;     // maybe use an int instead?
    private ArrayList<ReceiptItem> itemList;
    private String hereGo; // should be boolean?
    private String cardType;
    private String cardNum;
    private String paymentMethod;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal totalPrice;
    private GregorianCalendar dateTime;
    private String cashier;
    private String checkNumber;
    private int orderNumber;
    private boolean starred;
    private String note;

    /**************************************************************************
     * Receipt()
     *
     * Empty constructor. If you use this, don't forget to fill out some fields!
     * (Or don't. Not like I care or anything.)
     **************************************************************************/
    public Receipt() { }

    // constructor to construct Receipt from Parcel
    // FIFO

    /**************************************************************************
     * Receipt()
     *
     * This constructor takes in a Parcel containing the receipt's data and
     * "remakes" the receipt.
     *
     * This should only be used when a Receipt has been parceled (i.e. when we
     * pass it into an Intent and want to get it back).
     *
     * @param in The Parcel with the receipt's data
     **************************************************************************/
    protected Receipt(Parcel in) {
        storeName = in.readString();
        storeStreet = in.readString();
        storeCityState = in.readString();
        storePhone = in.readString();
        storeWebsite = in.readString();
        storeCategory = in.readString();
        if (in.readByte() == 0x01) {
            itemList = new ArrayList<>();
            in.readList(itemList, ReceiptItem.class.getClassLoader());
        } else {
            itemList = null;
        }
        hereGo = in.readString();
        cardType = in.readString();
        cardNum = in.readString();
        paymentMethod = in.readString();
        subtotal = (BigDecimal) in.readValue(BigDecimal.class.getClassLoader());
        tax = (BigDecimal) in.readValue(BigDecimal.class.getClassLoader());
        totalPrice = (BigDecimal) in.readValue(BigDecimal.class.getClassLoader());
        dateTime = (GregorianCalendar) in.readValue(GregorianCalendar.class.getClassLoader());
        cashier = in.readString();
        checkNumber = in.readString();
        orderNumber = in.readInt();
        starred = in.readByte() != 0x00;
        note = in.readString();
    }

    // Setters
    public void setStoreName(Object name) {
        if (name.toString().equals("null")) {
            storeName = null;
            return;
        }
        storeName = name.toString();
    }

    public void setStoreStreet(Object location) {
        if (location.toString().equals("null")) {
            storeStreet = null;
            return;
        }
        storeStreet = location.toString();
    }

    public void setStoreCityState(Object location) {
        if (location.toString().equals("null")) {
            storeCityState = null;
            return;
        }
        storeCityState = location.toString();
    }

    public void setStorePhone(Object phone) {
        if (phone.toString().equals("null")) {
            storePhone = null;
            return;
        }
        storePhone = phone.toString();
    }

    public void setStoreWebsite(Object website) {
        if (website.toString().equals("null")) {
            storeWebsite = null;
            return;
        }
        storeWebsite = website.toString();
    }

    public void setStoreCategory(Object category) {
        if (category.toString().equals("null")) {
            storeCategory = null;
            return;
        }
        storeCategory = category.toString();
    }

    public void createItemList(ArrayList<ReceiptItem> items) {
        itemList = items;
    }

    public void setHereGo(Object go) {
        if (go.toString().equals("null")) {
            hereGo = null;
            return;
        }
        hereGo = go.toString();
    }

    public void setCardType(Object type) {
        if (type.toString().equals("null")) {
            cardType = null;
            return;
        }
        cardType = type.toString();
    }

    public void setCardNum(Object num) {
        if (num.toString().equals("null")) {
            cardNum = null;
            return;
        }
        cardNum = num.toString();
    }

    public void setPaymentMethod(Object meth) {
        if (meth.toString().equals("null")) {
            paymentMethod = null;
            return;
        }
        paymentMethod = meth.toString();
    }

    public void setSubtotal(Object sub) {
        if (sub.toString().equals("null")) {
            subtotal = null;
            return;
        }
        subtotal = new BigDecimal(sub.toString().replaceAll(",",""));
    }

    public void setTax(Object t) {
        if (t.toString().equals("null")) {
            tax = null;
            return;
        }
        tax = new BigDecimal(t.toString().replaceAll(",",""));
    }

    public void setTotalPrice(Object total) {
        if (total.toString().equals("null")) {
            totalPrice = null;
            return;
        }
        totalPrice = new BigDecimal(total.toString().replaceAll(",",""));
    }

    /**
     *
     * @param date the incoming string for the date
     * @param time the incoming string for the time
     */
    public void setDateTime(Object date, Object time) {
        if (date.toString().equals("null") || time.toString().equals("null")) {
            dateTime = null;
            return;
        }
        String[] dateArrTmp = date.toString().split("/");
        int[] dateArr = new int[dateArrTmp.length];

        for (int i = 0; i < dateArrTmp.length; i++) {
            dateArr[i] = Integer.parseInt(dateArrTmp[i]);
        }

        String[] timeArrTmp = time.toString().split(":");
        int[] timeArr = new int[timeArrTmp.length];
        for (int i = 0; i < timeArrTmp.length; i++) {
            timeArr[i] = Integer.parseInt(timeArrTmp[i]);
        }
        dateTime = new GregorianCalendar(dateArr[2]+2000,dateArr[0],dateArr[1],timeArr[0],timeArr[1]);
    }

    public void setCashier(Object name) {
        if (name.toString().equals("null")) {
            cashier = null;
            return;
        }
        cashier = name.toString();
    }

    public void setCheckNumber(Object number) {
        if (number.toString().equals("null")) {
            checkNumber = null;
            return;
        }
        checkNumber = number.toString();
    }

    public void setOrderNumber(Object number) {
        if (number.toString().equals("null")) {
            return;
        }
        orderNumber = Integer.parseInt(number.toString());
    }


    // getters
    public String getStoreName() {
        return this.storeName;
    }

    public String getStoreStreet() {
        return this.storeStreet;
    }

    public String getStoreCityState() { return  this.storeCityState; }

    public String getStorePhone() {
        return this.storePhone;
    }

    public String getStoreWebsite() {
        return this.storeWebsite;
    }

    public String getStoreCategory() { return this.storeCategory; }

    public ArrayList<ReceiptItem> getItemList() { return this.itemList; }

    public String getHereGo() { return this.hereGo; }

    public String getCardType() { return this.cardType; }

    public String getCardNum() { return this.cardNum; }

    public String getPaymentMethod() { return this.paymentMethod; }

    public BigDecimal getSubtotal() { return this.subtotal; }

    public BigDecimal getTax() { return this.tax; }

    public BigDecimal getTotalPrice() { return this.totalPrice; }

    public GregorianCalendar getDateTime() { return this.dateTime; }

    public String getCashier() { return this.cashier; }

    public String getCheckNumber() { return this.checkNumber; }

    public int getOrderNumber() {return this.orderNumber; }

    public String getDate() {
        return this.dateTime.get(Calendar.MONTH) + "/" + this.dateTime.get(Calendar.DAY_OF_MONTH) + "/" + this.dateTime.get(Calendar.YEAR);
    }

    public String getTime() {
        return this.dateTime.get(Calendar.HOUR_OF_DAY) + ":" + this.dateTime.get(Calendar.MINUTE);
    }

    public void printReceipt() {
        System.out.println("Store Name: " + this.storeName);
        System.out.println("Store Street: " + this.storeStreet);
        System.out.println("Store City, State, ZIP: " + this.storeCityState);
        System.out.println("Store Phone: " + this.storePhone);
        System.out.println("Store Website: " + this.storeWebsite);
        System.out.println("Store Category: " + this.storeCategory);
        System.out.println("========THE ITEM LIST IS BELOW=========");
        for (int i = 0; i < itemList.size(); i++) {
            System.out.println("Item Name: " + itemList.get(i).getName() + " Item Description: " + itemList.get(i).getItemDesc() + " Item Price " + itemList.get(i).getPrice() + " Item Number " + itemList.get(i).getItemNum());
        }
        System.out.println("========END ITEM LIST=========");
        System.out.println("For " + this.hereGo);
        System.out.println(this.cardType + " " + this.cardNum);
        System.out.println("Card Method: " + this.paymentMethod);
        System.out.println("Subtotal: " + this.subtotal);
        System.out.println("Tax: " + this.tax);
        System.out.println("Total: " + this.totalPrice);
        printDateTime();
        System.out.println("Cashier: " + this.cashier);
        System.out.println("Check Number: " + this.checkNumber);
        System.out.println("Order Number: " + this.orderNumber);
    }

    private void printDateTime() {
        if (this.dateTime == null) return;
        System.out.println(this.dateTime.get(Calendar.MONTH) + "/" + this.dateTime.get(Calendar.DAY_OF_MONTH) + "/" + this.dateTime.get(Calendar.YEAR));
        System.out.println(this.dateTime.get(Calendar.HOUR_OF_DAY) + ":" + this.dateTime.get(Calendar.MINUTE));
    }


    // Parcel stuff
    //

    // ignore this, hardly ever used
    @Override
    public int describeContents() {
        return 0;
    }

    // writes contents of Receipt into Parcel
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storeName);
        dest.writeString(storeStreet);
        dest.writeString(storeCityState);
        dest.writeString(storePhone);
        dest.writeString(storeWebsite);
        dest.writeString(storeCategory);
        if (itemList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(itemList);
        }
        dest.writeString(hereGo);
        dest.writeString(cardType);
        dest.writeString(cardNum);
        dest.writeString(paymentMethod);
        dest.writeValue(subtotal);
        dest.writeValue(tax);
        dest.writeValue(totalPrice);
        dest.writeValue(dateTime);
        dest.writeString(cashier);
        dest.writeString(checkNumber);
        dest.writeInt(orderNumber);
        dest.writeByte((byte) (starred ? 0x01 : 0x00));
        dest.writeString(note);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Receipt> CREATOR = new Parcelable.Creator<Receipt>() {
        @Override
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        @Override
        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };

    public int getColumnCount() {
        int columnCount = 0;

        if (itemList.get(0).getTaxType() != '\u0000') columnCount++;
        if (itemList.get(0).getItemNum() != -1) columnCount++;
        if (itemList.get(0).getName() != null) columnCount++;
        if (itemList.get(0).getPrice() != null) columnCount++;
        if (itemList.get(0).getQuantity() != -1) columnCount++;

        return columnCount;
    }
}
