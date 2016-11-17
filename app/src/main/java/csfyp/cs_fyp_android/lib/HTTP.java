package csfyp.cs_fyp_android.lib;

import csfyp.cs_fyp_android.model.request.EventId;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.request.EventFilter;
import csfyp.cs_fyp_android.model.request.EventPost;
import csfyp.cs_fyp_android.model.respond.EventRespond;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
import csfyp.cs_fyp_android.model.respond.RegisterRespond;
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

    @POST("/api/get-event")
    Call<Event> getEvent (
            @Body EventId eventId
            );

    @POST("/api/get-events")
    Call<EventRespond> getEvents (
            @Body EventFilter eventFilter
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.5:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
