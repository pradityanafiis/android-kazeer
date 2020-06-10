package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Transaction;
import com.praditya.kazeer.view.dialog.DetailTransactionDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Transaction> transactions;
    private OnClickCallback onClickCallback;

    public TransactionAdapter(Context context) {
        this.context = context;
        this.transactions = new ArrayList<>();
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_transaction)
        CardView cvTransaction;
        @BindView(R.id.tv_transaction_date)
        TextView tvTransactionDate;
        @BindView(R.id.tv_customer_name)
        TextView tvCustomerName;
        @BindView(R.id.tv_total_items)
        TextView tvTotalItems;
        @BindView(R.id.tv_total_price)
        TextView tvTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickCallback {
        void openTransactionDetail(Transaction transaction);
    }

    @NonNull
    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.tvTransactionDate.setText(transaction.getDate());
        holder.tvCustomerName.setText(transaction.getCustomer().getName());
        holder.tvTotalItems.setText(transaction.getProductTransactions().size() + " items");
        holder.tvTotalPrice.setText(formatRupiah(transaction.getAmount()));
        holder.cvTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCallback.openTransactionDetail(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void clear() {
        transactions.clear();
        notifyDataSetChanged();
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}
