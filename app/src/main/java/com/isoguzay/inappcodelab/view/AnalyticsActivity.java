package com.isoguzay.inappcodelab.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.isoguzay.inappcodelab.R;

import static com.huawei.hms.analytics.type.HAEventType.RATE;
import static com.huawei.hms.analytics.type.HAParamType.COMMENTTYPE;

public class AnalyticsActivity extends AppCompatActivity implements View.OnClickListener {

    HiAnalyticsInstance instance;
    final static String CUSTOM_EVENT_FEEDBACK = "CustomEventFeedback";
    final static String CUSTOM_EVENT_PARAM_RESULT = "CustomEventParamResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        findViewById(R.id.button_feedback_yes).setOnClickListener(this);
        findViewById(R.id.button_feedback_no).setOnClickListener(this);
        findViewById(R.id.button_return).setOnClickListener(this);

        // Enable Analytics Kit Log
        HiAnalyticsTools.enableLog();
        // Generate the Analytics Instance
        instance = HiAnalytics.getInstance(getApplicationContext());
        // Enable collection capability
        instance.setAnalyticsEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_feedback_yes:
                reportCustomEvent("yes");
                reportRateEvent("yes");
                break;
            case R.id.button_feedback_no:
                reportCustomEvent("no");
                reportRateEvent("no");
                break;
            case R.id.button_return:
                Intent back = new Intent(this, MainActivity.class);
                startActivity(back);
                break;
            default:
                break;
        }
    }

    private void reportCustomEvent(String feedback) {
        // TODO: Report a customized Event
        // Event Name: CUSTOM_EVENT_FEEDBACK
        // Event Parameter:
        //  -- CUSTOM_EVENT_PARAM_RESULT: String

        Bundle bundle = new Bundle();
        bundle.putString(CUSTOM_EVENT_PARAM_RESULT, feedback);
        instance.onEvent(CUSTOM_EVENT_FEEDBACK, bundle);

        Toast.makeText(getApplicationContext(), "Custom Event Name: " + CUSTOM_EVENT_FEEDBACK + " " + bundle.getString(CUSTOM_EVENT_PARAM_RESULT), Toast.LENGTH_SHORT).show();
    }

    private void reportRateEvent(String feedback) {

        Bundle bundle = new Bundle();
        bundle.putString(COMMENTTYPE, feedback);
        instance.onEvent(RATE, bundle);

        Toast.makeText(getApplicationContext(), "PreDefine Event Name: " + RATE + " " + bundle.getString(COMMENTTYPE), Toast.LENGTH_SHORT).show();

    }
}
