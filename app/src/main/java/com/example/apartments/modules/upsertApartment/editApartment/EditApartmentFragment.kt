package com.example.apartments.modules.upsertApartment.editApartment
import androidx.navigation.fragment.navArgs
import com.example.apartments.modules.upsertApartment.base.BaseUpsertApartmentFragment

class EditApartmentFragment : BaseUpsertApartmentFragment("EditApartmentFragment") {
    private val args: EditApartmentFragmentArgs by navArgs()

    override fun getApartmentId(): String {
        return args.apartmentId
    }
}