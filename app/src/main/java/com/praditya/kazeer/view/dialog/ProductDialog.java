package com.praditya.kazeer.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.model.Category;
import com.praditya.kazeer.model.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDialog extends AppCompatDialogFragment {
    @BindView(R.id.spinner_product_category) MaterialSpinner spinnerProductCategory;
    @BindView(R.id.et_product_name) EditText etProductName;
    @BindView(R.id.et_product_price) EditText etProductPrice;
    @BindView(R.id.et_product_stock) EditText etProductStock;
    private String title;
    private boolean isEditing;
    private Product product;
    private DialogListener dialogListener;
    private ArrayList<Category> categories;

    public ProductDialog(String title, boolean isEditing) {
        this.title = title;
        this.isEditing = isEditing;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_product, null);

        builder.setView(view).setTitle(title)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onSaveClicked();
                    }
                });
        ButterKnife.bind(this, view);
        initDialog();
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void createProduct(Product product);
        void editProduct(Product product);
    }

    private void initDialog() {
        initSpinner();
        if (isEditing) {
            etProductName.setText(product.getName());
            etProductPrice.setText(String.valueOf(product.getPrice()));
            etProductStock.setText(String.valueOf(product.getStock()));
        }
    }

    private void onSaveClicked() {
        boolean ready = true;
        String name = etProductName.getText().toString().trim();
        String stringPrice = etProductPrice.getText().toString().trim();
        String stringStock = etProductStock.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Product name required");
            ready = false;
        }

        if (stringPrice.isEmpty()) {
            showMessage("Product price required");
            ready = false;
        }

        if (stringStock.isEmpty()) {
            showMessage("Product stock required");
            ready = false;
        }

        int price = 0;
        try {
            price = Integer.parseInt(stringPrice);
        } catch (NumberFormatException e) {
            showMessage("Product price only accept number");
            ready = false;
        }

        int stock = 0;
        try {
            stock = Integer.parseInt(stringStock);
        } catch (NumberFormatException e) {
            showMessage("Product stock only accept number");
            ready = false;
        }

        if (ready) {
            int id = categories.get(spinnerProductCategory.getSelectedIndex()).getId();
            if (isEditing) {
                product.setCategoryId(id);
                product.setName(name);
                product.setPrice(price);
                product.setStock(stock);
                dialogListener.editProduct(product);
            } else {
                product = new Product(id, name, price, stock);
                dialogListener.createProduct(product);
            }
        }
    }

    private void initSpinner() {
        ApiClient.getServices().getCategory().enqueue(new Callback<MultipleResponse<Category>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Category>> call, Response<MultipleResponse<Category>> response) {
                boolean error = response.body().isError();
                if (!error) {
                    categories = response.body().getData();
                    ArrayList<String> categoryName = new ArrayList<>();
                    for (Category category: categories) {
                        categoryName.add(category.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryName);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProductCategory.setAdapter(adapter);
                    if (isEditing) {
                        int indexSelected = adapter.getPosition(product.getCategory().getName());
                        spinnerProductCategory.setSelectedIndex(indexSelected);
                    }
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Category>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}
