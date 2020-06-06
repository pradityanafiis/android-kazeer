package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Customer;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseCustomerAdapter extends RecyclerView.Adapter<ChooseCustomerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Customer> customers;
    private ArrayList<Customer> unfilteredCustomers;
    private OnClickCallback onClickCallback;
    private int selectedPosition = -1;

    public ChooseCustomerAdapter(Context context) {
        this.context = context;
        customers = new ArrayList<>();
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
        unfilteredCustomers = new ArrayList<>(customers);
        notifyDataSetChanged();
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_choose_customer)
        CardView cvCustomer;
        @BindView(R.id.rb_customer)
        RadioButton rbCustomer;
        @BindView(R.id.tv_customer_name)
        TextView tvCustomerName;
        @BindView(R.id.tv_customer_telephone)
        TextView tvCustomerTelephone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickCallback {
        void selectCustomer(Customer customer);
    }

    @NonNull
    @Override
    public ChooseCustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseCustomerAdapter.ViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.tvCustomerName.setText(customer.getName());
        holder.tvCustomerTelephone.setText(customer.getTelephone());
        holder.cvCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCustomer(customer, position);
            }
        });
        holder.rbCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCustomer(customer, position);
            }
        });
        holder.rbCustomer.setChecked(selectedPosition == position);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    private void onClickCustomer(Customer customer, int position) {
        onClickCallback.selectCustomer(customer);
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
