package com.sxhardha.setup_module

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.google.android.gms.location.LocationResult
import com.stavro_xhardha.core_module.SharedViewModel
import com.stavro_xhardha.core_module.background.PrayerTimeWorkManager
import com.stavro_xhardha.core_module.brain.BaseFragment
import com.stavro_xhardha.core_module.brain.LocationTracker
import com.stavro_xhardha.core_module.brain.LocationTrackerListener
import com.stavro_xhardha.core_module.brain.viewModel
import java.util.*
import java.util.concurrent.TimeUnit

class SetupFragment : BaseFragment(), LocationTrackerListener {

    private val setupViewModel by viewModel {
        DaggerSetupComponent.factory().create(coreComponent = applicationComponent).setupViewModel
    }

    private lateinit var sharedViewModel: SharedViewModel

    private var locationTracker: LocationTracker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setup, container, false)
    }

    private fun askForNotifyingUser() {
        MaterialDialog(activity!!).show {
            title(R.string.app_name)
            message(R.string.do_you_want_to_get_notified)
            positiveButton(text = activity!!.resources.getString(R.string.yes)) {
                setupViewModel.updateNotificationFlags()
                it.dismiss()
            }
            cancelable(false)
            negativeButton(text = activity!!.resources.getString(R.string.no)) {
                it.dismiss()
            }.onDismiss {
                val compressionWork =
                    PeriodicWorkRequestBuilder<PrayerTimeWorkManager>(1, TimeUnit.HOURS)
                        .setInitialDelay(1, TimeUnit.MINUTES)
                        .build()
                WorkManager.getInstance(requireActivity()).enqueue(compressionWork)
                locationTracker = null

                findNavController().popBackStack()
            }
        }
    }

    override fun initializeComponents() {
        sharedViewModel = requireActivity().run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }
        locationTracker = LocationTracker(requireActivity(), this)
        locationTracker?.startLocationRequestProcess()
    }

    override fun observeTheLiveData() {
        setupViewModel.locationRequestTurnOff.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                locationTracker?.removeLocationRequest()
            }
        })
        setupViewModel.locationErrorToast.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireActivity(), event.peekContent(), Toast.LENGTH_LONG)
                    .show()
                requireActivity().finish()
            }
        })
        setupViewModel.serviceNotAvailableVisibility.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled().let {
                Toast.makeText(requireActivity(), event.peekContent(), Toast.LENGTH_LONG)
                    .show()
                requireActivity().finish()
            }
        })
        setupViewModel.prayerNotificationDialogViaibility.observe(
            viewLifecycleOwner,
            Observer { event ->
                event.getContentIfNotHandled()?.let {
                    askForNotifyingUser()
                }
            })
        sharedViewModel.onGpsOpened.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                locationTracker?.updateLocation()
            }
        })
        sharedViewModel.onLocationPermissiongranted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                locationTracker?.getUserLocation()
            }
        })
    }

    override fun onLocationError() {
        requireActivity().finish()
    }

    override fun onLocationResult(locationResult: LocationResult) {
        for (location in locationResult.locations) {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            setupViewModel.convertToAdress(geocoder, location.latitude, location.longitude)
        }
    }

    override fun onDestroyView() {
        locationTracker = null
        super.onDestroyView()
    }
}