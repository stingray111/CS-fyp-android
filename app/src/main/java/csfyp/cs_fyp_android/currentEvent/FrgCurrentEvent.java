package csfyp.cs_fyp_android.currentEvent;

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

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.home.AdtEvent;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventFilter;
import csfyp.cs_fyp_android.model.respond.EventRespond;
import retrofit2.Call;
import retrofit2.Response;

public class FrgCurrentEvent extends CustomFragment implements LoaderManager.LoaderCallbacks<List<Event>>{
    public static final String TAG = "CurrentEvent";
    public static final int CURRENT_EVENT_LOADER_ID = 4;

    private Toolbar mToolBar;
    private RecyclerView mEventRecyclerView;
    private AdtEvent mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;
    private List<Event> mData;
    private Response<EventRespond> mEventRespond;


    public FrgCurrentEvent() {
        super();
    }

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
        mEventAdapter = new AdtEvent();
        mEventRecyclerView.setAdapter(mEventAdapter);

        getLoaderManager().initLoader(CURRENT_EVENT_LOADER_ID, null, this);

        return v;
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new CustomLoader<List<Event>>(getContext()) {
            @Override
            public List<Event> loadInBackground() {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<EventRespond> call = httpService.getEvents(new EventFilter(((MainActivity)getActivity()).getmUserId(), 3));
                try {
                    mEventRespond = call.execute();
                    if(mEventRespond.isSuccessful() && mEventRespond.body().getErrorMsg() == null) {
                        return mEventRespond.body().getEvents();
                    } else
                        return null;
                } catch(Exception e) {
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        mData = data;
        if(mData != null) {
            mEventAdapter.setmEventList(data);
            mEventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

    }
}
