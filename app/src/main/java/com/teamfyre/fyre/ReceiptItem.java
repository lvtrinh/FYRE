package com.teamfyre.fyre;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * An internal class that gives each item of the receipt a home.
 * It is the basic structure of an item that would be on a receipt.
 */
public class ReceiptItem implements Parcelable {

    private String name;
    private BigDecimal price;
    private int itemNum;
    private String itemDesc;

    public ReceiptItem() { }

    public void setName(Object n) {
        if (n.toString().equals("null")) {
            name = null;
            return;
        }
        name = n.toString();
    }

    public void setPrice(Object p) {
        if (p.toString().equals("null")) {
            price = null;
            return;
        }
        price = new BigDecimal(p.toString().replaceAll(",",""));
    }

    public void setItemNum(Object i) {
        if (i.toString().equals("null")) {
            return;
        }
        itemNum = Integer.parseInt(i.toString());
    }

    public void setItemDesc(Object desc) {
        if (desc.toString().equals("null")) {
            itemDesc = null;
            return;
        }
        itemDesc = desc.toString();
    }

    /**
     *
     * @return the name of the ReceiptItem
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return the price of the ReceiptItem
     */
    public BigDecimal getPrice() {
        return this.price;
    }

    /**
     *
     * @return the specific item number of the ReceiptItem
     */
    public int getItemNum() { return this.itemNum; }

    /**
     *
     * @return the item description on the ReceiptItem
     */
    public String getItemDesc() { return this.itemDesc; }

    // Parcel Stuff
    //
    protected ReceiptItem(Parcel in) {
        name = in.readString();
        price = (BigDecimal) in.readValue(BigDecimal.class.getClassLoader());
        itemNum = in.readInt();
        itemDesc = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(price);
        dest.writeInt(itemNum);
        dest.writeString(itemDesc);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ReceiptItem> CREATOR = new Parcelable.Creator<ReceiptItem>() {
        @Override
        public ReceiptItem createFromParcel(Parcel in) {
            return new ReceiptItem(in);
        }

        @Override
        public ReceiptItem[] newArray(int size) {
            return new ReceiptItem[size];
        }
    };
}