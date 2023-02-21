package supej.zxanner

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import supej.zxanner.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    companion object {
        fun intent(from : Context, qrResult : String?) : Intent {
            return Intent(from, ResultActivity::class.java).also {
                it.putExtra(QR_RESULT, qrResult)
            }
        }
        private const val QR_RESULT = "QR_RESULT"
    }

    private val binding by lazy { ActivityResultBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.resultTv.text = intent.extras?.getString(QR_RESULT)
    }
}