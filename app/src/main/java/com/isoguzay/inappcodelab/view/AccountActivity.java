package com.isoguzay.inappcodelab.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.isoguzay.inappcodelab.R;
import com.isoguzay.inappcodelab.hms.account.Constant;
import com.isoguzay.inappcodelab.hms.account.ICallBack;
import com.isoguzay.inappcodelab.hms.account.IDTokenParser;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    * This activity have 3 Hms kits, they are Account, Push and Ads Kits.
    *
    * For Account Kit, get access token from Account Kit
    *
    * For Push Kit, get ID from Push server, client side push to device.
    *
    * For Ads Kit, Banner Ads Example
    *
    * */

    public static final String TAG = "HuaweiIdActivity";
    private static final String TAG_PUSH = "PushDemoLog";

    private HuaweiIdAuthService mAuthManager;
    private HuaweiIdAuthParams mAuthParam;
    HiAnalyticsInstance instance;
    private String pushToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findViewById(R.id.button_signIn).setOnClickListener(this);
        findViewById(R.id.button_signIn_code).setOnClickListener(this);
        findViewById(R.id.button_signOut).setOnClickListener(this);
        findViewById(R.id.button_push).setOnClickListener(this);

        // HMS ADS KIT
        HwAds.init(this);

        // Obtain BannerView based on the configuration in layout/ad_fragment.xml.
        BannerView bottomBannerView = findViewById(R.id.hw_banner_view);
        AdParam adParam = new AdParam.Builder().build();
        bottomBannerView.loadAd(adParam);

        BannerView bannerView = new BannerView(this);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_360_57);
        bannerView.loadAd(adParam);

        // Add BannerView to the layout.
        LinearLayout rootView = findViewById(R.id.root_view);
        rootView.addView(bannerView);

        // HMS Account Kit
        mAuthParam = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams();
        mAuthManager = HuaweiIdAuthManager.getService(AccountActivity.this, mAuthParam);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // After the HMS service is unbound, no automatic event (account event or InAppPurchase event, etc.) is reported.
        instance.unRegHmsSvcEvent();
    }

    private void signIn() {
        startActivityForResult(mAuthManager.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN);
    }

    private void signInCode() {
        mAuthParam = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setProfile()
                .setAuthorizationCode()
                .createParams();
        mAuthManager = HuaweiIdAuthManager.getService(AccountActivity.this, mAuthParam);
        startActivityForResult(mAuthManager.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN_CODE);
    }

    private void signOut() {
        Task<Void> signOutTask = mAuthManager.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "signOut Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
            }
        });
    }

    private void validateIdToken(String idToken) {
        if (TextUtils.isEmpty(idToken)) {
            Log.i(TAG, "ID Token is empty");
        } else {
            IDTokenParser idTokenParser = new IDTokenParser();
            try {
                idTokenParser.verify(idToken, new ICallBack() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onSuccess(String idTokenJsonStr) {
                        if (!TextUtils.isEmpty(idTokenJsonStr)) {
                            Log.i(TAG, "id Token Validate Success, verify signature: " + idTokenJsonStr);
                        } else {
                            Log.i(TAG, "Id token validate failed.");
                        }
                    }

                    @Override
                    public void onFailed() {
                        Log.i(TAG, "Id token validate failed.");
                    }
                });
            } catch (Exception e) {
                Log.i(TAG, "id Token validate failed." + e.getClass().getSimpleName());
            } catch (Error e) {
                Log.i(TAG, "id Token validate failed." + e.getClass().getSimpleName());
                if (Build.VERSION.SDK_INT < 23) {
                    Log.i(TAG, "android SDK Version is not support. Current version is: " + Build.VERSION.SDK_INT);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signIn:
                signIn();
                break;
            case R.id.button_signIn_code:
                signInCode();
                break;
            case R.id.button_signOut:
                signOut();
                break;
            case R.id.button_push:
                getPushToken();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                Log.i(TAG, huaweiAccount.getDisplayName() + " signIn success ");
                Log.i(TAG,"AccessToken: " + huaweiAccount.getAccessToken());
                validateIdToken(huaweiAccount.getIdToken());
            } else {
                Log.i(TAG, "signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }

        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN_CODE) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                Log.i(TAG, "signIn get code success.");
                Log.i(TAG, huaweiAccount.getDisplayName() + " getDisplayName success ");
                Log.i(TAG, huaweiAccount.getEmail() + " getEmail success ");
                Log.i(TAG, huaweiAccount.getDisplayName() + " getDisplayName success ");
                Log.i(TAG, huaweiAccount.getUid() + " getUid success ");
                Log.i(TAG, huaweiAccount.getHuaweiAccount() + " getHuaweiAccount success ");
                Log.i(TAG, huaweiAccount.getGivenName() + " getGivenName success ");
                Log.i(TAG,"ServerAuthCode: " + huaweiAccount.getAuthorizationCode());
            } else {
                Log.i(TAG, "signIn get code failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    private void getPushToken() {
        Log.i(TAG, "get token: begin");
        // get token
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(AccountActivity.this).getString("client/app_id");
                    pushToken = HmsInstanceId.getInstance(AccountActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(pushToken)) {
                        Log.i(TAG_PUSH, "get token:" + pushToken);
                        showPushLog(pushToken);
                    }
                } catch (Exception e) {
                    Log.i(TAG_PUSH,"getToken failed, " + e);

                }
            }
        }.start();
    }

    private void showPushLog(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View tvView = findViewById(R.id.tv_log);
                if (tvView instanceof TextView) {
                    ((TextView) tvView).setText(log);
                    Toast.makeText(AccountActivity.this, pushToken, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
