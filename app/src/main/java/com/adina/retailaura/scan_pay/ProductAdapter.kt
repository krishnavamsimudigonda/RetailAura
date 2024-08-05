package com.adina.retailaura.scan_pay


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adina.retailaura.Models.Product
import com.adina.retailaura.databinding.ItemProductBinding
import com.bumptech.glide.Glide

class productAdapter(
    private val products: MutableList<Product>,
    private val updateDiscountPrice: (Double) -> Unit,
    private val amountSaved: (Double) -> Unit
) : RecyclerView.Adapter<productAdapter.productViewHolder>() {

    inner class productViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image: ImageView = binding.image
        val name: TextView = binding.name
        val price: TextView = binding.price
        val quantity: TextView = binding.qty
        val plus: ImageView = binding.plus
        val minus: ImageView = binding.minus
        val discount: TextView = binding.discount

        fun bind(product: Product) {
            // Set product details
            name.text = product.name
            price.text = String.format("₹%.2f", product.price)
            price.paintFlags = price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            discount.text = String.format("₹%.2f", product.discountPrice)
            quantity.text = product.quantity.toString()

            // Load image using Glide
            Glide.with(image.context)
                .load(product.imageUrl)
                .into(image)

            // Handling click for "+" button
            plus.setOnClickListener {
                product.quantity++
                quantity.text = product.quantity.toString()
                updateDiscountPrice(calculateDiscountPrice())
                amountSaved(calculateAmountSaved())
                notifyItemChanged(adapterPosition)
            }

            // Handling click for "-" button
            minus.setOnClickListener {
                if (product.quantity > 0) {
                    product.quantity--
                    quantity.text = product.quantity.toString()
                    updateDiscountPrice(calculateDiscountPrice())
                    amountSaved(calculateAmountSaved())
                    notifyItemChanged(adapterPosition)
                }
            }
        }

        private fun calculateTotalPrice(): Double {
            return products.sumOf { it.price * it.quantity }
        }

        private fun calculateDiscountPrice(): Double {
            return products.sumOf { it.discountPrice * it.quantity }
        }

        private fun discountAmount(): Double {
            return calculateTotalPrice() - calculateDiscountPrice()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return productViewHolder(binding)
    }

    override fun onBindViewHolder(holder: productViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    fun addProduct(product: Product) {
        // Check if the product already exists
        val existingProduct = products.find { it.id == product.id }
        if (existingProduct != null) {
            existingProduct.quantity += product.quantity
            notifyItemChanged(products.indexOf(existingProduct))
        } else {
            products.add(product)
            notifyItemInserted(products.size - 1)
        }
        updateDiscountPrice(calculateDiscountPrice())
        amountSaved(calculateAmountSaved())
    }

    private fun calculateAmountSaved(): Double {
        return calculateTotalPrice() - calculateDiscountPrice()
    }

    private fun calculateTotalPrice(): Double {
        return products.sumOf { it.price * it.quantity }
    }

    private fun calculateDiscountPrice(): Double {
        return products.sumOf { it.discountPrice * it.quantity }
    }

    fun getProducts(): List<Product> {
        return products
    }
}
