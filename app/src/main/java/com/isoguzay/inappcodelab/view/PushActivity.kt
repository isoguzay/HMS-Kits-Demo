package com.isoguzay.inappcodelab.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.isoguzay.inappcodelab.R
import com.isoguzay.inappcodelab.hms.push.manager.PushManager
import kotlinx.android.synthetic.main.activity_push.*

class PushActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push)

        button_push_message.setOnClickListener {
            PushManager.getAccessToken()
            PushManager.getDeviceIdToken(applicationContext)
        }

    }
}