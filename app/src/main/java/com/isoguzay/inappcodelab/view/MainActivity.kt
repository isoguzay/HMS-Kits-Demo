package com.isoguzay.inappcodelab.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.huawei.agconnect.crash.AGConnectCrash
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AGConnectCrash.getInstance().enableCrashCollection(true)

        button_get.setOnClickListener {
            val goMainActivity = Intent(applicationContext, AccountActivity::class.java)
            startActivity(goMainActivity)
        }

        button_map.setOnClickListener {
            val goMapActivity = Intent(applicationContext, MapActivity::class.java)
            startActivity(goMapActivity)
        }

        button_analytics.setOnClickListener {
            val feedback = Intent(applicationContext, AnalyticsActivity::class.java)
            startActivity(feedback);
        }

        button_push.setOnClickListener {
            val push = Intent(applicationContext, PushActivity::class.java)
            startActivity(push);
        }
    }

}
