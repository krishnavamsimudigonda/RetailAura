package com.adina.retailaura.scan_pay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adina.retailaura.R
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    private var amount: Double = 0.0
    private var discount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Retrieve the amount and discount from the intent
        amount = intent.getDoubleExtra("amount", 0.0)
        discount = intent.getDoubleExtra("discount", 0.0)

        // Start the payment process with the specified amount
        startPayment(amount)
    }

    private fun startPayment(amount: Double) {
        val activity: Activity = this
        val co = Checkout()

        // Set your Razorpay API key
        co.setKeyID(<razor_pay_api>)

        try {
            val options = JSONObject()
            options.put("name", "RetailAura")
            options.put("description", "Shopping made Easy")
            options.put("image", "https://firebasestorage.googleapis.com/v0/b/retailaura-ddde4.appspot.com/o/retail_aura_logo1.jpg?alt=media&token=a7bff741-a32f-4948-9108-9be0b7c4f409") // Use a direct link to an image
            options.put("theme.color", "#0071CE")
            options.put("currency", "INR")
            options.put("amount", amount * 100) // Amount in paisa

            val prefill = JSONObject()
            // Add prefill details if needed, e.g., email, contact
            options.put("prefill", prefill)

            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        if (razorpayPaymentID != null) {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, QRCodeActivity::class.java)
            intent.putExtra("transaction_id", razorpayPaymentID)
            intent.putExtra("amount", amount) // Pass the total amount instead of zero
            intent.putExtra("discount", discount)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Payment ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }
}
