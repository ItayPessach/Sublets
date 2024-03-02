package com.example.apartments.modules.apartments.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel

interface OnItemClickListener {
    fun onItemClick(apartmentId: Int)
}

class ApartmentsRecyclerAdapter(var apartments: List<Apartment>?): RecyclerView.Adapter<ApartmentsViewHolder>() {
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApartmentsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.apartment_list_row, parent, false)
        return ApartmentsViewHolder(itemView, this)
    }

    override fun getItemCount(): Int = apartments?.size ?: 0

    override fun onBindViewHolder(holder: ApartmentsViewHolder, position: Int) {
        val apartment = apartments?.get(position)
        holder.bind(apartment)
        // holder.image?.setImageResource(apartment.image)
    }

    fun onClick(apartmentId: Int) {
        listener?.onItemClick(apartmentId)
    }

    fun onLikeClick(apartmentId: Int) {
        Log.i("ApartmentsRecyclerAdapter", "Like button clicked for apartment $apartmentId")
        val apartment = apartments?.get(apartmentId)

        if (apartment != null) {
            val liked = !apartment.liked

            if (liked) {
                ApartmentModel.instance.addLikedApartment(apartment.id)
            } else {
                ApartmentModel.instance.removeLikedApartment(apartment.id)
            }
        }
    }
}