package com.A306.runnershi.Fragment.SingleRun


import android.Manifest
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.DI.AppModule
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Fragment.HomeFragment
import com.A306.runnershi.R
import com.A306.runnershi.Services.TrackingService
import com.A306.runnershi.ViewModel.SingleRunViewModel
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_single_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import kotlin.math.absoluteValue

@AndroidEntryPoint
class SingleRunFragment : Fragment(R.layout.fragment_single_run), EasyPermissions.PermissionCallbacks {
    private val viewModel: UserViewModel by viewModels()

    private var curTimeMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        val mainActivity = activity as MainActivity
        val homeFragment = HomeFragment()
        val mapFragment = MapFragment()

        subscribeToObservers(mainActivity)

        view
        
        // 정지 버튼
        stopRunButton.setOnClickListener {
            // Dialog 띄우기
            showCancelRunningDialog(mainActivity, homeFragment, mapFragment)
        }

        // 지도로 보기 버튼
        toMapButton.setOnClickListener {
            mainActivity.makeCurrentFragment(mapFragment, "hide")
        }
    }

    private fun showCancelRunningDialog(activity: MainActivity, fragment: HomeFragment, mapFragment: MapFragment){
        activity.sendCommandToService("ACTION_PAUSE_SERVICE")
        val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("종료하십니까?")
                .setMessage("정말 달리기를 종료하시겠습니까?")
                .setIcon(R.drawable.ic_baseline_person_24)
                .setPositiveButton("종료"){ _, _ ->
                    TrackingService.totallyFinished.postValue(1)
                    activity.makeCurrentFragment(mapFragment, "hide")
                }
                .setNegativeButton("다시 뛰기"){ dialogInteface, _ ->
                    activity.sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
                    dialogInteface.cancel()
                }
                .create()
        dialog.show()
    }

    private fun subscribeToObservers(activity: MainActivity){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            if (it){
                pauseRunButton.text = "일시정지"
                pauseRunButton.setOnClickListener { activity.sendCommandToService("ACTION_PAUSE_SERVICE") }
            }else{
                pauseRunButton.text = "다시 달리기"
                pauseRunButton.setOnClickListener { activity.sendCommandToService("ACTION_START_OR_RESUME_SERVICE") }
            }
        })

        TrackingService.totalDistance.observe(viewLifecycleOwner, Observer {
            distanceText.text = String.format("%.2f", it/1000f)
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer{
            curTimeMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeMillis, false)
            timeText.text = formattedTime
        })

        TrackingService.totalPace.observe(viewLifecycleOwner, Observer {
            if (it > 0){
                val formattedPace = TrackingUtility.getPaceWithMilliAndDistance(it)
                paceText.text = formattedPace
            }else{
                paceText.text = "0' 00''"
            }
        })
    }

    private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                this,
                "앱 사용을 위해 위치 권한 항상 허용이 필요합니다.",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else{
            EasyPermissions.requestPermissions(
                this,
                "앱 사용을 위해 위치 권한 항상 허용이 필요합니다.",
                0,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>){}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.e("PERMISSION RESULT", requestCode.toString())
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}