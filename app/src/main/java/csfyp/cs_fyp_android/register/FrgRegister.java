package csfyp.cs_fyp_android.register;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.RegisterFrgBinding;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.RegisterStatus;
import csfyp.cs_fyp_android.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgRegister extends CustomFragment {
    public FrgRegister(){super();}

    public final static String TAG = "register";
    private Toolbar mToolBar;
    private Button mSubmitBtn;
    private RegisterFrgBinding mDataBinding;

    public static FrgRegister newInstance() {

        Bundle args = new Bundle();

        FrgRegister fragment = new FrgRegister();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.register_frg, container, false);
        View v = mDataBinding.getRoot();
        mToolBar = (Toolbar) v.findViewById(R.id.registerToolBar);
        mToolBar.setTitle("New Account");
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mDataBinding.userNameField.getText().toString().matches("")

        mSubmitBtn = (Button) v.findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mDataBinding.userNameField.getText().toString().matches("") ||
                        mDataBinding.emailField.getText().toString().matches("") ||
                        mDataBinding.)


                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                User user = new User("ken31ee", "321542431242", "Ken", "Tung", "hii", true, 1, 1, 1, "tungpakyin04@outlook.com", "61565916", "Good", 1);
                Call<RegisterStatus> call = httpService.createUser(user);
                call.enqueue(new Callback<RegisterStatus>() {
                    @Override
                    public void onResponse(Call<RegisterStatus> call, Response<RegisterStatus> response) {
                        if(response.isSuccessful()) {
                            if(!response.body().isSuccessful()) {
                                Log.i(TAG, "200 but not success");
                                Log.i(TAG, response.body().getErrorMsg());
                                Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_LONG).show();
                            } else
                                Log.i(TAG, "sucess");
                        } else
                            Log.i(TAG, "not 200: " + response.body().getErrorMsg());
                    }

                    @Override
                    public void onFailure(Call<RegisterStatus> call, Throwable t) {

                    }
                });

            }
        });

        return v;
    }
}


