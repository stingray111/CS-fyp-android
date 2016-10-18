package csfyp.cs_fyp_android.currentEvent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.home.AdtHome;

/**
 * Created by ray on 13/10/2016.
 */

public class FrgCurrentEvent extends CustomFragment {
    public FrgCurrentEvent() {
        super();
    }
    private Toolbar mToolBar;
    private RecyclerView mEventRecyclerView;
    private RecyclerView.Adapter mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;
    public static final String TAG = "CurrentEvent";

    public static FrgCurrentEvent newInstance() {

        Bundle args = new Bundle();

        FrgCurrentEvent fragment = new FrgCurrentEvent();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v  = inflater.inflate(R.layout.current_event_frg, container, false);
        mToolBar = (Toolbar) v.findViewById(R.id.currentEventToolBar);
        mToolBar.setTitle("Current Event");
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mEventRecyclerView = (RecyclerView) v.findViewById(R.id.currentEventRV);
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtHome();
        mEventRecyclerView.setAdapter(mEventAdapter);
        return v;
    }
}
