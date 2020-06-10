package com.praditya.kazeer.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Category> categories;
    private OnClickCallBack onClickCallBack;
    private int beforePosition = 0;

    public CategoryFilterAdapter(Context context) {
        this.context = context;
        categories = new ArrayList<>();
    }

    public void setCategories(ArrayList<Category> categories) {
        categories.add(0, new Category(0, "All"));
        //this.categories.clear();
        this.categories = categories;
        notifyDataSetChanged();
        beforePosition = 0;
    }

    public void setOnClickCallBack(OnClickCallBack onClickCallBack) {
        this.onClickCallBack = onClickCallBack;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.btn_category)
        Button btnCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public CategoryFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_button, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryFilterAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.btnCategory.setEnabled(true);
        holder.btnCategory.setAlpha(1);
        holder.btnCategory.setText(category.getName());
        holder.btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCallBack.changeCategory(category.getId());
                holder.btnCategory.setEnabled(false);
                holder.btnCategory.setAlpha(0.3f);
                notifyItemChanged(beforePosition);
                beforePosition = position;
            }
        });
        if (beforePosition == 0 && category.getId() == 0) {
            holder.btnCategory.setEnabled(false);
            holder.btnCategory.setAlpha(0.3f);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public interface OnClickCallBack {
        void changeCategory(int categoryId);
    }
}
