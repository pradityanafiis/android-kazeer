package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Category;
import com.praditya.kazeer.model.Customer;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ListViewHolder> implements Filterable {
    private ArrayList<Customer> customers;
    private ArrayList<Customer> unfilteredCustomers;
    private OnClickCallback onClickCallback;
    private Context context;

    public CustomerAdapter(Context context) {
        this.context = context;
        customers = new ArrayList<>();
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
        this.unfilteredCustomers = new ArrayList<>(customers);
        notifyDataSetChanged();
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_customer)
        CardView cvCustomer;
        @BindView(R.id.tv_customer_name)
        TextView tvCustomerName;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickCallback {
        void destroyCustomer(Customer customer);

        void updateCustomer(Customer customer);

        void showCustomer(Customer customer);
    }

    @NonNull
    @Override
    public CustomerAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ListViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.tvCustomerName.setText(customer.getName());
        holder.cvCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCallback.showCustomer(customer);
            }
        });
        holder.cvCustomer.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle(customer.getName());
                MenuItem delete = contextMenu.add(holder.getAdapterPosition(), 1, 0, "Delete");
                delete.setOnMenuItemClickListener(onMenuItemClickListener);
                MenuItem edit = contextMenu.add(holder.getAdapterPosition(), 2, 1, "Edit");
                edit.setOnMenuItemClickListener(onMenuItemClickListener);
            }

            MenuItem.OnMenuItemClickListener onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            onClickCallback.destroyCustomer(customer);
                            break;
                        case 2:
                            onClickCallback.updateCustomer(customer);
                            break;
                    }
                    return false;
                }
            };
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Customer> filteredCustomers = new ArrayList<>();
            if (charSequence == null && charSequence.length() == 0) {
                filteredCustomers.addAll(unfilteredCustomers);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Customer customer : unfilteredCustomers) {
                    if (customer.getName().toLowerCase().contains(filterPattern))
                        filteredCustomers.add(customer);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredCustomers;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            customers.clear();
            customers.addAll((Collection<? extends Customer>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
