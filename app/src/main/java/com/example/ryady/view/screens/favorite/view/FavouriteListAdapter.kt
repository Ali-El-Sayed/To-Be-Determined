package com.example.ryady.view.screens.favorite.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ryady.R
import com.example.ryady.databinding.FavouriteListItemBinding
import com.example.ryady.model.Product
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import com.getbase.floatingactionbutton.FloatingActionButton

class FavouriteListAdapter(
    private val listProduct: MutableList<Product>, private val listener: IFavouriteFragment, private val context: Context
) : RecyclerView.Adapter<FavouriteListAdapter.ViewHolder>() {

    lateinit var binding: FavouriteListItemBinding
    private lateinit var dialog: Dialog
    private lateinit var dialogBtnDelete: Button
    private lateinit var dialogBtnCancel: Button

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FavouriteListItemBinding.inflate(inflater, parent, false)
        initializeVerificationDialog()
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int = listProduct.size

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = listProduct[position].title
        val price = listProduct[position].maxPrice.toDouble()
        val priceExchanged = price / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(
            TheExchangeRate.chosenCurrency.first
        )!!)

        holder.tvPrice.text = priceExchanged.roundTo2DecimalPlaces().toString()
        holder.tvPriceCode.text = TheExchangeRate.chosenCurrency.first
        Glide.with(binding.root).load(listProduct[position].imageUrl).into(holder.ivProduct)
        holder.btnDelete.setOnClickListener {
            dialogBtnDelete.setOnClickListener {
                listener.deleteItem(listProduct[position].id)
                listProduct.removeAt(position)
                notifyDataSetChanged()
                dialog.dismiss()
            }
            dialogBtnCancel.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
        holder.itemView.setOnClickListener {
            listener.onItemClick(listProduct[position].id)
        }
    }

    private fun initializeVerificationDialog() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.delete_alert_dialog)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.verification_dialog_background))
        dialog.setCancelable(false)
        dialogBtnDelete = dialog.findViewById(R.id.btn_delete)
        dialogBtnCancel = dialog.findViewById(R.id.btn_cancel)
    }

    inner class ViewHolder(binding: FavouriteListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView
        val tvPrice: TextView
        val ivProduct: ImageView
        val tvPriceCode: TextView
        val btnDelete: FloatingActionButton

        init {
            tvTitle = binding.textTitle
            tvPrice = binding.tvPrice
            tvPriceCode = binding.tvPriceUnit
            ivProduct = binding.ivShoeIcon
            btnDelete = binding.btnDelete
        }

    }


}