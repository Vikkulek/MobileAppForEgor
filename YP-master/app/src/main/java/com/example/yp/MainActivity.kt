package com.example.yp

import android.content.Intent
import android.os.Bundle
import android.app.Activity
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult // Импорт IntentResult

class MainActivity : AppCompatActivity() {

    private lateinit var qrScanLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val teachersTableButton = findViewById<Button>(R.id.teachersTableButton)
        val groupsTableButton = findViewById<Button>(R.id.groupsTableButton)
        val cabinetsTableButton = findViewById<Button>(R.id.cabinetsTableButton)

        teachersTableButton.setOnClickListener {
            val intent = Intent(this, TeacherActivity::class.java)
            startActivity(intent)
        }

        groupsTableButton.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

        cabinetsTableButton.setOnClickListener {
            val intent = Intent(this, CabinetActivity::class.java)
            startActivity(intent)
        }

        QR_Scanning()


    }

    private fun QR_Scanning() {
        qrScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, result.data) // Парсим результат
                val scannedData = intentResult?.contents // Получаем отсканированный текст
                println("QR-код: $scannedData")
            } else {
                println("Сканирование отменено")
            }
        }

        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Сканируйте QR-код")
        qrScanLauncher.launch(integrator.createScanIntent())
    }
}
