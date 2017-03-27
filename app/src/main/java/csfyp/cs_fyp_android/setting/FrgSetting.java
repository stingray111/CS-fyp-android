package csfyp.cs_fyp_android.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.SettingFrgBinding;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.login.FrgLogin;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.model.respond.Logout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgSetting extends CustomFragment implements  GoogleApiClient.OnConnectionFailedListener {
    public FrgSetting(){super();}
    public static final String TAG = "Setting";
    private Toolbar mToolBar;
    private Button mlogoutBtn;
    private SettingFrgBinding mBinding;
    private GoogleApiClient mGoogleApiClient;

    public static FrgSetting newInstance() {
        Bundle args = new Bundle();
        FrgSetting fragment = new FrgSetting();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestId()
                .requestIdToken(getString(R.string.oauth_client_id))
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mBinding = DataBindingUtil.inflate(inflater, R.layout.setting_frg, container, false);
        mBinding.setHandlers(this);

        View v = mBinding.getRoot();
        mToolBar = (Toolbar) v.findViewById(R.id.settingToolBar);
        mToolBar.setTitle(R.string.title_setting);
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mlogoutBtn = (Button) v.findViewById(R.id.logoutBtn);
        mlogoutBtn.setOnClickListener(new View.OnClickListener() {
            View.OnClickListener _this = this;

            @Override
            public void onClick(final View view) {
                view.setOnClickListener(null);
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<ErrorMsgOnly> call = httpService.logout(new Logout(((MainActivity)getActivity()).getmToken()));
                call.enqueue(new Callback<ErrorMsgOnly>() {
                    @Override
                    public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getErrorMsg() == null) {
                                int acType = ((MainActivity)getActivity()).getmAcType();
                                if(acType == FrgLogin.FB){
                                    LoginManager.getInstance().logOut();
                                }else if(acType == FrgLogin.GOOGLE){
                                    if(mGoogleApiClient.isConnected()) {
                                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                        Log.d(TAG, "signout google");
                                    }else{
                                        Log.d(TAG, "no signout google");
                                    }
                                }

                                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove("userToken");
                                editor.remove("userId");
                                editor.remove("username");
                                editor.remove("msgToken");
                                editor.remove("acType");
                                editor.commit();

                                ((MainActivity)getActivity()).unbindService(((MainActivity)getActivity()).connection);

                                replaceFragment(FrgLogin.newInstance());
                            } else
                                Toast.makeText(getContext(), "Logout fail", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "Logout fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                        view.setOnClickListener(_this);
                        Toast.makeText(getContext(), "Logout fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    public void onClickChinese(View view) {
        Toast.makeText(getContext(), "set Chinese", Toast.LENGTH_SHORT).show();
        //setLocale("zh");
        ((MainActivity)getActivity()).setLanguage("zh");
    }

    public void onClickEnglish(View view) {
        Toast.makeText(getContext(), "set English", Toast.LENGTH_SHORT).show();
        //setLocale("en");
        ((MainActivity)getActivity()).setLanguage("en");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Google Connection Fail");
    }
}
