package csfyp.cs_fyp_android.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.R;

/**
 * Created by ray on 18/10/2016.
 */

public class FrgAbout extends CustomFragment{
    public FrgAbout(){ super();}
    public static final String TAG = "About";
    private Toolbar mToolBar;
    private TextView mhyperlink;

    public static FrgAbout newInstance(){
        Bundle args = new Bundle();
        FrgAbout fragment = new FrgAbout();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View v = inflater.inflate(R.layout.about_frg,container,false);
        mToolBar = (Toolbar) v.findViewById(R.id.aboutToolBar);
        mToolBar.setTitle("About");
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               onBack(TAG);
            }
        });

        mhyperlink = (TextView) v.findViewById(R.id.icon8Link);
        String linkText = "<a href='http://icon8.com'>icon8.com</a>";
        mhyperlink.setText(Html.fromHtml(linkText));
        mhyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        return v;
    }
}
