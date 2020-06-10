package com.praditya.kazeer.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.praditya.kazeer.R;
import com.praditya.kazeer.model.Customer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class CustomerDialog extends AppCompatDialogFragment {
    @BindView(R.id.et_customer_name) EditText etCustomerName;
    @BindView(R.id.et_customer_address) EditText etCustomerAddress;
    @BindView(R.id.et_customer_telephone) EditText etCustomerTelephone;
    @OnClick(R.id.btn_open_contact)
    void openContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, 1);
    }
    private String title;
    private boolean isEditing;
    private Customer customer;
    private DialogListener dialogListener;

    public CustomerDialog(String title, boolean isEditing) {
        this.title = title;
        this.isEditing = isEditing;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_customer, null);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Cursor cursor;
            try {
                Uri uri = data.getData();
                cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String contactName = cursor.getString(nameIndex);
                String contactNumber = cursor.getString(phoneIndex).replace("-", "").replace("+62 ", "0").trim();
                etCustomerName.setText(contactName);
                etCustomerTelephone.setText(contactNumber);
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage(), "error");
            }
        }
    }

    public interface DialogListener {
        void createCustomer(Customer customer);
        void editCustomer(Customer customer);
    }

    private void initDialog() {
        if (isEditing) {
            etCustomerName.setText(customer.getName());
            etCustomerAddress.setText(customer.getAddress());
            etCustomerTelephone.setText(customer.getTelephone());
        }
    }

    private void onSaveClicked() {
        boolean ready = true;
        String name = etCustomerName.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();
        String telephone = etCustomerTelephone.getText().toString().trim();

        if (name.isEmpty()) {
            showMessage("Customer name required", "error");
            ready = false;
        }

        if (address.isEmpty()) {
            showMessage("Customer address required", "error");
            ready = false;
        }

        if (telephone.isEmpty()) {
            showMessage("Customer telephone required", "error");
            ready = false;
        }

        if (ready) {
            if (isEditing) {
                customer.setName(name);
                customer.setAddress(address);
                customer.setTelephone(telephone);
                dialogListener.editCustomer(customer);
            } else {
                customer = new Customer(name, address, telephone);
                dialogListener.createCustomer(customer);
            }
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
