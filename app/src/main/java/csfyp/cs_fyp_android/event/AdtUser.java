package csfyp.cs_fyp_android.event;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EventItemUserBinding;
import csfyp.cs_fyp_android.databinding.PassedEventItemBinding;
import csfyp.cs_fyp_android.lib.eventBus.SwitchFrg;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.profile.FrgProfile;
import csfyp.cs_fyp_android.rating.FrgRating;


public class AdtUser extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<User> mUserList;
    private int mode;
    private int eventId;

    public static int NORMAL_MODE = 0;
    public static int PASSED_EVENT_MODE = 1;

    public AdtUser(int mode, int eventId) {
        this.mode = mode;
        this.eventId = eventId;
    }

    @Override
    public int getItemCount() {
        if (mUserList != null)
            return mUserList.size();
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (mode == NORMAL_MODE) {
            View itemView = inflater.inflate(R.layout.event_item_user, parent, false);
            return new NormalEventViewHolder(itemView);
        }
        else if (mode == PASSED_EVENT_MODE) {
            View itemView = inflater.inflate(R.layout.passed_event_item, parent, false);
            return new PassedEventViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.event_item_user, parent, false);
            return new NormalEventViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mode == PASSED_EVENT_MODE) {
            PassedEventViewHolder PEHolder = (PassedEventViewHolder) holder;
            PEHolder.getBinding().setHandlers(PEHolder);

            if (mUserList != null) {

                PEHolder.getBinding().setItem(mUserList.get(position));

                // is rated by other?
                if (mUserList.get(position).isRatedbyOther()) {
                    PEHolder.getBinding().rateBtn.setVisibility(View.GONE);
                    PEHolder.getBinding().ratedImg.setVisibility(View.VISIBLE);
                } else {
                    PEHolder.getBinding().rateBtn.setVisibility(View.VISIBLE);
                    PEHolder.getBinding().ratedImg.setVisibility(View.GONE);
                }

                // is self?
                if (mUserList.get(position).getId() == eventId)
                    PEHolder.getBinding().rateBtn.setVisibility(View.GONE);
                // is attended?
                if (!mUserList.get(position).isAttended()) {
                    Log.i("hey", "not attended" + mUserList.get(position).getUserName());
                    PEHolder.getBinding().rateBtn.setVisibility(View.GONE);
                }

                PEHolder.getBinding().executePendingBindings();
            }
        } else {
            NormalEventViewHolder NEHolder = (NormalEventViewHolder) holder;
            NEHolder.getBinding().setHandlers(NEHolder);
            if (mUserList != null) {
                NEHolder.getBinding().setItem(mUserList.get(position));
                NEHolder.getBinding().executePendingBindings();
            }
        }
    }

    public void setmUserList(List<User> mUserList) {
        this.mUserList = mUserList;
    }

    public class NormalEventViewHolder extends RecyclerView.ViewHolder{

        private EventItemUserBinding binding;

        public NormalEventViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        // each data item is just a string in this case
        public void onClickUserItem(View view) {

            Bundle b = new Bundle();
            b.putInt("userId", binding.getItem().getId());
            SwitchFrg temp;
            if (mode == NORMAL_MODE)
                temp = new SwitchFrg(FrgEvent.TAG, FrgProfile.TAG, b);
            else if (mode == PASSED_EVENT_MODE)
                temp = new SwitchFrg(FrgPassedEvent.TAG, FrgProfile.TAG, b);
            else
                temp = null;
            EventBus.getDefault().post(temp);
        }

        public EventItemUserBinding getBinding() {
            return binding;
        }

    }

    public class PassedEventViewHolder extends RecyclerView.ViewHolder{

        private PassedEventItemBinding passedBinding;

        public PassedEventViewHolder(View itemView) {
            super(itemView);
            passedBinding = DataBindingUtil.bind(itemView);
        }

        public void onClickUserItem(View view) {

            Bundle b = new Bundle();
            b.putInt("userId", passedBinding.getItem().getId());
            SwitchFrg temp;
            if (mode == NORMAL_MODE)
                temp = new SwitchFrg(FrgEvent.TAG, FrgProfile.TAG, b);
            else if (mode == PASSED_EVENT_MODE)
                temp = new SwitchFrg(FrgPassedEvent.TAG, FrgProfile.TAG, b);
            else
                temp = null;
            EventBus.getDefault().post(temp);
        }

        public void onCLickRateItem(View view) {
            Bundle b = new Bundle();
            b.putInt("userId", passedBinding.getItem().getId());
            b.putString("username", passedBinding.getItem().getDisplayName());
            b.putInt("eventId", eventId);
            SwitchFrg temp = new SwitchFrg(FrgPassedEvent.TAG, FrgRating.TAG, b);
            EventBus.getDefault().post(temp);
        }

        public PassedEventItemBinding getBinding() {
            return passedBinding;
        }

    }
}
