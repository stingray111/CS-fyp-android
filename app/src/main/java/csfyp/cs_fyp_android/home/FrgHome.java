package csfyp.cs_fyp_android.home;

import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import csfyp.cs_fyp_android.CustomMapFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.about.FrgAbout;
import csfyp.cs_fyp_android.currentEvent.FrgCurrentEvent;
import csfyp.cs_fyp_android.databinding.HomeFrgBinding;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.history.FrgHistory;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import csfyp.cs_fyp_android.newEvent.FrgNewEvent;
import csfyp.cs_fyp_android.profile.FrgProfile;
import csfyp.cs_fyp_android.setting.FrgSetting;
import retrofit2.Call;
import retrofit2.Response;

public class FrgHome extends CustomMapFragment implements LoaderManager.LoaderCallbacks<List<Event>> {

    public static final int HOME_LOADER_ID = 1;
    public static final int HOME_LOCATION_SETTING_CALLBACK = 2;
    public static final int HOME_PERMISSION_CALLBACK = 1;
    public static final String TAG = "HomeFragment";

    private boolean mIsPanelExpanded;
    private boolean mIsPanelAnchored;
    private boolean mIsLoadFinished = false;

    private List<Event> mData;

    private HomeFrgBinding mDataBinding;

    private LoaderManager.LoaderCallbacks<List<Event>> mCallbacks;

    // For Toolbar
    private Toolbar mToolBar;

    // For Event Recycler View
    private RecyclerView mEventRecyclerView;
    private AdtEvent mEventAdapter;
    private RecyclerView.LayoutManager mEventLayoutManager;

    // For Left Drawer
    private DrawerLayout mDrawerLayout;

    // For Sliding Up Panel
    private SlidingUpPanelLayout mLayout;

    // For HTTP
    private Response<EventListRespond> mEventRespond;

    public FrgHome() {}

    public static FrgHome newInstance() {

        Bundle args = new Bundle();

        FrgHome fragment = new FrgHome();
        fragment.setArguments(args);
        return fragment;
    }

    private void populateMapMarker() {
        if (mData != null && mIsMapReady && mIsLoadFinished) {
            mGoogleMap.clear();
            for (Event item : mData) {
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(item.getLatitude(), item.getLongitude()))
                        .title(item.getName())
                        .snippet(item.getHolder().getUserName() + "&" + item.getStartTime_formated() + "&" + (item.getCurrentPpl()+1) + "&" + item.getMaxPpl() + "&" + item.getId())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)));
            }
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

        setHasOptionsMenu(true);

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

        // Setting up RcyclerView for event
        mEventRecyclerView = mDataBinding.rvEvent;
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtEvent(this);
        mEventRecyclerView.setAdapter(mEventAdapter);

        // set self user
        mDataBinding.homeUsername.setText(((MainActivity)getActivity()).getmUsername());

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
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
        getLoaderManager().restartLoader(HOME_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new CustomLoader<List<Event>>(getContext()) {
            @Override
            public List<Event> loadInBackground() {
                if(mCurrentLocation != null){
                    HTTP httpService = HTTP.retrofit.create(HTTP.class);
                    Call<EventListRespond> call = httpService.getEvents(new EventListRequest(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1));
                    try {
                        mEventRespond = call.execute();
                        if (mEventRespond.isSuccessful()) {
                            if (mEventRespond.body().getErrorMsg() == null) {
                                Log.i(TAG, "Event list load Success");
                                return mEventRespond.body().getEvents();
                            } else {
                                Log.i(TAG, mEventRespond.body().getErrorMsg());
                                Toast.makeText(getContext(),mEventRespond.body().getErrorMsg(), Toast.LENGTH_LONG).show();
                                return null;
                            }
                        } else {
                            Log.i(TAG, "Not 200");
                            Toast.makeText(getContext(), "Not 200", Toast.LENGTH_LONG).show();
                            return null;
                        }
                    } catch (Exception e) {
                        Log.i(TAG, "Connect exception:" + e.getMessage());
                        return null;
                    }
                } else
                    return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        mData = data;
        if (mData != null) {
            mEventAdapter.setmEventList(data);
            mIsLoadFinished = true;
            mEventAdapter.notifyDataSetChanged();
            populateMapMarker();
        }

        mDataBinding.slideProgessBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

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

    @Override // TODO: 3/1/2017 override super here
    public void onInfoWindowClick(Marker marker) {
        super.onInfoWindowClick(marker);
        String[] temp = marker.getSnippet().split("&");
        switchFragment(FrgEvent.newInstance(Integer.parseInt(temp[4])));
    }

    public void switchFragment(Fragment fragment) {
        mLastTarget = mGoogleMap.getCameraPosition().target;
        mLastZoom = mGoogleMap.getCameraPosition().zoom;
        super.switchFragment(this, fragment);
    } // TODO: 3/1/2017 remove

    public void onClickNewEvent(View view) {
        if(mCurrentLocation != null)
            switchFragment(FrgNewEvent.newInstance(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
    }

    public void onClickJoined(View view){
        switchFragment(FrgCurrentEvent.newInstance());
    }

    public void onClickSetting(View view){ switchFragment(FrgSetting.newInstance());}

    public void onClickProfile(View view) {
        switchFragment(FrgProfile.newInstance(((MainActivity) getActivity()).getmUserId()));
    }

    public void onClickAbout(View view) {
        switchFragment(FrgAbout.newInstance());
    }

    public void onClickHistory(View view){ switchFragment(FrgHistory.newInstance());}
}


