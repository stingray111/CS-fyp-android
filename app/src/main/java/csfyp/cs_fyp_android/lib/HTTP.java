package csfyp.cs_fyp_android.lib;

import csfyp.cs_fyp_android.model.ErrorMsgOnly;
import csfyp.cs_fyp_android.model.EventFilter;
import csfyp.cs_fyp_android.model.EventPost;
import csfyp.cs_fyp_android.model.EventRespond;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.LoginRespond;
import csfyp.cs_fyp_android.model.RegisterRespond;
import csfyp.cs_fyp_android.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HTTP {
    @POST("/api/register")
    Call<RegisterRespond> createUser (
            @Body User user
    );

    @POST("/api/login")
    Call<LoginRespond> login (
            @Body Login login
    );

    @POST("/api/push-event")
    Call<ErrorMsgOnly> pushEvent (
            @Body EventPost eventPost
    );

    @POST("/api/get-events")
    Call<EventRespond> getEvents (
            @Body EventFilter eventFilter
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://172.18.1.91:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
