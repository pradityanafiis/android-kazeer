package com.praditya.kazeer.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.Services;
import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.api.response.SingleResponse;
import com.praditya.kazeer.model.Customer;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.CustomerAdapter;
import com.praditya.kazeer.view.dialog.CustomerDialog;
import com.praditya.kazeer.view.dialog.ShowCustomerDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerActivity extends AppCompatActivity implements CustomerAdapter.OnClickCallback, CustomerDialog.DialogListener {
    @BindView(R.id.rv_customer) RecyclerView rvCustomer;
    @BindView(R.id.progress_circular) ProgressBar progressBar;
    private CustomerAdapter adapter;
    private Services services = ApiClient.getServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        getSupportActionBar().setTitle("Manage Customer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_black_24);
        ButterKnife.bind(this);

        init();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void init() {
        adapter = new CustomerAdapter(CustomerActivity.this);
        adapter.setOnClickCallback(this);
        rvCustomer.setHasFixedSize(true);
        rvCustomer.setLayoutManager(new LinearLayoutManager(CustomerActivity.this));
        rvCustomer.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(CustomerActivity.this, R.drawable.divider));
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
                if (!error){
                    ArrayList<Customer> customers = response.body().getData();
                    adapter.setCustomers(customers);
                }else {
                    showMessage(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Customer>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @OnClick(R.id.btn_create_customer)
    void openCreateDialog() {
        CustomerDialog customerDialog = new CustomerDialog("Create Customer", false);
        customerDialog.show(getSupportFragmentManager(), "Customer");
    }

    @Override
    public void createCustomer(Customer customer) {
        showLoading(true);
        services.createCustomer(customer).enqueue(new Callback<SingleResponse<Customer>>() {
            @Override
            public void onResponse(Call<SingleResponse<Customer>> call, Response<SingleResponse<Customer>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    refreshCustomer();
                }
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Customer>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void destroyCustomer(Customer customer) {
        showLoading(true);
        services.deleteCustomer(customer.getId()).enqueue(new Callback<SingleResponse<Customer>>() {
            @Override
            public void onResponse(Call<SingleResponse<Customer>> call, Response<SingleResponse<Customer>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    refreshCustomer();
                }
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Customer>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void updateCustomer(Customer customer) {
        CustomerDialog customerDialog = new CustomerDialog("Edit Customer", true);
        customerDialog.setCustomer(customer);
        customerDialog.show(getSupportFragmentManager(), "Customer");
    }

    @Override
    public void editCustomer(Customer customer) {
        showLoading(true);
        services.editCustomer(customer.getId(), customer).enqueue(new Callback<SingleResponse<Customer>>() {
            @Override
            public void onResponse(Call<SingleResponse<Customer>> call, Response<SingleResponse<Customer>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    refreshCustomer();
                }
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Customer>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void showCustomer(Customer customer) {
        ShowCustomerDialog showCustomerDialog = new ShowCustomerDialog(this, customer);
        showCustomerDialog.show(getSupportFragmentManager(), "Show Customer");

    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showLoading(boolean visible) {
        if (visible) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }else {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }
}
