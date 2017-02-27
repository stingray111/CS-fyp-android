package csfyp.cs_fyp_android.lib;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;


public abstract class CustomLoader<D> extends AsyncTaskLoader<D> {

    private D mData;
    private boolean mLoading;
    private String mTaskName;       //default taskName is null


    public CustomLoader(Context ctx) {
        super(ctx);
    }

    @Override
    public void deliverResult(D data) {
        if (isReset()) {
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        D oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }

        mLoading = false;
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        mLoading = true;
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        resetTaskName();
        mLoading = false;

    }

    @Override
    public void onCanceled(D data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    private void releaseResources(D data) {
        data = null;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public void setTaskName(String taskName) {
        mTaskName = taskName;
    }

    public void resetTaskName() {
        mTaskName = null;
    }

    public boolean isTask(String taskName) {
        return taskName == null && mTaskName == null ||
                taskName != null && taskName.equals(mTaskName);
    }

    public boolean isTaskLoading(String taskName) {
        return isTask(taskName) && isLoading();
    }

    public boolean isTaskFinished(String taskName) {
        return isTask(taskName) && !isLoading();
    }


    /*********************************************************************/
    /** (4) Observer which receives notifications when the data changes **/
    /*********************************************************************/
}
