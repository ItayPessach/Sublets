package com.example.apartments.modules.apartments.myApartments

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.apartments.R
import com.example.apartments.modules.apartments.adapter.ApartmentsRecyclerAdapter
import com.example.apartments.modules.apartments.adapter.OnItemClickListener
import com.example.apartments.modules.apartments.base.BaseApartmentsFragment


class MyApartmentsFragment : BaseApartmentsFragment() {
    private var TAG = "MyApartmentsFragment"

    private val onEditClick: (apartmentId: String) -> Unit = {
        findNavController().navigate(R.id.action_myApartmentsFragment_to_editApartmentFragment, bundleOf("apartmentId" to it))
    }

    override fun setupApartmentsAdapter(): ApartmentsRecyclerAdapter {
        return ApartmentsRecyclerAdapter(viewModel.getMyApartments(), viewModel, onEditClick)
    }

    override fun observeApartments() {
        viewModel.apartments?.observe(viewLifecycleOwner) {
            progressBar.visibility = View.VISIBLE
            adapter.apartments = viewModel.getMyApartments()
            adapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }
    }

    override fun setupApartmentsAdapterListener(): OnItemClickListener {
        return object: OnItemClickListener {
            override fun onItemClick(apartmentId: Int) {
                Log.d(TAG, "ApartmentsRecyclerAdapter: apartment id is $apartmentId")
                val apartment = viewModel.apartments?.value?.get(apartmentId)
                apartment?.let {
                    findNavController().navigate(R.id.action_MyApartmentsFragment_to_expandedApartmentFragment, bundleOf("apartmentId" to apartment.id))
                }
            }
        }
    }
}