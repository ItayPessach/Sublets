package com.example.apartments.modules.apartments.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment

class ApartmentsViewHolder(itemView: View, adapter: ApartmentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
    private var titleTextView: TextView? = null
    private var descriptionTextView: TextView? = null
    private var image: ImageView? = null

    init {
        titleTextView = itemView.findViewById(R.id.tvApartmentsListTitle)
        descriptionTextView = itemView.findViewById(R.id.tvApartmentsListDescription)
        image = itemView.findViewById(R.id.ivApartmentsListImage)

        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                adapter.onClick(position)
            }
        }
    }

    fun bind(apartment: Apartment?) {
        titleTextView?.text = apartment?.title
        descriptionTextView?.text = apartment?.description
    }
}