package csfyp.cs_fyp_android.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.UUID;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
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
    private final String mRegexName = "^[a-zA-Z0-9 ]{0,20}$";
    private final String mRegexUserName = "^[a-zA-Z0-9]{5,20}$";
    private final String mRegexPhone = "^\\d{0,20}$";
    private final String mRegexPwd= "^([a-zA-z0-9]){8,15}$";
    @NotEmpty
    @Pattern(regex = mRegexUserName, message = "Username should be numbers or letters with length 5 to 20.")
    private EditText mUsernameField;
    @NotEmpty
    @Email
    private EditText mEmailField;
    @Password(min=8,scheme = Password.Scheme.ALPHA_NUMERIC,message = "Password should contain numbers and letters with length 8 to 20.")
    @NotEmpty
    private EditText mPasswordField;
    @ConfirmPassword
    private EditText mSecondPasswordField;
    @Pattern(regex = mRegexName,message = "Symbols are not allowed. Maximum length is 20")
    private EditText mFirstNameField;
    @NotEmpty
    @Pattern(regex = mRegexName,message = "Symbols are not allowed. Maximum length is 20")
    private EditText mLastNameField;
    @Pattern(regex = mRegexName,message = "Symbols are not allowed. Maximum length is 20")
    private EditText mNickNameField;
    private RadioButton mMaleBtn;
    private RadioButton mFemaleBtn;
    @Pattern(regex = mRegexPhone,message = "Invalid Phone Number")
    private EditText mPhoneField;
    private EditText mDescriptField;
    private Button mSubmitBtn;
    private Validator mValidator;

    private ProgressBar mProgressBar;


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
        mProgressBar = (ProgressBar) v.findViewById(R.id.registerProgressBar);
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
        //close the keyboard
        View currentView = getActivity().getCurrentFocus();
        if(currentView!=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(),0);
        }
        //validate the gender
        if(!mMaleBtn.isChecked() && !mFemaleBtn.isChecked()){
            Toast.makeText(getContext(), "Please Choose a gender." , Toast.LENGTH_SHORT).show();
            return;
        }
        //check password length
        if(mPasswordField.getText().toString().length() > 20){
            Toast.makeText(getContext(), "Password should be within 20 characters." , Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isMale = mMaleBtn.isChecked();

        final HTTP httpService = HTTP.retrofit.create(HTTP.class);
        User user = new User(mUsernameField.getText().toString()
                , mPasswordField.getText().toString()
                , mFirstNameField.getText().toString(),
                mLastNameField.getText().toString()
                , mNickNameField.getText().toString()
                , isMale, 0, 0, 0
                , mEmailField.getText().toString(),
                mPhoneField.getText().toString(),
                mDescriptField.getText().toString(), 1);
        Call<RegisterRespond> call = httpService.createUser(user);

        mProgressBar.setVisibility(View.VISIBLE);
        mSubmitBtn.setVisibility(View.GONE);

        call.enqueue(new Callback<RegisterRespond>() {
            @Override
            public void onResponse(Call<RegisterRespond> call, Response<RegisterRespond> response) {
                if(response.isSuccessful()) {
                    if(response.body().isSuccessful()) {
                        Toast.makeText(getContext(), "Register Successful!!" , Toast.LENGTH_SHORT).show();
                        String uuidInString = UUID.randomUUID().toString();
                        Call<LoginRespond> loginCall = httpService.login(new Login(mUsernameField.getText().toString(), mPasswordField.getText().toString(), uuidInString));
                        loginCall.enqueue(new Callback<LoginRespond>() {
                            @Override
                            public void onResponse(Call<LoginRespond> call, Response<LoginRespond> response) {
                                if(response.isSuccessful() && response.body().isSuccessful()){

                                    // save token to cache
                                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("userToken", response.body().getToken());
                                    editor.putInt("userId", response.body().getUserId());
                                    editor.putString("username", response.body().getUsername());
                                    Log.d(TAG, "onResponse: "+response.body().getUserId());
                                    editor.commit();
                                    MainActivity parent = (MainActivity)getActivity();
                                    parent.setmToken(response.body().getToken());
                                    parent.setmUserId(response.body().getUserId());
                                    parent.setmUsername(response.body().getUsername());

                                    replaceFragment(FrgHome.newInstance());
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginRespond> call, Throwable t) {
                                Toast.makeText(getContext(), t.toString() , Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                        mSubmitBtn.setVisibility(View.VISIBLE);
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


