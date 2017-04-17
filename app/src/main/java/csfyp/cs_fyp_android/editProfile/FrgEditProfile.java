package csfyp.cs_fyp_android.editProfile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EditProfileFrgBinding;

public class FrgEditProfile extends CustomFragment implements Validator.ValidationListener {

    public final static String TAG = "editProfile";
    private Toolbar mToolBar;
    private CircularImageView mPropic;
    private Uri mPropicUri;
    private final String mRegexName = "^[a-zA-Z0-9 ]{0,20}$";
    private final String mRegexPhone = "^\\d{0,20}$";

    @NotEmpty
    @Email
    private EditText mEmailField;
    @Password(min=8,scheme = Password.Scheme.ALPHA_NUMERIC,message = "Password should contain numbers and letters with length 8 to 20.")
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



    private EditProfileFrgBinding mDatabinding;


    public static FrgEditProfile newInstance() {

        Bundle args = new Bundle();

        FrgEditProfile fragment = new FrgEditProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDatabinding = DataBindingUtil.inflate(inflater, R.layout.edit_profile_frg, container, false);
        View v = mDatabinding.getRoot();
        return v;
    }

    @Override
    public void onValidationSucceeded() {
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
