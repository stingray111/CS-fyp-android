package csfyp.cs_fyp_android.register;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.respond.RegisterRespond;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ray on 6/11/2016.
 */

public class FrgRegister extends CustomFragment implements Validator.ValidationListener{
    public FrgRegister(){super();}

    public final static String TAG = "register";
    private Toolbar mToolBar;
    private final String mRegexName = "^[a-zA-Z0-9]{0,20}$";
    private final String mRegexPhone = "^\\d{0,20}$";
    private final String mRegexPwd= "^([a-zA-z0-9]){8,15}$";
    @NotEmpty
    @Pattern(regex = mRegexName)
    private EditText mUsernameField;
    @NotEmpty
    @Email
    private EditText mEmailField;
    @Password(min=8,scheme = Password.Scheme.ALPHA_NUMERIC)
    @NotEmpty
    private EditText mPasswordField;
    @ConfirmPassword
    private EditText mSecondPasswordField;
    @Pattern(regex = mRegexName)
    private EditText mFirstNameField;
    @NotEmpty
    @Pattern(regex = mRegexName)
    private EditText mLastNameField;
    @Pattern(regex = mRegexName)
    private EditText mNickNameField;
    private RadioButton mMaleBtn;
    private RadioButton mFemaleBtn;
    @Pattern(regex = mRegexPhone)
    private EditText mPhoneField;
    private EditText mDescriptField;
    private Button mSubmitBtn;
    private Validator mValidator;





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
        View v  =  inflater.inflate(R.layout.register_frg,container,false);
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

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mUsernameField = (EditText) v.findViewById(R.id.usernameField);
        mEmailField = (EditText) v.findViewById(R.id.emailField);
        mPasswordField = (EditText) v.findViewById(R.id.passwordField);
        mSecondPasswordField = (EditText) v.findViewById(R.id.secondPasswordField);
        mFirstNameField = (EditText) v.findViewById(R.id.firstNameField);
        mLastNameField = (EditText) v.findViewById(R.id.lastNameField);
        mNickNameField = (EditText) v.findViewById(R.id.nickNameField);
        mMaleBtn = (RadioButton) v.findViewById(R.id.maleBtn);
        mFemaleBtn = (RadioButton) v.findViewById(R.id.femaleBtn);
        mPhoneField = (EditText) v.findViewById(R.id.phoneField);
        mDescriptField = (EditText) v.findViewById(R.id.descriptField);

        mSubmitBtn = (Button) v.findViewById(R.id.submitBtn);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });

        return v;
    }

    @Override
    public void onValidationSucceeded() {
        HTTP httpService = HTTP.retrofit.create(HTTP.class);
        User user = new User(mUsernameField.getText().toString()
                , mPasswordField.getText().toString()
                , mFirstNameField.getText().toString(),
                mLastNameField.getText().toString()
                , mNickNameField.getText().toString()
                , true, 0, 0, 0
                , mEmailField.getText().toString(),
                mPhoneField.getText().toString(),
                mDescriptField.getText().toString(), 1);
        Call<RegisterRespond> call = httpService.createUser(user);
        call.enqueue(new Callback<RegisterRespond>() {
            @Override
            public void onResponse(Call<RegisterRespond> call, Response<RegisterRespond> response) {
                if(response.isSuccessful()) {
                    if(response.body().isSuccessful()) {
                        Toast.makeText(getContext(), "Register Sucessful!!" , Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterRespond> call, Throwable t) {

            }
        });
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


