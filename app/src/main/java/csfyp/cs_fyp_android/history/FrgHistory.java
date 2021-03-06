package csfyp.cs_fyp_android.history;

import android.databinding.DataBindingUtil;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.HistoryFrgBinding;
import csfyp.cs_fyp_android.event.FrgPassedEvent;
import csfyp.cs_fyp_android.home.AdtEvent;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.SwitchFrg;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import retrofit2.Call;
import retrofit2.Response;


public class FrgHistory extends CustomFragment implements LoaderManager.LoaderCallbacks<List<Event>>{

    public static final String TAG = "History";
    public static final int HISTORY_LOADER_ID = 3;

    private Toolbar mToolBar;
    private RecyclerView mEventRecyclerView;
    private AdtEvent mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;
    private List<Event> mData;
    private Response<EventListRespond> mEventRespond;
    private HistoryFrgBinding mDatabinding;


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
        mDatabinding = DataBindingUtil.inflate(inflater, R.layout.history_frg, container, false);
        View v  = mDatabinding.getRoot();
        mToolBar = mDatabinding.historyToolBar;
        mToolBar.setTitle(R.string.title_history);
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mEventRecyclerView = mDatabinding.rvHistory;
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtEvent(AdtEvent.HISTORY_MODE);
        mEventRecyclerView.setAdapter(mEventAdapter);

        getLoaderManager().initLoader(HISTORY_LOADER_ID, null, this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new CustomLoader<List<Event>>(getContext()) {
            @Override
            public List<Event> loadInBackground() {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<EventListRespond> call = httpService.getEvents(new EventListRequest(((MainActivity)getActivity()).getmUserId(), 2));
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
            mDatabinding.historyProgessBar.setVisibility(View.GONE);
            if (mData.size() == 0) {
                mDatabinding.historyEmptyMsg.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SwitchFrg event) {
        if (event.getFromTag().equals(FrgHistory.TAG)) {
            switchFragment(this, FrgPassedEvent.newInstance(event.getBundle().getInt("eventId")));
        }
    }

}
