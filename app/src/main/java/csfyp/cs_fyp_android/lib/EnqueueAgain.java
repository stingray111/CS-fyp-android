package csfyp.cs_fyp_android.lib;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by ray on 22/3/2017.
 */

public class EnqueueAgain<T> implements Runnable {
    Call<T> mCall;
    Callback<T> mBack;

    public EnqueueAgain(Call<T> call, Callback<T> back) {
        mCall = call;
        mBack = back;
    }

    @Override
    public void run() {
        mCall.clone().enqueue(mBack);
    }
}
