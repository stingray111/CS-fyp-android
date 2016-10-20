package csfyp.cs_fyp_android.home;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.event.FrgEvent;

public class AdtHome extends RecyclerView.Adapter<AdtHome.ViewHolder> {

    private Activity act;

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_item_event, parent, false);
        ViewHolder vh = new ViewHolder(v,((FragmentActivity) parent.getContext()).getSupportFragmentManager());
        act = (Activity) parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FragmentManager manager;
        public ViewHolder(View itemView, FragmentManager manager) {
            super(itemView);
            this.manager = manager;
        }
        // each data item is just a string in this case
        public void onClickEventItem(View view) {
            // TODO: 20/10/2016 put switch fragment to
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
                    .replace(R.id.parent_fragment_container, FrgEvent.newInstance())
                    .addToBackStack(null)
                    .commit();

        }
    }


}
