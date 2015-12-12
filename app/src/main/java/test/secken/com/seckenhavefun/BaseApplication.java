package test.secken.com.seckenhavefun;

import android.app.Application;

import com.secken.sdk.SeckenSDK;

/**
 * Created by asu on 12/9/15.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SeckenSDK.init(this, BuildConfig.SECKEN_SDK_APP_KEY, BuildConfig.SECKEN_SDK_APP_ID);
    }
}
