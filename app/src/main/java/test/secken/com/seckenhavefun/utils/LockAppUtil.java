package test.secken.com.seckenhavefun.utils;

import android.app.Activity;
import android.util.Log;

import test.secken.com.seckenhavefun.BaseApplication;
import test.secken.com.seckenhavefun.ui.LoginActivity;
import test.secken.com.seckenhavefun.ui.UnLockAppActivity;

/**
 * Created by asu on 12/14/15.
 */
public class LockAppUtil {

    private static final String TAG = "LockAppUtilTag";
    private BaseApplication mAppContext;
    private int activityForegroundCount = 0;
    private String[] IGNORE_ACTIVITIES_NAME = {UnLockAppActivity.class.getName(), LoginActivity.class.getName()};
    // We use it ignore show unLockPage, when it's return from unLock page with auth succeed.
    private boolean isReturnFromSuccess = false;

    public LockAppUtil(BaseApplication appContext) {
        mAppContext = appContext;
    }

    public void activityOnResume(Activity activity) {
        Log.d(TAG, "activityOnResume" + activity.getClass().getName());
        activityForegroundCount++;

        if (isReturnFromSuccess) {
            isReturnFromSuccess = false;
        }
    }

    public void activityOnPause(Activity activity) {
        Log.d(TAG, "activityOnPause" + activity.getClass().getName());
        activityForegroundCount--;
    }

    public void unLockAppSuccess(Activity activity) {
        if (activity.getClass().getName().equalsIgnoreCase(UnLockAppActivity.class.getName())) {
            isReturnFromSuccess = true;
        }
    }

    public boolean isNeedToLaunchUnLockPage(Activity activity) {
        for (String ignoreActivity : IGNORE_ACTIVITIES_NAME) {
            if (activity.getClass().getName().equalsIgnoreCase(ignoreActivity)) {
                Log.d(TAG, "isNeedToLaunchUnLockPage:false");
                return false;
            }
        }
        Log.d(TAG, "isNeedToLaunchUnLockPage:" + (!isReturnFromSuccess && activityForegroundCount <= 0 &&
                AccountManager.isLockAppEnable(mAppContext)));
        return (!isReturnFromSuccess && activityForegroundCount <= 0 &&
                AccountManager.isLockAppEnable(mAppContext));
    }
}
