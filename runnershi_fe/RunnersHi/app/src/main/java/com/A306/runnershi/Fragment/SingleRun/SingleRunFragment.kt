package com.A306.runnershi.Fragment.SingleRun


import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.DI.AppModule
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Fragment.Home.HomeFragment
import com.A306.runnershi.Fragment.SingleRun.RunResultFragment
import com.A306.runnershi.Model.Run
import com.A306.runnershi.R
import com.A306.runnershi.Services.Polyline
import com.A306.runnershi.Services.TrackingService
import com.A306.runnershi.ViewModel.SingleRunViewModel
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_run_result.*
import kotlinx.android.synthetic.main.fragment_single_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class SingleRunFragment : Fragment(R.layout.fragment_single_run), EasyPermissions.PermissionCallbacks {
    private val viewModel: UserViewModel by viewModels()

    private var curTimeMillis = 0L

    var runResult = Run()

    inner class mapFragmentToSingleRunFragment {
        fun getRunData(runData: Run) {
            runResult = runData
        }
    }

    // globalSubscribeToObserver에 들어갈 관련내용 여기부터

    private var map: GoogleMap? = null
    private var pathPoints = mutableListOf<Polyline>()

    // 여기까지

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        val mainActivity = activity as MainActivity
        val homeFragment = HomeFragment()
        val link = mapFragmentToSingleRunFragment()
        val mapFragment = MapFragment()
        val run = Run()
        val runResultFragment = RunResultFragment(runResult)

        // 임시로 뜯어보는 리팩토링 :
        subscribeToObservers(mainActivity)
//        val here = "SingleRunFrag"
//        val distanceView = distanceText
//        val timeView = timeText
//        val paceView = paceText
//
//        globalSubscribeToObservers(here, mainActivity, distanceView, timeView, paceView)


        // 정지 버튼
        stopRunButton.setOnClickListener {
            // Dialog 띄우기
            showCancelRunningDialog(mainActivity, homeFragment, mapFragment, runResultFragment)
        }

        // 지도로 보기 버튼
        toMapButton.setOnClickListener {
            mainActivity.makeCurrentFragment(mapFragment, "hide")
        }
    }

    private fun showCancelRunningDialog(activity: MainActivity, fragment: HomeFragment, mapFragment: MapFragment, runResultFragment: RunResultFragment){
        activity.sendCommandToService("ACTION_PAUSE_SERVICE")
//        activity.makeCurrentFragment(mapFragment, "hide")
        val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("종료하십니까?")
                .setMessage("정말 달리기를 종료하시겠습니까?")
                .setIcon(R.drawable.ic_baseline_person_24)
                .setPositiveButton("종료"){ _, _ ->
                    TrackingService.totallyFinished.postValue(1)

                    // 서비스를 완전히 종료하면 RunResultFragment에서 루트를 그릴 수 없습니다.
                    // 그러나 서비스를 계속 진행하게 된다면 그 루트가 끊임없이 바뀝니다. 그러므로 일단은 일시정지 해 줍시다.
                    activity.sendCommandToService("ACTION_PAUSE_SERVICE")

                    // 이 때 runResult를 받아오고, 이를 RunResult로 넘겨줍시다.
                    val runResultFragment = RunResultFragment(setRunResult())
                    activity.makeCurrentFragment(runResultFragment)
                }
                .setNegativeButton("다시 뛰기"){ dialogInteface, _ ->
                    activity.sendCommandToService("ACTION_START_OR_RESUME_SERVICE")
                    dialogInteface.cancel()
                }
                .create()
        dialog.show()
    }

    private fun setRunResult(): Run {
//        Log.i("임시로", "${runResult.title}")
//        var runResult = Run()
        val dateTimestamp = Calendar.getInstance().timeInMillis
        Log.i("셋런리져트에서1", "${dateTimestamp.toString()}")
        val distanceInMeters = TrackingService.totalDistance.value!!.toInt()
        val avgSpeed = ((distanceInMeters / 1000f) / (curTimeMillis / 1000f / 60 / 60) * 10).roundToInt() / 10f
        //총 걸린 시간
        val dateTimeSpent = TrackingUtility.getFormattedStopWatchTime(TrackingService.timeRunInMillis.value!!)
        val finalPace = TrackingUtility.getPaceWithMilliAndDistance(TrackingService.totalPace.value!!)

        // 임시로 저장해서 넘길 데이터
        val title = "${dateTimestamp}의 달리기"
        val bmp = null

        runResult = Run(title, bmp, dateTimestamp, avgSpeed, distanceInMeters, dateTimeSpent, finalPace)
        Log.i("셋런리져트에서2", "${runResult.title.toString()}")

        // 종료버튼을 누르는 순간의 runData 저장하기
//        map?.snapshot { bmp ->
//            Timber.e("사진 저장")
//            Timber.e(map?.toString())
//        }
        Log.i("셋런리져트에서3", "${runResult.title.toString()}")
//        return runResult
//        val runResultFragment = RunResultFragment(runResult)
//        activity.makeCurrentFragment(runResultFragment, "hide")
        return runResult
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

//        TrackingService.totallyFinished.observe(viewLifecycleOwner, Observer{
//            if (it > 0) {
//                if (map != null) {
////                    setRunResult(activity)
//                    activity.sendCommandToService("ACTION_STOP_SERVICE")
//                } else {
//                    TrackingService.totallyFinished.postValue(it+1)
//                }
//            }
//        })
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

//    // 리팩토링해보자 : SingleRun에 모두 담아버리고 여기서 Map이랑 RunResult로 넣어줄것
//
//    fun globalSubscribeToObservers(here:String, activity: MainActivity, distanceView: TextView?, timeView: TextView, paceView: TextView) {
//
//        // PART 1 : SingleRun에서 쓸 부분
//        if (here == "SingleRunFrag") {
//            TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
//                if (it) {
//                    pauseRunButton.text = "일시정지"
//                    pauseRunButton.setOnClickListener {
//                        activity.sendCommandToService("ACTION_PAUSE_SERVICE")
//                    }
//                } else {
//                    pauseRunButton.text = "다시 달리기"
//                }
//            })
//        }
//
//        // PART 1.5 : SingleRun, Map 모두 공통으로 쓰는 TextView 표기
//        TrackingService.totalDistance.observe(viewLifecycleOwner, Observer {
//            if (distanceView != null) {
//                distanceView.text = String.format("%.2f", it/1000f)
//            }
//        })
//        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
//            curTimeMillis = it
//            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeMillis, false)
//            timeView.text = formattedTime
//        })
//        TrackingService.totalPace.observe(viewLifecycleOwner, Observer {
//            if (it > 0) {
//                val formattedPace = TrackingUtility.getPaceWithMilliAndDistance(it)
//                paceView.text = formattedPace
//            } else {
//                paceView.text = "0' 00''"
//            }
//        })
//
//        // PART 2 : Map, RunResult에서 쓰일 지도 API 표기
//        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
//            pathPoints = it
//            addLatestPolyline()
//            moveCameraToUser()
//        })
//
//        // PART 3 : 달리기가 종료될 경우 RunResult로 해당 값 보내줄 것
//        TrackingService.totallyFinished.observe(viewLifecycleOwner, Observer {
//            if (it > 0) {
//                if (map != null) {
//                    zoomToSeeWholeTrack()
//                    activity.sendCommandToService("ACTION_STOP_SERVICE")
//                    tempSaveRun()
//                } else {
//                    TrackingService.totallyFinished.postValue(it + 1)
//                }
//            }
//        })
//
//    }
//
//    private fun tempSaveRun(): Run {
//        var runResult = Run()
//
//        map?.snapshot { bmp ->
//            val dateTimestamp = Calendar.getInstance().timeInMillis
//            val distanceInMeters = TrackingService.totalDistance.value!!.toInt()
//            val avgSpeed = ((distanceInMeters / 1000f) / (curTimeMillis / 1000f / 60 / 60) * 10).roundToInt() / 10f
//            val dateTimeSpent = TrackingUtility.getFormattedStopWatchTime(TrackingService.timeRunInMillis.value!!)
//            val finalPace = TrackingUtility.getPaceWithMilliAndDistance(TrackingService.totalPace.value!!)
//            val title = "${dateTimestamp}의 달리기"
//
//            runResult = Run(title, bmp, dateTimestamp, avgSpeed, distanceInMeters, dateTimeSpent, finalPace)
//        }
//        return runResult
//    }
//
//    private fun addLatestPolyline() {
//        for (polyline in pathPoints) {
//            val polylineOptions = PolylineOptions()
//                .color(Color.RED)
//                .width(8f)
//                .addAll(polyline)
//            map?.addPolyline(polylineOptions)
//        }
//    }
//
//    private fun moveCameraToUser() {
//        if ( pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
//            map?.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    pathPoints.last().last(), 15f
//                )
//            )
//        }
//    }
//
//    private fun zoomToSeeWholeTrack() {
//        val bounds = LatLngBounds.Builder()
//        for (polyline in pathPoints) {
//            for (pos in polyline) {
//                bounds.include(pos)
//            }
//        }
//
//        map?.moveCamera(
//            CameraUpdateFactory.newLatLngBounds(
//                bounds.build(),
//                routeDataView.width,
//                routeDataView.height,
//                (routeDataView.height * 0.05f).toInt()
//            )
//        )
//    }

}