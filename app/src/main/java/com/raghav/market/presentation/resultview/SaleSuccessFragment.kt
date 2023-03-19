package com.raghav.market.presentation.resultview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.raghav.market.R
import com.raghav.market.databinding.FragmentSaleSuccessBinding
import com.raghav.market.presentation.utils.formatResource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaleSuccessFragment : Fragment() {

    private var binding: FragmentSaleSuccessBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sale_success, container, false)
        binding = FragmentSaleSuccessBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.thankYouText?.text = String.formatResource(
            requireContext(),
            R.string.thank_you_seller, arguments?.getString("name")
        )
        binding?.ensureValuesText?.text = String.formatResource(
            requireContext(),
            R.string.ensure_values, arguments?.getString("price") ?: "0.0",
            arguments?.getString("weight") ?: "0.0"
        )
        binding?.sellMoreCta?.setOnClickListener {
            val options = NavOptions.Builder().setPopUpTo(R.id.salesuccesspage, true).build()
            findNavController().navigate(R.id.sellerpage, null, options)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}