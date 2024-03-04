package com.example.apartments.modules.apartments.myApartments

import android.util.Log
import android.view.View
import com.example.apartments.modules.apartments.adapter.ApartmentsRecyclerAdapter
import com.example.apartments.modules.apartments.adapter.OnItemClickListener
import com.example.apartments.modules.apartments.base.BaseApartmentsFragment


class MyApartmentsFragment : BaseApartmentsFragment() {
    private var TAG = "MyApartmentsFragment"

    override fun setupApartmentsAdapter(): ApartmentsRecyclerAdapter {
        return ApartmentsRecyclerAdapter(viewModel.getMyApartments(), viewModel)
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
                Log.d(TAG, "${viewModel.apartments?.value?.get(apartmentId)}")
                Log.d(TAG, "ApartmentsRecyclerAdapter: apartment id is $apartmentId")
            }
        }
    }
}