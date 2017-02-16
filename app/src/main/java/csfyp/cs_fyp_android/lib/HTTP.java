package csfyp.cs_fyp_android.lib;

import java.util.concurrent.TimeUnit;

import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.Login;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.EventAllRequest;
import csfyp.cs_fyp_android.model.request.EventCreateRequest;
import csfyp.cs_fyp_android.model.request.EventJoinQuitRequest;
import csfyp.cs_fyp_android.model.request.EventListRequest;
import csfyp.cs_fyp_android.model.request.EventRequest;
import csfyp.cs_fyp_android.model.request.Rate;
import csfyp.cs_fyp_android.model.request.SelfRate;
import csfyp.cs_fyp_android.model.request.UserRequest;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import csfyp.cs_fyp_android.model.respond.EventListRespond;
import csfyp.cs_fyp_android.model.respond.LoginRespond;
import csfyp.cs_fyp_android.model.respond.Logout;
import csfyp.cs_fyp_android.model.respond.RegisterRespond;
import csfyp.cs_fyp_android.model.respond.UserRespond;
import okhttp3.OkHttpClient;
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

    @POST("/api/post-self-rate")
    Call<ErrorMsgOnly> postSelfRate (
            @Body SelfRate selfRate
    );

    @POST("/api/post-rate")
    Call<ErrorMsgOnly> postRate (
            @Body Rate rate
    );

    @POST("/api/login")
    Call<LoginRespond> login (
            @Body Login login
    );

    @POST("/api/logout")
    Call<ErrorMsgOnly> logout (
            @Body Logout logout
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

    @POST("/api/get-all-events")
    Call<EventListRespond> getAllEvents (
            @Body EventAllRequest eventAllRequest
    );



    @POST("/api/join-event")
    Call<ErrorMsgOnly> joinEvent (
            @Body EventJoinQuitRequest join
    );

    @POST("/api/quit-event")
    Call<ErrorMsgOnly> quitEvent (
            @Body EventJoinQuitRequest quit
    );

    @POST("/api/delete-event")
    Call<ErrorMsgOnly> deleteEvent (
            @Body EventJoinQuitRequest delete
    );

    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            //.sslSocketFactory(SSL.getNewSSL(),SSL.getTm())
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://137.189.204.173:3000")
            //.baseUrl("http://192.168.1.5:3000")
            //.baseUrl("https://stingray.space:3000")
            //.baseUrl("http://172.18.6.87:3000")
            //.baseUrl("http://192.168.1.5:3000")
            .baseUrl("https://stingray.space:3000")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
