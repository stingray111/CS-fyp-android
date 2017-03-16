package csfyp.cs_fyp_android.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mikelau.croperino.Croperino;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.PropicUpdate;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
import csfyp.cs_fyp_android.model.respond.RegisterRespond;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgRegister extends CustomFragment implements Validator.ValidationListener{
    public FrgRegister(){super();}

    public final static String TAG = "register";
    private Toolbar mToolBar;
    private CircularImageView mPropic;
    private Uri mPropicUri;
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
    @ConfirmPassword(message = "Password doesn't match")
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

        //Initialize on every usage
        new CroperinoConfig("IMG_" + System.currentTimeMillis() + ".jpg", "/MeetUs/Pictures", "/sdcard/MeetUs/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(getActivity());
        CroperinoFileUtil.setupDirectory(getActivity());

        ((ImageButton) v.findViewById(R.id.addPropicBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Croperino.prepareChooser(getActivity(), "Select photo for the profile...", ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            }
        });

//
        mPropic = (CircularImageView) v.findViewById(R.id.registerProPic);

        // validation
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

        final boolean isMale = mMaleBtn.isChecked();

        mProgressBar.setVisibility(View.VISIBLE);
        mSubmitBtn.setVisibility(View.GONE);

        final HTTP httpService = HTTP.retrofit.create(HTTP.class);

        final Callback<LoginRespond> loginCallback = new Callback<LoginRespond>() {
            @Override
            public void onResponse(Call<LoginRespond> call, Response<LoginRespond> response) {
                if(response.isSuccessful() && response.body().isSuccessful()){
                    // save token to cache
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("userToken", response.body().getToken());
                    editor.putInt("userId", response.body().getUserId());
                    editor.putString("username", response.body().getUsername());
                    editor.putString("msgToken", response.body().getMsgToken());
                    Log.d(TAG, "onResponse: "+response.body().getUserId());

                    Gson gson = new Gson();
                    String selfStr = gson.toJson(response.body().getSelf());
                    editor.putString("self", selfStr);
                    editor.commit();
                    MainActivity parent = (MainActivity)getActivity();

                    // set information in Main Activity for later use
                    parent.setmToken(response.body().getToken());
                    parent.setmUserId(response.body().getUserId());
                    parent.setmUsername(response.body().getUsername());
                    parent.setmSelf(response.body().getSelf());
                    Log.d(TAG, "Token"+response.body().getMsgToken());
                    parent.setmMsgToken(response.body().getMsgToken());

                    switchFragment(FrgSelfRating.newInstance());
                }
            }

            @Override
            public void onFailure(Call<LoginRespond> call, Throwable t) {
                Toast.makeText(getContext(), t.toString() , Toast.LENGTH_SHORT).show();
            }
        };

        final Callback<RegisterRespond> respondCallback = new Callback<RegisterRespond>() {
            @Override
            public void onResponse(Call<RegisterRespond> call, Response<RegisterRespond> response) {
                if(response.isSuccessful()) {
                    if(response.body().isSuccessful()) {
                        Toast.makeText(getContext(), "Register Successful!!" , Toast.LENGTH_SHORT).show();
                        String uuidInString = UUID.randomUUID().toString();
                        Call<LoginRespond> loginCall = httpService.login(new Login(mUsernameField.getText().toString(), mPasswordField.getText().toString(), uuidInString));
                        loginCall.enqueue(loginCallback);
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
        };

        if (mPropicUri == null) {
            // no propic
            User user = new User(mUsernameField.getText().toString(),
                    mPasswordField.getText().toString(),
                    mFirstNameField.getText().toString(),
                    mLastNameField.getText().toString(),
                    mNickNameField.getText().toString(),
                    isMale, 0, 0, 0,
                    mEmailField.getText().toString(),
                    mPhoneField.getText().toString(),
                    mDescriptField.getText().toString(), 1);

            Call<RegisterRespond> call = httpService.createUser(user);
            call.enqueue(respondCallback);

        } else {
            // with propic
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child(mPropicUri.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(mPropicUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // todo post to MainActivity error
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.i("FirebaseS", downloadUrl.toString());

                    User user = new User(mUsernameField.getText().toString(),
                            mPasswordField.getText().toString(),
                            mFirstNameField.getText().toString(),
                            mLastNameField.getText().toString(),
                            mNickNameField.getText().toString(),
                            downloadUrl.toString(),
                            isMale, 0, 0, 0,
                            mEmailField.getText().toString(),
                            mPhoneField.getText().toString(),
                            mDescriptField.getText().toString(), 1);

                    Call<RegisterRespond> call = httpService.createUser(user);
                    call.enqueue(respondCallback);
                }
            });
        }




    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        if(mUsernameField.getText().toString().contains("fuck")){
            mUsernameField.setText("test"+(Math.abs(new Random().nextInt()%99999)+1));
            mEmailField.setText(mUsernameField.getText().toString()+"@test.com");
            mPasswordField.setText("aaaaaaaa1");
            mSecondPasswordField.setText("aaaaaaaa1");
            mLastNameField.setText(mUsernameField.getText().toString());
            mMaleBtn.toggle();
            mSubmitBtn.callOnClick();
            return;
        }
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

    public void switchFragment(Fragment to) {
        super.switchFragment(this, to);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PropicUpdate event) {
        Toast.makeText(getContext(), event.getFile().toString(), Toast.LENGTH_SHORT).show();

        mPropicUri = Uri.fromFile(event.getFile());
        Picasso.with(getContext()).load(event.getFile()).into(mPropic);
    }
}


