package com.example.apartments.modules.apartments.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.utils.dateUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class ApartmentsViewHolder(itemView: View, adapter: ApartmentsRecyclerAdapter): RecyclerView.ViewHolder(itemView) {
    private val TAG = "ApartmentsViewHolder"

    private var titleTextView: TextView
    private var priceTextView: TextView
    private var locationTextView: TextView
    private var roomsTextView: TextView
    private var propertyTypeTextView: TextView
    private var datesTextView: TextView

    private var image: ImageView
    private var likeButton: ImageButton

    init {
        titleTextView = itemView.findViewById(R.id.tvApartmentsListTitle)
        priceTextView = itemView.findViewById(R.id.tvApartmentsListPrice)
        locationTextView = itemView.findViewById(R.id.tvApartmentsListLocation)
        roomsTextView = itemView.findViewById(R.id.tvApartmentsListRooms)
        propertyTypeTextView = itemView.findViewById(R.id.tvApartmentsListPropertyType)
        datesTextView = itemView.findViewById(R.id.tvApartmentsListDates)
        image = itemView.findViewById(R.id.ivApartmentsListImage)
        likeButton = itemView.findViewById(R.id.ibApartmentsListLikeButton)

        itemView.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                adapter.onClick(position)
            }
        }

        likeButton.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                adapter.onLikeClick(position)
                if (adapter.apartments?.get(position)?.liked == true) {
                    likeButton.setImageResource(R.drawable.like_button)
                } else {
                    likeButton.setImageResource(R.drawable.liked_like_button)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(apartment: Apartment?) {
        titleTextView.text = apartment?.title
        priceTextView.text = "${apartment?.pricePerNight}$"
        locationTextView.text = apartment?.city
        roomsTextView.text = apartment?.numOfRooms.toString()
        propertyTypeTextView.text = apartment?.type.toString()
        datesTextView.text = "${dateUtils.formatDate(apartment?.startDate ?: 0)} - ${dateUtils.formatDate(apartment?.endDate ?: 0)}"

        Picasso.get()
            .load(apartment?.imageUrl)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    image.setImageBitmap(bitmap)
                }
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Log.e(TAG, e.toString())
                }
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Log.d(TAG, "onPrepareLoad")
                }
            })

        if (apartment?.liked == true) {
            likeButton.setImageResource(R.drawable.liked_like_button)
        }
    }
}