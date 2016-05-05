/******************************************************************************
 * ReceiptItem.java
 *
 * An internal class that gives each item of the receipt a home.
 * It is the basic structure of an item that would be on a receipt.
 *
 * Parcelable: we'll need to parcel this too.
 ******************************************************************************/
package com.teamfyre.fyre;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class ReceiptItem implements Parcelable {

    private String name;
    private BigDecimal price;
    private int itemNum;
    private String itemDesc;
    private int quantity;
    private char taxType;

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
            itemNum = -1; // -1 INDICATES NULL, MIGHT WANT TO USE THE INTEGER OBJECT
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

    public void setQuantity(Object q) {
        if (q.toString().equals("null")) {
            quantity = -1;
            return;
        }
        quantity = Integer.parseInt(q.toString());
    }

    public void setTaxType(Object tax) {
        if (tax.toString().equals("null")) {
            taxType = '\u0000'; // THIS IS THE NULL CHAR
            return;
        }
        taxType = tax.toString().charAt(0);
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

    public char getTaxType() { return this.taxType; }

    public int getQuantity() { return quantity; }

    // Parcel Stuff
    //
    protected ReceiptItem(Parcel in) {
        name = in.readString();
        price = (BigDecimal) in.readValue(BigDecimal.class.getClassLoader());
        itemNum = in.readInt();
        itemDesc = in.readString();
        quantity = in.readInt();
        taxType = (char) in.readValue(char.class.getClassLoader());
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
        dest.writeInt(quantity);
        dest.writeValue(taxType);
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