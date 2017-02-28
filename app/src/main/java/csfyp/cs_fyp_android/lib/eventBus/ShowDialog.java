package csfyp.cs_fyp_android.lib.eventBus;

import csfyp.cs_fyp_android.lib.NoticeDialogFragment;

public class ShowDialog {
    private NoticeDialogFragment mDialog;

    public ShowDialog(NoticeDialogFragment mDialog) {
        this.mDialog = mDialog;
    }

    public NoticeDialogFragment getDialog() {
        return mDialog;
    }
}
