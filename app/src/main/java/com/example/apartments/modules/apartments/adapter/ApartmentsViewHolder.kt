package com.example.apartments.modules.apartments.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment

class ApartmentsViewHolder(itemView: View, adapter: ApartmentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
    var titleTextView: TextView? = null
    var subtitleTextView: TextView? = null
    private var image: ImageView? = null
    //var student: Student? = null

    init {
        titleTextView = itemView.findViewById(R.id.tvApartmentsListTitle)
        subtitleTextView = itemView.findViewById(R.id.tvApartmentsListSubtitle)
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
        subtitleTextView?.text = apartment?.subtitle
    }
}