package com.adina.retailaura.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adina.retailaura.Models.Product
import com.adina.retailaura.chat.ChatActivity
import com.adina.retailaura.databinding.ActivityMainBinding
import com.adina.retailaura.scan_pay.PaymentActivity
import com.adina.retailaura.scan_pay.ScanActivity
import com.adina.retailaura.scan_pay.productAdapter
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scanActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var productAdapter: productAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productAdapter = productAdapter(
            products = mutableListOf(),
            updateDiscountPrice = { totalPrice: Double ->
                binding.totalPrice.text = String.format("%.2f", totalPrice)
                if (totalPrice > 0) {
                    binding.totalPrice.visibility = View.VISIBLE
                    binding.textView2.visibility = View.VISIBLE
                    binding.discount.visibility = View.VISIBLE
                }
            }, amountSaved = { amountSaved: Double ->
                binding.discount.text = String.format("%.2f", amountSaved)
            }
        )
        setupRecyclerView()

        binding.chat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            val products = productAdapter.getProducts()
            intent.putParcelableArrayListExtra("products", ArrayList(products))
            startActivity(intent)
        }

        binding.totalPrice.visibility = View.GONE
        binding.textView2.visibility = View.GONE
        binding.discount.visibility = View.GONE

        scanActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val barcodeData = result.data?.getStringExtra("barcodeData")
                    barcodeData?.let {
                        fetchProductFromFirebase(it)
                    }
                }
            }

        binding.totalPrice.setOnClickListener {
            val amount = (binding.totalPrice.text.toString().toDouble())
            val totalPriceInRupees = (binding.totalPrice.text.toString().toDouble())
            val discountInRupees = (binding.discount.text.toString().toDouble())
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("amount", amount)
            intent.putExtra("discount", discountInRupees)
            startActivity(intent)
        }

        binding.barcode.setOnClickListener {
            startScanner()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productAdapter
        }
    }

    private fun startScanner() {
        val intent = Intent(this, ScanActivity::class.java)
        scanActivityResultLauncher.launch(intent)
    }

    private fun fetchProductFromFirebase(barcode: String) {
        db.collection("products").document(barcode).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val product = document.toObject(Product::class.java)
                    product?.let {
                        binding.emptyCart.visibility = View.GONE
                        val item = Product(it.id, it.name, it.price, it.discountPrice, it.quantity, it.imageUrl)
                        productAdapter.addProduct(item)
                    }
                } else {
                    Toast.makeText(this, "No such product", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch product", Toast.LENGTH_SHORT).show()
            }
    }

}
