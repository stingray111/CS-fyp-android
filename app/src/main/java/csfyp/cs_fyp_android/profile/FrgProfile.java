package csfyp.cs_fyp_android.profile;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.ProfileFrgBinding;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.UserRequest;
import csfyp.cs_fyp_android.model.respond.UserRespond;
import retrofit2.Call;
import retrofit2.Response;

public class FrgProfile extends CustomFragment implements LoaderManager.LoaderCallbacks<User>{
    public static final String TAG = "ProfileFragment";
    public static final int USER_LOADER_ID = 9;
    private Toolbar mToolBar;
    private int mUserId;
    private User mUserObj;
    private RadarChart mChart;
    private ProfileFrgBinding mDataBinding;
    private Response<UserRespond> mUserRespond;

    public FrgProfile(){}

    public static FrgProfile newInstance(int id) {
        FrgProfile fragment = new FrgProfile();
        Bundle args = new Bundle();
        args.putInt("userId", id);
        fragment.setArguments(args);
        return fragment;
    }

    public void setChartData(float e, float a, float c, float n, float o) {

        float mult = 10;
        int cnt = 5;

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries1.add(new RadarEntry(e));
        entries1.add(new RadarEntry(a));
        entries1.add(new RadarEntry(c));
        entries1.add(new RadarEntry(n));
        entries1.add(new RadarEntry(o));

        RadarDataSet set1 = new RadarDataSet(entries1, null);
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setDrawValues(false);
        set1.setHighlightEnabled(false);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(false);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);
        mChart.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        mUserId = args.getInt("userId");

        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.profile_frg, container, false);
        View v  = mDataBinding.getRoot();

        mToolBar = (Toolbar) v.findViewById(R.id.profileToolBar);
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mChart = mDataBinding.radarChart;

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.GRAY);
        mChart.setWebLineWidthInner(0.6f);
        mChart.setWebColorInner(Color.GRAY);
        mChart.setWebAlpha(100);
        mChart.setRotationEnabled(false);
        mChart.setExtraTopOffset(15f);
        mChart.setExtraLeftOffset(10f);
        mChart.setExtraRightOffset(10f);

        MarkerView mv = new MarkerView(getContext(),R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control

        //setData();

        mChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setYOffset(15f);
        xAxis.setXOffset(15f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"E", "A", "C", "N", "O"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.BLACK);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(12f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(4f);
        yAxis.setGranularity(1f);
        yAxis.setDrawLabels(false);
        //yAxis.setGranularityEnabled();

        Legend l = mChart.getLegend();
        l.setEnabled(false);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(5f);
        l.setYEntrySpace(3f);
        l.setTextColor(Color.BLACK);

        getLoaderManager().initLoader(USER_LOADER_ID, null, this);

//        // load propic
//        Picasso.with(getContext())
//                .load(((MainActivity)getActivity()).getmSelf().getProPic())
//                .resize(300,300)
//                .centerCrop()
//                .placeholder(R.drawable.ic_propic_big)
//                .into(mDataBinding.profileProPic);

        return v;
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        return  new CustomLoader<User>(getContext()) {
            @Override
            public User loadInBackground() {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Log.i(TAG, mUserId+"");
                Call<UserRespond> call = httpService.getUser(new UserRequest(mUserId));
                try {
                    mUserRespond = call.execute();
                    if(mUserRespond.isSuccessful() && mUserRespond.body().getErrorMsg() == null) {
                        Log.i(TAG, "User load Success");
                        return mUserRespond.body().getUser();
                    } else
                        Log.i(TAG, "User load Not Success");
                        return null;
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User data) {
        mUserObj = data;
        if (mUserObj != null) {
            mToolBar.setTitle(mUserObj.getFullName());
            mDataBinding.setUserObj(mUserObj);
            mDataBinding.profileProgressBar.setVisibility(View.GONE);

            float e = mUserObj.getSelfExtraversion() + mUserObj.getAdjustmentExtraversionWeightedSum()/mUserObj.getAdjustmentWeight();
            float a = mUserObj.getSelfAgreeableness() + mUserObj.getAdjustmentAgreeablenessWeightedSum()/mUserObj.getAdjustmentWeight();
            float c = mUserObj.getSelfConscientiousness() + mUserObj.getAdjustmentConscientiousnessWeightedSum()/mUserObj.getAdjustmentWeight();
            float n = mUserObj.getSelfNeuroticism() + mUserObj.getAdjustmentNeuroticismWeightedSum()/mUserObj.getAdjustmentWeight();
            float o = mUserObj.getSelfOpenness() + mUserObj.getAdjustmentOpennessWeightedSum()/mUserObj.getAdjustmentWeight();

            setChartData(e, a, c, n, o);

            if (data.getDescription() == null || data.getDescription().replace(" ","").isEmpty())
                data.setDescription(getResources().getString(R.string.profile_no_description));
            if (data.getPhone() == null || data.getPhone().replace(" ","").isEmpty()) {
                data.setPhone(getResources().getString(R.string.profile_no_phone));
                mDataBinding.profilePhoneNo.setText(R.string.profile_no_phone);
            }

            Picasso.with(getContext())
                    .load(mUserObj.getProPic())
                    .resize(300,300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_propic_big)
                    .into(mDataBinding.profileProPic);
        }
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }
}
