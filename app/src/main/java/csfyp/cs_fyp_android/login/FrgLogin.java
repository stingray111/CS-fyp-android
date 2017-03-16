package csfyp.cs_fyp_android.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.LoginFrgBinding;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
import csfyp.cs_fyp_android.model.respond.ThirdPartySignInRespond;
import csfyp.cs_fyp_android.register.FrgRegister;
import csfyp.cs_fyp_android.register.FrgSelfRating;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.facebook.FacebookSdk;

import static com.google.android.gms.common.SignInButton.SIZE_WIDE;


public class FrgLogin extends CustomFragment implements Validator.ValidationListener, GoogleApiClient.OnConnectionFailedListener {
    public FrgLogin(){
        super();
    }

    public final static String TAG = "login";
    public static final int GOOGLE_SIGN_IN_CODE = 9001;

    private LoginFrgBinding mDataBinding;
    private ProgressDialog mProgressDialog;

    private Toolbar mToolBar;
    private Button mLoginBtn;
    @NotEmpty
    private EditText mInputEmailOrUsername;
    @Password(min=8,scheme = Password.Scheme.ALPHA_NUMERIC)
    private EditText mInputPassword;
    private TextView mRegisterBtn;
    private Validator mValidator;
    private ProgressBar mProgressBar;
    private SignInButton mGoogleSignInBtn;
    private GoogleApiClient mGoogleApiClient;
    private LoginButton mFacebookLoginButton;
    private AccessToken accessToken;
    public CallbackManager callbackManager;

    public static FrgLogin newInstance() {

        Bundle args = new Bundle();

        FrgLogin fragment = new FrgLogin();
        fragment.setArguments(args);
        return fragment;
    }

    public void switchFragment(Fragment to) {
        super.switchFragment(this, to);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            EventBus.getDefault().post(result);
        }else if(requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
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
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.login_frg, container, false);
        mDataBinding.setHandlers(this);

        View v = mDataBinding.getRoot();

        mLoginBtn = mDataBinding.loginBtn;
        mInputEmailOrUsername = mDataBinding.inputEmailOrUsername;

        mValidator = new Validator(this);
        mValidator.setValidationListener(this);

        mProgressBar = mDataBinding.loginProgressBar;
        mInputPassword = mDataBinding.inputPassword;
        mRegisterBtn = mDataBinding.registerBtn;

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(FrgRegister.newInstance());
            }

        });

        mGoogleSignInBtn = mDataBinding.googleSignInBtn;
        mGoogleSignInBtn.setSize(SIZE_WIDE);
        for (int i = 0 ;i < mGoogleSignInBtn.getChildCount();i++){
            View view = mGoogleSignInBtn.getChildAt(i);
            if(view instanceof TextView){
                ((TextView)view).setPadding(0,view.getPaddingTop(),18,view.getPaddingBottom());
            }
        }
        mGoogleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);
            }
        });

        mFacebookLoginButton = mDataBinding.facebookSignInBtn;
        ArrayList<String> permission = new ArrayList<String>();
        permission.add("public_profile");
        permission.add("email");
        mFacebookLoginButton.setReadPermissions(permission);
        mFacebookLoginButton.setFragment(this);
        mFacebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"facebook success");
                EventBus.getDefault().post(loginResult);
            }

            @Override
            public void onCancel() {
                //NO NEED TO HANDLE
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onValidationSucceeded() {
        String strEmailOrUsername = mInputEmailOrUsername.getText().toString();
        String strPassword = mInputPassword.getText().toString();

        if (!strEmailOrUsername.matches("") && !strPassword.matches("")) {

            String uuidInString = UUID.randomUUID().toString();

            //mProgressBar.setVisibility(View.VISIBLE);
            //mLoginBtn.setVisibility(View.GONE);

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
                            editor.putString("username", response.body().getUsername());
                            editor.putString("msgToken", response.body().getMsgToken());
                            Gson gson = new Gson();
                            String selfStr = gson.toJson(response.body().getSelf());
                            editor.putString("self", selfStr);
                            editor.commit();

                            MainActivity parent = (MainActivity)getActivity();
                            parent.setmToken(response.body().getToken());
                            parent.setmUserId(response.body().getUserId());
                            parent.setmUsername(response.body().getUsername());
                            parent.setmSelf(response.body().getSelf());
                            parent.setmMsgToken(response.body().getMsgToken());
                            replaceFragment(((MainActivity) getActivity()).getmHome());


                        } else {
                            // TODO: 6/11/2016 print error msg to user
                            if (response.body().getErrorMsg().matches("passwordWrong"))
                                mInputPassword.setError("Wrong Password");
                            if (response.body().getErrorMsg().matches("userNotfound"))
                                mInputEmailOrUsername.setError("User Not Found");
                            Log.i(TAG, response.body().getErrorMsg());
                            mProgressBar.setVisibility(View.GONE);
                            mLoginBtn.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginRespond> call, Throwable t) {
                    Log.i(TAG, "Connect exception:" + t.getMessage());
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    mLoginBtn.setVisibility(View.VISIBLE);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Google Connection Fail");
    }

    public void onClickLogin(View view) {
        mValidator.validate();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void googleLoginCallback(GoogleSignInResult result) {
        if (result.isSuccess()) {
            try {
                GoogleSignInAccount account = result.getSignInAccount();
                /*
                Log.d(TAG, "id: " + account.getId());
                Log.d(TAG, "token: " + account.getIdToken());
                Log.d(TAG, "first: " + account.getGivenName());
                Log.d(TAG, "last: " + account.getFamilyName());
                Log.d(TAG, "photo: " + String.valueOf(account.getPhotoUrl()));
                Log.d(TAG, "email" + account.getEmail());
                */
                User user = new User(
                        "gg+"+account.getId(),
                        1,
                        account.getGivenName(),
                        account.getFamilyName(),
                        String.valueOf(account.getPhotoUrl()),
                        0,
                        account.getEmail()
                );
                thirdPartySignIn(user);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void facebookLoginCallback(LoginResult loginResult){
        AccessToken accessToken = loginResult.getAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                String photoURL = "";
                try {
                    photoURL = (new JSONObject(object.optString("cover")).optString("source"));
                }catch (Exception e){
                }
                int gender = 0;
                if(object.optString("gender").equals("male")) gender = 1;
                if(object.optString("gender").equals("female")) gender = 2;

                /*
                Log.d(TAG, "complete");
                Log.d(TAG, object.optString("id"));
                Log.d(TAG, photoURL);
                Log.d(TAG, object.optString("first_name"));
                Log.d(TAG, object.optString("last_name"));
                Log.d(TAG, object.optString("gender"));
                Log.d(TAG, object.optString("gender"));
                */

                User user = new User(
                        "fb+"+object.optString("id"),
                        2,
                        object.optString("first_name"),
                        object.optString("last_name"),
                        photoURL,
                        gender,
                        ""
                );
                thirdPartySignIn(user);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","id,cover,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void thirdPartySignIn(User user){
        Log.d(TAG, user.getUserName());
        Log.d(TAG, user.getProPic());
        Log.d(TAG, user.getFirstName());
        Log.d(TAG, user.getLastName());
        Log.d(TAG, user.getEmail());
        Log.d(TAG, "" + user.getGender());
        Log.d(TAG, ""+ user.getActype());
        final HTTP httpService = HTTP.retrofit.create(HTTP.class);
        Call<ThirdPartySignInRespond> thirdPartyCall = httpService.thirdPartySignIn(user);
        final Callback<ThirdPartySignInRespond> thirdPartySignInRespondCallback = new Callback<ThirdPartySignInRespond>() {
            @Override
            public void onResponse(Call<ThirdPartySignInRespond> call, Response<ThirdPartySignInRespond> response) {
                if(response.isSuccessful()){
                    if(response.body().isSuccessful()) {
                        Log.d(TAG, "uid: "+ response.body().getUserId());
                        Log.d(TAG, "uid: "+ response.body().getMsgToken());
                        Log.d(TAG, "uid: "+ response.body().getToken());
                        Log.d(TAG, "uid: "+ response.body().getUsername());
                        Log.d(TAG, "uid: "+ response.body().getAcType());

                        // save token to cache
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userToken", response.body().getToken());
                        editor.putInt("userId", response.body().getUserId());
                        editor.putString("username", response.body().getUsername());
                        editor.putString("msgToken", response.body().getMsgToken());
                        editor.putInt("acType", response.body().getAcType());
                        Gson gson = new Gson();
                        String selfStr = gson.toJson(response.body().getSelf());
                        editor.putString("self", selfStr);
                        editor.commit();

                        MainActivity parent = (MainActivity)getActivity();
                        parent.setmToken(response.body().getToken());
                        parent.setmUserId(response.body().getUserId());
                        parent.setmUsername(response.body().getUsername());
                        parent.setmSelf(response.body().getSelf());
                        parent.setmMsgToken(response.body().getMsgToken());
                        parent.setmAcType(response.body().getAcType());

                        if(response.body().isSignIn()){
                            replaceFragment(((MainActivity) getActivity()).getmHome());
                        }
                        else{
                            switchFragment(FrgSelfRating.newInstance());
                        }

                    }else{
                        Log.d(TAG, "message: " + response.body().getErrorMsg());
                    }
                }else{
                    Log.d(TAG,"message: "+response.message());
                }
            }

            @Override
            public void onFailure(Call<ThirdPartySignInRespond> call, Throwable t) {
                Log.d(TAG,"fail");
            }
        };
        thirdPartyCall.enqueue(thirdPartySignInRespondCallback);
    }
}

