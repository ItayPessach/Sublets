package com.example.apartments.modules.apartments.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment
import java.text.SimpleDateFormat
import java.util.Date

class ApartmentsViewHolder(itemView: View, adapter: ApartmentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
    private var titleTextView: TextView? = null
    private var priceTextView: TextView? = null
    private var locationTextView: TextView? = null
    private var roomsTextView: TextView? = null
    private var propertyTypeTextView: TextView? = null
    private var datesTextView: TextView? = null

    private var image: ImageView? = null

    init {
        titleTextView = itemView.findViewById(R.id.tvApartmentsListTitle)
        priceTextView = itemView.findViewById(R.id.tvApartmentsListPrice)
        locationTextView = itemView.findViewById(R.id.tvApartmentsListLocation)
        roomsTextView = itemView.findViewById(R.id.tvApartmentsListRooms)
        propertyTypeTextView = itemView.findViewById(R.id.tvApartmentsListPropertyType)
        datesTextView = itemView.findViewById(R.id.tvApartmentsListDates)
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
        priceTextView?.text = "${apartment?.pricePerNight.toString()}$"
        locationTextView?.text = apartment?.city
        roomsTextView?.text = apartment?.numOfRooms.toString()
        propertyTypeTextView?.text = apartment?.apartmentType.toString()
        datesTextView?.text = "${formatDate(apartment?.startDate ?: 0)} - ${formatDate(apartment?.endDate ?: 0)}"
    }

    fun formatDate(milliseconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(Date(milliseconds))
    }
}