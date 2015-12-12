package test.secken.com.seckenhavefun.utils;

import android.content.Context;
import android.text.TextUtils;

import com.secken.sdk.SeckenSDK;

/**
 * Created by asu on 12/9/15.
 */
public class AccountManager {

    public enum AUTH_TYPE {FACE, VOICE, NONE}

    // properties
    private static final String KEY_SECKEN_TOKEN = "KeyMySeckenToken_";
    private static final String KEY_USER_NAME = "KeyMyUserName_";
    private static final String KEY_AUTH_TOKEN = "KeySeckenAuthToken_";
    private static final String KEY_FUNCTION_LOCK_APP = "KeyLockApp_";
    private static final String KEY_GET_SECKEN_TOKEN_TIME = "KeyGetSeckenTokenTime";
    private static final int SECKEN_TOKEN_VALID_DAYS = 3;

    public static boolean hasValidAccount(Context context) {
        return !TextUtils.isEmpty(getUserName(context)) &&
                !TextUtils.isEmpty(getAuthToken(context)) &&
                !TextUtils.isEmpty(getSeckenToken(context)) &&
                getAuthType(context) != AUTH_TYPE.NONE &&
                isSeckenTokenValid(context);

    }

    public static String getUserName(Context context) {
        return MyPreferenceManager.getInstance(context).getString(KEY_USER_NAME);
    }

    public static void setUserName(Context context, String userName) {
        MyPreferenceManager.getInstance(context).setString(KEY_USER_NAME, userName);
    }

    public static String getSeckenToken(Context context) {
        return MyPreferenceManager.getInstance(context).getString(KEY_SECKEN_TOKEN);
    }

    public static void setSeckenToken(Context context, String seckenToken) {
        MyPreferenceManager manager = MyPreferenceManager.getInstance(context);
        manager.setString(KEY_SECKEN_TOKEN, seckenToken);
        // we use this property to check whether seckenToken is valid or not.
        long getSeckenTokenTime = System.currentTimeMillis();
        manager.setLong(KEY_GET_SECKEN_TOKEN_TIME, getSeckenTokenTime);
    }

    public static AUTH_TYPE getAuthType(Context context) {

        if (SeckenSDK.hasFace(context)) {
            return AUTH_TYPE.FACE;
        } else if (SeckenSDK.hasVoice(context)) {
            return AUTH_TYPE.VOICE;
        } else {
            return AUTH_TYPE.NONE;
        }
    }

    public static void setAuthToken(Context context, String authToken) {
        MyPreferenceManager.getInstance(context).setString(KEY_AUTH_TOKEN, authToken);
    }

    public static String getAuthToken(Context context) {
        return MyPreferenceManager.getInstance(context).getString(KEY_AUTH_TOKEN);
    }

    public static void setLockAppEnabled(Context context, boolean enabled) {
        MyPreferenceManager.getInstance(context).setBoolean(KEY_FUNCTION_LOCK_APP, enabled);
    }

    public static boolean isLockAppEnable(Context context) {
        return MyPreferenceManager.getInstance(context).getBoolean(KEY_FUNCTION_LOCK_APP);
    }

    private static boolean isSeckenTokenValid(Context context) {
        long getTokenTime = MyPreferenceManager.getInstance(context).getLong(KEY_GET_SECKEN_TOKEN_TIME);
        return ((System.currentTimeMillis() - getTokenTime) <= SECKEN_TOKEN_VALID_DAYS * 60 * 60 * 24 * 1000);
    }

    public static void logout(Context context) {
        MyPreferenceManager manager = MyPreferenceManager.getInstance(context);
        manager.setString(KEY_AUTH_TOKEN, null);
        manager.setString(KEY_USER_NAME, null);
        manager.setString(KEY_SECKEN_TOKEN, null);
        manager.setBoolean(KEY_FUNCTION_LOCK_APP, false);
        manager.setLong(KEY_GET_SECKEN_TOKEN_TIME, 0);
    }
}
