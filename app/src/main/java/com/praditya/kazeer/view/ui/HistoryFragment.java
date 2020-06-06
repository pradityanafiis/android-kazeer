package com.praditya.kazeer.view.ui;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.Services;
import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.model.Transaction;
import com.praditya.kazeer.view.DividerItemDecorator;
import com.praditya.kazeer.view.adapter.TransactionAdapter;
import com.praditya.kazeer.view.dialog.DetailTransactionDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements TransactionAdapter.OnClickCallback{
    private TransactionAdapter adapter;
    private Services services = ApiClient.getServices();
    @BindView(R.id.rv_transaction)
    RecyclerView rvTransaction;
    @BindView(R.id.progress_circular)
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        adapter = new TransactionAdapter(this.getContext());
        adapter.setOnClickCallback(this);
        rvTransaction.setHasFixedSize(true);
        rvTransaction.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvTransaction.setAdapter(adapter);
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this.getContext(), R.drawable.divider));
        rvTransaction.addItemDecoration(dividerItemDecoration);
        refreshTransaction();
    }

    private void refreshTransaction() {
        showLoading(true);
        services.getTransaction().enqueue(new Callback<MultipleResponse<Transaction>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Transaction>> call, Response<MultipleResponse<Transaction>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    adapter.setTransactions(response.body().getData());
                } else {
                    showMessage(response.body().getMessage(), "error");
                }
            }

            @Override
            public void onFailure(Call<MultipleResponse<Transaction>> call, Throwable t) {
                showLoading(false);
                t.printStackTrace();
                showMessage(t.getMessage(), "error");
            }
        });
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

    private void showLoading(boolean visible) {
        if (visible)
            progressBar.setVisibility(ProgressBar.VISIBLE);
        else
            progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void openTransactionDetail(Transaction transaction) {
        DetailTransactionDialog dialog = new DetailTransactionDialog(transaction, this.getContext());
        dialog.show(getParentFragmentManager(), "Detail Transaction");
    }
}
