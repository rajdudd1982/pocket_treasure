package com.stavro_xhardha.pockettreasure.ui.names


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.stavro_xhardha.PocketTreasureApplication
import com.stavro_xhardha.pockettreasure.BaseFragment
import com.stavro_xhardha.pockettreasure.R
import kotlinx.android.synthetic.main.fragment_names.*
import javax.inject.Inject

class NamesFragment : BaseFragment() {

    @Inject
    lateinit var namesFragmentProviderFactory: NamesViewModelProviderFactory

    @Inject
    lateinit var layoutManager: LinearLayoutManager
    @Inject
    lateinit var namesAdapter: NamesAdapter

    private lateinit var namesViewModel: NamesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_names, container, false)
    }

    override fun performDi() {
        DaggerNamesFragmentComponent.builder()
            .namesFragmentModule(NamesFragmentModule(context!!))
            .pocketTreasureComponent((activity!!.application as PocketTreasureApplication).getPocketTreasureComponent())
            .build().inject(this)
    }

    override fun initViewModel() {
        namesViewModel = ViewModelProviders.of(this, namesFragmentProviderFactory).get(NamesViewModel::class.java)
    }

    override fun observeTheLiveData() {
        namesViewModel.allNamesList.observe(this, Observer {
            namesAdapter.setItemList(it)
            rvNames.adapter = namesAdapter
        })
        namesViewModel.progressBarVisibility.observe(this, Observer {
            pbNames.visibility = it
        })
        namesViewModel.errorLayoutVisibility.observe(this, Observer {
            llError.visibility = it
        })
    }

    override fun initializeComponent() {
        rvNames.layoutManager = layoutManager
    }
}