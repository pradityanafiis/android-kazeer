package com.praditya.kazeer.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Product;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class AdjustStockDialog extends AppCompatDialogFragment {
    private Product product;
    private DialogListener dialogListener;
    @BindView(R.id.toggle_adjust_stock)
    ToggleButton tbAdjustStock;
    @BindView(R.id.et_stock)
    EditText etStock;

    public AdjustStockDialog(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adjust_stock, null);

        builder.setView(view).setTitle("Adjust Stock")
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
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogListener");
        }
    }

    public interface DialogListener {
        void updateStock(Product product);
    }

    private void onSaveClicked() {
        boolean ready = true;
        String stockString = etStock.getText().toString().trim();

        if (stockString.isEmpty()) {
            showMessage("Stock required", "error");
            ready = false;
        }

        int stock = 0;
        try {
            stock = Integer.parseInt(stockString);
        } catch (NumberFormatException e) {
            showMessage("Stock only accept number", "error");
            ready = false;
        }

        if (tbAdjustStock.isChecked()) {
            product.setStock(product.getStock() + stock);
        } else {
            if (product.getStock() < stock) {
                showMessage("Failed to decrease, current stock only: " + product.getStock(), "error");
                ready = false;
            } else {
                product.setStock(product.getStock() - stock);
            }
        }

        if (ready) {
            dialogListener.updateStock(product);
        }
    }

    private void showMessage(String message, String type) {
        Toast toast = null;
        if (type.equalsIgnoreCase("success")) {
            toast = Toasty.success(this.getContext(), message);
        } else if (type.equalsIgnoreCase("error")) {
            toast = Toasty.error(this.getContext(), message);
        } else if (type.equalsIgnoreCase("warning")) {
            toast = Toasty.warning(this.getContext(), message);
        } else {
            toast = Toasty.normal(this.getContext(), message);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
