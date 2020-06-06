package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Category;
import com.praditya.kazeer.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ListViewHolder> implements Filterable {
    private ArrayList<Product> products;
    private ArrayList<Product> unfilteredProducts;
    private Context context;
    private OnClickCallback onClickCallback;

    public ProductAdapter(Context context) {
        this.context = context;
        products = new ArrayList<>();
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        unfilteredProducts = new ArrayList<>(products);
        notifyDataSetChanged();
    }

    public void setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_product) CardView cvProduct;
        @BindView(R.id.tv_product_category) TextView tvProductCategory;
        @BindView(R.id.tv_product_name) TextView tvProductName;
        @BindView(R.id.tv_product_price) TextView tvProductPrice;
        @BindView(R.id.tv_product_stock) TextView tvProductStock;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickCallback {
        void destroyProduct(Product product);
        void updateProduct(Product product);
    }

    @NonNull
    @Override
    public ProductAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ListViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvProductCategory.setText(product.getCategory().getName());
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(formatRupiah(product.getPrice()));
        if (product.getStock() <= 5) {
            holder.tvProductStock.setTextColor(ContextCompat.getColor(context, R.color.errorColor));
            holder.tvProductStock.setText("Stock: " + product.getStock());
        } else {
            holder.tvProductStock.setTextColor(ContextCompat.getColor(context, R.color.successColor));
            holder.tvProductStock.setText("Stock: " + product.getStock());
        }
        holder.cvProduct.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle(product.getName());
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
                            onClickCallback.destroyProduct(product);
                            break;
                        case 2:
                            onClickCallback.updateProduct(product);
                            break;
                    }
                    return false;
                }
            };
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Product> filteredProducts = new ArrayList<>();
            if (charSequence == null && charSequence.length() == 0) {
                filteredProducts.addAll(unfilteredProducts);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Product product: unfilteredProducts) {
                    if (product.getName().toLowerCase().contains(filterPattern))
                        filteredProducts.add(product);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredProducts;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            products.clear();
            products.addAll((Collection<? extends Product>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    private String formatRupiah(int number){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}