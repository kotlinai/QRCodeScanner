package com.kotlinaai.qrscanner.composables

import android.Manifest
import android.widget.ImageButton
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.kotlinaai.qrscanner.R
import com.kotlinaai.qrscanner.ui.theme.QRCodeScannerTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScanner(onBack: () -> Unit, onScanFinished: (String) -> Unit) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            permissionState.hasPermission -> {
                CameraViewer(onScanFinished)
            }
            permissionState.shouldShowRationale || !permissionState.permissionRequested -> {
                SideEffect {
                    permissionState.launchPermissionRequest()
                }
            }
        }
        Cover()
        Image(
            modifier = Modifier
                .size(32.dp)
                .offset(15.dp, 51.dp)
                .clickable { onBack() },
            painter = painterResource(id = R.drawable.nav_back_white),
            contentDescription = "back")
    }
}

@Composable
fun CameraViewer(onScanFinished: (String) -> Unit) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {PreviewView(it)}
    ){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview : Preview = Preview.Builder().build()
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->

                val buffer = image.planes[0].buffer
                val data = ByteArray(buffer.remaining())
                val height = image.height
                val width = image.width
                buffer.get(data)
                //TODO 调整crop的矩形区域，目前是全屏（全屏有更好的识别体验，但是在部分手机上可能OOM）
                val source = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)

                val bitmap = BinaryBitmap(HybridBinarizer(source))

                runCatching {
                    val result = MultiFormatReader().decodeWithState(bitmap)

                    //imageAnalysis.clearAnalyzer()
                    onScanFinished(result.text)
                }

                image.close()
            }

            preview.setSurfaceProvider(it.surfaceProvider)
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(context))
    }
}

@Composable
fun Cover2() {
    val cover = painterResource(id = R.drawable.box_scan)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                drawContent()
                drawRect(
                    color = Color.Black.copy(alpha = 0.3f),
                    blendMode = BlendMode.SrcOut
                )
            }
            .padding(top = 185.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(42.dp)) {
        Image(painter = cover, contentDescription = "cover")
        Text(text = "扫描二维码", fontSize = 17.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun Cover() {
    val cover = ImageBitmap.imageResource(id = R.drawable.box_scan)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val imageSize = 296.dp.roundToPx()
            drawRect(
                color = Color.Black.copy(alpha = 0.3f)
            )
            drawImage(
                cover,
                blendMode = BlendMode.Src,
                dstSize = IntSize(imageSize, imageSize),
                dstOffset = IntOffset(((size.width - imageSize) / 2).toInt(), 185.dp.roundToPx())
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 523.dp),
            text = "扫描二维码",
            fontSize = 17.sp,
            color = Color.White.copy(alpha = 0.7f))
    }

}

@androidx.compose.ui.tooling.preview.Preview(showSystemUi = true, showBackground = true)
@Composable
fun ScannerPreview() {
    QRCodeScannerTheme {
        androidx.compose.material.Surface {
            Cover()
        }
    }
}