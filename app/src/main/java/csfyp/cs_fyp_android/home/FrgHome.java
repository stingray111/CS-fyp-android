package csfyp.cs_fyp_android.home;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
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

import java.util.ArrayList;
import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.about.FrgAbout;
import csfyp.cs_fyp_android.currentEvent.FrgCurrentEvent;
import csfyp.cs_fyp_android.databinding.HomeFrgBinding;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.history.FrgHistory;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.newEvent.FrgNewEvent;
import csfyp.cs_fyp_android.profile.FrgProfile;
import csfyp.cs_fyp_android.setting.FrgSetting;

public class FrgHome extends CustomFragment implements LoaderManager.LoaderCallbacks<List<Event>>,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private static final int LOADER_ID = 1;
    private static final String TAG = "HomeFragment";

    private boolean mIsPanelExpanded;
    private boolean mIsPanelAnchored;
    private boolean mIsLoadFinished = false;
    private boolean mIsMapReady = false;
    private boolean mIsUseLocation = false;

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
    private Marker mSelfMarker;
    private LatLng mLastTarget;
    private float mLastZoom;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient client;

    // For Sliding Up Panel
    private SlidingUpPanelLayout mLayout;


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
            for (Event item : mData) {
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(item.getPosition())
                        .title(item.getEventName())
                        .snippet(item.getHolderName() + "&" + item.getEventStart() + "&" + item.getCurrentPpl() + "&" + item.getMaxPpl())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker)));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Pull-up Panel, try getting the last status
        if (savedInstanceState != null) {
            mIsPanelExpanded = savedInstanceState.getBoolean("isPanelExpanded");
            mCurrentLocation = savedInstanceState.getParcelable("currentLocation");
            mLastLocation = savedInstanceState.getParcelable("lastLocation");
            mLastTarget = savedInstanceState.getParcelable("lastTarget");
            mLastZoom = savedInstanceState.getFloat("lastZoom");
            mIsSetToSelfLocation = savedInstanceState.getBoolean("isSetToSelfLocation");

        }

        if (client == null) {
            client = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API)
                    .build();
        }

        setHasOptionsMenu(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);  // TODO: 21/10/2016 fix mapState
        outState.putParcelable("currentLocation", mCurrentLocation);
        outState.putParcelable("lastLocation", mLastLocation);
        outState.putParcelable("lastTarget", mLastTarget);
        outState.putFloat("lastZoom", mLastZoom);
        outState.putBundle("mapSaveInstanceState", mapState); //// TODO: 19/10/2016 change key
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
        mMapView = mDataBinding.homeMap;
        Bundle mapState; //// TODO: 19/10/2016 move to private
        if (savedInstanceState != null)
            mapState = savedInstanceState.getBundle("mapSaveInstanceState"); // TODO: 19/10/2016 change key
        else
            mapState = null;
        mMapView.onCreate(mapState);
        mMapView.getMapAsync(this);

        // Setting up RcyclerView for event
        mEventRecyclerView = mDataBinding.rvEvent;
        mEventLayoutManager = new LinearLayoutManager(getContext());
        mEventRecyclerView.setLayoutManager(mEventLayoutManager);
        mEventAdapter = new AdtEvent();
        mEventRecyclerView.setAdapter(mEventAdapter);

        // Setting up loader
        getLoaderManager().initLoader(LOADER_ID, null, this);

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
        mMapView.onResume();
        if (client.isConnected()) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onPause() {
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

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            return;
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
                if (marker == mSelfMarker)
                    return null;
                return myContentView;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        if(mCurrentLocation != null) {
            mSelfMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
        } else if (mLastLocation != null) {
            mSelfMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
        }

        if (mLastTarget != null)
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastTarget, mLastZoom));

        populateMapMarker();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mSelfMarker != null)
            mSelfMarker.remove();
        mSelfMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
        if (!mIsSetToSelfLocation) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 11.0f));
            mIsSetToSelfLocation = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
                    LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
                }
            } else {
                mIsUseLocation = false;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            mIsUseLocation = true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // check location setting
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        mIsUseLocation = true;

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), 2);
                        } catch (IntentSender.SendIntentException e) {
                            mIsUseLocation = false;
                        }
                        mIsUseLocation = true;
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        mIsUseLocation = false;
                        break;
                }

            }
        });

        // check permission for location
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); // TODO: 26/10/2016 request code change to constant 
            }
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            // TODO: 26/10/2016 check wheather user has turned location updates on or off
            LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mIsUseLocation = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mIsUseLocation = false;
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
        mIsLoadFinished = true;
        populateMapMarker();
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //TODO marker get id subsitude 123
        mLastTarget = mGoogleMap.getCameraPosition().target;
        mLastZoom = mGoogleMap.getCameraPosition().zoom;
        switchFragment(FrgEvent.newInstance(123));
    }

    public void onClickNewEvent(View view) {
        switchFragment(FrgNewEvent.newInstance());
    }

    public void onClickJoined(View view){
        switchFragment(FrgCurrentEvent.newInstance());
    }

    public void onClickSetting(View view){ switchFragment(FrgSetting.newInstance());}

    public void onClickProfile(View view) {
        //TODO marker get id subsitude 123
        switchFragment(FrgProfile.newInstance(123));
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
}


