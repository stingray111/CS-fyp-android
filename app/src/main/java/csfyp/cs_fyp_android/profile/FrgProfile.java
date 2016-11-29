package csfyp.cs_fyp_android.profile;

import android.databinding.DataBindingUtil;
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
    private ProfileFrgBinding mDatabinding;
    private Response<UserRespond> mUserRespond;

    public FrgProfile(){}

    public static FrgProfile newInstance(int id) {
        FrgProfile fragment = new FrgProfile();
        Bundle args = new Bundle();
        args.putInt("userId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        mUserId = args.getInt("userId");

        mDatabinding = DataBindingUtil.inflate(inflater, R.layout.profile_frg, container, false);
        View v  = mDatabinding.getRoot();

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

        getLoaderManager().initLoader(USER_LOADER_ID, null, this);

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
            mToolBar.setTitle(mUserObj.getUserName());
            mDatabinding.setUserObj(mUserObj);
            mDatabinding.profileProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }
}
