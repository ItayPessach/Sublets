package com.example.apartments.modules.apartments

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.apartments.R
import com.example.apartments.model.user.UserModel
import com.example.apartments.modules.apartments.adapter.ApartmentsRecyclerAdapter
import com.example.apartments.modules.apartments.adapter.OnItemClickListener
import com.example.apartments.modules.apartments.base.BaseApartmentsFragment


class ApartmentsFragment : BaseApartmentsFragment() {
    private var TAG = "ApartmentsFragment"

    override suspend fun preparations() {
        return UserModel.instance.getMe()
    }

    override fun setupApartmentsAdapter(): ApartmentsRecyclerAdapter {
        return ApartmentsRecyclerAdapter(viewModel.getAllApartments(), viewModel)
    }

    override fun observeApartments() {
        viewModel.apartments?.observe(viewLifecycleOwner) {
            progressBar.visibility = View.VISIBLE
            adapter.apartments = viewModel.getAllApartments()
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
                    findNavController().navigate(R.id.action_apartmentsFragment_to_expandedApartmentFragment, bundleOf("apartmentId" to apartment.id))
                }
            }
        }
    }
}