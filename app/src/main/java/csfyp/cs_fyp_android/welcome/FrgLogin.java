package csfyp.cs_fyp_android.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.LoginStatus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgLogin extends CustomFragment{
    public FrgLogin(){
        super();
    }

    public final static String TAG = "login";
    private Toolbar mToolBar;
    private Button mLoginBtn;
    private EditText mInputEmailOrUsername;
    private EditText mInputPassword;

    public static FrgLogin newInstance() {

        Bundle args = new Bundle();

        FrgLogin fragment = new FrgLogin();
        fragment.setArguments(args);
        return fragment;
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
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mLoginBtn = (Button) v.findViewById(R.id.loginBtn);
        mInputEmailOrUsername = (EditText) v.findViewById(R.id.inputEmailOrUsername);
        mInputPassword = (EditText) v.findViewById(R.id.inputPassword);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmailOrUsername = mInputEmailOrUsername.getText().toString();
                String strPassword = mInputPassword.getText().toString();

                if (!strEmailOrUsername.matches("") && !strPassword.matches("")){
                    String uuidInString = UUID.randomUUID().toString();

                    HTTP httpService = HTTP.retrofit.create(HTTP.class);
                    Login login = new Login(strEmailOrUsername, strPassword, uuidInString);
                    Call<LoginStatus> call = httpService.login(login);
                    call.enqueue(new Callback<LoginStatus>() {
                        @Override
                        public void onResponse(Call<LoginStatus> call, Response<LoginStatus> response) {
                            if(response.isSuccessful()){
                                if(response.body().isSuccessful()) {

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginStatus> call, Throwable t) {

                        }
                    });
                }
            }
        });

        return v;
    }
}
