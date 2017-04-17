package csfyp.cs_fyp_android.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.chat.ChatService;
import csfyp.cs_fyp_android.databinding.SettingFrgBinding;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.ChatServiceSetting;
import csfyp.cs_fyp_android.lib.eventBus.ErrorMsg;
import csfyp.cs_fyp_android.login.FrgLogin;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.model.respond.Logout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class FrgSetting extends CustomFragment implements  GoogleApiClient.OnConnectionFailedListener {
    public FrgSetting(){super();}
    public static final String TAG = "Setting";
    private Toolbar mToolBar;
    private Button mlogoutBtn;
    private SettingFrgBinding mBinding;
    private GoogleApiClient mGoogleApiClient;
    private Switch floatingChatMenuSwitch;
    private Spinner floatingChatMenuSpinner;

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

        floatingChatMenuSwitch = (Switch) v.findViewById(R.id.floatingChatMenuToggle);
        floatingChatMenuSpinner = (Spinner) v.findViewById(R.id.floatingChatMode);
        final View floatingChatMenuSpinnerColumn = v.findViewById(R.id.floatingChatModeColumn);
        if(getMainActivity().floatingChatMenuOn){
            floatingChatMenuSwitch.setChecked(true);
        }else{
            floatingChatMenuSwitch.setChecked(false);
            floatingChatMenuSpinnerColumn.setVisibility(GONE);
        }
        floatingChatMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //start chat head
                    getMainActivity().floatingChatMenuOn = true;
                    getMainActivity().chatHeadInit();
                    if(getMainActivity().messageFullEventList.size() <= 2) {
                        getMainActivity().floatingChatMenuShowing = getMainActivity().messageFullEventList;
                    }else{
                        getMainActivity().floatingChatMenuShowing = getMainActivity().messageFullEventList.subList(0,3);
                    }
                    new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    EventBus.getDefault().post(new ChatServiceSetting(getMainActivity().floatingChatMenuShowing,ChatServiceSetting.SWAP_EVENT_LIST));
                            }
                    },1000);
                    //floatingChatMenuSpinnerColumn.setVisibility(View.VISIBLE);
                    floatingChatMenuSpinnerColumn.setVisibility(View.GONE);
                }else{
                    //stop chat head
                    getMainActivity().stopService(new Intent(getMainActivity(),ChatService.class));
                    floatingChatMenuSpinnerColumn.setVisibility(View.GONE);
                }
            }
        });

        floatingChatMenuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //pos 0 is auto, pos 1 is custom
                Log.d("here","pos"+ position);
                if(position == 0){
                    //auto
                    if(getMainActivity().floatingChatMenuAuto != true){
                        getMainActivity().setFloatingChatToAuto();
                    }
                }else{
                    //custom
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

                                if(getMainActivity().messageFullEventList != null) {
                                    for (Event event : getMainActivity().messageFullEventList) {
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic("group_" + event.getId());
                                        Log.d(TAG,"unscribing");
                                    }
                                }

                                Intent intent = new Intent(getActivity(), ChatService.class);
                                ChatService.interfaceStarted = false;
                                ((MainActivity)getActivity()).stopService(intent);

                                replaceFragment(FrgLogin.newInstance());
                            } else
                                EventBus.getDefault().post(new ErrorMsg("Logout fail",Toast.LENGTH_SHORT));
                        } else
                            EventBus.getDefault().post(new ErrorMsg("Logout fail",Toast.LENGTH_SHORT));
                    }

                    @Override
                    public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                        view.setOnClickListener(_this);
                        EventBus.getDefault().post(new ErrorMsg("Logout fail",Toast.LENGTH_SHORT));
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
        EventBus.getDefault().post(new ErrorMsg("已設定為中文",Toast.LENGTH_SHORT));
        //setLocale("zh");
        ((MainActivity)getActivity()).setLanguage("zh");
    }

    public void onClickEnglish(View view) {
        EventBus.getDefault().post(new ErrorMsg("English is set",Toast.LENGTH_SHORT));
        //setLocale("en");
        ((MainActivity)getActivity()).setLanguage("en");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Google Connection Fail");
    }

    private MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }
}
