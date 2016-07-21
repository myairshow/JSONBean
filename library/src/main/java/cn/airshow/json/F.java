package cn.airshow.json;

import android.util.Log;

/**
 * Created by liuxunlan on 16/7/21.
 */
public class F {

    private final static String TAG = "JSONBean";

    public static final void out(Object obj) {
        if (obj == null) {
            Log.d(TAG, obj + "");
        } else if (obj instanceof String) {
            Log.d(TAG, (String) obj);
        } else if (obj instanceof Throwable) {
            Log.w(TAG, (Throwable) obj);
        } else {
            Log.d(TAG, obj.toString());
        }
    }

}
