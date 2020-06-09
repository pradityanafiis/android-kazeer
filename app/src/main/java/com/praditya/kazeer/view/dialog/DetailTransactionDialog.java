package com.praditya.kazeer.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Cart;
import com.praditya.kazeer.model.Customer;
import com.praditya.kazeer.model.Product;
import com.praditya.kazeer.model.Transaction;
import com.praditya.kazeer.view.adapter.ProductTransactionDetailAdapter;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailTransactionDialog extends BottomSheetDialogFragment {
    private Transaction transaction;
    private Context context;
    private ProductTransactionDetailAdapter adapter;
    @BindView(R.id.tv_transaction_date)
    TextView tvTransactionDate;
    @BindView(R.id.tv_invoice_number)
    TextView tvInvoiceNumber;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.rv_product_list)
    RecyclerView rvProductList;
    @BindView(R.id.tv_num_noi)
    TextView tvNumberofItems;
    @BindView(R.id.tv_num_total)
    TextView tvTotalPrice;
    @BindView(R.id.tv_num_payment)
    TextView tvPayment;
    @BindView(R.id.tv_num_change)
    TextView tvChange;

    public DetailTransactionDialog(Transaction transaction, Context context) {
        this.transaction = transaction;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detail_transaction, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        Customer customer = transaction.getCustomer();
        tvTransactionDate.setText(transaction.getDate());
        tvInvoiceNumber.setText(transaction.getInvoiceNumber());
        tvCustomerName.setText(customer.getName());
        tvNumberofItems.setText(transaction.getProductTransactions().size() + " items");
        tvTotalPrice.setText(formatRupiah(transaction.getAmount()));
        tvPayment.setText(formatRupiah(transaction.getPay()));
        tvChange.setText(formatRupiah(transaction.getChange()));
        adapter = new ProductTransactionDetailAdapter(transaction.getProductTransactions(), context);
        rvProductList.setHasFixedSize(true);
        rvProductList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvProductList.setAdapter(adapter);
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}
