package csfyp.cs_fyp_android.lib.eventBus;

import android.os.Bundle;

public class SwitchFrg {
    private String fromTag;
    private String toTag;
    private Bundle bundle;

    public SwitchFrg(String fromTag, String toTag, Bundle bundle) {
        this.fromTag = fromTag;
        this.toTag = toTag;
        this.bundle = bundle;
    }

    public String getFromTag() {
        return fromTag;
    }

    public String getToTag() {
        return toTag;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
