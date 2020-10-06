package msmartds.in.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class L {
	public static String temp="";
	private static final String TAG = "@-Test";
	public static void m(String str){
		if(str.length() > 4000) {
			Log.i(TAG, str.substring(0, 4000));
			m(str.substring(4000));
		} else{
			Log.i(TAG, str);
		}
	}
	public static void m2(String tag, String str){
		if(str.length() > 4000) {
			Log.d(tag, str.substring(0, 4000));
			m2(tag,str.substring(4000));
		} else{
			Log.d(tag, str);
		}
	}
	public static void s(Context context, String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

}
