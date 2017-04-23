package csfyp.cs_fyp_android.editProfile;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EditProfileFrgBinding;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.eventBus.PropicUpdate;
import csfyp.cs_fyp_android.lib.eventBus.RefreshLoader;
import csfyp.cs_fyp_android.lib.eventBus.SnackBarMessageContent;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.profile.FrgProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgEditProfile extends CustomFragment implements Validator.ValidationListener {

    public final static String TAG = "editProfile";
    private Toolbar mToolBar;
    private CircularImageView mPropic;
    private Uri mPropicUri;
    private final String mRegexName = "^[a-zA-Z0-9 ]{0,20}$";
    private final String mRegexPhone = "^\\d{0,20}$";


    private int mUserId;
    @NotEmpty
    @Pattern(regex = mRegexName,message = "Symbols are not allowed. Maximum length is 20")
    private EditText mFirstNameField;
    @NotEmpty
    @Pattern(regex = mRegexName,message = "Symbols are not allowed. Maximum length is 20")
    private EditText mLastNameField;
    @Pattern(regex = mRegexName,message = "Symbols are not allowed. Maximum length is 20")
    private EditText mNickNameField;
    @Pattern(regex = mRegexPhone,message = "Invalid Phone Number")
    private EditText mPhoneField;
    private EditText mDescriptField;
    private Button mSubmitBtn;
    private Validator mValidator;
    private String proPicUploadedURL;



    private EditProfileFrgBinding mDatabinding;


    public static FrgEditProfile newInstance(int id, String propPic, String fName, String lName, String nickName, String phoneNo, String description) {

        Bundle args = new Bundle();
        args.putInt("userId", id);
        args.putString("ProPic", propPic);
        args.putString("FirstName", fName);
        args.putString("LastName", lName);
        args.putString("NickName", nickName);
        args.putString("PhoneNo", phoneNo);
        args.putString("Description", description);

        FrgEditProfile fragment = new FrgEditProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDatabinding = DataBindingUtil.inflate(inflater, R.layout.edit_profile_frg, container, false);
        View v = mDatabinding.getRoot();

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mUserId = getArguments().getInt("userId");
        mPropic = mDatabinding.editProPic;

        if (getArguments().getString("ProPic") != null) {
            Picasso.with(getContext()).load(getArguments().getString("ProPic")).into(mPropic);
        }

        mFirstNameField = mDatabinding.editFirstNameField;
        mLastNameField = mDatabinding.editLastNameField;
        mNickNameField = mDatabinding.editNickNameField;
        mPhoneField = mDatabinding.editPhoneField;
        mDescriptField = mDatabinding.editDescriptField;

        if (!getArguments().getString("PhoneNo", "").equals(getResources().getString(R.string.profile_no_phone))) {
            mPhoneField.setText(getArguments().getString("PhoneNo", ""));
        }
        if (!getArguments().getString("Description", "").equals(getResources().getString(R.string.profile_no_description))) {
            mDescriptField.setText(getArguments().getString("Description", ""));
        }

        mFirstNameField.setText(getArguments().getString("FirstName", ""));
        mLastNameField.setText(getArguments().getString("LastName", ""));
        mNickNameField.setText(getArguments().getString("NickName", ""));

        mToolBar = mDatabinding.editToolBar;
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setTitle(R.string.editProfile);
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

        mDatabinding.addPropicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Croperino.prepareChooser(getActivity(), "Select photo for the profile...", ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            }
        });

        mDatabinding.editSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });

        return v;
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
    public void onValidationSucceeded() {
        View currentView = getActivity().getCurrentFocus();
        if(currentView!=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(),0);
        }

        final Callback<ErrorMsgOnly> respondCallback = new Callback<ErrorMsgOnly>() {
            @Override
            public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                if (response.isSuccessful()) {
                    if (response.body().isNull()) {
                        Toast.makeText(getContext(), "Edit Successful!!", Toast.LENGTH_SHORT).show();

                        ((MainActivity)getActivity()).getmSelf().setFirstName(mFirstNameField.getText().toString());
                        ((MainActivity)getActivity()).getmSelf().setLastName(mLastNameField.getText().toString());
                        ((MainActivity)getActivity()).getmSelf().setNickName(mNickNameField.getText().toString());
                        ((MainActivity)getActivity()).getmSelf().setPhone(mPhoneField.getText().toString());
                        ((MainActivity)getActivity()).getmSelf().setDescription(mDescriptField.getText().toString());
                        if (proPicUploadedURL != null)
                            ((MainActivity)getActivity()).getmSelf().setProPic(proPicUploadedURL);

                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Gson gson = new Gson();
                        String selfStr = gson.toJson(((MainActivity)getActivity()).getmSelf());
                        editor.putString("self", selfStr);
                        editor.commit();

                        EventBus.getDefault().post(new RefreshLoader(FrgProfile.USER_LOADER_ID));
                        EventBus.getDefault().post(new RefreshLoader(FrgHome.HOME_LOADER_ID));
                        mDatabinding.editProgressBar.setVisibility(View.GONE);
                        mDatabinding.editSubmitBtn.setVisibility(View.VISIBLE);
                        onBack(null);
                    } else {
                        Toast.makeText(getContext(), response.body().getErrorMsg(), Toast.LENGTH_SHORT).show();
                        mDatabinding.editProgressBar.setVisibility(View.GONE);
                        mDatabinding.editSubmitBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                EventBus.getDefault().post(new SnackBarMessageContent(t.toString()));
            }
        };

        final HTTP httpService = HTTP.retrofit.create(HTTP.class);

        mDatabinding.editProgressBar.setVisibility(View.VISIBLE);
        mDatabinding.editSubmitBtn.setVisibility(View.GONE);

        if (mPropicUri == null) {
            // no propic
            User user = new User(mUserId,
                    mFirstNameField.getText().toString(),
                    mLastNameField.getText().toString(),
                    mNickNameField.getText().toString(),
                    null,
                    mPhoneField.getText().toString(),
                    mDescriptField.getText().toString());

            Call<ErrorMsgOnly> call = httpService.editProfie(user);
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
                    proPicUploadedURL = downloadUrl.toString();

                    User user = new User(mUserId,
                            mFirstNameField.getText().toString(),
                            mLastNameField.getText().toString(),
                            mNickNameField.getText().toString(),
                            proPicUploadedURL,
                            mPhoneField.getText().toString(),
                            mDescriptField.getText().toString());

                    Call<ErrorMsgOnly> call = httpService.editProfie(user);
                    call.enqueue(respondCallback);
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

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PropicUpdate event) {
        Toast.makeText(getContext(), event.getFile().toString(), Toast.LENGTH_SHORT).show();

        mPropicUri = Uri.fromFile(event.getFile());
        Picasso.with(getContext()).load(event.getFile()).into(mPropic);

        PropicUpdate update = EventBus.getDefault().getStickyEvent(PropicUpdate.class);
        if(update != null) {
            EventBus.getDefault().removeStickyEvent(update);
        }
    }

    @Override
    public void onBack(String identifier) {
        View currentView = getActivity().getCurrentFocus();
        if(currentView!=null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(currentView.getWindowToken(),0);
        }
        super.onBack(identifier);
    }
}
