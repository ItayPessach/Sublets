package com.example.apartments.model.apartment

class ApartmentModel private constructor() {
    companion object {
        val instance: ApartmentModel = ApartmentModel()
    }

    fun getAllApartments(): List<Apartment> {
        val apartments: MutableList<Apartment> = mutableListOf()
        for (i in 1..20) {
            apartments.add(Apartment(i, "tel aviv $i", "location $i"))
        }

        return apartments
    }
}