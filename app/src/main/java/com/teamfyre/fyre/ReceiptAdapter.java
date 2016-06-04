/******************************************************************************
 * ReceiptAdapter.java
 *
 * This is the adapter for the RecyclerView used by the main and search activities.
 * The adapter is the "brains" of the RecyclerView, recycling the cards that the user
 * scrolls offscreen and replacing their data with new info.
 *
 * Each card contains the major fields of each Receipt: the merchant, the date,
 * and the price.
 ******************************************************************************/
package com.teamfyre.fyre;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 5/22/2016.
 */
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.RViewHolder> {
    private List<Receipt> receiptList;
    private NumberFormat numFormat = new DecimalFormat("'$'0.00");


    public static final String EXTRA_RECEIPT = "com.teamfyre.fyre.RECEIPT";

    /* ViewHolder holds the views, reducing the number of times
     * the system needs to call findViewById.
     *
     * Note that the OnClickListener is implemented here. That's the part that detects
     * that the user has clicked on a card.
     */
    public class RViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView cv;
        public TextView cv_name, cv_date, cv_price;
        public Context context;

        // Constructor
        public RViewHolder(View view) {
            super(view);
            cv = (CardView) view.findViewById(R.id.card_singleton);
            cv_name = (TextView) view.findViewById(R.id.card_store_name);
            cv_date = (TextView) view.findViewById(R.id.card_date);
            cv_price = (TextView) view.findViewById(R.id.card_price);
            context = view.getContext();
            view.setOnClickListener(this);
        }

        // when we click a card, take us to the detailed receipt page
        // use the receipt pertaining to the card we clicked
        // ayyy lmao
        @Override
        public void onClick(View v) {

            //Toast.makeText(context, "ayyy lmao", Toast.LENGTH_LONG).show();
            Receipt receipt = receiptList.get(this.getLayoutPosition());
            Intent detailIntent = new Intent(context, ReceiptDetailActivity.class);
            detailIntent.putExtra(EXTRA_RECEIPT,receipt);
            context.startActivity(detailIntent);

        }
    }

    public ReceiptAdapter(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }


    /**************************************************************************
     * swapData
     *
     * Replaces the list stored in this adapter with another list
     *
     * @param otherList the list contining the new data
     **************************************************************************/
    public void swapData(ArrayList<Receipt> otherList){
        receiptList.clear();
        receiptList.addAll(otherList);
        notifyDataSetChanged();
    }

    /**************************************************************************
     * onCreateViewHolder
     *
     * Creates the ViewHolder containing a singleton card element, and puts
     * it into view.
     *
     * @param parent the parent ViewGroup
     * @param viewType the ViewType
     * @return The ViewHolder
     **************************************************************************/
    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = View.inflate(context, R.layout.receipt_card_layout, null);
        return new RViewHolder(itemView);
    }

    /**************************************************************************
     * onBindViewHolder
     *
     * Displays data. Reloads receipt data.
     * @param holder
     * @param position
     **************************************************************************/
    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        Receipt receipt = receiptList.get(position);
        holder.cv_name.setText(receipt.getStoreName());
        holder.cv_date.setText(receipt.getDateUI());
        holder.cv_price.setText(numFormat.format(receipt.getTotalPrice().doubleValue()));
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }



}
