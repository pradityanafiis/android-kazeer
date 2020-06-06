package com.praditya.kazeer.view.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.praditya.kazeer.R;
import com.praditya.kazeer.api.ApiClient;
import com.praditya.kazeer.api.Services;
import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.api.response.SingleResponse;
import com.praditya.kazeer.model.DailyTransaction;
import com.praditya.kazeer.model.TransactionSummary;

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

public class HomeFragment extends Fragment {
    @OnClick(R.id.btn_create_transaction)
    void createTransaction() {
        startActivity(new Intent(getActivity(), TransactionActivity.class));
    }
    @BindView(R.id.cv_transaction_summary)
    CardView cvTransactionSummary;
    @BindView(R.id.tv_num_not)
    TextView tvNumberofTransaction;
    @BindView(R.id.tv_num_aot)
    TextView tvAmountofTransaction;
    @BindView(R.id.cv_transaction_chart)
    CardView cvTransactionChart;
    @BindView(R.id.transaction_chart)
    BarChart barChart;
    @BindView(R.id.progress_circular)
    ProgressBar progressBar;
    private Services services = ApiClient.getServices();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        getTransactionSummary();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTransactionSummary();
    }

    private void init(TransactionSummary transactionSummary) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (DailyTransaction dailyTransaction : transactionSummary.getDailyTransactions()) {
            barEntries.add(new BarEntry(dailyTransaction.getDate(), dailyTransaction.getAmount()));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Amount");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.5f);
        Description description = new Description();
        description.setEnabled(false);

        barChart.animateY(2000);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.setDescription(description);
        barChart.setScaleEnabled(false);
        barChart.invalidate();
        cvTransactionChart.setVisibility(View.VISIBLE);
        tvNumberofTransaction.setText(transactionSummary.getCountTransaction() + " transactions");
        tvAmountofTransaction.setText(formatRupiah(transactionSummary.getSumAmount()));
        cvTransactionSummary.setVisibility(View.VISIBLE);
    }

    private void getTransactionSummary() {
        cvTransactionSummary.setVisibility(View.INVISIBLE);
        cvTransactionChart.setVisibility(View.INVISIBLE);
        showLoading(true);
        services.getTransactionSummary().enqueue(new Callback<SingleResponse<TransactionSummary>>() {
            @Override
            public void onResponse(Call<SingleResponse<TransactionSummary>> call, Response<SingleResponse<TransactionSummary>> response) {
                showLoading(false);
                boolean error = response.body().isError();
                if (!error) {
                    init(response.body().getData());
                } else {
                    showMessage(response.body().getMessage(), "error");
                }
            }

            @Override
            public void onFailure(Call<SingleResponse<TransactionSummary>> call, Throwable t) {
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

    private String formatRupiah(int number) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return numberFormat.format(number);
    }
}
