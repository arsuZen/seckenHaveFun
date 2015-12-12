package test.secken.com.seckenhavefun.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.secken.sdk.SeckenSDK;
import com.secken.sdk.entity.ErrorInfo;
import com.secken.sdk.entity.FaceInfo;
import com.secken.sdk.entity.VoiceInfo;
import com.secken.sdk.toolbox.RequestListener;
import com.secken.sdk.util.ToastUtils;

import app.secken.com.lib.FaceTrainActivity;
import app.secken.com.lib.VoiceTrainActivity;
import test.secken.com.seckenhavefun.R;
import test.secken.com.seckenhavefun.utils.AccountManager;

public class MainActivity extends BaseActivity {

    private TextView tvUserName;
    private TextView tvVoiceVerify;
    private TextView tvFaceVerify;
    private SwitchCompat switchLockApp;
    private ImageButton btnDeleteVoiceVerify;
    private ImageButton btnDeleteFaceVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        tvUserName = (TextView) findViewById(R.id.tvUsername);
        tvVoiceVerify = (TextView) findViewById(R.id.tvVoiceVerify);
        tvFaceVerify = (TextView) findViewById(R.id.tvFaceVerify);

        btnDeleteFaceVerify = (ImageButton) findViewById(R.id.imgBtnDeleteFaceVerify);
        btnDeleteVoiceVerify = (ImageButton) findViewById(R.id.imgBtnDeleteVoiceVerify);

        switchLockApp = (SwitchCompat) findViewById(R.id.switchLockApp);
        switchLockApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Snackbar.make(buttonView, "U will need to verify account, when u use this app every time!", Snackbar.LENGTH_LONG)
                            .show();
                }
                AccountManager.setLockAppEnabled(MainActivity.this, isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindData2View();
    }

    // Update views from SeckenSDK's data info.
    private void bindData2View() {
        tvUserName.setText(AccountManager.getUserName(this));
        tvVoiceVerify.setText(SeckenSDK.hasVoice(this) ? "enable" : "disable");
        tvFaceVerify.setText(SeckenSDK.hasFace(this) ? "enable" : "disable");
        switchLockApp.setChecked(AccountManager.isLockAppEnable(this));
        btnDeleteVoiceVerify.setVisibility(SeckenSDK.hasVoice(this) ? View.VISIBLE : View.GONE);
        btnDeleteFaceVerify.setVisibility(SeckenSDK.hasFace(this) ? View.VISIBLE : View.GONE);
    }


    public void onBtnVoiceTrainingClicked(View view) {
        if (view.getId() == R.id.imgBtnVoiceTraining) {
            Bundle bundle = new Bundle();
            bundle.putString("token", AccountManager.getSeckenToken(this));
            bundle.putString("username", AccountManager.getUserName(this));
            openActivity(VoiceTrainActivity.class, bundle);
        }
    }

    public void onBtnDeleteVoiceVerifyClicked(View view) {
        if (view.getId() == R.id.imgBtnDeleteVoiceVerify) {
            Snackbar.make(view, "Wanna delete current VOICE verification?", Snackbar.LENGTH_LONG)
                    .setAction("DELETE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteVoiceVerification();
                        }
                    })
                    .show();
        }
    }

    private void deleteVoiceVerification() {
        mProgressDialog.show();

        VoiceInfo voiceInfo = new VoiceInfo(AccountManager.getSeckenToken(this), AccountManager.getUserName(this));
        SeckenSDK.voiceDelete(this, voiceInfo, new RequestListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                mProgressDialog.hide();
                ToastUtils.showToast(MainActivity.this, "Delete voice verification success");
                bindData2View();
            }

            @Override
            public void onFailed(ErrorInfo errorInfo) {
                mProgressDialog.hide();
                ToastUtils.showToast(MainActivity.this, "Delete voice verification failed");
            }
        });
    }

    public void onBtnFaceTrainingClicked(View view) {
        if (view.getId() == R.id.imgBtnFaceTraining) {
            Bundle bundle = new Bundle();
            bundle.putString("token", AccountManager.getSeckenToken(this));
            bundle.putString("username", AccountManager.getUserName(this));
            openActivity(FaceTrainActivity.class, bundle);
        }
    }

    public void onBtnDeleteFaceVerifyClicked(View view) {
        if (view.getId() == R.id.imgBtnDeleteFaceVerify) {
            Snackbar.make(view, "Wanna delete current FACE verification?", Snackbar.LENGTH_LONG)
                    .setAction("DELETE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteFaceVerification();
                        }
                    })
                    .show();
        }
    }

    private void deleteFaceVerification() {
        mProgressDialog.show();
        FaceInfo faceInfo = new FaceInfo(AccountManager.getSeckenToken(this), AccountManager.getUserName(this));
        SeckenSDK.faceDelete(this, faceInfo, new RequestListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                mProgressDialog.hide();
                ToastUtils.showToast(MainActivity.this, "Delete face verification success");
                bindData2View();
            }

            @Override
            public void onFailed(ErrorInfo errorInfo) {
                mProgressDialog.hide();
                String strError = "Delete face verification error " + (errorInfo != null ? errorInfo.errorMsg : "");
                ToastUtils.showToast(MainActivity.this, strError);
            }
        });
    }


}
