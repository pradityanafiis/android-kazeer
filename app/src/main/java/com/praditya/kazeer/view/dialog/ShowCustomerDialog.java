package com.praditya.kazeer.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Customer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowCustomerDialog extends BottomSheetDialogFragment {
    private Context context;
    private Customer customer;
    @BindView(R.id.tv_customer_name)
    TextView tvCustomerName;
    @BindView(R.id.tv_customer_telephone)
    TextView tvCustomerTelephone;
    @BindView(R.id.tv_customer_address)
    TextView tvCustomerAddress;

    public ShowCustomerDialog(Context context, Customer customer) {
        this.context = context;
        this.customer = customer;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_show_customer, container, false);
        ButterKnife.bind(this, view);
        tvCustomerName.setText(customer.getName());
        tvCustomerTelephone.setText(customer.getTelephone());
        tvCustomerAddress.setText(customer.getAddress());
        return view;
    }
}
