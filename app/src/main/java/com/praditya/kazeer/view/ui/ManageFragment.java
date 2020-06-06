package com.praditya.kazeer.view.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.praditya.kazeer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.manage_customer)
    public void openManageCustomer() {
        startActivity(new Intent(this.getActivity(), CustomerActivity.class));
    }

    @OnClick(R.id.manage_category)
    public void openManageCategory() {
        startActivity(new Intent(this.getActivity(), CategoryActivity.class));
    }

    @OnClick(R.id.manage_product)
    public void openProductCategory() {
        startActivity(new Intent(this.getActivity(), ProductActivity.class));
    }
}
