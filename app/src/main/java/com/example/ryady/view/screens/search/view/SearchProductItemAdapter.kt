package com.example.ryady.view.screens.search.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.SearchProductsQuery
import com.example.ryady.databinding.SearchListItemBinding
import com.example.ryady.model.extensions.roundTo2DecimalPlaces
import com.example.ryady.view.screens.settings.currency.TheExchangeRate


class SearchProductItemAdapter(
    private var searchList: List<SearchProductsQuery.Edge>, private val listener: onSearchItemClick
) : RecyclerView.Adapter<SearchProductItemAdapter.ViewHolder>() {
    private lateinit var binding: SearchListItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SearchProductItemAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = SearchListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchProductItemAdapter.ViewHolder, position: Int) {
        searchList[position].node.onProduct?.let { product ->
            val productImagesUrl: MutableList<SlideModel> = mutableListOf()
            product.images.edges.forEach {
                productImagesUrl.add(SlideModel(imageUrl = it.node.url.toString()))
            }
            holder.productImageSlider.setImageList(productImagesUrl)
            holder.tvTitle.text = product.title

            val price = product.variants.edges.first().node.price.amount.toString().toDouble()
            val priceExchanged =
                price / (TheExchangeRate.currency.rates?.get("EGP")!!) * (TheExchangeRate.currency.rates?.get(TheExchangeRate.chosenCurrency.first)!!)


            holder.tvPriceAmount.text = priceExchanged.roundTo2DecimalPlaces().toString()
            holder.tvPriceCode.text = TheExchangeRate.chosenCurrency.first
        }

        holder.cardItem.setOnClickListener {
            searchList[position].node.onProduct?.id?.let { it1 -> listener.onItemClick(it1) }
        }

    }

    override fun getItemCount(): Int = searchList.count()


    fun setList(newList: List<SearchProductsQuery.Edge>) {
        searchList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: SearchListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val tvTitle: TextView
        val productImageSlider: ImageSlider
        val tvPriceAmount: TextView
        val tvPriceCode: TextView
        val cardItem: CardView

        init {
            tvTitle = binding.title
            productImageSlider = binding.productImage
            tvPriceAmount = binding.priceAmount
            tvPriceCode = binding.priceCode
            cardItem = binding.cardItem
        }
    }
}