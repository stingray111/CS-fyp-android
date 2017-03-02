package csfyp.cs_fyp_android.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.CustomMapFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.HomeFrgTestBinding;

public class FrgHomeTest extends CustomMapFragment {

    private HomeFrgTestBinding mDataBinding;

    public FrgHomeTest() {
    }

    public static FrgHomeTest newInstance() {

        Bundle args = new Bundle();

        FrgHomeTest fragment = new FrgHomeTest();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.home_frg_test, container, false);
        View v = mDataBinding.getRoot();

        mMapView = mDataBinding.homeTestMap;
        MainActivity parentActivity = (MainActivity) getActivity();
        super.onCreateView(inflater, container, savedInstanceState);


        //Setting up Action Bar
        AppBarLayout appBarLayout = mDataBinding.appBarLayout;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(AppBarLayout appBarLayout) {
                return false;
            }
        });

        CollapsingToolbarLayout test = mDataBinding.collapsingToolbar;
        Toolbar bar = mDataBinding.toolbar;
        parentActivity.setSupportActionBar(bar);
        bar.setNavigationIcon(R.drawable.ic_hamburger);
        test.setTitle("Home");
        test.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));



        //parentActivity.setSupportActionBar();
        return v;
    }
}
