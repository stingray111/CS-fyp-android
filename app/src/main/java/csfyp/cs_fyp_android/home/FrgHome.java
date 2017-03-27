package csfyp.cs_fyp_android.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import csfyp.cs_fyp_android.CustomMapFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.about.FrgAbout;
import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.currentEvent.FrgCurrentEvent;
import csfyp.cs_fyp_android.databinding.HomeFrgBinding;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.history.FrgHistory;
import csfyp.cs_fyp_android.lib.ClusterableMarker;
import csfyp.cs_fyp_android.lib.CustomBatchLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.SSL;
import csfyp.cs_fyp_android.lib.Utils;
import csfyp.cs_fyp_android.lib.eventBus.ScrollEvent;
import csfyp.cs_fyp_android.lib.eventBus.SwitchFrg;
import csfyp.cs_fyp_android.model.BatchLoaderBundle;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import csfyp.cs_fyp_android.newEvent.FrgNewEvent;
import csfyp.cs_fyp_android.profile.FrgProfile;
import csfyp.cs_fyp_android.setting.FrgSetting;
import retrofit2.Call;
import retrofit2.Response;

public class FrgHome extends CustomMapFragment implements LoaderManager.LoaderCallbacks<BatchLoaderBundle> {

    private static final int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 12345;

    private static final int SORT_DISTANCE =1;
    private static final int SORT_POP =2;
    private static final int SORT_NAME =3;
    private int mSortState = 1;

    public static final int HOME_LOADER_ID = 1;
    public static final int HOME_LOCATION_SETTING_CALLBACK = 10;
    public static final int HOME_PERMISSION_CALLBACK = 11;
    public static final String TAG = "HomeFragment";

    private boolean mIsPanelExpanded;
    private boolean mIsPanelAnchored;
    private boolean mIsLoadFinished = false;
    private boolean mIsBLoaderLoaded = false;

    private List<Event> mData;

    private HomeFrgBinding mDataBinding;

    private LoaderManager.LoaderCallbacks<List<Event>> mCallbacks;

    // For Toolbar
    private Toolbar mToolBar;
    private Menu mMenu;

    // For Event Recycler View
    private RecyclerView mEventRecyclerView;
    private AdtEvent mEventAdapter;
    private LinearLayoutManager mEventLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private BLoader mbloader;
    private static long mStartAt;
    private int mOffset;
    private Location mCurrentListLocation;
    private ImageButton mSortButton;
    public GoogleSignInAccount googleAccout;

    // For Left Drawer
    private DrawerLayout mDrawerLayout;

    // For Swipe Layout
    private SwipeRefreshLayout homeRefreshSwipe;

    // For Sliding Up Panel
    private SlidingUpPanelLayout mLayout;

    // For HTTP

    public FrgHome() {}

    public static FrgHome newInstance() {

        Bundle args = new Bundle();

        FrgHome fragment = new FrgHome();
        fragment.setArguments(args);
        return fragment;
    }

    private void populateMapMarker() {
        if (mData != null && mIsMapReady && mIsLoadFinished) {
            mClusterManager.clearItems();
            for (Event item : mData) {
                ClusterableMarker marker = new ClusterableMarker(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker), item.getLatitude(), item.getLongitude(), item.getName(), item.getHolder().getDisplayName() + "&" + item.getStartTime_formated() + "&" + (item.getCurrentPpl()+1) + "&" + item.getMaxPpl() + "&" + item.getId());
                mClusterManager.addItem(marker);
            }
            mClusterManager.cluster();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        isUseLocationService = true;
        super.onCreate(savedInstanceState);

        // initialize Pull-up Panel, try getting the last status
        if (savedInstanceState != null) {
            mIsPanelExpanded = savedInstanceState.getBoolean("isPanelExpanded");
        }

        InputStream is = (InputStream) this.getResources().openRawResource(R.raw.server);
        try {
            SSL.setServerCert(is);
        }catch (java.io.IOException e){
            Toast.makeText(getActivity(),"SSL Error: please restart the app", Toast.LENGTH_LONG).show();
        }

        //chat messaging service
        if (!Utils.canDrawOverlays(getContext())){
            requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
        } else {
            chatHeadInit();
        }

    }

    private void chatHeadInit(){
        Intent serviceIntent = new Intent(getMainActivity(), ChatService.class);
        getMainActivity().bindService(serviceIntent, getMainActivity().connection, Context.BIND_AUTO_CREATE);
    }

    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG){
            if (!Utils.canDrawOverlays(getContext())) {
            }else{
                chatHeadInit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPanelExpanded", mIsPanelExpanded);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.home_frg, container, false);
        mDataBinding.setHandlers(this);
        View v = mDataBinding.getRoot();
        MainActivity parentActivity = (MainActivity) getActivity();

        mMapView = mDataBinding.homeMap;
        super.onCreateView(inflater, container, savedInstanceState);


        // Setting up Action Bar
        mToolBar = mDataBinding.homeToolbar;
        mToolBar.setTitle("Home");
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_hamburger);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // Setting up Pull-up Panel
        if (mIsPanelExpanded) {
            Fragment expandPanelAppBar = FrgExpandPanelAppBar.newInstance();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.frg_container, expandPanelAppBar);
            ft.commit();
        } else {
            Fragment notExpandPanelAppBar = FrgNotExpandPanelAppBar.newInstance();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.add(R.id.frg_container, notExpandPanelAppBar);
            ft.commit();
        }

        // Setting up Navigation Drawer
        mDrawerLayout = mDataBinding.drawerLayout;

        // Setting up swipe layout
        homeRefreshSwipe = mDataBinding.homeRefreshSwipe;
        homeRefreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeRefreshSwipe.setRefreshing(true);
                mIsLoadFinished = false;
                EventBus.getDefault().post(new ScrollEvent(ScrollEvent.FIRST));
            }
        });

        // Setting up RcyclerView for event
        mEventRecyclerView = mDataBinding.rvEvent;
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mScrollListener = new EndlessRecyclerViewScrollListener(mEventLayoutManager){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                EventBus.getDefault().post(new ScrollEvent(ScrollEvent.OTHERS));
            }
        };
        mEventRecyclerView.addOnScrollListener(mScrollListener);
        mEventAdapter = new AdtEvent(AdtEvent.HOME_MODE);
        mEventRecyclerView.setAdapter(mEventAdapter);

        // load propic
        Picasso.with(getContext())
                .load(((MainActivity)getActivity()).getmSelf().getProPic())
                .resize(100,100)
                .centerCrop()
                .placeholder(R.drawable.ic_propic_big)
                .into(mDataBinding.homeProPic);

        mSortButton = mDataBinding.sortButton;
        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortMenu();
            }
        });


        // set self user
        mDataBinding.homeUsername.setText(((MainActivity)getActivity()).getmSelf().getDisplayName());

        getLoaderManager().initLoader(HOME_LOADER_ID,null,this);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setting up listener for Pull-up Panel
        mLayout = mDataBinding.slidingLayout;
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, previousState + ":" + newState);

                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                    mIsPanelExpanded = true;
                else
                    mIsPanelExpanded = false;

                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED)
                    mIsPanelAnchored = true;
                else
                    mIsPanelAnchored = false;

                if (mIsPanelExpanded) {
                    Fragment expandPanelAppBar = FrgExpandPanelAppBar.newInstance();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.frg_container, expandPanelAppBar);
                    ft.commit();
                } else {
                    Fragment notExpandPanelAppBar = FrgNotExpandPanelAppBar.newInstance();
                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.replace(R.id.frg_container, notExpandPanelAppBar);
                    ft.commit();
                }
            }
        });
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

    public void showSortMenu(){
        PopupMenu popup = new PopupMenu(getContext(),mSortButton);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.option_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mSortState = 1;
                switch (item.getItemId()) {
                    case R.id.sortDistance:
                        mSortState = SORT_DISTANCE;
                        item.setChecked(true);
                        EventBus.getDefault().post(new ScrollEvent(ScrollEvent.FIRST));
                        return true;
                    case R.id.sortPopularity:
                        mSortState = SORT_POP;
                        item.setChecked(true);
                        EventBus.getDefault().post(new ScrollEvent(ScrollEvent.FIRST));
                        return true;
                    case R.id.sortName:
                        mSortState = SORT_NAME;
                        item.setChecked(true);
                        EventBus.getDefault().post(new ScrollEvent(ScrollEvent.FIRST));
                        return true;
                    default:
                        return false;
                }
            }
        });
        switch (mSortState){
            case SORT_DISTANCE:
                popup.getMenu().findItem(R.id.sortDistance).setChecked(true);
                break;
            case SORT_POP:
                popup.getMenu().findItem(R.id.sortPopularity).setChecked(true);
                break;
            case SORT_NAME:
                popup.getMenu().findItem(R.id.sortName).setChecked(true);
                break;
            default:
                popup.getMenu().findItem(R.id.sortDistance).setChecked(true);
        }
        popup.show();
    }


    @Override // TODO: 3/1/2017 override super here
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View myContentView = LayoutInflater.from(getContext()).inflate(
                        R.layout.home_map_item_info_window, null);
                TextView tvInfoWinTitle = ((TextView) myContentView.findViewById(R.id.infoWinTitle));
                tvInfoWinTitle.setText(marker.getTitle());
                if(marker.getSnippet() != null) {
                    String[] temp = marker.getSnippet().split("&");
                    TextView tvInfoWinUsername = ((TextView) myContentView.findViewById(R.id.infoWinUsername));
                    TextView tvInfoWinEventStart = ((TextView) myContentView.findViewById(R.id.infoWinEventStart));
                    TextView tvInfoWinCurrentPpl = ((TextView) myContentView.findViewById(R.id.infoWinCurrentPpl));
                    TextView tvInfoWinMaxPpl = ((TextView) myContentView.findViewById(R.id.infoWinMaxPpl));
                    tvInfoWinUsername.setText(temp[0]);
                    tvInfoWinEventStart.setText(temp[1]);
                    tvInfoWinCurrentPpl.setText(temp[2]);
                    tvInfoWinMaxPpl.setText(temp[3]);
                }
                return myContentView;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        populateMapMarker();
    }

    @Override // TODO: 3/1/2017 override super here
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if(!mIsBLoaderLoaded) {
            mIsBLoaderLoaded = true;
            mCurrentListLocation = location;
            EventBus.getDefault().post(new ScrollEvent(ScrollEvent.FIRST));
        }
    }

    @Override
    public BLoader onCreateLoader(int id, Bundle args) {
        String task;
        if(args != null)
            task = args.getString("task");
        else
            task = BLoader.TASK_FRESH_LOAD;
        if(task.equals(BLoader.TASK_LOAD_MORE)){
            mbloader = new BLoader(getContext(),mStartAt,mOffset,mCurrentListLocation,mData,BLoader.TASK_LOAD_MORE,mSortState);
        }else{//refresh
            homeRefreshSwipe.setRefreshing(true);
            mCurrentListLocation = mCurrentLocation;
            mbloader = new BLoader(getContext(),mCurrentListLocation,mSortState);
        }
        return mbloader;
    }

    @Override
    public void onLoadFinished(Loader<BatchLoaderBundle> loader, BatchLoaderBundle data) {
        if (data != null) {
            mData = new ArrayList<Event>(data.list);
            mOffset = mData.size();
            if (data.getStatus() == BatchLoaderBundle.LIST_NOT_END) {
                //add the loading item
                Event fake = new Event();
                fake.setId(-1);
                data.list.add(fake);
            }
            mEventAdapter.setmEventList(data.list);
            if(mOffset < 30) {
                if(mOffset != 0) mEventLayoutManager.scrollToPosition(0);
                mScrollListener.resetState();
            }
            mIsLoadFinished = true;
            mEventAdapter.notifyDataSetChanged();
            populateMapMarker();
            homeRefreshSwipe.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<BatchLoaderBundle> loader) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            getLoaderManager().destroyLoader(HOME_LOADER_ID);
        } else {
            getLoaderManager().restartLoader(HOME_LOADER_ID, null, this);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        super.onInfoWindowClick(marker);
        Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
        String[] temp = marker.getSnippet().split("&");
        switchFragment(FrgEvent.newInstance(Integer.parseInt(temp[4])));
    }

    public void onClickNewEvent(View view) {
        if(mCurrentLocation != null)
            switchFragment(FrgNewEvent.newInstance(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
    }

    public void onClickJoined(View view){
        switchFragment(FrgCurrentEvent.newInstance());
    }

    public void onClickSetting(View view){ switchFragment(FrgSetting.newInstance());}

    public void onClickProfile(View view) {
        switchFragment(FrgProfile.newInstance(getMainActivity().getmUserId()));
    }

    public void onClickAbout(View view) {
        switchFragment(FrgAbout.newInstance());
    }

    public void onClickHistory(View view){ switchFragment(FrgHistory.newInstance());}

    private MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

    @Subscribe( threadMode = ThreadMode.MAIN )
    public void onMessageEvent(SwitchFrg event) {
        if (event.getFromTag().equals(TAG)) {
            switchFragment(FrgEvent.newInstance(event.getBundle().getInt("eventId")));
        }
    }

    @Subscribe( threadMode = ThreadMode.MAIN)
    public void onScrollEvent(ScrollEvent se) {
        Bundle temp = new Bundle();
        if (se.getMode() == ScrollEvent.FIRST) {
            temp.putString("task",BLoader.TASK_FRESH_LOAD);
        }
        else if(se.getMode() == ScrollEvent.OTHERS) {
            temp.putString("task",BLoader.TASK_LOAD_MORE);
        }
        getLoaderManager().restartLoader(HOME_LOADER_ID,temp, this);
    }

    public static class BLoader extends CustomBatchLoader<BatchLoaderBundle> {
        public BLoader(Context context,long startAt,int offset,Location currentLocation,List<Event> eventList,String mode,int sortMode) {
            super(context);
            setTaskName(mode);
            setOffset(offset);
            mStartAt = startAt;
            this.mCurrentLocation = currentLocation;
            this.eventList = eventList;
            this.mSortMode = sortMode;
        }
        public BLoader(Context context,Location mCurrentLocation,int sortMode){
            super(context);
            setTaskName(BLoader.TASK_FRESH_LOAD);
            mStartAt = MAX_DATE;
            this.mCurrentLocation = mCurrentLocation;
            setOffset(0);
            eventList = new ArrayList<Event>();
            mSortMode = sortMode;
        }

        public final long MAX_DATE = 4102444800000L;
        private final int FIRST_REQUEST = 0;
        private final int OTHER_REQUEST = 1;
        private Location mCurrentLocation;
        private List<Event> eventList;
        private int mSortMode;

        @Override
        public BatchLoaderBundle loadMore() {
            List<Event> list = homeFrgReqSender(OTHER_REQUEST,mSortMode);
            eventList.addAll(list);
            if(list.size() != 0 ) {
                return new BatchLoaderBundle(eventList, BatchLoaderBundle.LIST_NOT_END);
            }else
                return new BatchLoaderBundle(eventList, BatchLoaderBundle.LIST_END);
        }

        @Override
        public BatchLoaderBundle refreshLoad() {
            return freshLoad();
        }

        @Override
        public BatchLoaderBundle freshLoad() {
            List<Event> temp = homeFrgReqSender(FIRST_REQUEST,mSortMode);
            if(temp == null) {
                return null;
            } else if(eventList == null){
                eventList = new ArrayList<Event>();
            }
            eventList.clear();
            eventList.addAll(temp);
            if (eventList.size() !=  0 )
                return new BatchLoaderBundle(eventList,BatchLoaderBundle.LIST_NOT_END);
            else
                return new BatchLoaderBundle(eventList,BatchLoaderBundle.LIST_END);
        }

        private List<Event> homeFrgReqSender(int mode,int sortMode){
            if(mode == FIRST_REQUEST){
                return homeFrgReqSender(MAX_DATE,0,mode,sortMode);
            }else if(mode == OTHER_REQUEST){
                return homeFrgReqSender(mStartAt,getOffset(),mode,sortMode);
            }
            return null;
        }

        private List<Event> homeFrgReqSender(long startAt, int offset,int mode,int sortMode){
            if(mCurrentLocation != null){
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<EventListRespond> call = httpService.getEvents(new EventListRequest(
                        mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(),
                        1,
                        offset,
                        startAt,
                        sortMode
                ));
                try{
                    Response<EventListRespond> mEventRespond = call.execute();
                    if(mEventRespond.isSuccessful()){
                        if(mEventRespond.body().getErrorMsg() == null){
                            List<Event> temp = mEventRespond.body().getEvents();
                            mStartAt = mEventRespond.body().getStartId();
                            return mEventRespond.body().getEvents();
                        }else{
                            Log.d("here",mEventRespond.body().getErrorMsg());
                        }
                    }else{
                        Log.i("here", "Not 200");
                        //Toast.makeText(getMainActivity(), "Not 200", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    Log.d("here","no internet");
                    e.printStackTrace();
                    //Toast.makeText(getMainActivity(), "You do not have an active internet connection, try again later.", Toast.LENGTH_LONG ).show();
                }
            }else{
                Log.d("here","location fail");
                // Toast.makeText(getMainActivity(), "You do not have an valid location service now, try again later.", Toast.LENGTH_LONG ).show();
            }
            return null;
        }
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(intent, requestCode);
    }
}

