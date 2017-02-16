package csfyp.cs_fyp_android.lib;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import csfyp.cs_fyp_android.R;

public class NoticeDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private NoticeDialogListener mListener;

    public NoticeDialogFragment() {}

    private int mTitleId;
    private int mMessageId;

    public static NoticeDialogFragment newInstance(int titleId, int messageId) {

        Bundle args = new Bundle();

        args.putInt("titleId", titleId);
        args.putInt("messageId", messageId);

        NoticeDialogFragment fragment = new NoticeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setDialogListener(NoticeDialogListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mTitleId = getArguments().getInt("titleId");
        mMessageId = getArguments().getInt("messageId");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mTitleId)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(NoticeDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
