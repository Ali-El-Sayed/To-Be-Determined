package com.example.ryady.view.screens.Favourite.View

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ryady.R
import com.example.ryady.databinding.FragmentFavouriteBinding
import com.example.ryady.databinding.FragmentLoginBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.Favourite.ViewModel.FavouriteViewModel
import com.example.ryady.view.screens.product.viewModel.ProductViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment(), IFavouriteFragment {
    lateinit var binding: FragmentFavouriteBinding
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater,container,false)
        return  binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.getAllFavouriteProduct()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.productList.collectLatest {
                withContext(Dispatchers.Main){
                    when (it){
                        is Response.Error -> {

                        }
                        is Response.Loading -> {


                        }
                        is Response.Success -> {
                            binding.rvFavouriteList.adapter = FavouriteListAdapter(it.data.toMutableList() , this@FavouriteFragment,requireContext())
                        }
                    }
                }
            }
        }
    }

    override fun deleteItem(itemId: String) {


        viewModel.deleteItem(itemId)
    }

    override fun onItemClick(itemId: String) {
        findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToProductInfoFragment(itemId))
    }


}