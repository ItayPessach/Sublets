package com.example.apartments.modules.apartments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.databinding.FragmentApartmentsBinding
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.user.UserModel
import com.example.apartments.modules.apartments.adapter.ApartmentsRecyclerAdapter
import com.example.apartments.modules.apartments.adapter.OnItemClickListener
import kotlinx.coroutines.launch

class ApartmentsFragment : Fragment() {
    private var TAG = "ApartmentsFragment"

    private var _binding: FragmentApartmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ApartmentsViewModel

    private var apartmentsRecyclerView: RecyclerView? = null
    private var adapter: ApartmentsRecyclerAdapter? = null
    private var progressBar: ProgressBar? = null;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApartmentsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ApartmentsViewModel::class.java]

        lifecycleScope.launch {
            UserModel.instance.getMe()
        }

        progressBar = binding.progressBar

        progressBar?.visibility = View.VISIBLE
        viewModel.getAllApartments {
            apartmentsRecyclerView = binding.rvApartmentsFragmentList
            apartmentsRecyclerView?.setHasFixedSize(true)
            apartmentsRecyclerView?.layoutManager = LinearLayoutManager(context)
            adapter = ApartmentsRecyclerAdapter(viewModel.apartments?.value, viewModel)

            adapter?.listener = object: OnItemClickListener {
                override fun onItemClick(apartmentId: Int) {
                    Log.d(TAG, "${viewModel.apartments?.value?.get(apartmentId)}")
                    Log.d(TAG, "ApartmentsRecyclerAdapter: apartment id is $apartmentId")
                }
            }

            apartmentsRecyclerView?.adapter = adapter

            viewModel.apartments?.observe(viewLifecycleOwner) {
                progressBar?.visibility = View.VISIBLE
                adapter?.apartments = it
                adapter?.notifyDataSetChanged()
                progressBar?.visibility = View.GONE
            }

            binding.pullToRefresh.setOnRefreshListener {
                reloadData()
            }

            ApartmentModel.instance.apartmentsListLoadingState.observe(viewLifecycleOwner) { state ->
                binding.pullToRefresh.isRefreshing = state == ApartmentModel.LoadingState.LOADING
            }
        }

        return binding.root
    }

    private fun reloadData() {
        progressBar?.visibility = View.VISIBLE
        viewModel.refreshAllApartments {
            progressBar?.visibility = View.GONE
        }
    }
}