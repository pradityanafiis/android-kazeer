package com.praditya.kazeer.view.ui;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.Services;
import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.database.KazeerDatabase;
import com.praditya.kazeer.model.Customer;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.ChooseCustomerAdapter;
import com.praditya.kazeer.view.adapter.ChooseProductAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseCustomerFragment extends Fragment implements ChooseCustomerAdapter.OnClickCallback {
    @BindView(R.id.rv_customer)
    RecyclerView rvCustomer;
    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    @BindView(R.id.tv_selected_customer_name)
    TextView tvCustomerName;

    @OnClick(R.id.cv_selected_customer)
    void finalizeCart() {
        if (customer == null) {
            showMessage("Please select customer!", "error");
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("customer", customer);
            ReviewCartFragment fragment = new ReviewCartFragment();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.transaction_fragment, fragment)
                    .addToBackStack("Choose Customer")
                    .commit();
        }
    }

    private ChooseCustomerAdapter adapter;
    private Services services = ApiClient.getServices();
    private Customer customer = null;

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_customer, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        setCustomer(null);
        adapter = new ChooseCustomerAdapter(this.getContext());
        adapter.setOnClickCallback(this);
        rvCustomer.setHasFixedSize(true);
        rvCustomer.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvCustomer.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this.getContext(), R.drawable.divider));
        rvCustomer.addItemDecoration(dividerItemDecoration);
        refreshCustomer();
    }

    private void refreshCustomer() {
        showLoading(true);
        services.getCustomer().enqueue(new Callback<MultipleResponse<Customer>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Customer>> call, Response<MultipleResponse<Customer>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    adapter.setCustomers(response.body().getData());
                } else {
                    showMessage(response.body().getMessage(), "error");
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Customer>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage(), "error");
            }
        });
    }

    private void showMessage(String message, String type) {
        Toast toast = null;
        if (type == "success") {
            toast = Toasty.success(this.getContext(), message);
        } else if (type == "error") {
            toast = Toasty.error(this.getContext(), message);
        } else if (type == "warning") {
            toast = Toasty.warning(this.getContext(), message);
        } else {
            toast = Toasty.normal(this.getContext(), message);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    private void showLoading(boolean visible) {
        if (visible)
            progressBar.setVisibility(ProgressBar.VISIBLE);
        else
            progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void selectCustomer(Customer customer) {
        setCustomer(customer);
        tvCustomerName.setText(customer.getName());
    }
}
