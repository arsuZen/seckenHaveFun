package test.secken.com.seckenhavefun;

import android.app.Application;

import com.secken.sdk.SeckenSDK;

import test.secken.com.seckenhavefun.utils.LockAppUtil;

/**
 * Created by asu on 12/9/15.
 */
public class BaseApplication extends Application {

    public LockAppUtil mLockAppUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        SeckenSDK.init(this, BuildConfig.SECKEN_SDK_APP_KEY, BuildConfig.SECKEN_SDK_APP_ID);
        mLockAppUtil = new LockAppUtil(this);
    }
}
