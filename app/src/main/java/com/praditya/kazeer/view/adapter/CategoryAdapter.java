package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ListViewHolder> implements Filterable {
    private ArrayList<Category> categories;
    private ArrayList<Category> unfilteredCategories;
    private OnClickCallback onClickCallback;
    private Context context;

    public CategoryAdapter(Context context) {
        this.context = context;
        categories = new ArrayList<>();
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
        this.unfilteredCategories = new ArrayList<>(categories);
        notifyDataSetChanged();
    }

    public void setOnClick(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_category) CardView cvCategory;
        @BindView(R.id.tv_category_name) TextView tvCategoryName;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickCallback {
        void destroyCategory(Category category);
        void updateCategory(Category category);
    }

    @NonNull
    @Override
    public CategoryAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ListViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.cvCategory.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle(category.getName());
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
                            onClickCallback.destroyCategory(category);
                            break;
                        case 2:
                            onClickCallback.updateCategory(category);
                    }
                    return false;
                }
            };
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Category> filteredCategories = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredCategories.addAll(unfilteredCategories);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Category category: unfilteredCategories) {
                    if (category.getName().toLowerCase().contains(filterPattern)) {
                        filteredCategories.add(category);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredCategories;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            categories.clear();
            categories.addAll((Collection<? extends Category>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
