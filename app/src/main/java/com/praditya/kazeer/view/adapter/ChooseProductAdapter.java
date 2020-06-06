package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.praditya.kazeer.R;
import com.praditya.kazeer.database.KazeerDatabase;
import com.praditya.kazeer.model.Cart;
import com.praditya.kazeer.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseProductAdapter extends RecyclerView.Adapter<ChooseProductAdapter.ListViewHolder> implements Filterable {
    private ArrayList<Product> products;
    private ArrayList<Product> unfilteredProducts;
    private OnClickCallback onClickCallback;
    private Context context;

    public ChooseProductAdapter(Context context) {
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
        @BindView(R.id.tv_product_name)
        TextView tvProductName;
        @BindView(R.id.tv_product_price)
        TextView tvProductPrice;
        @BindView(R.id.enb_quantity)
        ElegantNumberButton enbQuantity;
        @BindView(R.id.btn_add)
        ImageButton btnAdd;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickCallback {
        void addToCart(Product product);

        void removeFromCart(Product product);
    }

    @NonNull
    @Override
    public ChooseProductAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_product, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseProductAdapter.ListViewHolder holder, int position) {
        Product product = products.get(position);
        Cart cart = getSingleCart(product);
        if (cart != null) {
            holder.btnAdd.setVisibility(View.INVISIBLE);
            holder.enbQuantity.setNumber(String.valueOf(cart.getQuantity()));
            holder.enbQuantity.setRange(0, product.getStock());
            holder.enbQuantity.setVisibility(View.VISIBLE);
            holder.enbQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    if (newValue > oldValue) {
                        onClickCallback.addToCart(product);
                    } else {
                        onClickCallback.removeFromCart(product);
                    }
                    notifyItemChanged(position);
                }
            });
        } else {
            holder.enbQuantity.setVisibility(View.INVISIBLE);
            holder.btnAdd.setVisibility(View.VISIBLE);
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCallback.addToCart(product);
                    notifyItemChanged(position);
                }
            });
        }
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(formatRupiah(product.getPrice()));
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
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Product product : unfilteredProducts) {
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

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }

    private Cart getSingleCart(Product product) {
        KazeerDatabase kazeerDatabase = KazeerDatabase.getInstance(context);
        Cart singleCart = kazeerDatabase.cartDao().getSingleCart(product.getId());
        return singleCart;
    }
}
