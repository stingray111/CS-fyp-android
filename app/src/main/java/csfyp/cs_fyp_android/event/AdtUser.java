package csfyp.cs_fyp_android.event;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.EventItemUserBinding;
import csfyp.cs_fyp_android.databinding.PassedEventItemBinding;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.profile.FrgProfile;
import csfyp.cs_fyp_android.rating.FrgRating;


public class AdtUser extends RecyclerView.Adapter<AdtUser.ViewHolder>{

    private List<User> mUserList;
    private Fragment mFragment;

    public AdtUser(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public int getItemCount() {
        if (mUserList != null)
            return mUserList.size();
        else
            return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        EventItemUserBinding binding;
        PassedEventItemBinding passedBinding;
        ViewHolder holder;
        if (mFragment instanceof FrgEvent) {
            binding = DataBindingUtil.inflate(inflater, R.layout.event_item_user, parent, false);
            holder = new ViewHolder(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        }
        else if (mFragment instanceof FrgPassedEvent) {
            passedBinding = DataBindingUtil.inflate(inflater, R.layout.passed_event_item, parent, false);
            holder = new ViewHolder(passedBinding.getRoot());
            holder.setPassedBinding(passedBinding);
            return holder;
        } else {
            binding = DataBindingUtil.inflate(inflater, R.layout.event_item_user, parent, false);
            holder = new ViewHolder(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mFragment instanceof FrgPassedEvent) {
            holder.getPassedBinding().setHandlers(holder);

            if (mUserList != null) {

                holder.getPassedBinding().setItem(mUserList.get(position));

                // is rated by other?
                if (mUserList.get(position).isRatedbyOther()) {
                    holder.getPassedBinding().rateBtn.setVisibility(View.GONE);
                    holder.getPassedBinding().ratedImg.setVisibility(View.VISIBLE);
                } else {
                    holder.getPassedBinding().rateBtn.setVisibility(View.VISIBLE);
                    holder.getPassedBinding().ratedImg.setVisibility(View.GONE);
                }

                // is self?
                if (mUserList.get(position).getId() == ((FrgPassedEvent)mFragment).getmSelfUserId())
                    holder.getPassedBinding().rateBtn.setVisibility(View.GONE);
                // is attended?
                if (!mUserList.get(position).isAttended()) {
                    Log.i("hey", "not attended" + mUserList.get(position).getUserName());
                    holder.getPassedBinding().rateBtn.setVisibility(View.GONE);
                }

                holder.getPassedBinding().executePendingBindings();
            }
        } else {
            holder.getBinding().setHandlers(holder);
            if (mUserList != null) {
                holder.getBinding().setItem(mUserList.get(position));
                holder.getBinding().executePendingBindings();
            }
        }
    }

    public void setmUserList(List<User> mUserList) {
        this.mUserList = mUserList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private EventItemUserBinding binding;
        private PassedEventItemBinding passedBinding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        // each data item is just a string in this case
        public void onClickUserItem(View view) {

            FragmentTransaction ft = ((AppCompatActivity) binding.getRoot().getContext()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
                    .add(R.id.parent_fragment_container, FrgProfile.newInstance(binding.getItem().getId()))
                    .hide(mFragment)
                    .addToBackStack(null)
                    .commit();
        }

        public void onCLickRateItem(View view) {
            FragmentTransaction ft = ((AppCompatActivity) passedBinding.getRoot().getContext()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
                    .add(R.id.parent_fragment_container, FrgRating.newInstance(passedBinding.getItem().getId(), passedBinding.getItem().getUserName(), ((FrgPassedEvent)mFragment).getmEventId()))
                    .hide(mFragment)
                    .addToBackStack(null)
                    .commit();
        }

        public EventItemUserBinding getBinding() {
            return binding;
        }

        public PassedEventItemBinding getPassedBinding() {
            return passedBinding;
        }

        public void setBinding(EventItemUserBinding binding) {
            this.binding = binding;
        }

        public void setPassedBinding(PassedEventItemBinding passedBinding) {
            this.passedBinding = passedBinding;
        }
    }
}
