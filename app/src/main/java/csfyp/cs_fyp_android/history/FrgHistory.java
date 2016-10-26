package csfyp.cs_fyp_android.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.home.AdtEvent;

/**
 * Created by ray on 20/10/2016.
 */


public class FrgHistory extends CustomFragment{
    public FrgHistory(){super();}
    private Toolbar mToolBar;
    private RecyclerView mEventRecyclerView;
    private RecyclerView.Adapter mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;
    public static final String TAG = "History";

    public static FrgHistory newInstance() {

        Bundle args = new Bundle();

        FrgHistory fragment = new FrgHistory();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v  = inflater.inflate(R.layout.history_frg, container, false);
        mToolBar = (Toolbar) v.findViewById(R.id.historyToolBar);
        mToolBar.setTitle("History");
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mEventRecyclerView = (RecyclerView) v.findViewById(R.id.historyRV);
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtEvent();
        mEventRecyclerView.setAdapter(mEventAdapter);
        return v;
    }
}
