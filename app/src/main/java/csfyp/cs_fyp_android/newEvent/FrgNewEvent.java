package csfyp.cs_fyp_android.newEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import csfyp.cs_fyp_android.CustomMapFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.NewEventFrgBinding;
import csfyp.cs_fyp_android.lib.CustomScrollView;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.TimeConverter;
import csfyp.cs_fyp_android.model.request.EventCreateRequest;
import csfyp.cs_fyp_android.model.respond.EventRespond;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrgNewEvent extends CustomMapFragment implements Validator.ValidationListener, GoogleMap.OnMarkerDragListener {

    private static final String TAG = "NewEventFragment";
    private NewEventFrgBinding mDataBinding;
    private Toolbar mToolBar;

    private Marker mMarker;

    private Spinner mMinPplSpinner;
    private Spinner mMaxPplSpinner;

    private DatePickerDialog mStartDatePickerDialog;
    private TimePickerDialog mStartTimePickerDialog;
    private DatePickerDialog mDeadlineDatePickerDialog;
    private TimePickerDialog mDeadlineTimePickerDialog;

    @NotEmpty
    private EditText mEventName;
    @NotEmpty
    private EditText mLocation;
    private Validator mValidator;

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

    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private String mEventDeadlineString;
    private String mEventStartString;

    public FrgNewEvent() {}

    public static FrgNewEvent newInstance() {
        FrgNewEvent fragment = new FrgNewEvent();
        return fragment;
    }

    public static FrgNewEvent newInstance(double latitude, double longitude) {
        Bundle args = new Bundle();
        FrgNewEvent fragment = new FrgNewEvent();
        args.putDouble("currentLatitude", latitude);
        args.putDouble("currentLongitude", longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        isUseLocationService = true;
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null){
            mCurrentLatitude = args.getDouble("currentLatitude");
            mCurrentLongitude = args.getDouble("currentLongitude");
        }

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
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.new_event_frg, container, false);
        mDataBinding.setHandlers(this);
        View v = mDataBinding.getRoot();
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();

        mMapView = mDataBinding.newEventMap;
        super.onCreateView(inflater, container, savedInstanceState);

        // Setting up Action Bar
        mToolBar = mDataBinding.newEventToolbar;
        mToolBar.setTitle(R.string.title_new_event);
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBack(null);
            }
        });

        //setting up edit text
        mEventName = (EditText) v.findViewById(R.id.eventName);
        mLocation = (EditText) v.findViewById(R.id.eventLocation);
        mValidator= new Validator(this);
        mValidator.setValidationListener(this);

        // scroll view

        ((CustomScrollView) v.findViewById(R.id.customScrollView)).addInterceptScrollView(mMapView);

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
        minPplSpinnerAdapter.add("3"); minPplSpinnerAdapter.add("4");
        minPplSpinnerAdapter.add("5"); minPplSpinnerAdapter.add("6"); minPplSpinnerAdapter.add("7"); minPplSpinnerAdapter.add("8");
        minPplSpinnerAdapter.add("9"); minPplSpinnerAdapter.add("10"); minPplSpinnerAdapter.add(getResources().getString(R.string.new_event_minppl_hint));
        mMinPplSpinner.setAdapter(minPplSpinnerAdapter);
        mMinPplSpinner.setSelection(minPplSpinnerAdapter.getCount());
        mMinPplSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(position == 8)) {
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
        maxPplSpinnerAdapter.add("3"); maxPplSpinnerAdapter.add("4"); maxPplSpinnerAdapter.add("5");
        maxPplSpinnerAdapter.add("6"); maxPplSpinnerAdapter.add("7"); maxPplSpinnerAdapter.add("8"); maxPplSpinnerAdapter.add("9");
        maxPplSpinnerAdapter.add("10"); maxPplSpinnerAdapter.add("11"); maxPplSpinnerAdapter.add("12"); maxPplSpinnerAdapter.add("13");
        maxPplSpinnerAdapter.add("14"); maxPplSpinnerAdapter.add("15"); maxPplSpinnerAdapter.add(getResources().getString(R.string.new_event_maxppl_hint));
        mMaxPplSpinner.setAdapter(maxPplSpinnerAdapter);
        mMaxPplSpinner.setSelection(maxPplSpinnerAdapter.getCount());
        mMaxPplSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(position ==13)) {
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
                mNewEventMonth = i1 + 1;
                mNewEventDay = i2;
                mStartTimePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mNewEventHour = i;
                        mNewEventMin = i1;
                        mEventStartString = mNewEventYear + "/" + String.format("%02d", mNewEventMonth) + "/" + String.format("%02d", mNewEventDay) + " " + String.format("%02d", mNewEventHour) +  ":" + String.format("%02d", mNewEventMin);
                        mDataBinding.eventStartText.setText(mEventStartString);
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
                mDeadlineEventMonth = i1 + 1 ;
                mDeadlineEventDay = i2;
                mDeadlineTimePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        mDeadlineEventHour = i;
                        mDeadlineEventMin = i1;
                        mEventDeadlineString = mDeadlineEventYear + "/" + String.format("%02d", mDeadlineEventMonth) + "/" + String.format("%02d", mDeadlineEventDay) + " " + String.format("%02d", mDeadlineEventHour) + ":" + String.format("%02d", mDeadlineEventMin);
                        mDataBinding.eventDeadlineText.setText(mEventDeadlineString);
                    }
                }, hour, min, false);
                mDeadlineTimePickerDialog.show();
            }
        }, year, month, day);

        if(savedInstanceState != null) {
            mDataBinding.eventStartText.setText(mNewEventYear + "/" + String.format("%02d", mNewEventMonth) + "/" + String.format("%02d", mNewEventDay) + " " + String.format("%02d", mNewEventHour) + ":" + String.format("%02d", mNewEventMin));
            mDataBinding.eventDeadlineText.setText(mDeadlineEventYear + "/" + String.format("%02d", mDeadlineEventMonth) + "/" + String.format("%02d", mDeadlineEventDay) + " " + String.format("%02d", mDeadlineEventHour) + ":" + String.format("%02d", mDeadlineEventMin));
        }


        mDataBinding.submitEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataBinding.submitEvent.setVisibility(View.GONE);
                mDataBinding.createProgressBar.setVisibility(View.VISIBLE);
                mValidator.validate();
            }

        });

        return v;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (mDataBinding.eventName.getText().toString().matches("fuck")){
            mDataBinding.eventName.setText("Event "+(Math.abs(new Random().nextInt()%99999)+1));
            mDataBinding.eventLocation.setText("Somewhere in hong kong");
            mDataBinding.eventDescription.setText("This is a autogenerated event");
            mMaxPpl = 10;
            mMinPpl = 2;
            mEventStartString = ("2018/" + String.format("%02d", 1) + "/" + String.format("%02d", 1) + " " + String.format("%02d", 12) + ":" + String.format("%02d", 12));
            mDataBinding.eventStartText.setText(mEventStartString);
            mEventDeadlineString =("2017/" + String.format("%02d", 12) + "/" + String.format("%02d", 1) + " " + String.format("%02d", 1) + ":" + String.format("%02d", 1));
            mDataBinding.eventDeadlineText.setText(mEventDeadlineString);
            onValidationSucceeded();
            return;
        }


        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(parentActivity);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(parentActivity,message,Toast.LENGTH_LONG).show();
            }
        }

        mDataBinding.submitEvent.setVisibility(View.VISIBLE);
        mDataBinding.createProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onValidationSucceeded() {
        if (mMinPpl == 0) {
            Toast.makeText(getContext(), "Please choose the minimum participant", Toast.LENGTH_SHORT).show();
            mDataBinding.submitEvent.setVisibility(View.VISIBLE);
            mDataBinding.createProgressBar.setVisibility(View.GONE);
            return;
        }
        if (mMaxPpl == 0) {
            Toast.makeText(getContext(), "Please choose the maximum participant", Toast.LENGTH_SHORT).show();
            mDataBinding.submitEvent.setVisibility(View.VISIBLE);
            mDataBinding.createProgressBar.setVisibility(View.GONE);
            return;
        }
        if (mMaxPpl < mMinPpl) {
            Toast.makeText(getContext(), "Number of Maximum people must be bigger then number of minimum people", Toast.LENGTH_SHORT).show();
            mDataBinding.submitEvent.setVisibility(View.VISIBLE);
            mDataBinding.createProgressBar.setVisibility(View.GONE);
            return;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date start = format.parse(mDataBinding.eventStartText.getText().toString());
            Date ddl = format.parse(mDataBinding.eventDeadlineText.getText().toString());
            if (start.after(ddl) || start.equals(ddl)) {
                //Pass
            } else {
                Toast.makeText(getContext(), "Please set the deadline time before the start time.", Toast.LENGTH_SHORT).show();
                mDataBinding.submitEvent.setVisibility(View.VISIBLE);
                mDataBinding.createProgressBar.setVisibility(View.GONE);
                return;
            }
        } catch (Exception e){
            Toast.makeText(getContext(), "Please choose the date and time.", Toast.LENGTH_SHORT).show();
            mDataBinding.submitEvent.setVisibility(View.VISIBLE);
            mDataBinding.createProgressBar.setVisibility(View.GONE);
            return;
        }


        if (!mDataBinding.eventName.getText().toString().matches("")
                    /*&& !mDataBinding.eventDescription.getText().toString().matches("")*/
                && !mDataBinding.eventLocation.getText().toString().matches("")
                && mMaxPpl != 0
                && mMinPpl != 0
                && !mDataBinding.eventDeadlineText.getText().toString().matches("Click to set time")
                && !mDataBinding.eventStartText.getText().toString().matches("Click to set time")) {
            HTTP httpService = HTTP.retrofit.create(HTTP.class);
            LatLng position = mMarker.getPosition();
            MainActivity parentActivity = (MainActivity) getActivity();
            EventCreateRequest event = new EventCreateRequest(
                    mDataBinding.eventName.getText().toString(),
                    position.latitude,
                    position.longitude,
                    mDataBinding.eventLocation.getText().toString(),
                    ((MainActivity) getActivity()).getmUserId(),
                    mMaxPpl,
                    mMinPpl,
                    TimeConverter.localToUTC(mEventStartString),
                    TimeConverter.localToUTC(mEventDeadlineString),
                    mDataBinding.eventDescription.getText().toString());
            Call<EventRespond> call = httpService.pushEvent(event);
            call.enqueue(new Callback<EventRespond>() {
                @Override
                public void onResponse(Call<EventRespond> call, Response<EventRespond> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).mChatService.addEvent(response.body().getEvent());
                        onBack(null);
                    } else {
                        Toast.makeText(getContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<EventRespond> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onClickSetEventStart(View view) {
        mStartDatePickerDialog.show();
    }

    public void onClickSetEventDeadline(View view) {
        mDeadlineDatePickerDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        mGoogleMap.setOnMarkerDragListener(this);

        if (mCurrentLatitude != 0.0 && mCurrentLongitude != 0.0){
            mMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mCurrentLatitude, mCurrentLongitude))
                    .draggable(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker))
                    .title("Choose where the event take place")
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLatitude, mCurrentLongitude), 12.0f));
        } else {
            mMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(22.25, 114.1667))
                    .draggable(true)
                    .title("Choose where the event take place")
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.25, 114.1667), 12.0f));
        }
    }



    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mMarker = marker;
    }
}
