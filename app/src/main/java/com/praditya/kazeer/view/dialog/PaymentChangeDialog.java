package com.praditya.kazeer.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.praditya.kazeer.R;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentChangeDialog extends BottomSheetDialogFragment {
    private Context context;
    private int paymentChange;
    @BindView(R.id.tv_change)
    TextView tvPaymentChange;

    public PaymentChangeDialog(Context context, int paymentChange) {
        this.context = context;
        this.paymentChange = paymentChange;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_payment, container, false);
        ButterKnife.bind(this, view);
        tvPaymentChange.setText(formatRupiah(paymentChange));
        return view;
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}
