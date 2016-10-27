package csfyp.cs_fyp_android.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.databinding.ProfileFrgBinding;
import csfyp.cs_fyp_android.lib.CustomLoader;
import csfyp.cs_fyp_android.model.User;

import static com.google.android.gms.analytics.internal.zzy.m;

public class FrgProfile extends CustomFragment implements LoaderManager.LoaderCallbacks<User>{
    public static final String TAG = "ProfileFragment";
    private Toolbar mToolBar;
    public FrgProfile() { super(); }
    private int mUserID;
    private User mUserObj;
    private ProfileFrgBinding mDatabinding;

    public FrgProfile(int id){ this.mUserID = id;}

    public static FrgProfile newInstance(int id) {
        //TODO: give id to the fragment

        Bundle args = new Bundle();

        FrgProfile fragment = new FrgProfile(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v  = inflater.inflate(R.layout.profile_frg, container, false);
        mToolBar = (Toolbar) v.findViewById(R.id.profileToolBar);
        mToolBar.setTitle("username");
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });
        return v;
    }

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        return  new CustomLoader<User>(getContext()) {
            @Override
            public User loadInBackground() {
                //TODO: connect to server
                return new User("Luk","Ping Shan","Stingray",true,10,1,2,"psluk@link.cuhk.edu.hk","23456789","I am crazy",12);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User data) {
        mUserObj = data;
        mDatabinding.setUserObj(mUserObj);
    }

    @Override
    public void onLoaderReset(Loader<User> loader) {

    }
}
