package com.adina.retailaura.scan_pay

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.adina.retailaura.R
import net.glxn.qrgen.android.QRCode

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode)

        val transactionId = intent.getStringExtra("transaction_id")
        val amount = intent.getDoubleExtra("amount", 0.0)
        val discount = intent.getDoubleExtra("discount", 0.0)

        // Display the amounts
        val paidAmountTextView = findViewById<TextView>(R.id.paidAmountTextView)
        val savedAmountTextView = findViewById<TextView>(R.id.savedAmountTextView)
        val qrImageView = findViewById<ImageView>(R.id.qrImageView)

        paidAmountTextView.text = "Bill Paid Amount:₹$amount"
        savedAmountTextView.text = "Yayy \uD83C\uDF89\nAmount Saved: ₹$discount"

        // Generate and display the QR code
        val qrCodeBitmap = generateQRCode(transactionId.toString())
        qrImageView.setImageBitmap(qrCodeBitmap)
    }

    private fun generateQRCode(data: String): Bitmap {
        return QRCode.from(data).withSize(512, 512).bitmap()
    }
}
