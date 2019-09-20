package com.stavro_xhardha.pockettreasure.ui.quran


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.stavro_xhardha.PocketTreasureApplication
import com.stavro_xhardha.pockettreasure.R
import com.stavro_xhardha.pockettreasure.background.QuranWorker
import com.stavro_xhardha.pockettreasure.brain.viewModel
import kotlinx.android.synthetic.main.error_layout.*
import kotlinx.android.synthetic.main.fragment_quran.*

class QuranFragment : Fragment(), QuranAdapterContract {

    private val quranViewModel by viewModel {
        PocketTreasureApplication.getPocketTreasureComponent().quranViewModelFactory.create(it)
    }
    private lateinit var compressionWork: WorkRequest

    private val quranAdapter by lazy {
        QuranAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quran, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    view.findNavController().popBackStack(R.id.homeFragment, false)
                }
            })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeComponents()
        observeTheLiveData()
    }

    fun initializeComponents() {
        startQuranWorker()
        rvSuras.adapter = quranAdapter
        btnRetry.setOnClickListener {
            startQuranWorker()
        }
    }

    private fun startQuranWorker() {
        quranViewModel.showProgress()

        compressionWork = OneTimeWorkRequestBuilder<QuranWorker>().build()

        WorkManager.getInstance(requireActivity()).enqueue(compressionWork)

        WorkManager.getInstance(requireActivity()).getWorkInfoByIdLiveData(compressionWork.id)
            .observe(this, Observer {
                if (it != null && it.state == WorkInfo.State.SUCCEEDED) {
                    quranViewModel.startQuranDatabaseCall()
                } else {
                    if (it != null && it.state == WorkInfo.State.FAILED) {
                        quranViewModel.showError()
                    }
                }
            })
    }

    fun observeTheLiveData() {
        quranViewModel.surahs.observe(this, Observer {
            quranAdapter.submitList(it)
        })

        quranViewModel.errorVisibility.observe(this, Observer {
            llError.visibility = it
        })

        quranViewModel.progressVisibility.observe(this, Observer {
            pbQuran.visibility = it
        })

        quranViewModel.listVisibility.observe(this, Observer {
            rvSuras.visibility = it
        })
    }

    override fun onSurahClicked(surahsNumber: Int) {
        val action = QuranFragmentDirections.actionQuranFragmentToAyaFragment(surahsNumber)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        WorkManager.getInstance(requireActivity()).cancelWorkById(compressionWork.id)
        super.onDestroy()
    }
}