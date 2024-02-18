package com.example.apartments.modules.apartments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.modules.apartments.adapter.ApartmentsRecyclerAdapter
import com.example.apartments.modules.apartments.adapter.OnItemClickListener

class ApartmentsFragment : Fragment() {
    private var TAG = "ApartmentsFragment"

    private var apartmentsRecyclerView: RecyclerView? = null
    private var adapter: ApartmentsRecyclerAdapter? = null
    var apartments: List<Apartment>? = null
    var progressBar: ProgressBar? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_apartments, container, false)
        progressBar = view.findViewById(R.id.progressBar)

        progressBar?.visibility = View.VISIBLE

        adapter = ApartmentsRecyclerAdapter(this.apartments)

        ApartmentModel.instance.getAllApartments { apartments ->
            this.apartments = apartments
            adapter?.apartments = apartments
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }

        apartmentsRecyclerView = view.findViewById(R.id.rvApartmentsFragmentList)
        apartmentsRecyclerView?.setHasFixedSize(true)
        apartmentsRecyclerView?.layoutManager = LinearLayoutManager(context)

        adapter?.listener = object: OnItemClickListener {
            override fun onItemClick(apartmentId: Int) {
                Log.d(TAG, "ApartmentsRecyclerAdapter: apartment id is $apartmentId")
            }
        }

        apartmentsRecyclerView?.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()

        ApartmentModel.instance.getAllApartments { apartments ->
            this.apartments = apartments
            adapter?.apartments = apartments
            adapter?.notifyDataSetChanged()
            progressBar?.visibility = View.GONE
        }
    }
}