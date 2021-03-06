package com.A306.runnershi.Fragment.SingleRun

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Fragment.Home.HomeFragment
import com.A306.runnershi.Fragment.Profile.ProfileFragment
import com.A306.runnershi.Helper.RetrofitClient
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
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run_result.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
//class RunResultFragment(distance:String, time:String, pace:String) : Fragment(R.layout.fragment_run_result) {
class RunResultFragment(var runResult: Run, val runningDate: Calendar, val timeSpentInSeconds:Long) : Fragment(R.layout.fragment_run_result) {

    companion object{
        var TITLE = ""
    }

    private val userViewModel: UserViewModel by viewModels()
    var token = ""

    private val runViewModel: SingleRunViewModel by viewModels()

    private var map: GoogleMap? = null
    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeMillis = 0L

    private val resultDistance = runResult.distance
    private val resultTime = runResult.time.toString()
    private val resultPace = runResult.pace.toString()
    private var resultRoute = runResult.img


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ????????? ???????????? ???????????? polyline ?????? ????????????
        routeDataView.onCreate(savedInstanceState)
        routeDataView.getMapAsync {
            map = it
            addAllPolylines()
        }

        // polyline ???????????? ????????????
        val mainActivity = activity as MainActivity
        val profileFragment = ProfileFragment()

        subscribeToObservers(mainActivity)

        dateTextView.text = runResult.title
        distanceDataView.text = "$resultDistance K"
        timeDataView.text = resultTime
        paceDataView.text = resultPace

        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.e("?????? ??????????????? ?????? ?????? ???????????????", "????????? ????????? ?????????...")
            } else {
                token = it.token.toString()
            }
        })
        userViewModel.userInfo.removeObserver(Observer {  })

        // ???????????? ?????? ????????? ????????????????
        // TODO "???????????? detailview??? ??????????????? ????????? home?????? ???????????????"

        saveRunBtn.setOnClickListener {
            generateRunTitle()
            generateRunImg()
//            var keyHash = KakaoSdk.keyHash
//            Timber.e("????????? $keyHash")
        }
    }

    private fun generateRunTitle() {
        // ???????????? ??????????????????!
        val newtitle = generateTitleView.text.toString()
        Timber.e("???????????????")
        Log.e("???????????? ?????????", "${newtitle}?????????")

        if (newtitle != "") {
            runResult.title = newtitle
        }
        Log.e("???????????? ?????????", "${runResult.title.toString()}")

        Timber.e("????????? ????????????????")
    }

    private fun generateRunImg() {
        // ?????? ????????? ??????????????????
        map?.snapshot { bmp ->
            Timber.e("?????? ??????")
            Timber.e(map?.toString())
            resultRoute = bmp
            runResult.img = resultRoute
            Timber.e("?????? ?????????????????????????")
            Timber.e(resultRoute.toString())
            saveToDB()
        }
    }

    private fun saveToDB() {
        Timber.e("????????? ?????? ?????????????")
        Log.i("???????????? ??????", "${runResult.img.toString()}")
        Log.i("???????????? ?????? ??????", "${runResult.title.toString()}")
        runViewModel.insertRun(runResult)
        saveToServer()
    }

    private fun saveToServer() {
        val distance = runResult.distance.toDouble()
//        val endTime = runResult.timestamp.toString()
        val endTime = runningDate.time.toString()
        val runningTime = timeSpentInSeconds.toInt()
        val runningTitle = runResult.title.toString()

        TITLE = runningTitle

        Timber.e("Distance : ${distance}, EndTime: ${endTime}, runningTime : ${runningTime}, runningTitle: $runningTitle")

//        val body = mapOf(
//            "distance" to distance,
//            "endTime" to endTime,
//            "runningTime" to runningTime,
//            "title" to runningTitle)
        RetrofitClient.getInstance().recordCreate(token, distance, endTime, runningTime, runningTitle).enqueue(afterSaveToDB)
    }

    private var afterSaveToDB = object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("?????????????????????", "????????? ???????????????!")
        }
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            val mainActivity = activity as MainActivity
            val profileFragment = ProfileFragment()

            val result = response.body()?.string().toString()
            if (result == "SUCCESS") {
                Toast.makeText(context, "???????????? ??????????????? ??????????????????", Toast.LENGTH_LONG).show()
                Log.i("????????? ??????", "??????")
                mainActivity.sendCommandToService("ACTION_STOP_SERVICE")
                mainActivity.makeCurrentFragment(profileFragment)

            } else {
                Toast.makeText(context, "???????????? ???????????? ???????????????", Toast.LENGTH_LONG).show()
                Log.i("????????? ??????", "??????")
                Log.i("????????? ??????", "$result")
            }
        }

    }
//    private fun subscribeToObservers(activity: MainActivity, homeFragment: HomeFragment) {
//        TrackingService.totallyFinished.observe(viewLifecycleOwner, Observer {
//
//        })
//    }

    // ?????? ???????????? ?????? :
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions= PolylineOptions()
                    .color(Color.RED)
                    .width(8f)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }
//    val mainActivity = activity as MainActivity
//    val link = SingleRunFragment.mapFragmentToSingleRunFragment()
//    private val mapFragment = MapFragment(link)
//    private val subscribeToObservers = mapFragment.subscribeToObservers(mainActivity)

    private fun subscribeToObservers(activity: MainActivity) {
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.totallyFinished.observe(viewLifecycleOwner, Observer {
            if (it > 0) {
                if (map != null) {
                    zoomToSeeWholeTrack()
                } else {
                    TrackingService.totallyFinished.postValue(it+1)
                }
            }
        })
    }

    private fun addLatestPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                    .color(Color.RED)
                    .width(8f)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if ( pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            pathPoints.last().last(), 15f
                    )
            )
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                routeDataView.width,
                routeDataView.height,
                (routeDataView.height * 0.05f).toInt()
            )
        )
    }


}

