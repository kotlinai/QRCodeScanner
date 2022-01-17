package com.kotlinaai.qrcodescanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.kotlinaai.qrscanner.QRScannerContract

class MainActivity : AppCompatActivity() {

    private val qrScannerRequest = registerForActivityResult(QRScannerContract()) {
        it?.let { result -> tvResult.text = result }
    }

    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        qrScannerRequest.launch(null)
    }
}