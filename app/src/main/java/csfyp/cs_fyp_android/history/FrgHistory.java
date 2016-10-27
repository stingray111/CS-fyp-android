package csfyp.cs_fyp_android.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.home.AdtEvent;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.model.Event;


public class FrgHistory extends CustomFragment implements LoaderManager.LoaderCallbacks<List<Event>>{

    public static final String TAG = "History";
    public static final int HISTORY_LOADER_ID = 3;

    private Toolbar mToolBar;
    private RecyclerView mEventRecyclerView;
    private AdtEvent mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;
    private List<Event> mData;

    public FrgHistory(){

    }

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

        mEventRecyclerView = (RecyclerView) v.findViewById(R.id.rvHistory);
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtEvent();
        mEventRecyclerView.setAdapter(mEventAdapter);

        getLoaderManager().initLoader(HISTORY_LOADER_ID, null, this);

        return v;
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new CustomLoader<List<Event>>(getContext()) {
            @Override
            public List<Event> loadInBackground() {
                List<Event> list = new ArrayList<>();
                list.add(new Event("My 1st Event", new LatLng(22.363843, 114.121513), "ken31ee", 2, 10, "This is my first event"));
                list.add(new Event("My 2nd Event", new LatLng(22.337171, 114.163399), "stingRay", 10, 20, "This is my second event"));
                list.add(new Event("My 3rd Event", new LatLng(22.352991, 114.103489), "ken31ee", 3, 10, "This is my third event"));
                list.add(new Event("My 4th Event", new LatLng(22.381419, 114.194298), "stingRay", 3, 10, "This is my fourth event"));
                return list;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        mEventAdapter.setmEventList(data);
        mData = data;
        mEventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

    }
}
