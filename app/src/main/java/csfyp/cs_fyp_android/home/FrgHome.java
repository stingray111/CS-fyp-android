package csfyp.cs_fyp_android.home;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
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

import static android.app.Activity.RESULT_OK;

public class FrgHome extends CustomFragment implements LoaderManager.LoaderCallbacks<List<Event>>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    public static final int HOME_LOADER_ID = 1;
    public static final int HOME_LOCATION_SETTING_CALLBACK = 2;
    public static final int HOME_PERMISSION_CALLBACK = 1;
    public static final String TAG = "HomeFragment";

    private boolean mIsPanelExpanded;
    private boolean mIsPanelAnchored;
    private boolean mIsLoadFinished = false;
    private boolean mIsMapReady = false;
    private boolean mIsPermissionGranted = false;
    private boolean mIsLocationSettingEnable = false;

    private boolean mIsSetToSelfLocation = false;
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

    // For Google Map
    private LatLng mLastTarget;
    private float mLastZoom;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient client;

    // For Sliding Up Panel
    private SlidingUpPanelLayout mLayout;

    // For HTTP
    private Response<EventListRespond> mEventRespond;


    public FrgHome() {
    }

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

    private void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    private void checkLocationSettings() {
        LocationServices.SettingsApi.checkLocationSettings(client, mLocationSettingsRequest)
                        .setResultCallback(this);
    }

    private void startLocationUpdate() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mIsLocationSettingEnable && mIsPermissionGranted) {
                Toast.makeText(getContext(),"Location update started", Toast.LENGTH_LONG).show();
                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Pull-up Panel, try getting the last status
        if (savedInstanceState != null) {
            mIsPanelExpanded = savedInstanceState.getBoolean("isPanelExpanded");
            mLastTarget = savedInstanceState.getParcelable("lastTarget");
            mLastZoom = savedInstanceState.getFloat("lastZoom");
            mIsSetToSelfLocation = savedInstanceState.getBoolean("isSetToSelfLocation");
        }

        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        if (mMapView != null)
            mMapView.onSaveInstanceState(mapState);  // TODO: 21/10/2016 fix mapState
        outState.putParcelable("lastTarget", mLastTarget);
        outState.putFloat("lastZoom", mLastZoom);
        outState.putBundle("mapSaveInstanceState", mapState);
        outState.putBoolean("isPanelExpanded", mIsPanelExpanded);
        outState.putBoolean("isSetToSelfLocation", mIsSetToSelfLocation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.home_frg, container, false);
        mDataBinding.setHandlers(this);
        View v = mDataBinding.getRoot();
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();

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

        // Setting up Google Map
        new Runnable() {
            @Override
            public void run() {
                mMapView.onCreate(null);
            }
        };

        mMapView = mDataBinding.homeMap;

        if (savedInstanceState != null) {
            Bundle mapState;
            mapState = savedInstanceState.getBundle("mapSaveInstanceState"); // TODO: 19/10/2016 change key
            mMapView.onCreate(mapState);
        }
        else
            mMapView.onCreate(null);

        mMapView.getMapAsync(this);


        // Setting up RcyclerView for event
        mEventRecyclerView = mDataBinding.rvEvent;
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtEvent();
        mEventRecyclerView.setAdapter(mEventAdapter);

        // check location setting
        buildGoogleApiClient();

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(HOME_LOADER_ID, null, this);
        if (mMapView != null)
            mMapView.onResume();
        if (client.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    public void onPause() {

        // TODO: 22/11/2016 pause network 
        super.onPause();
        mMapView.onPause();
        if (client.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mIsMapReady = true;
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        }

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

        if (mLastTarget != null)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastTarget, mLastZoom));

        populateMapMarker();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        getLoaderManager().restartLoader(HOME_LOADER_ID, null, this);
        if (!mIsSetToSelfLocation) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 11.0f));
            mIsSetToSelfLocation = true;
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                mIsLocationSettingEnable = true;
                startLocationUpdate();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getActivity(), HOME_LOCATION_SETTING_CALLBACK);
                } catch (IntentSender.SendIntentException e) {
                    // TODO: 27/10/2016 ignore?
                    Toast.makeText(getContext(),"Try get Location fail", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == HOME_PERMISSION_CALLBACK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mIsPermissionGranted = true;
                startLocationUpdate();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // result for location setting prompt
        if (requestCode == HOME_LOCATION_SETTING_CALLBACK) {
            if (resultCode == RESULT_OK) {
                mIsLocationSettingEnable = true;
                startLocationUpdate();
            } else {
                LocationServices.SettingsApi.checkLocationSettings(client, mLocationSettingsRequest)
                        .setResultCallback(this);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // check location setting

        createLocationRequest();
        buildLocationSettingsRequest();
        checkLocationSettings();

        // check permission for location
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, HOME_PERMISSION_CALLBACK);
            }
        } else {
            // permission granted
            mIsPermissionGranted = true;
            startLocationUpdate();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
                                Log.i(TAG, mEventRespond.body().getEvents().get(0).getHolder().getUserName());
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
                        Log.i(TAG, "Connect exception");
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
    public void onInfoWindowClick(Marker marker) {
        String[] temp = marker.getSnippet().split("&");
        mLastTarget = mGoogleMap.getCameraPosition().target;
        mLastZoom = mGoogleMap.getCameraPosition().zoom;
        switchFragment(FrgEvent.newInstance(Integer.parseInt(temp[4])));
    }

    public void switchFragment(Fragment fragment) {
        mLastTarget = mGoogleMap.getCameraPosition().target;
        mLastZoom = mGoogleMap.getCameraPosition().zoom;
        super.switchFragment(this, fragment);
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
        switchFragment(FrgProfile.newInstance(((MainActivity) getActivity()).getmUserId()));
    }

    public void onClickAbout(View view) {
        switchFragment(FrgAbout.newInstance());
    }

    public void onClickHistory(View view){ switchFragment(FrgHistory.newInstance());}

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    class LoadingMapAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            return null;
        }
    }
}


