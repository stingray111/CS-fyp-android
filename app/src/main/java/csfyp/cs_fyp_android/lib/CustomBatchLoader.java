package csfyp.cs_fyp_android.lib;

import android.content.Context;

public abstract class CustomBatchLoader<D> extends CustomLoader<D> {
    public static final String TASK_FRESH_LOAD = "freshLoad";
    public static final String TASK_REFRESH_LOAD = "refreshLoad";
    public static final String TASK_LOAD_MORE = "loadMore";

    public static final int BATCH_SIZE = 20;
    public static final int FIRST_BATCH_SIZE = 50;
    private int nextBatchNo;

    public CustomBatchLoader(Context context) {
        super(context);
        setTaskName(TASK_FRESH_LOAD);
        nextBatchNo = 0;
    }

    public int getNextBatchNo() {
        return nextBatchNo;
    }

    public void startFreshLoad() {
        reset();
        setTaskName(TASK_FRESH_LOAD);
        startLoading();
    }

    public void startRefreshLoad() {
        reset();
        setTaskName(TASK_REFRESH_LOAD);
        startLoading();
    }

    public void startLoadMore() {
        reset();
        setTaskName(TASK_LOAD_MORE);
        startLoading();
    }

    @Override
    public void resetTaskName() {
        setTaskName(TASK_FRESH_LOAD);
    }

    @Override
    public D loadInBackground() {
        if (TASK_FRESH_LOAD.equals(getTaskName())) {
            return freshLoad();
        } else if (TASK_REFRESH_LOAD.equals(getTaskName())) {
            return refreshLoad();
        } else if (TASK_LOAD_MORE.equals(getTaskName())) {
            return loadMore();
        } else
            return loadOther();
    }

    public D loadOther() {
        return null;
    }

    public abstract D loadMore();

    public abstract D refreshLoad();

    public abstract D freshLoad();

}

