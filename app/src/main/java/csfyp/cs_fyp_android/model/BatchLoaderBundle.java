package csfyp.cs_fyp_android.model;

import java.util.List;

/**
 * Created by ray on 14/3/2017.
 */

public class BatchLoaderBundle {
    public List<Event> list;
    private int status;
    public static final int LIST_NOT_END = 0;
    public static final int LIST_END = 1;

    public BatchLoaderBundle(List<Event> list,int status){
        this.list = list;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
