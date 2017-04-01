package csfyp.cs_fyp_android.home;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.currentEvent.FrgCurrentEvent;
import csfyp.cs_fyp_android.databinding.HomeItemEventBinding;
import csfyp.cs_fyp_android.event.FrgEvent;
import csfyp.cs_fyp_android.event.FrgPassedEvent;
import csfyp.cs_fyp_android.history.FrgHistory;
import csfyp.cs_fyp_android.lib.eventBus.SwitchFrg;
import csfyp.cs_fyp_android.model.Event;

import static csfyp.cs_fyp_android.home.AdtEvent.EventComparator.decending;
import static csfyp.cs_fyp_android.home.AdtEvent.EventComparator.getComparator;

public class AdtEvent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> mEventList;
    private int mode;

    public static int HOME_MODE = 0;
    public static int ONGOING_MODE = 1;
    public static int HISTORY_MODE = 2;
    private static final String TAG = "AdtEvent";

    public AdtEvent(int mode) {
        this.mode = mode;
    }

    @Override
    public int getItemCount() {
        if (mEventList != null) {
            return mEventList.size();
        }
        else
            return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_event, parent, false);
            return new EventViewHolder(itemView);
        }else {
            // progress bar at the bottom of the list
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_event_progress,parent,false);
            return new ProgressBaeViewHolder((v));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Event e = mEventList.get(position);
        if(e.getId() >= 0){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof EventViewHolder) {
            ((EventViewHolder)holder).getBinding().setHandlers((EventViewHolder)holder);
            if (mEventList != null) {
                ((EventViewHolder)holder).getBinding().setItem(mEventList.get(position));
                ((EventViewHolder)holder).getBinding().executePendingBindings();
            }
        }
    }

    public void setmEventList(List<Event> mEventList) {
        this.mEventList = mEventList;
    }

    public void sortEventList(MenuItem item){
        switch(item.getItemId()){
            case R.id.sortDistance:
                Collections.sort(mEventList, decending(getComparator(EventComparator.DISTANCE_SORT)));
                break;
            case R.id.sortPopularity:
                break;
            case R.id.sortName:
                Collections.sort(mEventList, decending(getComparator(EventComparator.NAME_SORT)));
                break;
        }
        this.notifyDataSetChanged();
    }



    public class EventViewHolder extends RecyclerView.ViewHolder {

        private HomeItemEventBinding binding;

        public EventViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        // each data item is just a string in this case
        public void onClickEventItem(View view) {
            FragmentTransaction ft = ((AppCompatActivity)binding.getRoot().getContext()).getSupportFragmentManager().beginTransaction();
            if (mode == HOME_MODE) {
                Bundle b = new Bundle();
                b.putInt("eventId", binding.getItem().getId());
                SwitchFrg temp = new SwitchFrg(FrgHome.TAG, FrgEvent.TAG, b);
                EventBus.getDefault().post(temp);
            } else if (mode == ONGOING_MODE) {
                Bundle b = new Bundle();
                b.putInt("eventId", binding.getItem().getId());
                SwitchFrg temp = new SwitchFrg(FrgCurrentEvent.TAG, FrgEvent.TAG, b);
                temp.getBundle().putInt("eventId", binding.getItem().getId());
                EventBus.getDefault().post(temp);
//                ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
//                        .add(R.id.parent_fragment_container, FrgEvent.newInstance(binding.getItem().getId()))
//                        .hide(mFragment)
//                        .addToBackStack(null)
//                        .commit();
            } else if (mode == HISTORY_MODE) {
                Bundle b = new Bundle();
                b.putInt("eventId", binding.getItem().getId());
                SwitchFrg temp = new SwitchFrg(FrgHistory.TAG, FrgPassedEvent.TAG, b);
                temp.getBundle().putInt("eventId", binding.getItem().getId());
                EventBus.getDefault().post(temp);
//                ft.setCustomAnimations(R.anim.frg_slide_top_enter, R.anim.frg_slide_bottom_exit, R.anim.frg_slide_bottom_enter, R.anim.frg_slide_top_exit)
//                        .add(R.id.parent_fragment_container, FrgPassedEvent.newInstance(binding.getItem().getId()))
//                        .hide(mFragment)
//                        .addToBackStack(null)
//                        .commit();
            }
        }

        public HomeItemEventBinding getBinding() {
            return binding;
        }
    }

    public class ProgressBaeViewHolder extends RecyclerView.ViewHolder {

        public ProgressBaeViewHolder(View itemView) {
            super(itemView);
        }

    }

    public enum EventComparator implements Comparator<Event> {
        ID_SORT{
            public int compare(Event o1, Event o2) {
                return Integer.valueOf(o1.getId()).compareTo(o2.getId());
            }},
        NAME_SORT {
            public int compare(Event o1, Event o2) {
                return o1.getName().compareTo(o2.getName());
            }},
        DISTANCE_SORT{
            public int compare(Event o1,Event o2){
                float distance1 = o1.retrieveLocation().distanceTo(MainActivity.mCurrentLocation);
                float distance2 = o2.retrieveLocation().distanceTo(MainActivity.mCurrentLocation);
                return Float.compare(distance1,distance2);
            }};


        public static Comparator<Event> decending(final Comparator<Event> other) {
            return new Comparator<Event>() {
                public int compare(Event o1, Event o2) {
                    return -1 * other.compare(o1, o2);
                }
            };
        }

        public static Comparator<Event> getComparator(final EventComparator... multipleOptions) {
            return new Comparator<Event>() {
                public int compare(Event o1, Event o2) {
                    for (EventComparator option : multipleOptions) {
                        int result = option.compare(o1, o2);
                        if (result != 0) {
                            return result;
                        }
                    }
                    return 0;
                }
            };
        }
    }


}
