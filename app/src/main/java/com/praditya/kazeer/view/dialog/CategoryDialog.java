package com.praditya.kazeer.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryDialog extends AppCompatDialogFragment {
    @BindView(R.id.et_category_name) EditText etCategoryName;
    private DialogListener listener;
    private String title;
    private boolean isEditing;
    private Category category;

    public CategoryDialog(String title, boolean isEditing) {
        this.title = title;
        this.isEditing = isEditing;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_category, null);

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
            listener = (DialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void createCategory(Category category);
        void editCategory(Category category);
    }

    private void initDialog() {
        if (isEditing) {
            etCategoryName.setText(category.getName());
        }
    }

    private void onSaveClicked() {
        boolean ready = true;
        String name = etCategoryName.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Category name required");
            ready = false;
        }

        if (ready) {
            if (isEditing) {
                category.setName(name);
                listener.editCategory(category);
            }else {
                category = new Category(name);
                listener.createCategory(category);
            }
        }
    }

    private void showMessage(String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}
