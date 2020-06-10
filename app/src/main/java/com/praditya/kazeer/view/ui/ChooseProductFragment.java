package com.praditya.kazeer.view.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.Services;
import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.database.KazeerDatabase;
import com.praditya.kazeer.model.Cart;
import com.praditya.kazeer.model.Category;
import com.praditya.kazeer.model.Product;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.CategoryFilterAdapter;
import com.praditya.kazeer.view.adapter.ChooseProductAdapter;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseProductFragment extends Fragment implements ChooseProductAdapter.OnClickCallback {
    private ChooseProductAdapter adapter;
    private CategoryFilterAdapter filterAdapter;
    private Services services = ApiClient.getServices();
    private KazeerDatabase kazeerDatabase;
    @BindView(R.id.rv_product)
    RecyclerView rvProduct;
    @BindView(R.id.rv_category_filter)
    RecyclerView rvCategoryFilter;
    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_total_items)
    TextView tvTotalItems;
    @OnClick(R.id.cv_cart)
    void chooseCustomer() {
        if (isCartEmpty()) {
            showMessage("Cart empty!", "error");
        } else {
            ChooseCustomerFragment fragment = new ChooseCustomerFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.transaction_fragment, fragment)
                    .addToBackStack("Choose Product")
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_product, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_product:
                SearchView searchView = (SearchView) item.getActionView();
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
                break;
            case R.id.clear_cart:
                kazeerDatabase.cartDao().clearCart();
                showMessage("Cart cleared!", "success");
                getCategory();
                refreshProduct(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        kazeerDatabase = KazeerDatabase.getInstance(getContext());

        filterAdapter = new CategoryFilterAdapter(this.getContext());
        rvCategoryFilter.setHasFixedSize(true);
        rvCategoryFilter.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false));
        rvCategoryFilter.setAdapter(filterAdapter);

        adapter = new ChooseProductAdapter(this.getContext());
        adapter.setOnClickCallback(this);
        rvProduct.setHasFixedSize(true);
        rvProduct.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvProduct.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this.getContext(), R.drawable.divider));
        rvProduct.addItemDecoration(dividerItemDecoration);
        getCategory();
        refreshProduct(0);
    }

    private void refreshProduct(int categoryId) {
        showLoading(true);
        services.getProductAvailable(categoryId).enqueue(new Callback<MultipleResponse<Product>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Product>> call, Response<MultipleResponse<Product>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    adapter.setProducts(response.body().getData());
                } else {
                    showMessage(response.body().getMessage(), "error");
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Product>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage(), "error");
            }
        });
        setCartDetail();
    }

    private void getCategory() {
        services.getCategory().enqueue(new Callback<MultipleResponse<Category>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Category>> call, Response<MultipleResponse<Category>> response) {
                boolean error = response.body().isError();
                if (!error) {
                    filterAdapter.setCategories(response.body().getData());
                    filterAdapter.setOnClickCallBack(new CategoryFilterAdapter.OnClickCallBack() {
                        @Override
                        public void changeCategory(int categoryId) {
                            refreshProduct(categoryId);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Category>> call, Throwable t) {
                t.printStackTrace();
                showMessage(t.getMessage(), "error");
            }
        });
    }

    private boolean isCartEmpty() {
        int row = kazeerDatabase.cartDao().countCart();
        if (row > 0) {
            return false;
        } else {
            return true;
        }
    }

    private Cart getSingleCart(Product product) {
        Cart singleCart = kazeerDatabase.cartDao().getSingleCart(product.getId());
        return singleCart;
    }

    private boolean isAvailable(int stock, int quantity) {
        if (stock >= quantity)
            return true;
        else
            return false;
    }

    private void setCartDetail() {
        int totalItems = kazeerDatabase.cartDao().getTotalItems();
        int totalPrice = kazeerDatabase.cartDao().getTotalPrice();
        tvTotalItems.setText(totalItems + " Items");
        tvTotalPrice.setText(formatRupiah(totalPrice));
    }

    @Override
    public void addToCart(Product product) {
        Cart cart = getSingleCart(product);
        if (cart == null) {
            cart = new Cart(product.getId(), product.getName(), 1, product.getPrice(), product.getPrice());
            if (isAvailable(product.getStock(), cart.getQuantity())) {
                kazeerDatabase.cartDao().addToCart(cart);
            } else {
                showMessage("Product out of stock!", "warning");
            }
        } else {
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getPrice());
            if (isAvailable(product.getStock(), cart.getQuantity())) {
                kazeerDatabase.cartDao().updateCart(cart);
            } else {
                showMessage("Product out of stock!", "warning");
            }
        }
        setCartDetail();
    }

    @Override
    public void removeFromCart(Product product) {
        Cart cart = getSingleCart(product);
        if (cart == null) {
            showMessage("Cart not exist", "error");
        } else {
            if (cart.getQuantity() > 1) {
                cart.setQuantity(cart.getQuantity() - 1);
                cart.setTotalPrice(cart.getQuantity() * cart.getPrice());
                kazeerDatabase.cartDao().updateCart(cart);
            } else {
                kazeerDatabase.cartDao().removeFromCart(cart);
            }
            setCartDetail();
        }
    }

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
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
}
