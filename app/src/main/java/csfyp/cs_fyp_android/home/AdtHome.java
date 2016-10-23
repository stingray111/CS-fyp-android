package csfyp.cs_fyp_android.home;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.HomeItemEventBinding;
import csfyp.cs_fyp_android.event.FrgEvent;

public class AdtHome extends RecyclerView.Adapter<AdtHome.ViewHolder> {

    private Activity act;
    private Fragment mFragment;

    public AdtHome(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mFragment.getContext());
        HomeItemEventBinding binding = DataBindingUtil.inflate(inflater, R.layout.home_item_event, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setHandlers(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        HomeItemEventBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        // each data item is just a string in this case
        public void onClickEventItem(View view) {
            // TODO: 20/10/2016 put switch fragment  
            FragmentTransaction ft = ((AppCompatActivity)binding.getRoot().getContext()).getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
                    .replace(R.id.parent_fragment_container, FrgEvent.newInstance())
                    .addToBackStack(null)
                    .commit();

        }
    }


}
