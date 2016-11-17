package csfyp.cs_fyp_android.lib;

import csfyp.cs_fyp_android.model.request.EventCreateRequest;
import csfyp.cs_fyp_android.model.request.EventRequest;
import csfyp.cs_fyp_android.model.request.UserRequest;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
import csfyp.cs_fyp_android.model.respond.RegisterRespond;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.respond.UserRespond;
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

    @POST("/api/get-user")
    Call<UserRespond> getUser(
            @Body UserRequest userRequest
    );

    @POST("/api/push-event")
    Call<ErrorMsgOnly> pushEvent (
            @Body EventCreateRequest eventPost
    );

    @POST("/api/get-event")
    Call<Event> getEvent (
            @Body EventRequest eventId
    );

    @POST("/api/get-events")
    Call<EventListRespond> getEvents (
            @Body EventListRequest eventFilter
    );

    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://192.168.1.5:3000")
            .baseUrl("http://54.179.174.239:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
