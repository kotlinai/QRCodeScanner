package com.kotlinaai.qrscanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class QRScannerContract: ActivityResultContract<Void?, String?>() {
    override fun createIntent(context: Context, input: Void?): Intent =
        Intent(context, QRScannerActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null
        else intent.getStringExtra(QRScannerActivity.DATA)
    }

}