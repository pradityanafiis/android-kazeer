package com.praditya.kazeer.view.ui;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.Services;
import com.praditya.kazeer.api.response.SingleResponse;
import com.praditya.kazeer.database.KazeerDatabase;
import com.praditya.kazeer.model.Cart;
import com.praditya.kazeer.model.Customer;
import com.praditya.kazeer.model.ProductTransaction;
import com.praditya.kazeer.model.Transaction;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.ChooseProductAdapter;
import com.praditya.kazeer.view.adapter.ProductCartReviewAdapter;
import com.praditya.kazeer.view.dialog.PaymentChangeDialog;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewCartFragment extends Fragment {
    private ProductCartReviewAdapter adapter;
    private Services services = ApiClient.getServices();
    private KazeerDatabase kazeerDatabase;
    private int cashPayment;
    @BindView(R.id.rv_product_list)
    RecyclerView rvProductList;
    @BindView(R.id.tv_num_noi)
    TextView tvNumberofItems;
    @BindView(R.id.tv_num_tp)
    TextView tvTotalPice;
    @BindView(R.id.et_pay)
    EditText etPay;
    @BindView(R.id.btn_create_transaction)
    Button btnCreateTransaction;
    @OnClick(R.id.btn_create_transaction)
    void onClickCreateTransaction() {
        Customer customer = (Customer) getArguments().getSerializable("customer");
        ArrayList<ProductTransaction> productTransactions = new ArrayList<>();
        for (Cart cart : getCarts()) {
            ProductTransaction productTransaction = new ProductTransaction(cart.getProductId(), cart.getProductName(), cart.getPrice(), cart.getQuantity(), cart.getTotalPrice());
            productTransactions.add(productTransaction);
        }
        Transaction transaction = new Transaction(customer.getId(), getTotalPrice(), getCashPayment(), getCashPayment() - getTotalPrice(), productTransactions);
        createTransaction(transaction);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_cart, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        kazeerDatabase = KazeerDatabase.getInstance(getContext());
        adapter = new ProductCartReviewAdapter(this.getContext());
        adapter.setCarts(getCarts());
        rvProductList.setHasFixedSize(true);
        rvProductList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvProductList.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this.getContext(), R.drawable.divider));
        rvProductList.addItemDecoration(dividerItemDecoration);
        tvNumberofItems.setText(getTotalItems() + " items");
        tvTotalPice.setText(formatRupiah(getTotalPrice()));
        btnCreateTransaction.setVisibility(View.INVISIBLE);
        etPay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnCreateTransaction.setVisibility(View.INVISIBLE);
                if (charSequence.length() != 0) {
                    String payString = charSequence.toString().trim();
                    int cashPayment = 0;
                    try {
                        cashPayment = Integer.parseInt(payString);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (cashPayment >= getTotalPrice()) {
                        btnCreateTransaction.setVisibility(View.VISIBLE);
                        setCashPayment(cashPayment);
                    } else {
                        btnCreateTransaction.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public int getCashPayment() {
        return cashPayment;
    }

    public void setCashPayment(int cashPayment) {
        this.cashPayment = cashPayment;
    }

    private ArrayList<Cart> getCarts() {
        return (ArrayList<Cart>) kazeerDatabase.cartDao().getAllCart();
    }

    private int getTotalItems() {
        return kazeerDatabase.cartDao().getTotalItems();
    }

    private int getTotalPrice() {
        return kazeerDatabase.cartDao().getTotalPrice();
    }

    private void createTransaction(Transaction transaction) {
        services.createTransaction(transaction).enqueue(new Callback<SingleResponse<Transaction>>() {
            @Override
            public void onResponse(Call<SingleResponse<Transaction>> call, Response<SingleResponse<Transaction>> response) {
                boolean error = response.body().isError();
                if (!error) {
                    kazeerDatabase.cartDao().clearCart();
                    getParentFragmentManager().popBackStack("Choose Product", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    PaymentChangeDialog paymentChangeDialog = new PaymentChangeDialog(getContext(), transaction.getChange());
                    paymentChangeDialog.show(getParentFragmentManager(), "Payment Change");
                } else {
                    showMessage(response.body().getMessage(), "error");
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<Transaction>> call, Throwable t) {
                t.printStackTrace();
                showMessage(t.getMessage(), "error");
            }
        });
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
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