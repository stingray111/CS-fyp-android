package csfyp.cs_fyp_android.lib.eventBus;

/**
 * Created by ray on 1/3/2017.
 */

public class ScrollEvent {
    public static final int FIRST = 0 ;
    public static final int OTHERS = 1;
    int mode;
    public ScrollEvent(int mode){
        this.mode = mode;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
