package test.secken.com.seckenhavefun.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.secken.sdk.SeckenCode;
import com.secken.sdk.SeckenSDK;
import com.secken.sdk.entity.AuthInfo;
import com.secken.sdk.entity.ErrorInfo;
import com.secken.sdk.toolbox.RequestListener;
import com.secken.sdk.ui.SeckenUISDK;
import com.secken.sdk.util.ToastUtils;

import app.secken.com.lib.FaceTrainActivity;
import app.secken.com.lib.FaceVertifyActivity;
import app.secken.com.lib.VoiceTrainActivity;
import app.secken.com.lib.VoiceVertifyActivity;
import test.secken.com.seckenhavefun.R;
import test.secken.com.seckenhavefun.utils.AccountManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etUserName;
    ProgressBar mProgressBar;
    Button btnLoginWithVoice;
    Button btnLoginWithFace;

    enum LOGIN_TYPE {VOICE, FACE}

    LOGIN_TYPE loginType;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {
                case SeckenCode.FACE_TRAIN_SUCCESS://训练人脸成功
                    ToastUtils.showToast(LoginActivity.this, "Face Training Success");
                    bundle = new Bundle();
                    bundle.putString("username", AccountManager.getUserName(LoginActivity.this));
                    bundle.putString("token", AccountManager.getSeckenToken(LoginActivity.this));
                    openActivity(FaceVertifyActivity.class, bundle);
                    break;

                case SeckenCode.FACE_TRAIN_FAIL://训练人脸失败
                    ToastUtils.showToast(LoginActivity.this, "Face Training Fail");
                    break;

                case SeckenCode.VOICE_TRAIN_SUCCESS://训练声音成功
                    ToastUtils.showToast(LoginActivity.this, "Voice Training Success");
                    bundle = new Bundle();
                    bundle.putString("username", AccountManager.getUserName(LoginActivity.this));
                    bundle.putString("token", AccountManager.getSeckenToken(LoginActivity.this));
                    openActivity(VoiceVertifyActivity.class, bundle);
                    break;
                case SeckenCode.VOICE_TRAIN_FAIL://训练声音失败
                    ToastUtils.showToast(LoginActivity.this, "Voice Training Fail");
                    break;
                case SeckenCode.VERTIFY_SUCCESS://验证成功（包括所有验证方式验证的结果）
                    bundle = msg.getData();
                    /**
                     * auth_token: 集成SeckenSDK的app可将该字段提交至自己服务器，由自己服务器和洋葱服务器进行二次确认，
                     * 以确保洋葱SDK返回的验证结果未被篡改。
                     * (跟洋葱服务器进行二次确认接口：https://api.sdk.yangcong.com/query_auth_token)
                     */
                    String authToken = bundle.getString("auth_token");
                    AccountManager.setAuthToken(LoginActivity.this, authToken);
                    ToastUtils.showToast(LoginActivity.this, "Verify Success AuthToken:" + authToken);

                    Intent mainPageIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    LoginActivity.this.startActivity(mainPageIntent);
                    break;
            }
        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        initView();
        SeckenUISDK.setHandler(mHandler);
    }

    private void initView() {
        etUserName = (EditText) findViewById(R.id.etUserName);
        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);

        btnLoginWithFace = (Button) findViewById(R.id.btnLoginWithFace);
        btnLoginWithFace.setOnClickListener(this);
        btnLoginWithVoice = (Button) findViewById(R.id.btnLoginWithVoice);
        btnLoginWithVoice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String userName = etUserName.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Snackbar.make(v, "Empty userName", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null)
                    .show();
            return;
        }

        switch (v.getId()) {
            case R.id.btnLoginWithFace:
                loginType = LOGIN_TYPE.FACE;
                break;
            case R.id.btnLoginWithVoice:
                loginType = LOGIN_TYPE.VOICE;
                break;
        }

        getSeckenToken(userName);
    }

    private void getSeckenToken(final String userName) {
        showProgressBar();

        AuthInfo authInfo = new AuthInfo(this, userName);
        SeckenSDK.authorize(this, authInfo, new RequestListener() {
            @Override
            public void onSuccess(Bundle data) {
                hideProgressBar();
                if (data != null) {
                    String seckenToken = data.getString("token");

                    if (!TextUtils.isEmpty(seckenToken)) {
                        // Auth success.
                        ToastUtils.showToast(LoginActivity.this, "Secken auth success !!!");
                        getSeckenTokenSuccess(userName, seckenToken);
                    } else {
                        ToastUtils.showToast(LoginActivity.this, "Secken auth success but with empty secken token!");
                    }

                } else {
                    ToastUtils.showToast(LoginActivity.this, "Secken auth success but with empty data info!");
                }
            }

            @Override
            public void onFailed(ErrorInfo errorInfo) {
                hideProgressBar();
                if (errorInfo != null) {
                    ToastUtils.showToast(LoginActivity.this, errorInfo.errorMsg);
                }
            }
        });
    }

    private void getSeckenTokenSuccess(String userName, String seckenToken) {
        // save user info to sharePreferences.
        AccountManager.setUserName(this, userName);
        AccountManager.setSeckenToken(this, seckenToken);

        Bundle bundle = new Bundle();
        bundle.putString("username", userName);
        bundle.putString("token", seckenToken);

        if (loginType == LOGIN_TYPE.VOICE) {
            if (SeckenSDK.hasVoice(this)) {
                // VoiceVerify
                ToastUtils.showToast(this, "Voice Verify");
                openActivity(VoiceVertifyActivity.class, bundle);
            } else {
                // VoiceTraining
                ToastUtils.showToast(this, "Voice Training");
                openActivity(VoiceTrainActivity.class, bundle);
            }
        } else {
            if (SeckenSDK.hasFace(this)) {
                // FaceVerify
                ToastUtils.showToast(this, "Face Verify");
                openActivity(FaceVertifyActivity.class, bundle);
            } else {
                // FaceTraining
                ToastUtils.showToast(this, " Face Training");
                openActivity(FaceTrainActivity.class, bundle);
            }
        }
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        etUserName.setEnabled(false);
        btnLoginWithVoice.setEnabled(false);
        btnLoginWithFace.setEnabled(false);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        etUserName.setEnabled(true);
        btnLoginWithVoice.setEnabled(true);
        btnLoginWithFace.setEnabled(true);
    }

    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        SeckenUISDK.setHandler(null);
        super.onDestroy();
    }
}
