package csfyp.cs_fyp_android.newEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.NewEventFrgBinding;
import csfyp.cs_fyp_android.lib.HTTP;

public class FrgNewEvent extends CustomFragment implements OnMapReadyCallback {

    private static final String TAG = "NewEventFragment";
    private NewEventFrgBinding mDataBinding;
    private Toolbar mToolBar;

    private Spinner mMinPplSpinner;
    private Spinner mMaxPplSpinner;

    private Bundle mMapState;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mClient;

    private DatePickerDialog mStartDatePickerDialog;
    private TimePickerDialog mStartTimePickerDialog;
    private DatePickerDialog mDeadlineDatePickerDialog;
    private TimePickerDialog mDeadlineTimePickerDialog;

    private int mMinPpl;
    private int mMaxPpl;

    private int mNewEventYear;
    private int mNewEventMonth;
    private int mNewEventDay;
    private int mNewEventHour;
    private int mNewEventMin;

    private int mDeadlineEventYear;
    private int mDeadlineEventMonth;
    private int mDeadlineEventDay;
    private int mDeadlineEventHour;
    private int mDeadlineEventMin;

    public FrgNewEvent() {

    }

    public static FrgNewEvent newInstance() {

        Bundle args = new Bundle();

        FrgNewEvent fragment = new FrgNewEvent();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClient = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        if(savedInstanceState != null) {
            mNewEventYear = savedInstanceState.getInt("newEventYear");
            mNewEventMonth = savedInstanceState.getInt("newEventMonth");
            mNewEventDay = savedInstanceState.getInt("newEventDay");
            mNewEventHour = savedInstanceState.getInt("newEventHour");
            mNewEventMin = savedInstanceState.getInt("newEventMin");
            mDeadlineEventYear = savedInstanceState.getInt("deadlineEventYear");
            mDeadlineEventMonth = savedInstanceState.getInt("deadlineEventMonth");
            mDeadlineEventDay = savedInstanceState.getInt("deadlineEventDay");
            mDeadlineEventHour = savedInstanceState.getInt("deadlineEventHour");
            mDeadlineEventMin = savedInstanceState.getInt("deadlineEventMin");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapState = new Bundle();
        mMapView.onSaveInstanceState(mapState);
        outState.putBundle("newEventMapSaveInstanceState", mapState);
        outState.putInt("newEventYear", mNewEventYear);
        outState.putInt("newEventMonth", mNewEventMonth);
        outState.putInt("newEventDay", mNewEventDay);
        outState.putInt("newEventHour", mNewEventHour);
        outState.putInt("newEventMin", mNewEventMin);
        outState.putInt("deadlineEventYear", mDeadlineEventYear);
        outState.putInt("deadlineEventMonth", mDeadlineEventMonth);
        outState.putInt("deadlineEventDay", mDeadlineEventDay);
        outState.putInt("deadlineEventHour", mDeadlineEventHour);
        outState.putInt("deadlineEventMin", mDeadlineEventMin);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.new_event_frg, container, false);
        mDataBinding.setHandlers(this);
        View v = mDataBinding.getRoot();

        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();

        // Setting up Action Bar
        mToolBar = mDataBinding.newEventToolbar;
        mToolBar.setTitle("Create New Event");
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack(null);
            }
        });

        // Setting up Google Map
        mMapView = (MapView) mDataBinding.newEventMap;
        if (savedInstanceState != null)
            mMapState = savedInstanceState.getBundle("newEventMapSaveInstanceState");
        else
            mMapState = null;
        mMapView.onCreate(mMapState);
        mMapView.getMapAsync(this);

        // Setting up Spinners
        mMinPplSpinner = mDataBinding.minPplSpinner;
        ArrayAdapter<String> minPplSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1;
            }
        };
        minPplSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minPplSpinnerAdapter.add("1"); minPplSpinnerAdapter.add("2"); minPplSpinnerAdapter.add("3"); minPplSpinnerAdapter.add("4");
        minPplSpinnerAdapter.add("5"); minPplSpinnerAdapter.add("6"); minPplSpinnerAdapter.add("7"); minPplSpinnerAdapter.add("8");
        minPplSpinnerAdapter.add("9"); minPplSpinnerAdapter.add("10"); minPplSpinnerAdapter.add("Min People");
        mMinPplSpinner.setAdapter(minPplSpinnerAdapter);
        mMinPplSpinner.setSelection(minPplSpinnerAdapter.getCount());
        mMinPplSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(position == 10)) {
                    mMinPpl = Integer.parseInt((String) parent.getItemAtPosition(position));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mMaxPplSpinner = mDataBinding.maxPplSpinner;
        ArrayAdapter<String> maxPplSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1;
            }


        };
        maxPplSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxPplSpinnerAdapter.add("2"); maxPplSpinnerAdapter.add("3"); maxPplSpinnerAdapter.add("4"); maxPplSpinnerAdapter.add("5");
        maxPplSpinnerAdapter.add("6"); maxPplSpinnerAdapter.add("7"); maxPplSpinnerAdapter.add("8"); maxPplSpinnerAdapter.add("9");
        maxPplSpinnerAdapter.add("10"); maxPplSpinnerAdapter.add("11"); maxPplSpinnerAdapter.add("12"); maxPplSpinnerAdapter.add("13");
        maxPplSpinnerAdapter.add("14"); maxPplSpinnerAdapter.add("15"); maxPplSpinnerAdapter.add("Max People");
        mMaxPplSpinner.setAdapter(maxPplSpinnerAdapter);
        mMaxPplSpinner.setSelection(maxPplSpinnerAdapter.getCount());
        mMaxPplSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(position ==14)) {
                    mMaxPpl = Integer.parseInt((String) parent.getItemAtPosition(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Setting up Start Date Picker Dialog
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_YEAR);
        mStartDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                mNewEventYear = i;
                mNewEventMonth = i1;
                mNewEventDay = i2;
                mStartTimePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mNewEventHour = i;
                        mNewEventMin = i1;
                        mDataBinding.eventStartText.setText(mNewEventYear + "/" + mNewEventMonth + "/" + mNewEventDay + " " + mNewEventHour + ":" + mNewEventMin);
                    }
                }, hour, min, false);
                mStartTimePickerDialog.show();
            }
        }, year, month, day);

        // Setting up Deadline Date Picker Dialog
        mDeadlineDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                mDeadlineEventYear = i;
                mDeadlineEventMonth = i1;
                mDeadlineEventDay = i2;
                mDeadlineTimePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mDeadlineEventHour = i;
                        mDeadlineEventMin = i1;
                        mDataBinding.eventDeadlineText.setText(mDeadlineEventYear + "/" + mDeadlineEventMonth + "/" + mDeadlineEventDay + " " + mDeadlineEventHour + ":" + mDeadlineEventMin);
                    }
                }, hour, min, false);
                mDeadlineTimePickerDialog.show();
            }
        }, year, month, day);

        if(savedInstanceState != null) {
            mDataBinding.eventStartText.setText(mNewEventYear + "/" + mNewEventMonth + "/" + mNewEventDay + " " + mNewEventHour + ":" + mNewEventMin);
            mDataBinding.eventDeadlineText.setText(mDeadlineEventYear + "/" + mDeadlineEventMonth + "/" + mDeadlineEventDay + " " + mDeadlineEventHour + ":" + mDeadlineEventMin);
        }


        mDataBinding.submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDataBinding.eventName.getText().toString().matches("")
                        && mMaxPpl != 0
                        && mMinPpl != 0
                        && !mDataBinding.eventDeadlineText.getText().toString().matches("Click to set time")
                        && !mDataBinding.eventStartText.getText().toString().matches("Click to set time"))
                {

                    HTTP httpService = HTTP.retrofit.create(HTTP.class);
                    //httpService.pushEvent(new EventPost(mDataBinding.eventName.getText().toString(), mLat, mLong, "Hong Kong", mHolderId, mMaxPpl, mMinPpl, mDesciption));
                }

            }
        });



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mClient, getIndexApiAction());
        mClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(22.25, 114.1667))
                .title("You")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_self_marker)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.25, 114.1667), 12.0f));
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("New Event") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public void onClickSetEventStart(View view) {
        mStartDatePickerDialog.show();
    }

    public void onClickSetEventDeadline(View view) {
        mDeadlineDatePickerDialog.show();
    }
}
