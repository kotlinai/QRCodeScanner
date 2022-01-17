package com.kotlinaai.qrscanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kotlinaai.qrscanner.composables.QRScanner
import com.kotlinaai.qrscanner.ui.theme.QRCodeScannerTheme

class QRScannerActivity : ComponentActivity() {
    companion object {
        const val DATA = "data"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRCodeScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    QRScanner(
                        onBack = {finish()},
                        onScanFinished = {
                            Log.d("QRScanner", it)

                            setResult(RESULT_OK, Intent().apply {
                                putExtra(DATA, it)
                            })
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QRCodeScannerTheme {
        //QRScanner()
    }
}