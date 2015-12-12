package test.secken.com.seckenhavefun.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by asu on 12/9/15.
 */
public class MyPreferenceManager {

    private static final String sharePreferenceName = "SeckenHaveFun";
    private static MyPreferenceManager mInstance;
    private Context mCtx;
    private SharedPreferences sp;

    private MyPreferenceManager(Context context) {
        mCtx = context;
    }

    public synchronized static MyPreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyPreferenceManager(context);
        }
        return mInstance;
    }

    private SharedPreferences getMyPreferences() {
        if (sp == null) {
            sp = mCtx.getApplicationContext().getSharedPreferences(sharePreferenceName, Context.MODE_PRIVATE);
        }
        return sp;
    }

    public String getString(String key) {
        return getMyPreferences().getString(key, null);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = getMyPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return getMyPreferences().getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getMyPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public long getLong(String key) {
        return getMyPreferences().getLong(key, 0);
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = getMyPreferences().edit();
        editor.putLong(key, value);
        editor.commit();
    }

}
