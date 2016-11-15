package csfyp.cs_fyp_android.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.UUID;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
import csfyp.cs_fyp_android.register.FrgRegister;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FrgLogin extends CustomFragment implements Validator.ValidationListener{
    public FrgLogin(){
        super();
    }

    public final static String TAG = "login";
    private Toolbar mToolBar;
    private Button mLoginBtn;
    @NotEmpty
    private EditText mInputEmailOrUsername;
    @Password(min=8,scheme = Password.Scheme.ALPHA_NUMERIC)
    private EditText mInputPassword;
    private TextView mRegisterBtn;
    private Validator mValidator;

    public static FrgLogin newInstance() {

        Bundle args = new Bundle();

        FrgLogin fragment = new FrgLogin();
        fragment.setArguments(args);
        return fragment;
    }

    public void switchFragment(Fragment to) {
        super.switchFragment(this, to);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.login_frg,container,false);
        mToolBar = (Toolbar) v.findViewById(R.id.loginToolBar);
        mToolBar.setTitle("Login");
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);

        mLoginBtn = (Button) v.findViewById(R.id.loginBtn);
        mInputEmailOrUsername = (EditText) v.findViewById(R.id.inputEmailOrUsername);

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mInputPassword = (EditText) v.findViewById(R.id.inputPassword);
        mRegisterBtn = (TextView) v.findViewById(R.id.registerBtn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(FrgRegister.newInstance());
            }

        });
        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });
        return v;
    }

    @Override
    public void onValidationSucceeded() {
            String strEmailOrUsername = mInputEmailOrUsername.getText().toString();
            String strPassword = mInputPassword.getText().toString();

            if (!strEmailOrUsername.matches("") && !strPassword.matches("")) {
                Log.i(TAG, strEmailOrUsername);
                Log.i(TAG, strPassword);

                String uuidInString = UUID.randomUUID().toString();

                HTTP httpService = HTTP.retrofit.create(HTTP.class);
                Login login = new Login(strEmailOrUsername, strPassword, uuidInString);
                Call<LoginRespond> call = httpService.login(login);

                call.enqueue(new Callback<LoginRespond>() {
                    @Override
                    public void onResponse(Call<LoginRespond> call, Response<LoginRespond> response) {
                        if (response.isSuccessful()) {
                            if (response.body().isSuccessful()) {

                                // save token to cache
                                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("userToken", response.body().getToken());
                                editor.putInt("userId", response.body().getUserId());
                                editor.commit();

                                replaceFragment(FrgHome.newInstance());

                            } else {
                                // TODO: 6/11/2016 print error msg to user
                                Log.i(TAG, response.body().getErrorMsg());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginRespond> call, Throwable t) {

                    }
                });
            }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
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

    }
}
