package csfyp.cs_fyp_android.event;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import csfyp.cs_fyp_android.R;

/**
 * Created by ray on 13/10/2016.
 */

public class FrgEvent extends Fragment {
    public FrgEvent() {
        super();
    }

    public static FrgEvent newInstance() {
        
        Bundle args = new Bundle();
        
        FrgEvent fragment = new FrgEvent();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.event_frg,container,false);
        return v;
    }


}
