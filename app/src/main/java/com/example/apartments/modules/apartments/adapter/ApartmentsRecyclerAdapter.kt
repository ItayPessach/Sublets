package com.example.apartments.modules.apartments.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.user.UserModel
import com.example.apartments.modules.apartments.ApartmentsViewModel

interface OnItemClickListener {
    fun onItemClick(apartmentId: Int)
}

class ApartmentsRecyclerAdapter(var apartments: List<Apartment>?, private val viewModel: ApartmentsViewModel, private val onEditClick: (apartmentId: String) -> Unit): RecyclerView.Adapter<ApartmentsViewHolder>() {
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApartmentsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.apartment_list_row, parent, false)
        return ApartmentsViewHolder(itemView, this)
    }

    override fun getItemCount(): Int = apartments?.size ?: 0

    override fun onBindViewHolder(holder: ApartmentsViewHolder, position: Int) {
        val apartment = apartments?.get(position)
        holder.bind(apartment)
    }

    fun onClick(apartmentId: Int) {
        listener?.onItemClick(apartmentId)
    }

    fun onLikeClick(position: Int) {
        val apartment = apartments?.get(position)

        apartment?.let {
            val liked = !apartment.liked
            viewModel.onLikeClick(apartment.id, liked)
        }
    }

    fun onEditClick(position: Int) {
        val apartment = apartments?.get(position)
        apartment?.let {
            onEditClick(it.id)
        }
    }

    fun onDeleteClick(position: Int) {
        val apartment = apartments?.get(position)
        apartment?.let {
            viewModel.onDeleteClick(it.id)
        }
    }
}