package csfyp.cs_fyp_android;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class CustomScrollView extends ScrollView {

    List<View> mInterceptScrollViews = new ArrayList<View>();

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addInterceptScrollView(View view) {
        mInterceptScrollViews.add(view);
    }

    public void removeInterceptScrollView(View view) {
        mInterceptScrollViews.remove(view);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mInterceptScrollViews.size() > 0) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            Rect bounds = new Rect();

            for (View view : mInterceptScrollViews) {
                view.getHitRect(bounds);
                if (bounds.contains(x, y)) {
                    //were touching a view that should intercept scrolling
                    return false;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        //Log.i("CustomScrollView", "onTouchEvent. action: " + ev.getAction() );
        return true;
    }
}
