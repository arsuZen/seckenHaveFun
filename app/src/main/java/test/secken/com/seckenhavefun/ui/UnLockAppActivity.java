package test.secken.com.seckenhavefun.ui;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.secken.sdk.SeckenCode;
import com.secken.sdk.SeckenSDK;
import com.secken.sdk.toolbox.SeckenHandler;
import com.secken.sdk.ui.SeckenUISDK;
import com.secken.sdk.util.ToastUtils;

import app.secken.com.lib.FaceVertifyActivity;
import app.secken.com.lib.VoiceVertifyActivity;
import test.secken.com.seckenhavefun.BaseApplication;
import test.secken.com.seckenhavefun.R;
import test.secken.com.seckenhavefun.utils.AccountManager;

public class UnLockAppActivity extends BaseActivity implements View.OnClickListener {

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new SeckenHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            switch (msg.what) {

                case SeckenCode.VERTIFY_SUCCESS://验证成功（包括所有验证方式验证的结果）
                    bundle = msg.getData();
                    /**
                     * auth_token: 集成SeckenSDK的app可将该字段提交至自己服务器，由自己服务器和洋葱服务器进行二次确认，
                     * 以确保洋葱SDK返回的验证结果未被篡改。
                     * (跟洋葱服务器进行二次确认接口：https://api.sdk.yangcong.com/query_auth_token)
                     */
                    String authToken = bundle.getString("auth_token");
                    AccountManager.setAuthToken(UnLockAppActivity.this, authToken);
                    ToastUtils.showToast(UnLockAppActivity.this, "Verify Success AuthToken:" + authToken);

                    accountVerifySuccess();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_lock_app);
        initView();
        SeckenUISDK.setHandler(mHandler);
    }

    private void initView() {

        if (SeckenSDK.hasFace(this)) {
            TextView tvFaceVerify = (TextView) findViewById(R.id.tvFaceVerify);
            tvFaceVerify.setVisibility(View.VISIBLE);
            tvFaceVerify.setText(getStringWithHighLight("FACE verify", 0, 4));
            tvFaceVerify.setOnClickListener(this);
        }

        if (SeckenSDK.hasVoice(this)) {
            TextView tvVoiceVerify = (TextView) findViewById(R.id.tvVoiceVerify);
            tvVoiceVerify.setVisibility(View.VISIBLE);
            tvVoiceVerify.setText(getStringWithHighLight("VOICE verify", 0, 5));
            tvVoiceVerify.setOnClickListener(this);
        }

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnLockAppActivity.this.logout();
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("username", AccountManager.getUserName(this));
        bundle.putString("token", AccountManager.getSeckenToken(this));

        switch (v.getId()) {
            case R.id.tvFaceVerify:
                openActivity(FaceVertifyActivity.class, bundle);
                break;
            case R.id.tvVoiceVerify:
                openActivity(VoiceVertifyActivity.class, bundle);
                break;
        }
    }

    private void accountVerifySuccess() {
        ((BaseApplication) getApplication()).mLockAppUtil.unLockAppSuccess(this);
        finish();
    }

    private SpannableStringBuilder getStringWithHighLight(@NonNull String context, int from, int to) {
        SpannableStringBuilder sb = new SpannableStringBuilder(context);
        ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));
        StyleSpan ss = new StyleSpan(Typeface.BOLD);
        sb.setSpan(fcs, from, to, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(ss, from, to, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return sb;
    }
}
