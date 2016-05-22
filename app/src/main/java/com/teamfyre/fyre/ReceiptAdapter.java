package com.teamfyre.fyre;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Leo on 5/22/2016.
 */
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.RViewHolder> {
    private List<Receipt> receiptList;

    // ViewHolder class definition
    public class RViewHolder extends RecyclerView.ViewHolder {
        public CardView cv;
        public TextView cv_name, cv_date, cv_price;

        public RViewHolder(View view){
            super(view);
            cv = (CardView) view.findViewById(R.id.card_singleton);
            cv_name = (TextView) view.findViewById(R.id.card_store_name);
            cv_date = (TextView) view.findViewById(R.id.card_date);
            cv_price = (TextView) view.findViewById(R.id.card_price);
        }
    }

    public ReceiptAdapter(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = View.inflate(context, R.layout.receipt_card_layout, null);
        return new RViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RViewHolder holder, int position) {
        Receipt receipt = receiptList.get(position);
        holder.cv_name.setText(receipt.getStoreName());
        holder.cv_date.setText(receipt.getDate());
        holder.cv_price.setText(receipt.getTotalPrice().toString());
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }
}
