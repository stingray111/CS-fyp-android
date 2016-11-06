package csfyp.cs_fyp_android.lib;

import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.LoginStatus;
import csfyp.cs_fyp_android.model.RegisterStatus;
import csfyp.cs_fyp_android.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HTTP {
    @POST("/api/register")
    Call<RegisterStatus> createUser (
            @Body User user
    );

    @POST("/api/login")
    Call<LoginStatus> login (
            @Body Login login
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.5:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
