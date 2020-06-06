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
import com.praditya.kazeer.model.Category;
import com.praditya.kazeer.view.dialog.CategoryDialog;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.CategoryAdapter;
import com.praditya.kazeer.view.dialog.CategoryDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity implements CategoryDialog.DialogListener {
    @BindView(R.id.rv_category) RecyclerView rvCategory;
    @BindView(R.id.progress_circular) ProgressBar progressBar;
    private CategoryAdapter adapter;
    private Services services = ApiClient.getServices();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportActionBar().setTitle("Manage Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_black_24);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        adapter = new CategoryAdapter(CategoryActivity.this);
        rvCategory.setHasFixedSize(true);
        rvCategory.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
        rvCategory.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(CategoryActivity.this, R.drawable.divider));
        rvCategory.addItemDecoration(dividerItemDecoration);

        refreshCategory();
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

    @OnClick(R.id.btn_create_category)
    public void openCreateDialog() {
        CategoryDialog categoryDialog = new CategoryDialog("Create Category", false);
        categoryDialog.show(getSupportFragmentManager(), "Category");
    }

    private void openEditDialog(Category category) {
        CategoryDialog categoryDialog = new CategoryDialog("Edit Category", true);
        categoryDialog.setCategory(category);
        categoryDialog.show(getSupportFragmentManager(), "Category");
    }

    private void refreshCategory() {
        showLoading(true);
        services.getCategory().enqueue(new Callback<MultipleResponse<Category>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Category>> call, Response<MultipleResponse<Category>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    ArrayList<Category> categories = response.body().getData();
                    adapter.setCategories(categories);
                    adapter.setOnClick(new CategoryAdapter.OnClickCallback() {
                        @Override
                        public void destroyCategory(Category category) {
                            deleteCategory(category);
                        }

                        @Override
                        public void updateCategory(Category category) {
                            openEditDialog(category);
                        }
                    });
                }else {
                    showMessage(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Category>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void createCategory(Category category) {
        showLoading(true);
        services.createCategory(category).enqueue(new Callback<SingleResponse<Category>>() {
            @Override
            public void onResponse(Call<SingleResponse<Category>> call, Response<SingleResponse<Category>> response) {
                showLoading(false);
                showMessage(response.body().getMessage());
                boolean error = response.body().isError();
                if (!error) {
                    refreshCategory();
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<Category>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void editCategory(Category category) {
        showLoading(true);
        services.editCategory(category.getId(), category).enqueue(new Callback<SingleResponse<Category>>() {
            @Override
            public void onResponse(Call<SingleResponse<Category>> call, Response<SingleResponse<Category>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    refreshCategory();
                }
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Category>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage());
            }
        });
    }

    private void deleteCategory(Category category) {
        showLoading(true);
        services.deleteCategory(category.getId()).enqueue(new Callback<SingleResponse<Category>>() {
            @Override
            public void onResponse(Call<SingleResponse<Category>> call, Response<SingleResponse<Category>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    refreshCategory();
                }
                showMessage(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<SingleResponse<Category>> call, Throwable t) {
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
