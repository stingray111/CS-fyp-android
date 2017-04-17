package csfyp.cs_fyp_android.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.model.User;

/**
 * Created by ray on 12/4/2017.
 */

public class FrgChat extends CustomFragment {

    private static String TAG = "FrgChat";
    private Toolbar mToolbar;
    private TextView  mEmptyMsg;
    private RecyclerView mChatRv;
    private ChatFrgAdt mChatFrgAdt;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static FrgChat newInstance() {
        Bundle args = new Bundle();
        FrgChat fragment = new FrgChat();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_frg,container,false);

        mToolbar = (Toolbar)v.findViewById(R.id.chatToolBar);
        mToolbar.setTitle(R.string.navbar_chat);
        mToolbar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mEmptyMsg = (TextView)v.findViewById(R.id.chatEmptyMsg);

        mChatRv = (RecyclerView)v.findViewById(R.id.rvChat);
        mLayoutManager = new LinearLayoutManager(getContext());
        mChatRv.setLayoutManager(mLayoutManager);


        if(getMainActivity().getMessageFullEventList()!= null && getMainActivity().getMessageFullEventList().size() == 0){
            Log.d("here","no");
            mEmptyMsg.setVisibility(View.VISIBLE);
            mChatRv.setVisibility(View.GONE);
        }else {
            Log.d("here","yes");
            mEmptyMsg.setVisibility(View.GONE);
            mChatRv.setVisibility(View.VISIBLE);
            mChatFrgAdt = new ChatFrgAdt(getMainActivity().getMessageFullEventList(), getContext());
            mChatRv.setAdapter(mChatFrgAdt);
        }
        return v;
    }


    public MainActivity getMainActivity(){
        return (MainActivity)getActivity();
    }

}
