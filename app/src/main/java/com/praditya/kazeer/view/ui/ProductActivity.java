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
import com.praditya.kazeer.model.Product;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.ProductAdapter;
import com.praditya.kazeer.view.dialog.CustomerDialog;
import com.praditya.kazeer.view.dialog.ProductDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductActivity extends AppCompatActivity implements ProductDialog.DialogListener {
    @BindView(R.id.rv_product) RecyclerView rvProduct;
    @BindView(R.id.progress_circular) ProgressBar progressBar;
    private ProductAdapter adapter;
    private Services services = ApiClient.getServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle("Manage Product");
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
        adapter = new ProductAdapter(ProductActivity.this);
        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(new LinearLayoutManager(ProductActivity.this));
        rvProduct.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(ProductActivity.this, R.drawable.divider));
        rvProduct.addItemDecoration(dividerItemDecoration);
        refreshProduct();
    }

    @OnClick(R.id.btn_create_product)
    void openCreateDialog() {
        ProductDialog productDialog = new ProductDialog("Create Product", false);
        productDialog.show(getSupportFragmentManager(), "Product");
    }

    private void openEditDialog(Product product) {
        ProductDialog productDialog = new ProductDialog("Edit Product", true);
        productDialog.setProduct(product);
        productDialog.show(getSupportFragmentManager(), "Product");
    }

    private void refreshProduct() {
        showLoading(true);
        services.getProduct().enqueue(new Callback<MultipleResponse<Product>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Product>> call, Response<MultipleResponse<Product>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    adapter.setProducts(response.body().getData());
                    adapter.setOnClickCallback(new ProductAdapter.OnClickCallback() {
                        @Override
                        public void destroyProduct(Product product) {
                            deleteProduct(product);
                        }

                        @Override
                        public void updateProduct(Product product) {
                            openEditDialog(product);
                        }
                    });
                }else {
                    showMessage(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Product>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void createProduct(Product product) {
        showLoading(true);
        services.createProduct(product).enqueue(new Callback<SingleResponse<Product>>() {
            @Override
            public void onResponse(Call<SingleResponse<Product>> call, Response<SingleResponse<Product>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error)
                    refreshProduct();
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Product>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void editProduct(Product product) {
        showLoading(true);
        services.editProduct(product.getId(), product).enqueue(new Callback<SingleResponse<Product>>() {
            @Override
            public void onResponse(Call<SingleResponse<Product>> call, Response<SingleResponse<Product>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error)
                    refreshProduct();
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Product>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    private void deleteProduct(Product product) {
        showLoading(true);
        services.deleteProduct(product.getId()).enqueue(new Callback<SingleResponse<Product>>() {
            @Override
            public void onResponse(Call<SingleResponse<Product>> call, Response<SingleResponse<Product>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    refreshProduct();
                }
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Product>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
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
