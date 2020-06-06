package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Cart;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductCartReviewAdapter extends RecyclerView.Adapter<ProductCartReviewAdapter.ViewHolder> {
    private ArrayList<Cart> carts;
    private Context context;

    public ProductCartReviewAdapter(Context context) {
        this.context = context;
        carts = new ArrayList<>();
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts = carts;
        notifyDataSetChanged();
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
    public ProductCartReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_review_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCartReviewAdapter.ViewHolder holder, int position) {
        Cart cart = carts.get(position);
        holder.tvQuantity.setText(cart.getQuantity() + "x");
        holder.tvProductName.setText(cart.getProductName());
        holder.tvTotalPrice.setText(formatRupiah(cart.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}
