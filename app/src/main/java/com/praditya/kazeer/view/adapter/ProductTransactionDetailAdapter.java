package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.ProductTransaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductTransactionDetailAdapter extends RecyclerView.Adapter<ProductTransactionDetailAdapter.ViewHolder> {
    private ArrayList<ProductTransaction> productTransactions;
    private Context context;

    public ProductTransactionDetailAdapter(ArrayList<ProductTransaction> productTransactions, Context context) {
        this.productTransactions = productTransactions;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @BindView(R.id.tv_total_price)
        TextView tvTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ProductTransactionDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_review_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductTransactionDetailAdapter.ViewHolder holder, int position) {
        ProductTransaction productTransaction = productTransactions.get(position);
        holder.tvQuantity.setText(productTransaction.getQuantity() + "x");
        holder.tvProductName.setText(productTransaction.getProductName());
        holder.tvTotalPrice.setText(formatRupiah(productTransaction.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return productTransactions.size();
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}
