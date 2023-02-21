package supej.zxanner

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch
import supej.zxanner.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity(), Executor {

    private var counter: Int = 0
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val cameraController by lazy {
        LifecycleCameraController(this).apply {
            bindToLifecycle(this@MainActivity)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }
    private val resultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            clearAndSetAnalyzer()
        }
    private var barcodeScanner: BarcodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mainViewfinder.controller = cameraController
        binding.mainViewfinder.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        clearAndSetAnalyzer()
    }

    override fun onDestroy() {
        super.onDestroy()
        barcodeScanner?.close()
    }

    private fun clearAndSetAnalyzer() {
        barcodeScanner?.close()
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)
        barcodeScanner?.let { scanner ->
            val analyzer = MlKitAnalyzer(
                listOf(scanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                this
            ) { result ->
                val result = result.getValue(scanner)
                if (result?.isEmpty() == false) {
                    barcodeScanner?.close()
                    resultContract.launch(ResultActivity.intent(this, result.first().displayValue))
                }
            }

            cameraController.setImageAnalysisAnalyzer(
                this,
                analyzer
            )
        }
    }

    override fun execute(p0: Runnable?) {
        lifecycleScope.launch { p0?.run() }
    }
}