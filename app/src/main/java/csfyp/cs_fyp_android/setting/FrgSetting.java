package csfyp.cs_fyp_android.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

public class FrgSetting extends CustomFragment{
    public FrgSetting(){super();}
    public static final String TAG = "Setting";
    private Toolbar mToolBar;
    private Button mlogoutBtn;
    private SettingFrgBinding mBinding;

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
            @Override
            public void onClick(View view) {
                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Call<ErrorMsgOnly> call = httpService.logout(new Logout(((MainActivity)getActivity()).getmToken()));
                call.enqueue(new Callback<ErrorMsgOnly>() {
                    @Override
                    public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getErrorMsg() == null) {
                                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.remove("userToken");
                                editor.remove("userId");
                                editor.remove("username");
                                editor.commit();
                                ((MainActivity) getActivity()).setmHome(FrgHome.newInstance());
//                                FragmentManager fm = getActivity().getSupportFragmentManager();
//                                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//                                    fm.popBackStack();h
//                                }

                                replaceFragment(FrgLogin.newInstance());
                            } else
                                Toast.makeText(getContext(), "Logout fail", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "Logout fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                        Toast.makeText(getContext(), "Logout fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return v;
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
}
