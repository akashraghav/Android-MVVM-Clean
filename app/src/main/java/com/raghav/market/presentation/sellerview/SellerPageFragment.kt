package com.raghav.market.presentation.sellerview

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.raghav.market.R
import com.raghav.market.databinding.FragmentSellerViewBinding
import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.utils.toPrecision
import com.raghav.market.presentation.utils.SnackBarHelper.Companion.showSnackbarAction
import com.raghav.market.presentation.utils.formatResource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerPageFragment : Fragment() {

    private val vm: SellerPageViewModel by viewModels()
    private var binding: FragmentSellerViewBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_seller_view, container, false)
        binding = FragmentSellerViewBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.loadView.observe(viewLifecycleOwner) {
            toggleProgressView(it)
        }
        vm.loadSellers().observe(viewLifecycleOwner) {
            if (it is Result.Error) {
                showSnackbarAction(view, R.string.retry, R.string.failed_to_load) {
                    vm.loadSellers()
                }
            }
        }
        vm.loadCities().observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                updateSpinner(it.data!!)
            } else {
                showSnackbarAction(view, R.string.retry, R.string.failed_to_load) {
                    vm.loadCities()
                }
            }
        }
        vm.factor.observe(viewLifecycleOwner) {
            binding?.loyaltyFactorText?.text =
                String.formatResource(requireContext(), R.string.price_factor_format, it)
        }
        vm.grossPrice.observe(viewLifecycleOwner) {
            binding?.produceGrossPrice?.text = String.formatResource(requireContext(), R.string.price_format, it)
        }
        binding?.produceWeight?.addTextChangedListener() {
            if (it?.isEmpty() == false)
                vm.updateWeight(it.toString().toDouble())
        }
        binding?.sellerName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding?.sellerName?.hasFocus() == true) {
                    binding?.sellerId?.text = SpannableStringBuilder(vm.getSellerId(s.toString()))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding?.sellerId?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding?.sellerId?.hasFocus() == true) {
                    binding?.sellerName?.text = SpannableStringBuilder(vm.getSellerName(s.toString()))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding?.sellProduceCta?.setOnClickListener {
            if (validateInput()) {
                val bundle = Bundle()
                bundle.putString("name", binding?.sellerName!!.text.toString())
                bundle.putString("weight", vm.weight.toString())
                bundle.putString("price", vm.grossPrice.value.toString())
                val options = NavOptions.Builder().setPopUpTo(R.id.sellerpage, true).build()
                findNavController().navigate(R.id.salesuccesspage, bundle, options)
            } else {
                showValidationFailure()
            }
        }
    }

    private fun validateInput(): Boolean {
        return !(binding?.sellerName?.text?.isEmpty() ?: true
                || binding?.produceWeight?.text?.isEmpty() ?: true)
    }

    private fun showValidationFailure() {
        Toast.makeText(requireContext(), R.string.fill_mandatory_fields, Toast.LENGTH_SHORT).show()
    }

    private fun updateSpinner(data: List<CityInfo>) {
        val nameList = data.map { it.name }
        val adapter = ArrayAdapter(requireContext(), R.layout.village_list_item, android.R.id.text1, nameList)
        adapter.setDropDownViewResource(R.layout.village_list_dropdown_item)
        binding?.cityNameSpinner?.adapter = adapter
        binding?.cityNameSpinner?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val price = data.first { nameList[position] == it.name }.price
                binding?.grossPriceLabel?.text =
                    String.formatResource(requireContext(), R.string.gross_price_filled, price.toPrecision())
                vm.updatePricePerKg(price)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    private fun toggleProgressView(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressView?.visibility = View.VISIBLE
        } else {
            binding?.progressView?.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}