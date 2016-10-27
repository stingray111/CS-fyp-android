package csfyp.cs_fyp_android.home;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.HomeItemEventBinding;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.model.Event;

public class AdtEvent extends RecyclerView.Adapter<AdtEvent.ViewHolder> {

    private List<Event> mEventList;
    private Fragment mParent;

    public AdtEvent() {

    }

    @Override
    public int getItemCount() {
        if (mEventList != null)
            return mEventList.size();
        else
            return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());  // TODO: 26/10/2016 change to parent
        HomeItemEventBinding binding = DataBindingUtil.inflate(inflater, R.layout.home_item_event, parent, false);
        ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getBinding().setHandlers(holder);
        holder.getBinding().setVariable(BR.item, mEventList.get(position));
        holder.getBinding().executePendingBindings();
    }

    public void setmEventList(List<Event> mEventList) {
        this.mEventList = mEventList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private HomeItemEventBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        // each data item is just a string in this case
        public void onClickEventItem(View view) {
            // // TODO: 26/10/2016 save map target 
            FragmentTransaction ft = ((AppCompatActivity)binding.getRoot().getContext()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
                    .replace(R.id.parent_fragment_container, FrgEvent.newInstance(123))
                    .addToBackStack(null)
                    .commit();
        }

        public void setBinding(HomeItemEventBinding binding) {
            this.binding = binding;
        }

        public HomeItemEventBinding getBinding() {
            return binding;
        }
    }


}
