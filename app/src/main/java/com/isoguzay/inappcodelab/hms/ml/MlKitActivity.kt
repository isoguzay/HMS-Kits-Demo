package com.isoguzay.inappcodelab.hms.ml

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.isoguzay.inappcodelab.R
import com.isoguzay.inappcodelab.hms.ml.text.TextActivity
import kotlinx.android.synthetic.main.activity_ml_kit.*

class MlKitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ml_kit)

        button_text_func.setOnClickListener {
            val textFunc = Intent(applicationContext, TextActivity::class.java)
            startActivity(textFunc)
        }
    }

}