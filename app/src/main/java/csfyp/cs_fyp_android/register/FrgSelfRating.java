package csfyp.cs_fyp_android.register;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.RegisterSelfRatingFrgBinding;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.NoticeDialogFragment;
import csfyp.cs_fyp_android.model.request.SelfRate;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgSelfRating  extends CustomFragment {

    private RegisterSelfRatingFrgBinding mBinding;
    private Toolbar mToolBar;
    private NoticeDialogFragment mDialog;

    public FrgSelfRating() {}

    public static FrgSelfRating newInstance() {

        FrgSelfRating fragment = new FrgSelfRating();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.register_self_rating_frg, container, false);
        View v = mBinding.getRoot();

        mToolBar = mBinding.selfRatingToolBar;
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);

        parentActivity.getSupportActionBar().setTitle("Self Personality Testing");

        mToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.selfRatingSkip) {
                    // // TODO: 3/2/2017 add skip action
                    mDialog = NoticeDialogFragment.newInstance(R.string.self_rate_skip_dialog_title, R.string.self_rate_skip_dialog_message);
                    mDialog.setDialogListener(new NoticeDialogFragment.NoticeDialogListener() {
                        @Override
                        public void onDialogPositiveClick(DialogFragment dialog) {
                            replaceFragment(((MainActivity) getActivity()).getmHome());
                        }

                        @Override
                        public void onDialogNegativeClick(DialogFragment dialog) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show(getFragmentManager(), "self_rating_skip_dialog");
                }
                return true;
            }
        });

        mBinding.setHandlers(this);

        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.self_rating_menu, menu);
    }

    public void onClickSubmitSelfRate(View v) {
        // // TODO: 4/2/2017 send to server
        HTTP httpService = HTTP.retrofit.create(HTTP.class);

        float e = (6 - mBinding.selfRateQ1.getProgress() + mBinding.selfRateQ6.getProgress())/2f;
        float a = (6 - mBinding.selfRateQ7.getProgress() + mBinding.selfRateQ2.getProgress())/2f;
        float c = (6 - mBinding.selfRateQ3.getProgress() + mBinding.selfRateQ8.getProgress())/2f;
        float n = (6 - mBinding.selfRateQ4.getProgress() + mBinding.selfRateQ9.getProgress())/2f;
        float o = (6 - mBinding.selfRateQ5.getProgress() + mBinding.selfRateQ10.getProgress())/2f;

        Call<ErrorMsgOnly> call = httpService.postSelfRate(new SelfRate(e, a, c, n, o, ((MainActivity) getActivity()).getmUserId()));
        call.enqueue(new Callback<ErrorMsgOnly>() {
            @Override
            public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                if (response.isSuccessful() && response.body().getErrorMsg() == null)
                    replaceFragment(((MainActivity) getActivity()).getmHome());
            }

            @Override
            public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {

            }
        });

        replaceFragment(((MainActivity) getActivity()).getmHome());
    }
}
