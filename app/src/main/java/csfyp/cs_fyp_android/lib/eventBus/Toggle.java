package csfyp.cs_fyp_android.lib.eventBus;

/**
 * Created by ken on 1/4/2017.
 */

public class Toggle {
    public static final int SHOW = 0;
    public static final int HIDDEN = 1;

    private int mode;

    public Toggle(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
