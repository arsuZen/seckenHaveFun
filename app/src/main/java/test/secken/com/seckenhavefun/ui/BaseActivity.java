package test.secken.com.seckenhavefun.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.secken.sdk.SeckenSDK;
import com.secken.sdk.entity.AppBaseInfo;
import com.secken.sdk.entity.ErrorInfo;
import com.secken.sdk.toolbox.RequestListener;
import com.secken.sdk.util.ToastUtils;

import test.secken.com.seckenhavefun.BaseApplication;
import test.secken.com.seckenhavefun.utils.AccountManager;

/**
 * Created by asu on 12/9/15.
 */
public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        if (!AccountManager.hasValidAccount(this)) {
            // clear user local info.
            AccountManager.logout(this);
            launchLoginPage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final boolean isNeedUnlockPage = ((BaseApplication) getApplication()).mLockAppUtil.isNeedToLaunchUnLockPage(this);
        ((BaseApplication) getApplication()).mLockAppUtil.activityOnResume(this);

        if (isNeedUnlockPage) {
            Intent intent = new Intent(this, UnLockAppActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((BaseApplication) getApplication()).mLockAppUtil.activityOnPause(this);
    }

    protected void logout() {
        mProgressDialog.show();
        AppBaseInfo appBaseInfo = new AppBaseInfo(AccountManager.getSeckenToken(this), AccountManager.getUserName(this));
        SeckenSDK.logout(this, appBaseInfo, new RequestListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                mProgressDialog.hide();
                ToastUtils.showToast(BaseActivity.this, "Secken logout success");
                AccountManager.logout(BaseActivity.this);
                launchLoginPage();
            }

            @Override
            public void onFailed(ErrorInfo errorInfo) {
                mProgressDialog.hide();
                ToastUtils.showToast(BaseActivity.this, "Secken logout failed");
                AccountManager.logout(BaseActivity.this);
                launchLoginPage();
            }
        });
    }

    protected void launchLoginPage() {
        // Invalid user, launch login page.
        Intent intent = new Intent(this, LoginActivity.class);
        // Clear activity stack.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }
}
