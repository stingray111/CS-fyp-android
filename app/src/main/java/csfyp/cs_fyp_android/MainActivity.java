package csfyp.cs_fyp_android;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.home.FrgHome;
import csfyp.cs_fyp_android.profile.FrgProfile;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.parent_fragment_container, FrgEvent.newInstance());
        ft.commit();
    }
}
