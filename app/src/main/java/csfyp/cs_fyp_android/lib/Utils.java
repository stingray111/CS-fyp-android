package csfyp.cs_fyp_android.lib;


import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Utils {
	public static String LogTag = "draw overlay";

	public static boolean canDrawOverlays(Context context){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}else{
			return Settings.canDrawOverlays(context);
		}


	}
	public static int dpToPx(Context context,float dpValue) {
		final float scale =context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
