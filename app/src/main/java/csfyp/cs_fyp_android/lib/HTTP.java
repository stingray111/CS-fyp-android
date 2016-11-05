package csfyp.cs_fyp_android.lib;

import csfyp.cs_fyp_android.model.User;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface HTTP {
    @POST("/api/{path}")
    Call<Boolean> createUser (
            @Path("path") String path,
            @Body User user
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.1.5:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
