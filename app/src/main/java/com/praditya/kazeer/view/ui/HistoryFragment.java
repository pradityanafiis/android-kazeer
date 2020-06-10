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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements TransactionAdapter.OnClickCallback {
    private TransactionAdapter adapter;
    private Services services = ApiClient.getServices();
    @BindView(R.id.rv_transaction)
    RecyclerView rvTransaction;
    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.btn_history_today)
    Button btnHistoryToday;
    @BindView(R.id.btn_history_thisweek)
    Button btnHistoryThisweek;
    @BindView(R.id.btn_history_thismonth)
    Button btnHistoryThismonth;
    @BindView(R.id.btn_history_all)
    Button btnHistoryAll;
    @OnClick({R.id.btn_history_all, R.id.btn_history_today, R.id.btn_history_thisweek, R.id.btn_history_thismonth})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_history_all:
                setButtonActive(btnHistoryAll);
                refreshTransaction("all");
                break;
            case R.id.btn_history_today:
                setButtonActive(btnHistoryToday);
                refreshTransaction("today");
                break;
            case R.id.btn_history_thisweek:
                setButtonActive(btnHistoryThisweek);
                refreshTransaction("this_week");
                break;
            case R.id.btn_history_thismonth:
                setButtonActive(btnHistoryThismonth);
                refreshTransaction("this_month");
                break;
        }
    }

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
        refreshTransaction("today");
        setButtonActive(btnHistoryToday);
    }

    private void refreshTransaction(String range) {
        showLoading(true);
        services.getTransaction(range).enqueue(new Callback<MultipleResponse<Transaction>>() {
            @Override
            public void onResponse(Call<MultipleResponse<Transaction>> call, Response<MultipleResponse<Transaction>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    tvMessage.setVisibility(View.INVISIBLE);
                    adapter.setTransactions(response.body().getData());
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                    tvMessage.setText("Transaction empty");
                    adapter.clear();
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

    @Override
    public void openTransactionDetail(Transaction transaction) {
        DetailTransactionDialog dialog = new DetailTransactionDialog(transaction, this.getContext());
        dialog.show(getParentFragmentManager(), "Detail Transaction");
    }

    private void setButtonActive(Button button) {
        btnHistoryToday.setEnabled(true);
        btnHistoryToday.setAlpha(1);
        btnHistoryThisweek.setEnabled(true);
        btnHistoryThisweek.setAlpha(1);
        btnHistoryThismonth.setEnabled(true);
        btnHistoryThismonth.setAlpha(1);
        btnHistoryAll.setEnabled(true);
        btnHistoryAll.setAlpha(1);
        button.setEnabled(false);
        button.setAlpha(0.3f);
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
}
