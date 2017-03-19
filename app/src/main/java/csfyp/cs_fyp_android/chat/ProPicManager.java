package csfyp.cs_fyp_android.chat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.util.Pair;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.model.BitmapAndBtn;
import csfyp.cs_fyp_android.model.Event;
import csfyp.cs_fyp_android.model.respond.ThirdPartySignInRespond;

/**
 * Created by ray on 19/3/2017.
 */

public class ProPicManager {
    private static final String TAG = "Pro Pic Man";
    private Bitmap defaultBitmap;


    private List<Pair<String,OwnTarget>> targets = new ArrayList<Pair<String,OwnTarget>>();

    public void setBtn(String url, Context context, FloatingActionButton btn) throws InterruptedException {
        if(url == null || url.equals("")){
            if(defaultBitmap == null ) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_propic_big);
                defaultBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
            }
            EventBus.getDefault().post(new BitmapAndBtn(defaultBitmap,btn));
            return;
        }
        for(Pair<String,OwnTarget> item: targets){
            if(item.first.equals(url) ){
                new Thread(new OwnThread(item.second,btn)).start();
                return;
            }
        }
        OwnTarget target = new OwnTarget(btn,context);
        targets.add(new Pair<String, OwnTarget>(url,target));

        Picasso.with(context)
                .load(url)
                .resize(120,120)
                .centerCrop()
                .placeholder(R.drawable.ic_propic_big)
                .into(targets.get(targets.size()-1).second);
    }

    private class OwnThread implements Runnable{
        public OwnTarget target;
        public FloatingActionButton btn;
        public OwnThread(OwnTarget target,FloatingActionButton btn){
            this.target = target;
            this.btn = btn;
        }
        @Override
        public void run() {
            while(target.getDone() == false){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            EventBus.getDefault().post(new BitmapAndBtn(target.getBitmap(),btn));
        }
    }

    private class OwnTarget implements Target{
        private Bitmap bitmap = null;
        private FloatingActionButton btn;
        private Context context;
        private volatile boolean done = false;

        public Bitmap getBitmap() {
            return bitmap;
        }


        public OwnTarget(FloatingActionButton btn,Context context){
            this.btn = btn;
            this.context = context;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Drawable drawable = new BitmapDrawable(getBaseContext().getResources(),bitmap);
            this.bitmap = bitmap;
            EventBus.getDefault().post(new BitmapAndBtn(bitmap,btn));
            done = true;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            done = true;
            Log.d(TAG,"fail");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }

        public boolean getDone(){
            return done;
        }
    }

}
