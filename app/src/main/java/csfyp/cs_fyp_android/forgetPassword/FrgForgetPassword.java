package csfyp.cs_fyp_android.forgetPassword;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.databinding.ForgetPasswordFrgBinding;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.ErrorMsg;
import csfyp.cs_fyp_android.lib.eventBus.SnackBarMessageContent;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EmailOnly;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * Created by ray on 22/3/2017.
 */

public class FrgForgetPassword extends CustomFragment implements Validator.ValidationListener{
    private static String TAG = "ForgetPassword";
    private ForgetPasswordFrgBinding mDatabinding;
    private Toolbar mToolBar;
    @Email
    private EditText mEmailField;
    private Button mSubmitButton;
    private ProgressBar mProgressBar;

    private Validator mValidator;

    public FrgForgetPassword(){
    }

    public static FrgForgetPassword newInstance() {
        Bundle args = new Bundle();
        FrgForgetPassword fragment = new FrgForgetPassword();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mDatabinding = DataBindingUtil.inflate(inflater, R.layout.forget_password_frg, container, false);

        mToolBar = mDatabinding.forgetPwdToolBar;
        mToolBar.setTitle("Forget Password");
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mProgressBar = mDatabinding.forgetPwdSubmitProgress;

        mEmailField = mDatabinding.forgetPwEmail;
        mSubmitButton = mDatabinding.forgetPwdSubmitBtn;
        mSubmitButton.setOnClickListener(new ValidatorOnClickListener());

        View v  = mDatabinding.getRoot();
        return v;
    }

    @Override
    public void onValidationSucceeded() {
        sendToServer(mEmailField.getText().toString());
    }

    private void sendToServer(final String email){
        final HTTP httpService = HTTP.retrofit.create(HTTP.class);
        final Callback<ErrorMsgOnly> forgetPwdCallback = new Callback<ErrorMsgOnly>() {
            @Override
            public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                if(response.isSuccessful()) {
                    if(response.body().isNull()) {
                        EventBus.getDefault().post(new SnackBarMessageContent("New password has been sent to your email"));
                        getActivity().onBackPressed();
                    }
                    else{
                        EventBus.getDefault().post(new ErrorMsg(response.body().getErrorMsg(), Toast.LENGTH_LONG));
                        mSubmitButton.setOnClickListener(new ValidatorOnClickListener());
                        mSubmitButton.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(GONE);
                    }
                }else{
                    EventBus.getDefault().post(new ErrorMsg("Server Error", Toast.LENGTH_LONG));
                    mSubmitButton.setOnClickListener(new ValidatorOnClickListener());
                    mSubmitButton.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                EventBus.getDefault().post(new ErrorMsg("Cannot connect to server", Toast.LENGTH_LONG));
                mSubmitButton.setOnClickListener(new ValidatorOnClickListener());
                mSubmitButton.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(GONE);
            }
        };
        Call<ErrorMsgOnly> forgetPwdCall = httpService.forgetUser(new EmailOnly(email));
        forgetPwdCall.enqueue(forgetPwdCallback);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
        mSubmitButton.setOnClickListener(new ValidatorOnClickListener());
        mSubmitButton.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(GONE);
    }

    private class ValidatorOnClickListener implements  View.OnClickListener{
        @Override
        public void onClick(View v) {
            v.setOnClickListener(null);
            v.setVisibility(GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mValidator.validate();
        }
    }


}
