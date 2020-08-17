package com.isoguzay.inappcodelab.hms.ml.text

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.isoguzay.inappcodelab.R
import kotlinx.android.synthetic.main.activity_ml_kit.*
import kotlinx.android.synthetic.main.activity_text.*

class TextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text)

        button_text_recog.setOnClickListener {
            val textRecognition = Intent(applicationContext, TextRecognitionActivity::class.java)
            startActivity(textRecognition)
        }

        button_document_recog.setOnClickListener {
            val documentRecog = Intent(applicationContext, DocumentRecognitionActivity::class.java)
            startActivity(documentRecog)
        }

        button_bank_card_recog.setOnClickListener {
            val bankCardRecog = Intent(applicationContext, BankCardRecognitionActivity::class.java)
            startActivity(bankCardRecog)
        }

        button_general_card_recog.setOnClickListener {
            val generalCardRecog = Intent(applicationContext, GeneralCardRecognitionActivity::class.java)
            startActivity(generalCardRecog)
        }

    }
}