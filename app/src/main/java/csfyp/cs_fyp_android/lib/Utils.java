package csfyp.cs_fyp_android.lib;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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


	public static String intentToString(Intent intent) {
		if (intent == null) {return null;}
		String out = intent.toString();
		Bundle extras = intent.getExtras();
		/*
		if (extras != null) {
			extras.size();
			out += "\n" + printBundle(extras);
		}
		*/
		if (intent.getAction() != null)     out+="\nAction = " + intent.getAction();
		if (intent.getType() != null)       out+="\nType = " + intent.getType();
		if (intent.getData() != null)       out+="\nData = " + intent.getData();
		if (intent.getPackage() != null)    out+="\nPackage = " + intent.getPackage();
		if (intent.getDataString() != null) out+="\nDataString = " + intent.getDataString();
		return out;
	}

}
